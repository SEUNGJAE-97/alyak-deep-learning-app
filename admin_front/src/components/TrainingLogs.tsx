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
import { AlertCircle, Activity } from "lucide-react";
import { motion } from "motion/react";
import { useMemo, useState } from "react";
import { cn } from "@/src/lib/utils";
import { useTrainingStream } from "./TrainingStreamContext";

type MetricKey = "train_loss" | "val_loss" | "val_acc" | "mAP" | "lr";
type ChartPoint = { epoch: number } & Partial<Record<MetricKey, number>>;
type ParsedEpochLine = {
  epoch: number;
  total: number;
  point: ChartPoint;
};

const METRICS: {
  key: MetricKey;
  label: string;
  color: string;
  format: (v: number) => string;
}[] = [
  {
    key: "train_loss",
    label: "Train Loss",
    color: "#ff897d",
    format: (v) => v.toFixed(4),
  },
  {
    key: "val_loss",
    label: "Val Loss",
    color: "#ffb951",
    format: (v) => v.toFixed(4),
  },
  {
    key: "val_acc",
    label: "Val Acc",
    color: "#7bd0ff",
    format: (v) => (v * 100).toFixed(1) + "%",
  },
  {
    key: "mAP",
    label: "mAP",
    color: "#a8f0c6",
    format: (v) => (v * 100).toFixed(1) + "%",
  },
  {
    key: "lr",
    label: "LR",
    color: "#c8b0ff",
    format: (v) => v.toExponential(2),
  },
];

function parseLogLine(line: string): ParsedEpochLine | null {
  const epochMatch = line.match(/\[EPOCH\s+(\d+)\/(\d+)\]/);
  if (!epochMatch) return null;
  const epoch = parseInt(epochMatch[1], 10);
  const total = parseInt(epochMatch[2], 10);
  const point: ChartPoint = { epoch };
  const extract = (key: string) => {
    const m = line.match(new RegExp(`${key}=([\\d.eE+\\-]+)`));
    return m ? parseFloat(m[1]) : undefined;
  };
  const tl = extract("loss");
  const vl = extract("val_loss");
  const va = extract("val_acc");
  const mp = extract("mAP");
  const lr = extract("lr");
  if (tl !== undefined) point.train_loss = tl;
  if (vl !== undefined) point.val_loss = vl;
  if (va !== undefined) point.val_acc = va;
  if (mp !== undefined) point.mAP = mp;
  if (lr !== undefined) point.lr = lr;
  return { epoch, total, point };
}

function extractRemainingTime(logs: string[]): string | null {
  for (let i = logs.length - 1; i >= 0; i -= 1) {
    const line = logs[i];
    const match = line.match(/eta=(\d{2}:\d{2}:\d{2})/);
    if (match) return match[1];
  }
  return null;
}

function MetricTooltip({
  active,
  payload,
  label,
  activeMetrics,
}: {
  active?: boolean;
  payload?: { dataKey: string; value: number; color: string }[];
  label?: number;
  activeMetrics: Set<MetricKey>;
}) {
  if (!active || !payload?.length) return null;
  return (
    <div className="bg-surface-container-highest/95 border border-outline-variant/20 rounded-xl p-3 shadow-2xl backdrop-blur-sm">
      <p className="text-[10px] font-bold text-primary mb-2 uppercase tracking-widest">
        Epoch {label}
      </p>
      {payload
        .filter((p) => activeMetrics.has(p.dataKey as MetricKey))
        .map((p) => {
          const meta = METRICS.find((m) => m.key === p.dataKey);
          return (
            <div key={p.dataKey} className="flex items-center gap-2 py-0.5">
              <div
                className="w-2 h-2 rounded-full shrink-0"
                style={{ backgroundColor: p.color }}
              />
              <span className="text-[10px] text-on-surface-variant w-16">
                {meta?.label}
              </span>
              <span className="text-[10px] font-mono text-on-surface font-bold">
                {meta?.format(p.value) ?? p.value}
              </span>
            </div>
          );
        })}
    </div>
  );
}

