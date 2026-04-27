import {
  BrainCircuit,
  Cpu,
  Database,
  HelpCircle,
  History as HistoryIcon,
  LayoutDashboard,
  LogOut,
  RotateCcw,
  ShieldCheck,
  Terminal,
} from "lucide-react";
import { cn } from "@/src/lib/utils";
import { motion } from "motion/react";
import { useEffect, useRef, useState } from "react";
import { ViewType } from "../App";
import { useTrainingStream } from "./TrainingStreamContext";

const navItems: { icon: any; label: ViewType; displayLabel: string }[] = [
  { icon: LayoutDashboard, label: "Overview", displayLabel: "Overview" },
  { icon: BrainCircuit, label: "Training", displayLabel: "Training" },
  { icon: Terminal, label: "TrainingLogs", displayLabel: "Live Training Logs" },
  { icon: HistoryIcon, label: "Archives", displayLabel: "Archives" },
];

const secondaryItems = [{ icon: HelpCircle, label: "Support" }];
const MAX_AUTO_RETRY = 10;

type TrainingSystemStatusResponse = {
  status?: "READY" | "OFFLINE";
  connected?: boolean;
  message?: string;
  device?: string;
  cpuName?: string | null;
  cpuLoadPercent?: number | null;
  gpuAvailable?: boolean;
  gpuName?: string | null;
  gpuMemoryTotalMb?: number | null;
  gpuMemoryUsedMb?: number | null;
};

function ResourceItem({
  icon: Icon,
  label,
  detail,
  usage,
}: {
  icon: any;
  label: string;
  detail: string;
  usage: number;
}) {
  return (
    <div className="space-y-1.5">
      <div className="flex justify-between items-end">
        <div className="flex items-center gap-2">
          <Icon className="w-3 h-3 text-on-surface-variant opacity-50" />
          <span className="text-[9px] font-bold text-on-surface uppercase tracking-wider">
            {label}
          </span>
        </div>
        <span className="text-[8px] font-mono text-on-surface-variant">
          {detail}
        </span>
      </div>
      <div className="relative h-1 bg-surface-container-highest rounded-full overflow-hidden">
        <motion.div
          initial={{ width: 0 }}
          animate={{ width: `${usage}%` }}
          className={cn(
            "h-full rounded-full transition-colors",
            usage > 80
              ? "bg-error"
              : usage > 50
                ? "bg-tertiary"
                : "bg-primary shadow-[0_0_8px_rgba(123,208,255,0.4)]",
          )}
        />
      </div>
    </div>
  );
}

interface SidebarProps {
  onLogout: () => void;
  currentView: ViewType;
  onViewChange: (view: ViewType) => void;
}

