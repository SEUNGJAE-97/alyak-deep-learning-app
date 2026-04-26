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

function parseLogLine(line: string): ChartPoint | null {
  const epochMatch = line.match(/\[EPOCH\s+(\d+)\/\d+\]/);
  if (!epochMatch) return null;
  const epoch = parseInt(epochMatch[1], 10);
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
  return point;
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

const SHOW_DUMMY_COMPOSITE_METRICS = true;

const mockCompositeMetricsData: ChartPoint[] = [
  {
    epoch: 1,
    train_loss: 1.23,
    val_loss: 1.35,
    val_acc: 0.52,
    mAP: 0.41,
    lr: 0.001,
  },
  {
    epoch: 2,
    train_loss: 1.05,
    val_loss: 1.19,
    val_acc: 0.58,
    mAP: 0.47,
    lr: 0.001,
  },
  {
    epoch: 3,
    train_loss: 0.89,
    val_loss: 1.02,
    val_acc: 0.64,
    mAP: 0.53,
    lr: 0.0008,
  },
  {
    epoch: 4,
    train_loss: 0.76,
    val_loss: 0.91,
    val_acc: 0.7,
    mAP: 0.59,
    lr: 0.0008,
  },
  {
    epoch: 5,
    train_loss: 0.65,
    val_loss: 0.83,
    val_acc: 0.75,
    mAP: 0.66,
    lr: 0.0006,
  },
  {
    epoch: 6,
    train_loss: 0.56,
    val_loss: 0.77,
    val_acc: 0.79,
    mAP: 0.71,
    lr: 0.0006,
  },
  {
    epoch: 7,
    train_loss: 0.49,
    val_loss: 0.71,
    val_acc: 0.83,
    mAP: 0.76,
    lr: 0.0004,
  },
  {
    epoch: 8,
    train_loss: 0.43,
    val_loss: 0.67,
    val_acc: 0.86,
    mAP: 0.8,
    lr: 0.0004,
  },
  {
    epoch: 9,
    train_loss: 0.39,
    val_loss: 0.64,
    val_acc: 0.88,
    mAP: 0.84,
    lr: 0.0002,
  },
  {
    epoch: 10,
    train_loss: 0.35,
    val_loss: 0.61,
    val_acc: 0.9,
    mAP: 0.87,
    lr: 0.0002,
  },
];

export default function TrainingLogs() {
  const { logs, streamStatus, progress } = useTrainingStream();
  const [activeMetrics, setActiveMetrics] = useState<Set<MetricKey>>(
    new Set(METRICS.map((m) => m.key)),
  );
  const chartData = useMemo<ChartPoint[]>(() => {
    const points: ChartPoint[] = [];
    for (const line of logs) {
      const p = parseLogLine(line);
      if (p) points.push(p);
    }
    return points;
  }, [logs]);
  const effectiveChartData = useMemo(
    () =>
      SHOW_DUMMY_COMPOSITE_METRICS && chartData.length === 0
        ? mockCompositeMetricsData
        : chartData,
    [chartData],
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