export default function TrainingLogs() {
  const { logs, streamStatus, progress } = useTrainingStream();
  const [activeMetrics, setActiveMetrics] = useState<Set<MetricKey>>(
    new Set(METRICS.map((m) => m.key)),
  );
  const chartData = useMemo<ChartPoint[]>(() => {
    const byEpoch = new Map<number, ChartPoint>();
    for (const line of logs) {
      const parsed = parseLogLine(line);
      if (!parsed) continue;
      const prev = byEpoch.get(parsed.epoch) ?? { epoch: parsed.epoch };
      byEpoch.set(parsed.epoch, { ...prev, ...parsed.point });
    }
    return [...byEpoch.values()].sort((a, b) => a.epoch - b.epoch);
  }, [logs]);
  const effectiveChartData = chartData;
  const remainingTime = useMemo(() => {
    if (streamStatus === "done") return "00:00:00";
    return extractRemainingTime(logs) ?? "--:--:--";
  }, [logs, streamStatus]);
  const lossCurveData = useMemo(
    () =>
      effectiveChartData
        .filter((point) => typeof point.train_loss === "number")
        .map((point) => ({ epoch: point.epoch, loss: point.train_loss as number })),
    [effectiveChartData],
  );
  const accuracyCurveData = useMemo(
    () =>
      effectiveChartData
        .filter((point) => typeof point.val_acc === "number")
        .map((point) => ({ epoch: point.epoch, acc: point.val_acc as number })),
    [effectiveChartData],
  );
  const toggleMetric = (key: MetricKey) => {
    setActiveMetrics((prev) => {
      const next = new Set(prev);
      if (next.has(key)) {
        if (next.size === 1) return prev;
        next.delete(key);
      } else {
        next.add(key);
      }
      return next;
    });
  };

  // Status can be 'normal' | 'warning' | 'error'
  const status: "normal" | "warning" | "error" = "normal";

  return (
    <div className="training-logs ml-64 mt-36 p-8 min-h-screen bg-background">
      <style>{`
        .training-logs .recharts-surface:focus,
        .training-logs .recharts-surface:focus-visible,
        .training-logs .recharts-wrapper:focus,
        .training-logs .recharts-wrapper:focus-visible,
        .training-logs .recharts-responsive-container:focus,
        .training-logs .recharts-responsive-container:focus-visible,
        .training-logs .recharts-responsive-container *:focus,
        .training-logs .recharts-responsive-container *:focus-visible {
          outline: none !important;
          box-shadow: none !important;
        }
      `}</style>
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
              {remainingTime}
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
            {lossCurveData.length === 0 ? (
              <div className="h-full flex items-center justify-center">
                <p className="text-[11px] text-on-surface-variant/40 font-mono">
                  SSE loss 로그 대기 중...
                </p>
              </div>
            ) : (
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={lossCurveData}>
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
                <XAxis dataKey="epoch" hide />
                <YAxis hide />
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
                  connectNulls
                  stroke="#ff897d"
                  strokeWidth={3}
                  fillOpacity={1}
                  fill="url(#colorLoss)"
                  animationDuration={2000}
                />
              </AreaChart>
            </ResponsiveContainer>
            )}
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
            {accuracyCurveData.length === 0 ? (
              <div className="h-full flex items-center justify-center">
                <p className="text-[11px] text-on-surface-variant/40 font-mono">
                  SSE accuracy 로그 대기 중...
                </p>
              </div>
            ) : (
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={accuracyCurveData}>
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
                <XAxis dataKey="epoch" hide />
                <YAxis hide />
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
                  connectNulls
                  stroke="#7bd0ff"
                  strokeWidth={3}
                  fillOpacity={1}
                  fill="url(#colorAcc)"
                  animationDuration={2000}
                />
              </AreaChart>
            </ResponsiveContainer>
            )}
          </div>
        </section>
      </div>

      <section className="mt-8 bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-2xl p-6 shadow-xl relative overflow-hidden group">
        <div className="absolute top-0 left-0 w-1 h-full bg-primary/50" />
        <div className="flex items-center justify-between mb-6 flex-wrap gap-3">
          <h4 className="text-xs font-black text-on-surface uppercase tracking-widest flex items-center gap-2">
            <Activity className="w-4 h-4 text-primary" />
            Composite Metrics
          </h4>
          <div className="flex items-center gap-2 flex-wrap">
            {METRICS.map((m) => {
              const isActive = activeMetrics.has(m.key);
              return (
                <button
                  key={m.key}
                  onClick={() => toggleMetric(m.key)}
                  className={cn(
                    "flex items-center gap-1.5 px-3 py-1.5 rounded-lg text-[10px] font-bold uppercase tracking-wider transition-all border",
                    isActive
                      ? "border-transparent text-background"
                      : "border-outline-variant/20 text-on-surface-variant hover:border-outline-variant/50",
                  )}
                  style={isActive ? { backgroundColor: m.color } : {}}
                >
                  <div
                    className="w-1.5 h-1.5 rounded-full shrink-0"
                    style={{
                      backgroundColor: isActive ? "rgba(0,0,0,0.4)" : m.color,
                    }}
                  />
                  {m.label}
                </button>
              );
            })}
          </div>
        </div>
        <div className="h-72">
          {effectiveChartData.length === 0 ? (
            <div className="h-full flex items-center justify-center">
              <p className="text-[11px] text-on-surface-variant/40 font-mono">
                학습 시작 후 epoch 데이터가 표시됩니다...
              </p>
            </div>
          ) : (
            <ResponsiveContainer width="100%" height="100%">
              <LineChart
                data={effectiveChartData}
                margin={{ top: 4, right: 8, left: -16, bottom: 24 }}
              >
                <CartesianGrid
                  strokeDasharray="3 3"
                  stroke="#2a2b33"
                  vertical={false}
                />
                <XAxis
                  dataKey="epoch"
                  tick={{ fontSize: 10, fill: "#8a8d94" }}
                  axisLine={false}
                  tickLine={false}
                  label={{
                    value: "Epoch",
                    position: "bottom",
                    offset: -4,
                    fontSize: 9,
                    fill: "#8a8d94",
                  }}
                />
                <YAxis
                  tick={{ fontSize: 10, fill: "#8a8d94" }}
                  axisLine={false}
                  tickLine={false}
                  width={48}
                />
                <Tooltip
                  content={<MetricTooltip activeMetrics={activeMetrics} />}
                />
                {METRICS.map((m) =>
                  activeMetrics.has(m.key) ? (
                    <Line
                      key={m.key}
                      type="monotone"
                      dataKey={m.key}
                      connectNulls
                      stroke={m.color}
                      strokeWidth={2}
                      dot={false}
                      activeDot={{ r: 4, strokeWidth: 0 }}
                      isAnimationActive={false}
                    />
                  ) : null,
                )}
              </LineChart>
            </ResponsiveContainer>
          )}
        </div>
      </section>
    </div>
  );
}
