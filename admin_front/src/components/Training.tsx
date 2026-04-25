import {
  Search,
  Settings2,
  Play,
  Clock,
  Image as ImageIcon,
  ChevronDown,
  Info,
} from "lucide-react";
import { motion } from "motion/react";
import { createPortal } from "react-dom";
import { useEffect, useMemo, useRef, useState } from "react";
import { cn } from "@/src/lib/utils";

type TrainingItem = {
  id: number;
  imagePath: string;
  status: "INBOX" | "TRAINING_SET" | "TRASH";
  boxCount: number;
};

type LabelingPageResponse = {
  content: TrainingItem[];
  totalElements: number;
};

type TrainingJobResponse = {
  id: number;
  externalJobId?: string;
  status: "PENDING" | "RUNNING" | "SUCCEEDED" | "FAILED" | "CANCELLED";
  progress: number;
  message?: string;
};

type ToastState = {
  type: "success" | "error";
  text: string;
} | null;

function TooltipSelect<T extends string>({
  label,
  value,
  onChange,
  options,
  tooltipTitle,
  tooltipRows,
}: {
  label: string;
  value: T;
  onChange: (v: T) => void;
  options: { value: T; label: string }[];
  tooltipTitle: string;
  tooltipRows: { key: string; desc: string }[];
}) {
  const [tooltipOpen, setTooltipOpen] = useState(false);
  const tooltipWrapRef = useRef<HTMLDivElement>(null);
  const tooltipPanelRef = useRef<HTMLDivElement>(null);
  const btnRef = useRef<HTMLButtonElement>(null);
  const [tooltipPos, setTooltipPos] = useState({ x: 0, y: 0 });

  useEffect(() => {
    const handleClick = (e: MouseEvent) => {
      if (
        tooltipWrapRef.current &&
        !tooltipWrapRef.current.contains(e.target as Node) &&
        tooltipPanelRef.current &&
        !tooltipPanelRef.current.contains(e.target as Node)
      ) {
        setTooltipOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  const openTooltip = () => {
    if (btnRef.current) {
      const rect = btnRef.current.getBoundingClientRect();
      setTooltipPos({ x: rect.right + 8, y: Math.max(8, rect.top - 8) });
    }
    setTooltipOpen(true);
  };

  return (
    <div className="space-y-3">
      {/* Label row */}
      <div className="flex items-center gap-2 px-1">
        <span className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">
          {label}
        </span>
        <div className="relative" ref={tooltipWrapRef}>
          <button
            ref={btnRef}
            type="button"
            onMouseEnter={openTooltip}
            onMouseLeave={() => setTooltipOpen(false)}
            onClick={() => setTooltipOpen((v) => !v)}
            className="w-4 h-4 rounded-full border border-outline-variant/40 text-[9px] font-bold text-on-surface-variant flex items-center justify-center hover:border-primary/60 hover:text-primary transition-colors"
          >
            <Info className="w-2.5 h-2.5" />
          </button>
          {tooltipOpen &&
            createPortal(
              <div
                ref={tooltipPanelRef}
                style={{
                  position: "fixed",
                  left: tooltipPos.x,
                  top: tooltipPos.y,
                }}
                className="w-56 bg-surface-container-highest border border-outline-variant/20 rounded-xl p-3 z-[9999] shadow-xl"
                onMouseEnter={() => setTooltipOpen(true)}
                onMouseLeave={() => setTooltipOpen(false)}
              >
                <p className="text-[10px] font-bold text-on-surface mb-2">
                  {tooltipTitle}
                </p>
                {tooltipRows.map(({ key, desc }) => (
                  <div
                    key={key}
                    className="flex justify-between py-1.5 border-b border-outline-variant/10 last:border-0 gap-3"
                  >
                    <span className="text-[10px] font-bold text-on-surface shrink-0">
                      {key}
                    </span>
                    <span className="text-[10px] text-on-surface-variant text-right">
                      {desc}
                    </span>
                  </div>
                ))}
              </div>,
              document.body,
            )}
        </div>
      </div>

      {/* Select dropdown */}
      <div className="relative">
        <select
          value={value}
          onChange={(e) => onChange(e.target.value as T)}
          className="w-full bg-surface-container-high border border-outline-variant/20 rounded-xl p-4 text-xs text-on-surface focus:outline-none focus:border-primary transition-colors appearance-none cursor-pointer"
        >
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        <ChevronDown className="absolute right-4 top-1/2 -translate-y-1/2 w-4 h-4 text-outline-variant pointer-events-none" />
      </div>
    </div>
  );
}

export default function Training() {
  const [learningRate, setLearningRate] = useState(0.001);
  const [optimizer, setOptimizer] = useState("Adam");
  const [freezeLayers, setFreezeLayers] = useState("medium");
  const [batchSize, setBatchSize] = useState(16);
  const [epochs, setEpochs] = useState(100);
  const [items, setItems] = useState<TrainingItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [searchText, setSearchText] = useState("");
  const [job, setJob] = useState<TrainingJobResponse | null>(null);
  const [isStarting, setIsStarting] = useState(false);
  const [jobError, setJobError] = useState<string | null>(null);
  const [logs, setLogs] = useState<string[]>([]);
  const [toast, setToast] = useState<ToastState>(null);

  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
  const fastApiBaseUrl =
    import.meta.env.VITE_FAST_API_BASE_URL ?? "http://localhost:8001";
  const token = localStorage.getItem("admin_access_token");

  const resolveImageUrl = (imagePath: string) => {
    if (!imagePath) return "";
    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
      return imagePath;
    }
    return `${apiBaseUrl}${imagePath}`;
  };

  useEffect(() => {
    const fetchTrainingItems = async () => {
      if (!token) return;
      setIsLoading(true);
      try {
        const response = await fetch(
          `${apiBaseUrl}/api/admin/labeling/items?status=TRAINING_SET&page=0&pageSize=200`,
          { headers: { Authorization: `Bearer ${token}` } },
        );
        if (!response.ok) throw new Error("Training Set 목록 조회 실패");
        const pageData = (await response.json()) as LabelingPageResponse;
        setItems(pageData.content ?? []);
      } catch (error) {
        console.error("[Training] fetch failed", error);
        setItems([]);
      } finally {
        setIsLoading(false);
      }
    };

    void fetchTrainingItems();
  }, [apiBaseUrl, token]);

  useEffect(() => {
    if (!job?.externalJobId) return;

    let receivedDone = false;
    let hasReceivedLog = false;
    const es = new EventSource(
      `${fastApiBaseUrl}/train/jobs/${job.externalJobId}/logs/stream`,
    );

    es.onopen = () => {
      // optional: connection established
    };

    es.addEventListener("log", (event) => {
      try {
        const parsed = JSON.parse((event as MessageEvent).data) as { line?: string };
        if (!parsed.line) return;
        hasReceivedLog = true;
        setLogs((prev) => [...prev.slice(-199), parsed.line!]);
      } catch {
        // ignore malformed log payload
      }
    });

    es.addEventListener("progress", () => {
      // progress is polled from Spring, no-op for now
    });

    es.addEventListener("done", (event) => {
      receivedDone = true;
      try {
        const parsed = JSON.parse((event as MessageEvent).data) as {
          status?: string;
          message?: string;
        };
        if (parsed.status === "SUCCEEDED") {
          setToast({ type: "success", text: parsed.message ?? "학습이 완료되었습니다." });
        } else {
          setToast({ type: "error", text: parsed.message ?? "학습이 종료되었습니다." });
        }
      } catch {
        setToast({ type: "success", text: "학습이 완료되었습니다." });
      }
      es.close();
    });

    es.addEventListener("error", () => {
      if (!receivedDone && !hasReceivedLog && es.readyState === EventSource.CLOSED) {
        setToast({ type: "error", text: "로그 스트림 연결에 실패했습니다." });
      }
    });

    return () => {
      es.close();
    };
  }, [fastApiBaseUrl, job?.externalJobId]);

  useEffect(() => {
    if (!toast) return;
    const timer = window.setTimeout(() => setToast(null), 3500);
    return () => window.clearTimeout(timer);
  }, [toast]);

  const handleStartTraining = async () => {
    if (!token) return;
    setIsStarting(true);
    setJobError(null);
    try {
      const response = await fetch(`${apiBaseUrl}/api/admin/training/jobs`, {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          datasetStatus: "TRAINING_SET",
          epochs,
          batchSize,
          learningRate,
          optimizer,
          freezeLayers,
        }),
      });
      if (!response.ok) throw new Error("학습 시작에 실패했습니다.");
      const created = (await response.json()) as TrainingJobResponse;
      setJob(created);
      setLogs([]);
    } catch (error) {
      setJobError(error instanceof Error ? error.message : "학습 시작 중 오류가 발생했습니다.");
    } finally {
      setIsStarting(false);
    }
  };

  const filteredItems = useMemo(() => {
    const keyword = searchText.trim().toLowerCase();
    if (!keyword) return items;
    return items.filter(
      (item) =>
        item.imagePath.toLowerCase().includes(keyword) ||
        String(item.id).includes(keyword),
    );
  }, [items, searchText]);

  return (
    <div className="ml-64 mt-36 p-8 pr-[420px] min-h-screen bg-background">
      {/* Top Status Widget */}
      <div className="fixed top-28 left-64 right-0 z-40 h-12 bg-surface-container-low/60 backdrop-blur-md border-b border-outline-variant/10 flex items-center justify-between px-8">
        <div className="flex items-center gap-6">
          <div className="flex items-center gap-2">
            <ImageIcon className="w-4 h-4 text-primary" />
            <span className="text-xs font-mono text-on-surface-variant">
              Total Images:{" "}
              <span className="text-on-surface font-bold">{items.length}</span>
            </span>
          </div>
          <div className="flex items-center gap-2">
            <Clock className="w-4 h-4 text-tertiary" />
            <span className="text-xs font-mono text-on-surface-variant">
              Job:{" "}
              <span className="text-on-surface font-bold">
                {job ? `${job.status} (${job.progress ?? 0}%)` : "IDLE"}
              </span>
            </span>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-2 h-2 rounded-full bg-green-500 ai-pulse"></div>
          <span className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">
            System Ready
          </span>
        </div>
      </div>
      {toast && (
        <div
          className={cn(
            "fixed top-44 right-[420px] z-50 rounded-xl px-4 py-2 text-xs font-bold shadow-lg",
            toast.type === "success"
              ? "bg-green-500/90 text-black"
              : "bg-error/90 text-white",
          )}
        >
          {toast.text}
        </div>
      )}

      <div className="flex flex-col gap-8">
        <header className="flex justify-between items-center">
          <h1 className="text-3xl font-black tracking-tight text-on-surface">
            Data Grid
          </h1>
          <div className="relative group w-96">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-outline group-focus-within:text-primary transition-colors" />
            <input
              className="w-full bg-surface-container border border-outline-variant/20 rounded-xl py-3 pl-12 pr-4 text-xs text-on-surface placeholder:text-outline-variant focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary/20 transition-all"
              placeholder="데이터셋 필터링 및 검색..."
              value={searchText}
              onChange={(e) => setSearchText(e.target.value)}
              type="text"
            />
          </div>
        </header>

        {/* Image Grid */}
        <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-6">
          {filteredItems.map((item, idx) => (
            <motion.div
              key={item.id}
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ delay: idx * 0.05 }}
              className="group bg-surface-container rounded-xl overflow-hidden border border-outline-variant/10 hover:border-primary/50 transition-all shadow-lg"
            >
              <div className="aspect-square overflow-hidden relative">
                <img
                  className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                  src={resolveImageUrl(item.imagePath)}
                  alt={`training-item-${item.id}`}
                  referrerPolicy="no-referrer"
                />
                <div className="absolute inset-0 bg-gradient-to-t from-background/80 to-transparent opacity-0 group-hover:opacity-100 transition-opacity flex items-end p-3">
                  <span className="text-[8px] font-bold text-white uppercase tracking-widest bg-primary/40 px-1.5 py-0.5 rounded">
                    Metadata Synced
                  </span>
                </div>
              </div>
              <div className="p-3">
                <p className="text-[10px] font-bold text-on-surface truncate">
                  {item.imagePath.split("/").pop() ?? `item-${item.id}`}
                </p>
                <p className="text-[9px] text-on-surface-variant font-mono mt-1 opacity-60">
                  ID {item.id} · BOX {item.boxCount}
                </p>
              </div>
            </motion.div>
          ))}
        </div>
        {isLoading && (
          <p className="text-xs text-on-surface-variant">
            Training Set 불러오는 중...
          </p>
        )}
      </div>

      {/* Right Configuration Panel */}
      <aside className="fixed right-0 top-16 bottom-0 w-[380px] bg-background/80 backdrop-blur-2xl border-l border-outline-variant/10 z-30 flex flex-col p-6 shadow-2xl">
        <div className="flex items-center gap-3 mb-8">
          <Settings2 className="w-5 h-5 text-primary" />
          <h2 className="text-sm font-bold uppercase tracking-widest text-on-surface">
            Hyperparameter Configuration
          </h2>
        </div>

        <div className="space-y-8 flex-1 overflow-y-auto pr-2 custom-scrollbar">
          {/* Base Model Select */}
          <div className="space-y-3">
            <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest px-1">
              Base Model Select
            </label>
          </div>

          {/* Learning Rate */}
          <div className="space-y-4">
            <div className="flex justify-between items-center px-1">
              <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">
                Learning Rate
              </label>
              <span className="text-xs font-mono text-primary font-bold">
                {learningRate.toFixed(4)}
              </span>
            </div>
            <div className="space-y-4 px-1">
              <input
                type="range"
                min="0.0001"
                max="0.01"
                step="0.0001"
                value={learningRate}
                onChange={(e) => setLearningRate(parseFloat(e.target.value))}
                className="w-full h-1.5 bg-surface-container-highest rounded-full appearance-none cursor-pointer accent-primary"
              />
              <input
                type="number"
                value={learningRate}
                onChange={(e) => setLearningRate(parseFloat(e.target.value))}
                className="w-full bg-surface-container-high border border-outline-variant/20 rounded-xl p-3 text-xs font-mono text-on-surface focus:outline-none focus:border-primary transition-colors"
                step="0.0001"
              />
            </div>
          </div>

          {/* Batch Size */}
          <div className="space-y-3">
            <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest px-1">
              Batch Size
            </label>
            <div className="relative">
              <select
                value={batchSize}
                onChange={(e) => setBatchSize(Number(e.target.value))}
                className="w-full bg-surface-container-high border border-outline-variant/20 rounded-xl p-4 text-xs text-on-surface focus:outline-none focus:border-primary transition-colors appearance-none cursor-pointer"
              >
                <option value={16}>16</option>
                <option value={32}>32</option>
                <option value={64}>64</option>
                <option value={128}>128</option>
              </select>
              <ChevronDown className="absolute right-4 top-1/2 -translate-y-1/2 w-4 h-4 text-outline-variant pointer-events-none" />
            </div>
          </div>

          {/* Epochs */}
          <div className="space-y-3">
            <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest px-1">
              Epochs
            </label>
            <input
              type="number"
              value={epochs}
              onChange={(e) => setEpochs(Number(e.target.value))}
              className="w-full bg-surface-container-high border border-outline-variant/20 rounded-xl p-4 text-xs font-mono text-on-surface focus:outline-none focus:border-primary transition-colors"
            />
          </div>

          {/* Optimizer - 드롭다운 + 툴팁 */}
          <TooltipSelect
            label="Optimizer"
            value={optimizer}
            onChange={setOptimizer}
            options={[
              { value: "Adam", label: "Adam" },
              { value: "SGD", label: "SGD" },
              { value: "RMSprop", label: "RMSprop" },
            ]}
            tooltipTitle="옵티마이저 설명"
            tooltipRows={[
              { key: "Adam", desc: "적응형 학습률. 파인튜닝 기본값으로 권장" },
              { key: "SGD", desc: "안정적 수렴. 데이터가 많을 때 유리" },
              { key: "RMSprop", desc: "불안정한 그래디언트 환경에 적합" },
            ]}
          />

          {/* Freeze Layers - 드롭다운 + 툴팁 */}
          <TooltipSelect
            label="Freeze Layers"
            value={freezeLayers}
            onChange={setFreezeLayers}
            options={[
              { value: "none", label: "None — 모든 레이어 학습" },
              { value: "light", label: "Light — 앞 25% 동결" },
              { value: "medium", label: "Medium — 앞 50% 동결" },
              { value: "heavy", label: "Heavy — 앞 75% 동결" },
            ]}
            tooltipTitle="데이터 수 기준 추천"
            tooltipRows={[
              { key: "None", desc: "1000개 이상" },
              { key: "Light", desc: "500 ~ 1000개" },
              { key: "Medium", desc: "100 ~ 500개" },
              { key: "Heavy", desc: "100개 이하" },
            ]}
          />

          <div className="pt-6 border-t border-outline-variant/10">
            <button
              onClick={handleStartTraining}
              disabled={isStarting}
              className="w-full py-4 bg-gradient-to-r from-primary to-primary/80 text-on-primary font-black text-xs uppercase tracking-[0.2em] rounded-xl flex items-center justify-center gap-3 shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all group disabled:opacity-60"
            >
              <Play className="w-5 h-5 fill-current group-hover:scale-110 transition-transform" />
              {isStarting ? "STARTING..." : "INITIATE TRAINING"}
            </button>
            {jobError && (
              <p className="text-[10px] text-error text-center mt-2">{jobError}</p>
            )}
            <p className="text-[9px] text-center text-on-surface-variant mt-4 font-semibold uppercase tracking-widest opacity-60 italic">
              Ensure GPU cluster is in idle state before starting.
            </p>
          </div>
        </div>
      </aside>
    </div>
  );
}
