import {
  createContext,
  useContext,
  useCallback,
  useEffect,
  useRef,
  useState,
  type ReactNode,
} from "react";

type StreamStatus = "idle" | "connecting" | "running" | "done" | "error";
type GlobalToast = {
  type: "success" | "error";
  title: string;
  description?: string;
} | null;

export type TrainingJob = {
  id: number;
  externalJobId?: string;
  status: "PENDING" | "RUNNING" | "SUCCEEDED" | "FAILED" | "CANCELLED";
  progress: number;
  message?: string;
};

type TrainingStreamContextType = {
  logs: string[];
  streamStatus: StreamStatus;
  progress: number;
  job: TrainingJob | null;
  setJob: (job: TrainingJob | null) => void;
  clearLogs: () => void;
  connectStream: (externalJobId?: string) => Promise<void>;
  toast: GlobalToast;
  dismissToast: () => void;
};

const TrainingStreamContext = createContext<TrainingStreamContextType | null>(
  null,
);

const TRAINING_STREAM_CACHE_KEY = "training_stream_cache_v1";

function extractLatestModelName(logs: string[]): string | null {
  for (let i = logs.length - 1; i >= 0; i -= 1) {
    const line = logs[i];
    const match = line.match(/Best weights:\s.*\/([^\/]+)\/weights\/best\.pt/i);
    if (match?.[1]) {
      return match[1];
    }
  }
  return null;
}