export default function Sidebar({
  onLogout,
  currentView,
  onViewChange,
}: SidebarProps) {
  const { progress, streamStatus } = useTrainingStream();
  const [systemStatus, setSystemStatus] = useState<TrainingSystemStatusResponse | null>(null);
  const [systemRetryCount, setSystemRetryCount] = useState(0);
  const [manualRetryTick, setManualRetryTick] = useState(0);
  const retryCountRef = useRef(0);
  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
  const token = localStorage.getItem("admin_access_token");

  useEffect(() => {
    if (!token) return;
    let mounted = true;
    let timerId: number | undefined;

    const fetchSystemStatus = async () => {
      try {
        const response = await fetch(`${apiBaseUrl}/api/admin/training/system-status`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!response.ok) throw new Error("시스템 상태 조회 실패");
        const data = (await response.json()) as TrainingSystemStatusResponse;
        if (!mounted) return;
        setSystemStatus(data);
        if (data.connected === false || data.status === "OFFLINE") {
          setSystemRetryCount((prev) => {
            const next = Math.min(prev + 1, MAX_AUTO_RETRY);
            retryCountRef.current = next;
            return next;
          });
        } else {
          setSystemRetryCount(0);
          retryCountRef.current = 0;
        }
      } catch {
        if (!mounted) return;
        setSystemStatus({
          status: "OFFLINE",
          connected: false,
          message: "재연결 시도 중...",
          device: "cpu",
        });
        setSystemRetryCount((prev) => {
          const next = Math.min(prev + 1, MAX_AUTO_RETRY);
          retryCountRef.current = next;
          return next;
        });
      } finally {
        if (mounted && retryCountRef.current < MAX_AUTO_RETRY) {
          timerId = window.setTimeout(fetchSystemStatus, 7000);
        }
      }
    };

    void fetchSystemStatus();
    return () => {
      mounted = false;
      if (timerId) window.clearTimeout(timerId);
    };
  }, [apiBaseUrl, token, manualRetryTick]);

  const isSystemConnected =
    systemStatus?.connected !== false && systemStatus?.status !== "OFFLINE";
  const cpuText =
    systemStatus?.cpuLoadPercent != null
      ? `CPU ${systemStatus.cpuLoadPercent.toFixed(1)}%`
      : "CPU -";
  const cpuNameText = systemStatus?.cpuName?.trim() || "CPU 정보 없음";
  const gpuNameText = systemStatus?.gpuName?.trim() || "GPU 정보 없음";
  const gpuText =
    systemStatus?.gpuAvailable
      ? systemStatus.gpuMemoryTotalMb != null && systemStatus.gpuMemoryUsedMb != null
        ? `GPU ${systemStatus.gpuMemoryUsedMb}/${systemStatus.gpuMemoryTotalMb}MB`
        : `GPU ${systemStatus.gpuName ?? "사용 가능"}`
      : "GPU 미사용";
  const cpuUsage = Math.max(0, Math.min(100, systemStatus?.cpuLoadPercent ?? 0));
  const gpuUsage = systemStatus?.gpuAvailable && systemStatus?.gpuMemoryTotalMb && systemStatus?.gpuMemoryUsedMb
    ? Math.max(0, Math.min(100, Math.round((systemStatus.gpuMemoryUsedMb / systemStatus.gpuMemoryTotalMb) * 100)))
    : 0;
  const isAutoRetryExhausted = !isSystemConnected && systemRetryCount >= MAX_AUTO_RETRY;

  return (
    <aside className="fixed left-0 top-0 h-screen w-64 z-40 bg-surface-container-lowest border-r border-outline-variant/10 flex flex-col p-4 gap-2">
      <div className="mt-20 mb-8 px-2 flex items-center gap-3">
        <div className="w-10 h-10 rounded-xl bg-primary/20 border border-primary/30 flex items-center justify-center shadow-[0_0_15px_rgba(123,208,255,0.2)]">
          <BrainCircuit className="w-6 h-6 text-primary" />
        </div>
        <div>
          <h2 className="text-sm font-black text-primary leading-none uppercase tracking-tighter">
            Alyak Training Console
          </h2>
          <p className="text-[10px] text-on-surface font-black uppercase tracking-widest mt-1 bg-primary/10 px-1.5 py-0.5 rounded border border-primary/20 inline-block">
            web v1
          </p>
        </div>
      </div>
      <nav className="flex-1 flex flex-col gap-1">
        {navItems.map((item) => (
          <button
            key={item.label}
            onClick={() => onViewChange(item.label)}
            className={cn(
              "w-full flex justify-between items-center px-3 py-2.5 rounded-lg transition-all text-xs font-semibold uppercase tracking-widest text-left group",
              currentView === item.label
                ? "bg-surface-container text-primary"
                : "text-on-surface-variant hover:text-on-surface hover:bg-surface-container/50",
            )}
          >
            <div className="flex items-center gap-3">
              <item.icon className="w-4 h-4" />
              <span>{item.displayLabel}</span>
            </div>

            {item.label === "TrainingLogs" && (
              <div className="flex items-center gap-1.5">
                <div
                  className={cn(
                    "w-1.5 h-1.5 rounded-full",
                    streamStatus === "done"
                      ? "bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.7)]"
                      : "bg-primary ai-pulse",
                  )}
                />
                <span className="text-[8px] opacity-40">{progress}%</span>
              </div>
            )}
          </button>
        ))}
      </nav>
      <div className="px-2 mb-4 space-y-4">
        <div className="bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-2xl p-4 shadow-xl">
          <div className="flex items-center justify-between mb-4">
            <h3 className="text-[9px] font-black text-on-surface-variant uppercase tracking-[0.2em] flex items-center gap-2">
              <Cpu className="w-3 h-3 text-primary" />
              Server Status
            </h3>
            <div className="flex items-center gap-2">
              {!isSystemConnected ? (
                <div className="w-1.5 h-1.5 rounded-full bg-red-500 ai-pulse" />
              ) : (
                <div className="w-1.5 h-1.5 rounded-full bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.6)]" />
              )}
              <span className="text-[8px] text-on-surface-variant font-bold">
                {isSystemConnected
                  ? "ONLINE"
                  : isAutoRetryExhausted
                    ? "OFFLINE"
                    : `RETRY ${systemRetryCount}`}
              </span>
              {isAutoRetryExhausted && (
                <button
                  onClick={() => {
                    retryCountRef.current = 0;
                    setSystemRetryCount(0);
                    setManualRetryTick((prev) => prev + 1);
                  }}
                  className="w-5 h-5 rounded-md border border-outline-variant/30 text-on-surface-variant hover:text-on-surface hover:border-outline-variant/60 transition-colors flex items-center justify-center"
                  title="재연결"
                  aria-label="재연결"
                >
                  <RotateCcw className="w-3 h-3" />
                </button>
              )}
            </div>
          </div>
          <div className="space-y-3">
            <ResourceItem
              icon={Cpu}
              label="CPU"
              detail={cpuNameText}
              usage={cpuUsage}
            />
            <ResourceItem
              icon={Database}
              label="LOAD"
              detail={cpuText}
              usage={cpuUsage}
            />
            <ResourceItem
              icon={ShieldCheck}
              label="GPU"
              detail={systemStatus?.gpuAvailable ? gpuNameText : gpuText}
              usage={gpuUsage}
            />
          </div>
        </div>
      </div>
      <div className="border-t border-outline-variant/10 pt-4 flex flex-col gap-1">
        {secondaryItems.map((item) => (
          <a
            key={item.label}
            href="#"
            className="flex items-center gap-3 px-3 py-2 text-on-surface-variant hover:text-on-surface transition-all text-xs font-semibold uppercase tracking-widest"
          >
            <item.icon className="w-4 h-4" />
            <span>{item.label}</span>
          </a>
        ))}
        <button
          onClick={onLogout}
          className="flex items-center gap-3 px-3 py-2 text-error/70 hover:text-error transition-all text-xs font-semibold uppercase tracking-widest mt-2"
        >
          <LogOut className="w-4 h-4" />
          <span>Disconnect</span>
        </button>
      </div>
    </aside>
  );
}
