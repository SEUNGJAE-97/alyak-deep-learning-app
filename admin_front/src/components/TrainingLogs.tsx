import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  AreaChart,
  Area,
} from "recharts";
import {
  Terminal,
  AlertCircle,
  Activity,
  History as HistoryIcon,
  Info,
} from "lucide-react";
import { motion, AnimatePresence } from "motion/react";
import { useEffect, useRef } from "react";
import { cn } from "@/src/lib/utils";
import { useTrainingStream } from "./TrainingStreamContext";

const mockLossData = [
  { step: 0, loss: 0.5 },
  { step: 50, loss: 0.35 },
  { step: 100, loss: 0.22 },
  { step: 150, loss: 0.15 },
  { step: 200, loss: 0.09 },
  { step: 250, loss: 0.06 },
  { step: 300, loss: 0.04 },
  { step: 350, loss: 0.03 },
  { step: 400, loss: 0.025 },
  { step: 450, loss: 0.021 },
];

const mockAccuracyData = [
  { step: 0, acc: 0.65 },
  { step: 50, acc: 0.72 },
  { step: 100, acc: 0.81 },
  { step: 150, acc: 0.88 },
  { step: 200, acc: 0.92 },
  { step: 250, acc: 0.95 },
  { step: 300, acc: 0.97 },
  { step: 350, acc: 0.98 },
  { step: 400, acc: 0.988 },
  { step: 450, acc: 0.992 },
];

export default function TrainingLogs() {
  const { logs, streamStatus, progress } = useTrainingStream();

  const consoleRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (consoleRef.current) {
      consoleRef.current.scrollTop = consoleRef.current.scrollHeight;
    }
  }, [logs]);

  const streamMessage =
    streamStatus === "idle"
      ? "대기 중"
      : streamStatus === "connecting"
        ? "스트림 연결 중..."
        : streamStatus === "running"
          ? "연결됨"
          : streamStatus === "done"
            ? "학습 종료"
            : "스트림 연결 종료";

  // Status can be 'normal' | 'warning' | 'error'
  const status: "normal" | "warning" | "error" = "normal";

  return (
    <div className="ml-64 mt-36 p-8 min-h-screen bg-background">
      {/* Top Progress Section */}
      <section className="mb-12 bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-2xl p-8 shadow-2xl">
        <div className="flex justify-between items-end mb-6">
          <div className="space-y-1">
            <h2 className="text-[10px] font-black text-primary uppercase tracking-[0.3em]">
              Training Progress
            </h2>
            <h3 className="text-2xl font-bold text-on-surface">
              {streamStatus === "running" || streamStatus === "done"
                ? `Training (${progress}%)`
                : "Training"}
            </h3>
          </div>
          <div className="text-right">
            <p className="text-[10px] text-on-surface-variant font-bold uppercase tracking-widest mb-1">
              Remaining Time
            </p>
            <p className="text-xl font-mono text-on-surface font-black">
              01:12:45
            </p>
          </div>
        </div>

        <div className="relative h-4 bg-surface-container-highest rounded-full overflow-hidden border border-outline-variant/10">
          <motion.div
            initial={{ width: 0 }}
            animate={{ width: `${progress}%` }}
            transition={{ duration: 1.5, ease: "easeOut" }}
            className={cn(
              "h-full transition-colors duration-500",
              status === "normal"
                ? "bg-primary shadow-[0_0_15px_rgba(123,208,255,0.5)]"
                : status === "warning"
                  ? "bg-yellow-500 shadow-[0_0_15px_rgba(234,179,8,0.5)]"
                  : "bg-error shadow-[0_0_15px_rgba(186,26,26,0.5)]",
            )}
          />
          {/* Scanning Effect */}
          <motion.div
            animate={{ x: ["-100%", "200%"] }}
            transition={{ duration: 3, repeat: Infinity, ease: "linear" }}
            className="absolute top-0 bottom-0 w-20 bg-gradient-to-r from-transparent via-white/20 to-transparent skew-x-12"
          />
        </div>

        <div className="mt-4 flex justify-between">
          <div className="flex items-center gap-2">
            <Activity className="w-4 h-4 text-primary" />
            <span className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">
              Active Step: <span className="text-on-surface">45,210</span>
            </span>
          </div>
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <div className="w-1.5 h-1.5 rounded-full bg-primary ai-pulse" />
              <span className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">
                Learning Rate: 0.0001
              </span>
            </div>
          </div>
        </div>
      </section>
      {/* Main Charts Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8 mb-12">
        {/* Loss Curve */}
        <section className="bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-2xl p-6 shadow-xl relative overflow-hidden group">
          <div className="absolute top-0 left-0 w-1 h-full bg-error/50" />
          <h4 className="text-xs font-black text-on-surface mb-6 uppercase tracking-widest flex items-center gap-2">
            <AlertCircle className="w-4 h-4 text-error" />
            Loss Curve
          </h4>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={mockLossData}>
                <defs>
                  <linearGradient id="colorLoss" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#ba1a1a" stopOpacity={0.2} />
                    <stop offset="95%" stopColor="#ba1a1a" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid
                  strokeDasharray="3 3"
                  stroke="#2a2b33"
                  vertical={false}
                />
                <XAxis dataKey="step" hide />
                <YAxis hide domain={[0, 0.6]} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "#191f31",
                    border: "1px solid #45464d",
                    borderRadius: "8px",
                  }}
                  itemStyle={{ fontSize: "10px", color: "#ff897d" }}
                />
                <Area
                  type="monotone"
                  dataKey="loss"
                  stroke="#ff897d"
                  strokeWidth={3}
                  fillOpacity={1}
                  fill="url(#colorLoss)"
                  animationDuration={2000}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </section>

        {/* Accuracy Curve */}
        <section className="bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-2xl p-6 shadow-xl relative overflow-hidden group">
          <div className="absolute top-0 left-0 w-1 h-full bg-primary/50" />
          <h4 className="text-xs font-black text-on-surface mb-6 uppercase tracking-widest flex items-center gap-2">
            <Activity className="w-4 h-4 text-primary" />
            Accuracy Curve
          </h4>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={mockAccuracyData}>
                <defs>
                  <linearGradient id="colorAcc" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#7bd0ff" stopOpacity={0.2} />
                    <stop offset="95%" stopColor="#7bd0ff" stopOpacity={0} />
                  </linearGradient>
                </defs>
                <CartesianGrid
                  strokeDasharray="3 3"
                  stroke="#2a2b33"
                  vertical={false}
                />
                <XAxis dataKey="step" hide />
                <YAxis hide domain={[0.6, 1.0]} />
                <Tooltip
                  contentStyle={{
                    backgroundColor: "#191f31",
                    border: "1px solid #45464d",
                    borderRadius: "8px",
                  }}
                  itemStyle={{ fontSize: "10px", color: "#7bd0ff" }}
                />
                <Area
                  type="monotone"
                  dataKey="acc"
                  stroke="#7bd0ff"
                  strokeWidth={3}
                  fillOpacity={1}
                  fill="url(#colorAcc)"
                  animationDuration={2000}
                />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </section>
      </div>
    </div>
  );
}
