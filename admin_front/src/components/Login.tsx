import { useEffect, useState } from "react";
import React from "react";
import {
  Mail,
  Lock,
  LogIn,
  ShieldAlert,
  Fingerprint,
  ShieldCheck,
  Cpu,
} from "lucide-react";
import { motion } from "motion/react";
import loginPillLogo from "../assets/images/login-pill-logo.png";

type AdminLoginResponse = {
  accessToken: string;
  expiresIn: number;
  userId: number;
};

type LoginSuccessPayload = {
  accessToken: string;
  userId: number;
};

type TrainingSystemStatusResponse = {
  connected?: boolean;
  status?: "READY" | "OFFLINE";
  runningJobs?: number;
  pendingJobs?: number;
};

export default function Login({
  onLogin,
}: {
  onLogin: (payload: LoginSuccessPayload) => void;
}) {
  const [isLoading, setIsLoading] = useState(false);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [systemOnline, setSystemOnline] = useState(false);
  const [runningJobs, setRunningJobs] = useState(0);
  const [pendingJobs, setPendingJobs] = useState(0);

  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

  useEffect(() => {
    const es = new EventSource(`${apiBaseUrl}/api/internal/training/jobs/system-status/stream`);

    es.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data) as TrainingSystemStatusResponse;
        const online = data.connected !== false && data.status !== "OFFLINE";
        setSystemOnline(online);
        setRunningJobs(data.runningJobs ?? 0);
        setPendingJobs(data.pendingJobs ?? 0);
      } catch {
        setSystemOnline(false);
        setRunningJobs(0);
        setPendingJobs(0);
      }
    };

    es.onerror = () => {
      setSystemOnline(false);
      setRunningJobs(0);
      setPendingJobs(0);
    };

    return () => {
      es.close();
    };
  }, [apiBaseUrl]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const response = await fetch(`${apiBaseUrl}/api/auth/admin/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        throw new Error("관리자 로그인에 실패했습니다.");
      }

      const data = (await response.json()) as AdminLoginResponse;
      onLogin({ accessToken: data.accessToken, userId: data.userId });
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "로그인 중 오류가 발생했습니다.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen w-full flex items-center justify-center p-4 relative overflow-hidden bg-background">
      {/* Background Visualization */}
      <div className="fixed inset-0 z-0">
        <img
          className="w-full h-full object-cover opacity-20 grayscale brightness-50"
          src="https://picsum.photos/seed/network/1920/1080?blur=4"
          referrerPolicy="no-referrer"
          alt="Neural network background"
        />
        <div className="absolute inset-0 bg-gradient-to-tr from-background via-background/80 to-transparent"></div>
      </div>

      <motion.main
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="relative z-10 w-full max-w-6xl grid grid-cols-1 md:grid-cols-12 gap-0 overflow-hidden rounded-2xl shadow-2xl bg-surface-container/40 backdrop-blur-2xl border border-outline-variant/10"
      >
        {/* Left Panel */}
        <div className="md:col-span-7 p-8 md:p-16 flex flex-col justify-between">
          <div>
            <div className="flex items-center gap-3 mb-12">
              <div className="w-20 h-20 bg-transparent flex items-center justify-center rounded-lg shadow-lg border border-outline-variant/10 p-1">
                <img
                  src={loginPillLogo}
                  alt="Alyak Logo"
                  className="w-full h-auto"
                />
              </div>
              <span className="text-2xl font-black tracking-tighter text-primary">
                Alyak
              </span>
            </div>

            <h1 className="text-4xl md:text-6xl font-extrabold tracking-tight text-on-surface mb-6 leading-tight">
              알약 탐지 <br />
              <span className="text-primary">강화학습</span> <br />
              관리 포털
            </h1>

            <p className="text-on-surface-variant max-w-md text-lg leading-relaxed font-medium">
              알약 식별 모델의 학습 데이터를 검증하고, 실시간으로 강화학습
              루프를 모니터링합니다.
            </p>
          </div>

          <div className="mt-12 space-y-6 hidden md:block">
            <motion.div
              animate={{ rotate: [-2, 0, -2] }}
              transition={{ duration: 6, repeat: Infinity, ease: "easeInOut" }}
              className="bg-surface-container-low p-6 rounded-xl border border-outline-variant/10 max-w-xs shadow-xl"
            >
              <div className="flex items-center justify-between mb-4">
                <span className="text-[10px] uppercase tracking-widest text-on-surface-variant font-bold">
                  시스템 상태
                </span>
                <div
                  className={
                    systemOnline
                      ? "w-2 h-2 rounded-full bg-tertiary ai-pulse"
                      : "w-2 h-2 rounded-full bg-red-500 ai-pulse"
                  }
                ></div>
              </div>
              <div className="text-2xl font-black text-on-surface">
                {systemOnline ? "ONLINE" : "OFFLINE"}
              </div>
              <p className="text-[10px] text-on-surface-variant mt-1 font-medium">
                {systemOnline
                  ? "학습 서버/아카이브 API 연결 정상"
                  : "학습 서버 상태를 확인할 수 없습니다"}
              </p>
              <div className="h-1 bg-surface-container-highest mt-3 rounded-full overflow-hidden">
                <motion.div
                  initial={{ width: 0 }}
                  animate={{ width: "100%" }}
                  transition={{ duration: 2, delay: 0.5 }}
                  className="h-full bg-primary"
                />
              </div>
            </motion.div>

            <motion.div
              animate={{ x: [48, 40, 48], rotate: [1, -1, 1] }}
              transition={{ duration: 5, repeat: Infinity, ease: "easeInOut" }}
              className="bg-surface-container-high p-6 rounded-xl border border-outline-variant/10 max-w-xs shadow-xl transform"
            >
              <div className="text-[10px] uppercase tracking-widest text-on-surface-variant font-bold mb-2">
                최근 학습 상태
              </div>
              <div className="flex items-baseline gap-2">
                <span className="text-3xl font-black text-primary">
                  {runningJobs > 0 ? "RUNNING" : systemOnline ? "READY" : "WAIT"}
                </span>
                <span className="text-xs text-tertiary font-mono">QUEUE {pendingJobs}</span>
              </div>
              <p className="text-[10px] text-on-surface-variant mt-2 font-medium">
                {runningJobs > 0
                  ? `현재 ${runningJobs}개 작업이 실행 중입니다`
                  : systemOnline
                    ? "즉시 학습 요청을 처리할 수 있습니다"
                    : "학습 서버 재연결을 대기 중입니다"}
              </p>
            </motion.div>
          </div>
        </div>

        {/* Right Panel */}
        <div className="md:col-span-5 bg-surface-container-low/60 p-8 md:p-16 flex flex-col justify-center border-l border-outline-variant/10">
          <div className="mb-10">
            <h2 className="text-2xl font-bold text-on-surface mb-2">
              관리자 포털
            </h2>
          </div>

          <form className="space-y-6" onSubmit={handleSubmit}>
            <div className="space-y-2">
              <label
                className="text-[10px] uppercase tracking-widest font-bold text-on-surface-variant px-1"
                htmlFor="id"
              >
                사원 ID / 이메일
              </label>
              <div className="relative group">
                <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-outline group-focus-within:text-primary transition-colors" />
                <input
                  className="w-full bg-background border border-outline-variant/20 rounded-lg py-4 pl-12 pr-4 text-on-surface placeholder:text-outline-variant focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary/20 transition-all"
                  id="id"
                  placeholder="admin_id_042"
                  type="text"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  required
                />
              </div>
            </div>

            <div className="space-y-2">
              <div className="flex justify-between items-center px-1">
                <label
                  className="text-[10px] uppercase tracking-widest font-bold text-on-surface-variant"
                  htmlFor="password"
                >
                  보안 키
                </label>
                <a
                  className="text-[10px] font-bold text-primary hover:underline"
                  href="#"
                >
                  접속 정보를 잊으셨나요?
                </a>
              </div>
              <div className="relative group">
                <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-outline group-focus-within:text-primary transition-colors" />
                <input
                  className="w-full bg-background border border-outline-variant/20 rounded-lg py-4 pl-12 pr-4 text-on-surface placeholder:text-outline-variant focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary/20 transition-all"
                  id="password"
                  placeholder="••••••••"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  required
                />
              </div>
            </div>

            {errorMessage && (
              <p className="text-xs text-red-400 px-1">{errorMessage}</p>
            )}

            <div className="flex items-center gap-3 px-1">
              <input
                type="checkbox"
                id="remember"
                className="w-4 h-4 rounded border-outline-variant/30 bg-background text-primary focus:ring-primary"
              />
              <label
                className="text-xs text-on-surface-variant font-medium select-none cursor-pointer"
                htmlFor="remember"
              >
                로그인 상태 유지
              </label>
            </div>

            <button
              disabled={isLoading}
              className="w-full bg-primary text-on-primary font-bold py-4 rounded-lg shadow-lg shadow-primary/20 hover:scale-[1.01] active:scale-[0.98] transition-all flex items-center justify-center gap-2 group relative overflow-hidden"
              type="submit"
            >
              {isLoading ? (
                <div className="w-5 h-5 border-2 border-on-primary/30 border-t-on-primary rounded-full animate-spin" />
              ) : (
                <>
                  로그인
                  <LogIn className="w-5 h-5 transition-transform group-hover:translate-x-1" />
                </>
              )}
            </button>
          </form>
        </div>
      </motion.main>

      <footer className="fixed bottom-8 w-full text-center z-10 pointer-events-none opacity-40">
        <p className="text-[10px] text-on-surface-variant font-bold uppercase tracking-[0.4em]">
          © 2026 V1 Alyak Admin Portal
        </p>
      </footer>
    </div>
  );
}