export function TrainingStreamProvider({ children }: { children: ReactNode }) {
  const [logs, setLogs] = useState<string[]>([]);
  const [streamStatus, setStreamStatus] = useState<StreamStatus>(() => {
    try {
      const raw = localStorage.getItem(TRAINING_STREAM_CACHE_KEY);
      if (!raw) return "idle";
      const parsed = JSON.parse(raw) as { streamStatus?: StreamStatus };
      return parsed.streamStatus ?? "idle";
    } catch {
      return "idle";
    }
  });
  const [progress, setProgress] = useState(0);
  const [job, setJob] = useState<TrainingJob | null>(() => {
    try {
      const raw = localStorage.getItem(TRAINING_STREAM_CACHE_KEY);
      if (!raw) return null;
      const parsed = JSON.parse(raw) as { job?: TrainingJob | null };
      return parsed.job ?? null;
    } catch {
      return null;
    }
  });
  const [toast, setToast] = useState<GlobalToast>(null);
  const esRef = useRef<EventSource | null>(null);
  const hydratedRef = useRef(false);

  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
  const fastApiBaseUrl =
    import.meta.env.VITE_FAST_API_BASE_URL ?? "http://localhost:8001";

  const connectStream = useCallback(
    async (externalJobId?: string) => {
      // 파라미터 추가
      const token = localStorage.getItem("admin_access_token");
      if (!token) return;
      if (esRef.current) return;

      setStreamStatus("connecting");

      try {
        let jobId = externalJobId; // 직접 받은 경우 Spring 재조회 생략

        if (!jobId) {
          // 앱 최초 마운트 시 - Spring에서 RUNNING job 조회
          const res = await fetch(
            `${apiBaseUrl}/api/admin/training/jobs?page=0&pageSize=1&sort=id,desc`,
            { headers: { Authorization: `Bearer ${token}` } },
          );
          if (!res.ok) {
            setStreamStatus("error");
            return;
          }

          const page = await res.json();
          const latestJob = page.content?.[0];

          if (!latestJob?.externalJobId || latestJob.status !== "RUNNING") {
            setJob(latestJob ?? null);
            setProgress(latestJob?.progress ?? 0);
            if (!latestJob) {
              setStreamStatus("idle");
            } else if (latestJob.status === "SUCCEEDED") {
              setStreamStatus("done");
            } else if (latestJob.status === "FAILED" || latestJob.status === "CANCELLED") {
              setStreamStatus("error");
            } else {
              setStreamStatus("idle");
            }
            return;
          }
          jobId = latestJob.externalJobId as string;
        }

        // SSE 연결
        const es = new EventSource(
          `${fastApiBaseUrl}/train/jobs/${jobId}/logs/stream`,
        );
        esRef.current = es;

        es.addEventListener("log", (e) => {
          const payload = JSON.parse((e as MessageEvent).data) as {
            line?: string;
            progress?: number;
          };
          if (payload.line) {
            setLogs((prev) => [...prev.slice(-499), payload.line!]);
          }
          if (payload.progress !== undefined) {
            setProgress(payload.progress);
          }
          setStreamStatus("running");
        });

        es.addEventListener("done", (e) => {
          const payload = JSON.parse((e as MessageEvent).data) as {
            status?: "SUCCEEDED" | "FAILED" | "CANCELLED";
            message?: string;
          };
          if (payload.message) {
            setLogs((prev) => [...prev, `[SYSTEM] ${payload.message}`]);
          }
          const isSuccess = payload.status === "SUCCEEDED";
          if (isSuccess) {
            setStreamStatus("done");
            setProgress(100);
            setJob((prev) =>
              prev
                ? {
                    ...prev,
                    status: "SUCCEEDED",
                    progress: 100,
                    message: payload.message ?? prev.message,
                  }
                : prev,
            );
            setToast({
              type: "success",
              title: "학습 완료",
              description: (() => {
                const modelName = extractLatestModelName(logs);
                return modelName
                  ? `${modelName} 모델 학습이 성공적으로 완료되었습니다.`
                  : "모델 학습이 성공적으로 완료되었습니다.";
              })(),
            });
          } else {
            setStreamStatus("error");
            setJob((prev) =>
              prev
                ? {
                    ...prev,
                    status: payload.status ?? "FAILED",
                    progress: prev.progress ?? 0,
                    message: payload.message ?? prev.message,
                  }
                : prev,
            );
            setToast({
              type: "error",
              title: "학습 실패",
              description: payload.message ?? "학습 중 오류가 발생했습니다.",
            });
          }
          es.close();
          esRef.current = null;
        });

        es.addEventListener("error", () => {
          setStreamStatus("error");
          setToast({
            type: "error",
            title: "학습 실패",
            description: "로그 스트림 연결에 실패했습니다.",
          });
          es.close();
          esRef.current = null;
        });
      } catch {
        setStreamStatus("error");
        setToast({
          type: "error",
          title: "학습 실패",
          description: "로그 스트림 연결에 실패했습니다.",
        });
      }
    },
    [apiBaseUrl, fastApiBaseUrl],
  );

  useEffect(() => {
    if (hydratedRef.current) return;
    hydratedRef.current = true;
    try {
      const raw = localStorage.getItem(TRAINING_STREAM_CACHE_KEY);
      if (!raw) return;
      const parsed = JSON.parse(raw) as {
        logs?: string[];
        progress?: number;
      };
      if (Array.isArray(parsed.logs)) {
        setLogs(parsed.logs.slice(-500));
      }
      if (typeof parsed.progress === "number") {
        setProgress(parsed.progress);
      }
    } catch {
      // ignore malformed cache
    }
  }, []);

  useEffect(() => {
    try {
      localStorage.setItem(
        TRAINING_STREAM_CACHE_KEY,
        JSON.stringify({
          logs: logs.slice(-500),
          streamStatus,
          progress,
          job,
        }),
      );
    } catch {
      // ignore storage failures
    }
  }, [logs, streamStatus, progress, job]);

  useEffect(() => {
    void connectStream();
    return () => {
      esRef.current?.close();
      esRef.current = null;
    };
  }, [connectStream]);

  const clearLogs = () => {
    setLogs([]);
    setProgress(0);
    setStreamStatus("idle");
    try {
      localStorage.removeItem(TRAINING_STREAM_CACHE_KEY);
    } catch {
      // ignore storage failures
    }
  };
  const dismissToast = () => setToast(null);

  return (
    <TrainingStreamContext.Provider
      value={{
        logs,
        streamStatus,
        progress,
        job,
        setJob,
        clearLogs,
        connectStream,
        toast,
        dismissToast,
      }}
    >
      {children}
    </TrainingStreamContext.Provider>
  );
}

export function useTrainingStream() {
  const ctx = useContext(TrainingStreamContext);
  if (!ctx) throw new Error("TrainingStreamProvider 밖에서 사용됨");
  return ctx;
}
