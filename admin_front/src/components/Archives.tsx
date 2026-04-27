import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";
import {
  History as HistoryIcon,
  Database,
  Cpu,
  ArrowRight,
  TrendingUp,
  FileText,
  Layers,
  ChevronRight,
  Calendar,
} from "lucide-react";
import { motion } from "motion/react";
import { useEffect, useMemo, useState } from "react";
import { cn } from "@/src/lib/utils";

type ArchiveStatus = "STABLE" | "ARCHIVED" | "DEPRECATED";
type ArchiveModel = {
  id: number;
  version: string;
  date: string;
  status: ArchiveStatus;
  accuracy: number | null;
  loss: number | null;
  map: number | null;
  dataset: string | null;
  params: { lr: number | null; batch: number | null; epochs: number | null; optimizer?: string | null };
};

type ArchiveListResponse = {
  content: ArchiveModel[];
};

type CompareResponse = {
  baseVersion: string;
  targetVersion: string;
  metrics: Array<{ metric: string; base: number | null; target: number | null }>;
};

export default function Archives() {
  const [models, setModels] = useState<ArchiveModel[]>([]);
  const [selectedModelId, setSelectedModelId] = useState<number | null>(null);
  const [compareData, setCompareData] = useState<Array<{ metric: string; new: number; old: number }>>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
  const token = localStorage.getItem("admin_access_token");

  const selectedModel = useMemo(
    () => models.find((model) => model.id === selectedModelId) ?? null,
    [models, selectedModelId],
  );

  useEffect(() => {
    const fetchArchives = async () => {
      if (!token) return;
      setIsLoading(true);
      setError(null);
      try {
        const response = await fetch(`${apiBaseUrl}/api/admin/archives/models?page=0&pageSize=50`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (!response.ok) throw new Error("아카이브 목록을 불러오지 못했습니다.");
        const page = (await response.json()) as ArchiveListResponse;
        const content = page.content ?? [];
        setModels(content);
        setSelectedModelId(content[0]?.id ?? null);
      } catch (e) {
        setError(e instanceof Error ? e.message : "아카이브 조회 중 오류가 발생했습니다.");
      } finally {
        setIsLoading(false);
      }
    };
    void fetchArchives();
  }, [apiBaseUrl, token]);

  useEffect(() => {
    const fetchCompare = async () => {
      if (!token || !selectedModel || models.length < 2) {
        setCompareData([]);
        return;
      }
      const target = models.find((m) => m.id !== selectedModel.id);
      if (!target) {
        setCompareData([]);
        return;
      }

      try {
        const response = await fetch(
          `${apiBaseUrl}/api/admin/archives/models/compare?baseId=${target.id}&targetId=${selectedModel.id}`,
          { headers: { Authorization: `Bearer ${token}` } },
        );
        if (!response.ok) throw new Error("비교 데이터 조회 실패");
        const data = (await response.json()) as CompareResponse;
        const mapped = data.metrics.map((metric) => ({
          metric: metric.metric,
          old: metric.base ?? 0,
          new: metric.target ?? 0,
        }));
        setCompareData(mapped);
      } catch {
        setCompareData([]);
      }
    };
    void fetchCompare();
  }, [apiBaseUrl, models, selectedModel, token]);

  const statusLabel = (status: ArchiveStatus) => {
    if (status === "STABLE") return "Stable";
    if (status === "DEPRECATED") return "Deprecated";
    return "Archived";
  };

  const formatDate = (date?: string | null) => {
    if (!date) return "-";
    return date.slice(0, 10);
  };

  const metricPercent = (value: number | null) => (value == null ? 0 : value * 100);

  return (
    <div className="ml-64 mt-36 p-8 min-h-screen bg-background">
      <header className="mb-12">
        <div className="flex items-center gap-3 mb-2">
          <HistoryIcon className="w-6 h-6 text-primary" />
          <h1 className="text-3xl font-black tracking-tight text-on-surface uppercase">Archives</h1>
        </div>
        <p className="text-on-surface-variant text-sm font-medium">기록 보관 및 모델 버전별 성능 비교 시스템</p>
      </header>

      <div className="grid grid-cols-1 xl:grid-cols-12 gap-8">
        {/* Left: Model Version List */}
        <div className="xl:col-span-4 space-y-4">
          <div className="flex items-center justify-between px-2 mb-4">
            <h3 className="text-xs font-black text-on-surface-variant uppercase tracking-widest">Model Versions</h3>
            <span className="text-[10px] font-mono text-primary bg-primary/10 px-2 py-0.5 rounded">Total: {models.length}</span>
          </div>

          <div className="space-y-3">
            {models.map((model) => (
              <motion.button
                key={model.id}
                whileHover={{ x: 4 }}
                onClick={() => setSelectedModelId(model.id)}
                className={cn(
                  "w-full text-left p-4 rounded-2xl border transition-all relative overflow-hidden group",
                  selectedModel?.id === model.id
                    ? "bg-surface-container-high border-primary/40 shadow-xl shadow-primary/5"
                    : "bg-surface-container-low/40 border-outline-variant/10 hover:border-outline-variant/30",
                )}
              >
                {selectedModel?.id === model.id && (
                  <div className="absolute left-0 top-0 bottom-0 w-1 bg-primary" />
                )}
                <div className="flex justify-between items-start mb-2 text-xs">
                  <span className={cn(
                    "font-bold px-2 py-0.5 rounded-[4px] tracking-widest uppercase text-[9px]",
                    model.status === "STABLE" ? "bg-green-500/20 text-green-400" :
                    model.status === "ARCHIVED" ? "bg-primary/20 text-primary" : "bg-on-surface-variant/20 text-on-surface-variant",
                  )}>
                    {statusLabel(model.status)}
                  </span>
                  <span className="text-on-surface-variant font-mono opacity-60">{formatDate(model.date)}</span>
                </div>
                <h4 className="text-sm font-black text-on-surface mb-1">{model.version}</h4>
                <div className="flex gap-4 text-[10px] font-mono text-on-surface-variant">
                  <span>Acc: <span className="text-on-surface">{metricPercent(model.accuracy).toFixed(1)}%</span></span>
                  <span>mAP: <span className="text-on-surface">{(model.map ?? 0).toFixed(3)}</span></span>
                </div>
              </motion.button>
            ))}
            {!isLoading && models.length === 0 && (
              <p className="text-xs text-on-surface-variant px-2">표시할 아카이브가 없습니다.</p>
            )}
            {error && <p className="text-xs text-error px-2">{error}</p>}
          </div>
        </div>

        {/* Right: Detailed Analysis */}
        <div className="xl:col-span-8 space-y-8">
          {!selectedModel ? (
            <section className="bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-3xl p-8 shadow-2xl">
              <p className="text-sm text-on-surface-variant">선택된 모델이 없습니다.</p>
            </section>
          ) : (
            <>
          {/* Performance Comparison Dashboard */}
          <section className="bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-3xl p-8 shadow-2xl relative overflow-hidden">
            <h3 className="text-xs font-black text-on-surface mb-8 uppercase tracking-widest flex items-center gap-2">
              <TrendingUp className="w-4 h-4 text-primary" />
              Performance Comparison: {selectedModel.version}
            </h3>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div className="h-64">
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={compareData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                    <XAxis dataKey="metric" axisLine={false} tickLine={false} tick={{ fontSize: 10, fill: '#909097' }} />
                    <YAxis axisLine={false} tickLine={false} hide />
                    <Tooltip 
                      contentStyle={{ backgroundColor: '#191f31', border: '1px solid #45464d', borderRadius: '12px' }}
                      itemStyle={{ fontSize: '10px' }}
                    />
                    <Legend iconType="circle" wrapperStyle={{ fontSize: '10px', paddingTop: '20px' }} />
                    <Bar name={selectedModel.version} dataKey="new" fill="#7bd0ff" radius={[6, 6, 0, 0]} />
                    <Bar name="Base" dataKey="old" fill="#ffafd3" fillOpacity={0.4} radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="space-y-6 flex flex-col justify-center">
                <div className="grid grid-cols-2 gap-4">
                  <ComparisonStat
                    label="Accuracy"
                    value={metricPercent(selectedModel.accuracy).toFixed(1)}
                    unit="%"
                  />
                  <ComparisonStat
                    label="mAP"
                    value={(selectedModel.map ?? 0).toFixed(3)}
                  />
                </div>
                <div className="p-4 bg-surface-container-high/50 rounded-2xl border border-outline-variant/10">
                  <p className="text-[10px] text-on-surface-variant uppercase font-bold tracking-widest mb-2 flex items-center gap-2">
                    <Info className="w-3 h-3" />
                    Insight
                  </p>
                  <p className="text-xs text-on-surface leading-relaxed opacity-80 font-medium">
                    모델 버전별 아카이브 정보를 기반으로 성능과 하이퍼파라미터를 비교합니다.
                  </p>
                </div>
              </div>
            </div>
          </section>

          {/* Dataset & Parameter History */}
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <section className="bg-surface-container-low p-6 rounded-2xl border border-outline-variant/10">
              <h4 className="text-xs font-black text-on-surface uppercase tracking-widest mb-6 flex items-center gap-2">
                <Database className="w-4 h-4 text-primary" />
                Dataset History
              </h4>
              <div className="space-y-4">
                <div className="flex items-center gap-4">
                  <div className="w-10 h-10 rounded-xl bg-surface-container-high flex items-center justify-center">
                    <FileText className="w-5 h-5 text-on-surface-variant" />
                  </div>
                  <div>
                    <p className="text-[10px] text-on-surface-variant font-bold uppercase tracking-widest mb-0.5">Source Identifier</p>
                    <p className="text-xs font-mono text-on-surface font-bold">{selectedModel.dataset ?? "-"}</p>
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4 pt-4 border-t border-outline-variant/10">
                  <div>
                    <p className="text-[10px] text-on-surface-variant font-bold uppercase tracking-widest mb-1">Image Count</p>
                    <p className="text-sm font-black text-on-surface uppercase tracking-tighter">12,450장</p>
                  </div>
                  <div>
                    <p className="text-[10px] text-on-surface-variant font-bold uppercase tracking-widest mb-1">Augmented</p>
                    <p className="text-sm font-black text-on-surface uppercase tracking-tighter">Yes (3.5x)</p>
                  </div>
                </div>
              </div>
            </section>

            <section className="bg-surface-container-low p-6 rounded-2xl border border-outline-variant/10">
              <h4 className="text-xs font-black text-on-surface uppercase tracking-widest mb-6 flex items-center gap-2">
                <Cpu className="w-4 h-4 text-tertiary" />
                Hyperparameters
              </h4>
              <div className="space-y-3">
                <ParamRow label="Learning Rate" value={selectedModel.params.lr ?? "-"} mono />
                <ParamRow label="Batch Size" value={selectedModel.params.batch ?? "-"} />
                <ParamRow label="Epochs" value={selectedModel.params.epochs ?? "-"} />
                <ParamRow label="Optimizer" value={selectedModel.params.optimizer ?? "-"} />
              </div>
            </section>
          </div>
          
          <button className="w-full py-4 bg-surface-container-high border border-primary/20 text-primary font-black text-xs uppercase tracking-[0.2em] rounded-2xl flex items-center justify-center gap-3 hover:bg-primary/10 transition-all">
            <Layers className="w-4 h-4" />
            이 모델 가중치로 롤백 (Rollback)
            <ArrowRight className="w-4 h-4" />
          </button>
          </>
          )}
        </div>
      </div>
    </div>
  );
}

function ComparisonStat({ label, value, unit, positive }: { label: string, value: string, unit?: string, positive?: boolean }) {
  const isPositive = parseFloat(value) >= 0;
  return (
    <div className="bg-surface-container-high/30 p-4 rounded-xl border border-outline-variant/10">
      <p className="text-[9px] text-on-surface-variant uppercase font-bold tracking-widest mb-1">{label}</p>
      <p className={cn(
        "text-lg font-black tabular-nums",
        isPositive ? "text-green-400" : "text-error"
      )}>
        {isPositive ? '+' : ''}{value}{unit}
      </p>
    </div>
  );
}

function ParamRow({ label, value, mono }: { label: string, value: string | number, mono?: boolean }) {
  return (
    <div className="flex justify-between items-center py-2 border-b border-outline-variant/5 last:border-0">
      <span className="text-[10px] text-on-surface-variant font-medium uppercase tracking-wider">{label}</span>
      <span className={cn("text-xs font-bold text-on-surface", mono && "font-mono")}>{value}</span>
    </div>
  );
}

function Info({ className }: { className?: string }) {
  return (
    <svg 
      className={className}
      xmlns="http://www.w3.org/2000/svg" 
      width="24" 
      height="24" 
      viewBox="0 0 24 24" 
      fill="none" 
      stroke="currentColor" 
      strokeWidth="2" 
      strokeLinecap="round" 
      strokeLinejoin="round"
    >
      <circle cx="12" cy="12" r="10" />
      <path d="M12 16v-4" />
      <path d="M12 8h.01" />
    </svg>
  );
}
