import { 
  BarChart, 
  Bar, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  ResponsiveContainer,
  Legend,
  Radar,
  RadarChart,
  PolarGrid,
  PolarAngleAxis,
  PolarRadiusAxis,
} from 'recharts';
import { 
  History as HistoryIcon,
  Database,
  Cpu,
  ArrowRight,
  TrendingUp,
  FileText,
  Layers,
  ChevronRight,
  Calendar
} from 'lucide-react';
import { motion } from 'motion/react';
import { useState } from 'react';
import { cn } from '@/src/lib/utils';

const modelVersions = [
  { 
    version: 'v2.1.0-LTYK', 
    date: '2026-04-21', 
    accuracy: 99.2, 
    loss: 0.021, 
    map: 0.985, 
    dataset: 'Alyak_Full_v4',
    params: { lr: 0.0001, batch: 64, epochs: 100 },
    status: 'Stable'
  },
  { 
    version: 'v2.0.5-PRE', 
    date: '2026-04-15', 
    accuracy: 98.5, 
    loss: 0.034, 
    map: 0.972, 
    dataset: 'Alyak_Base_v3',
    params: { lr: 0.0005, batch: 32, epochs: 150 },
    status: 'Archived'
  },
  { 
    version: 'v1.8.2-TEST', 
    date: '2026-03-28', 
    accuracy: 97.1, 
    loss: 0.052, 
    map: 0.945, 
    dataset: 'Alyak_Alpha_v2',
    params: { lr: 0.001, batch: 32, epochs: 80 },
    status: 'Deprecated'
  },
  { 
    version: 'v1.0.0-INIT', 
    date: '2026-02-10', 
    accuracy: 92.4, 
    loss: 0.120, 
    map: 0.880, 
    dataset: 'Alyak_Raw_v1',
    params: { lr: 0.001, batch: 16, epochs: 50 },
    status: 'Archived'
  },
];

const compareData = [
  { metric: 'Accuracy', new: 99.2, old: 98.5 },
  { metric: 'mAP', new: 98.5, old: 97.2 },
  { metric: 'Recall', new: 97.8, old: 96.5 },
  { metric: 'Precision', new: 98.9, old: 97.8 },
];

export default function Archives() {
  const [selectedModel, setSelectedModel] = useState(modelVersions[0]);

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
            <span className="text-[10px] font-mono text-primary bg-primary/10 px-2 py-0.5 rounded">Total: {modelVersions.length}</span>
          </div>
          
          <div className="space-y-3">
            {modelVersions.map((model) => (
              <motion.button
                key={model.version}
                whileHover={{ x: 4 }}
                onClick={() => setSelectedModel(model)}
                className={cn(
                  "w-full text-left p-4 rounded-2xl border transition-all relative overflow-hidden group",
                  selectedModel.version === model.version 
                    ? "bg-surface-container-high border-primary/40 shadow-xl shadow-primary/5" 
                    : "bg-surface-container-low/40 border-outline-variant/10 hover:border-outline-variant/30"
                )}
              >
                {selectedModel.version === model.version && (
                  <div className="absolute left-0 top-0 bottom-0 w-1 bg-primary" />
                )}
                <div className="flex justify-between items-start mb-2 text-xs">
                  <span className={cn(
                    "font-bold px-2 py-0.5 rounded-[4px] tracking-widest uppercase text-[9px]",
                    model.status === 'Stable' ? "bg-green-500/20 text-green-400" :
                    model.status === 'Archived' ? "bg-primary/20 text-primary" : "bg-on-surface-variant/20 text-on-surface-variant"
                  )}>
                    {model.status}
                  </span>
                  <span className="text-on-surface-variant font-mono opacity-60">{model.date}</span>
                </div>
                <h4 className="text-sm font-black text-on-surface mb-1">{model.version}</h4>
                <div className="flex gap-4 text-[10px] font-mono text-on-surface-variant">
                  <span>Acc: <span className="text-on-surface">{model.accuracy}%</span></span>
                  <span>mAP: <span className="text-on-surface">{model.map}</span></span>
                </div>
              </motion.button>
            ))}
          </div>
        </div>

        {/* Right: Detailed Analysis */}
        <div className="xl:col-span-8 space-y-8">
          {/* Performance Comparison Dashboard */}
          <section className="bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-3xl p-8 shadow-2xl relative overflow-hidden">
            <h3 className="text-xs font-black text-on-surface mb-8 uppercase tracking-widest flex items-center gap-2">
              <TrendingUp className="w-4 h-4 text-primary" />
              Performance Comparison: {selectedModel.version} vs v2.0.5-PRE
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
                    <Bar name="v2.0.5-PRE (Old)" dataKey="old" fill="#ffafd3" fillOpacity={0.4} radius={[6, 6, 0, 0]} />
                  </BarChart>
                </ResponsiveContainer>
              </div>

              <div className="space-y-6 flex flex-col justify-center">
                <div className="grid grid-cols-2 gap-4">
                  <ComparisonStat label="Accuracy Delta" value={(selectedModel.accuracy - 98.5).toFixed(1)} unit="%" />
                  <ComparisonStat label="mAP Improvement" value={(selectedModel.map - 0.972).toFixed(3)} positive />
                </div>
                <div className="p-4 bg-surface-container-high/50 rounded-2xl border border-outline-variant/10">
                  <p className="text-[10px] text-on-surface-variant uppercase font-bold tracking-widest mb-2 flex items-center gap-2">
                    <Info className="w-3 h-3" />
                    Insight
                  </p>
                  <p className="text-xs text-on-surface leading-relaxed opacity-80 font-medium">
                    {selectedModel.accuracy > 98.5 
                      ? "현재 선택된 모델이 이전 버전 대비 전반적인 탐지 정확도 및 객체 검출 정밀도(mAP)에서 유의미한 성능 향상을 보이고 있습니다."
                      : "이전 버전 대비 정확도는 하락했으나, 특정 환경(예: 저조도)에서의 데이터셋 노이즈에 대한 견고성이 강화된 것으로 판단됩니다."}
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
                    <p className="text-xs font-mono text-on-surface font-bold">{selectedModel.dataset}</p>
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
                <ParamRow label="Learning Rate" value={selectedModel.params.lr} mono />
                <ParamRow label="Batch Size" value={selectedModel.params.batch} />
                <ParamRow label="Epochs" value={selectedModel.params.epochs} />
                <ParamRow label="Optimizer" value="AdamW" />
              </div>
            </section>
          </div>
          
          <button className="w-full py-4 bg-surface-container-high border border-primary/20 text-primary font-black text-xs uppercase tracking-[0.2em] rounded-2xl flex items-center justify-center gap-3 hover:bg-primary/10 transition-all">
            <Layers className="w-4 h-4" />
            이 모델 가중치로 롤백 (Rollback)
            <ArrowRight className="w-4 h-4" />
          </button>
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
