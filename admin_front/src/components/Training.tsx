import { 
  Search, 
  Settings2, 
  Play, 
  Clock, 
  Image as ImageIcon,
  ChevronDown,
  Sliders,
  Type
} from 'lucide-react';
import { motion } from 'motion/react';
import { useEffect, useMemo, useState } from 'react';
import { cn } from '@/src/lib/utils';

type TrainingItem = {
  id: number;
  imagePath: string;
  status: 'INBOX' | 'TRAINING_SET' | 'TRASH';
  boxCount: number;
};

type LabelingPageResponse = {
  content: TrainingItem[];
  totalElements: number;
};

export default function Training() {
  const [learningRate, setLearningRate] = useState(0.001);
  const [optimizer, setOptimizer] = useState('Adam');
  const [items, setItems] = useState<TrainingItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [searchText, setSearchText] = useState('');

  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';
  const token = localStorage.getItem('admin_access_token');

  const resolveImageUrl = (imagePath: string) => {
    if (!imagePath) return '';
    if (imagePath.startsWith('http://') || imagePath.startsWith('https://')) {
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
        if (!response.ok) throw new Error('Training Set 목록 조회 실패');
        const pageData = (await response.json()) as LabelingPageResponse;
        setItems(pageData.content ?? []);
      } catch (error) {
        console.error('[Training] fetch failed', error);
        setItems([]);
      } finally {
        setIsLoading(false);
      }
    };

    void fetchTrainingItems();
  }, [apiBaseUrl, token]);

  const filteredItems = useMemo(() => {
    const keyword = searchText.trim().toLowerCase();
    if (!keyword) return items;
    return items.filter((item) =>
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
            <span className="text-xs font-mono text-on-surface-variant">Total Images: <span className="text-on-surface font-bold">{items.length}</span></span>
          </div>
          <div className="flex items-center gap-2">
            <Clock className="w-4 h-4 text-tertiary" />
            <span className="text-xs font-mono text-on-surface-variant">Est. Time: <span className="text-on-surface font-bold">2h 15m</span></span>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <div className="w-2 h-2 rounded-full bg-green-500 ai-pulse"></div>
          <span className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">System Ready</span>
        </div>
      </div>

      <div className="flex flex-col gap-8">
        <header className="flex justify-between items-center">
          <h1 className="text-3xl font-black tracking-tight text-on-surface">Data Grid</h1>
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
                  <span className="text-[8px] font-bold text-white uppercase tracking-widest bg-primary/40 px-1.5 py-0.5 rounded">Metadata Synced</span>
                </div>
              </div>
              <div className="p-3">
                <p className="text-[10px] font-bold text-on-surface truncate">{item.imagePath.split('/').pop() ?? `item-${item.id}`}</p>
                <p className="text-[9px] text-on-surface-variant font-mono mt-1 opacity-60">ID {item.id} · BOX {item.boxCount}</p>
              </div>
            </motion.div>
          ))}
        </div>
        {isLoading && (
          <p className="text-xs text-on-surface-variant">Training Set 불러오는 중...</p>
        )}
      </div>

      {/* Right Configuration Panel */}
      <aside className="fixed right-0 top-16 bottom-0 w-[380px] bg-background/80 backdrop-blur-2xl border-l border-outline-variant/10 z-30 flex flex-col p-6 shadow-2xl">
        <div className="flex items-center gap-3 mb-8">
          <Settings2 className="w-5 h-5 text-primary" />
          <h2 className="text-sm font-bold uppercase tracking-widest text-on-surface">Hyperparameter Configuration</h2>
        </div>

        <div className="space-y-8 flex-1 overflow-y-auto pr-2 custom-scrollbar">
          {/* Base Model Select */}
          <div className="space-y-3">
            <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest px-1">Base Model Select</label>
            <div className="relative">
              <select className="w-full bg-surface-container-high border border-outline-variant/20 rounded-xl p-4 text-xs text-on-surface focus:outline-none focus:border-primary transition-colors appearance-none cursor-pointer">
                <option>ViT-Base (Vision Transformer)</option>
                <option>ResNet-50</option>
                <option>EfficientNet-B4</option>
                <option>Swin Transformer</option>
              </select>
              <ChevronDown className="absolute right-4 top-1/2 -translate-y-1/2 w-4 h-4 text-outline-variant pointer-events-none" />
            </div>
          </div>

          {/* Learning Rate */}
          <div className="space-y-4">
            <div className="flex justify-between items-center px-1">
              <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest">Learning Rate</label>
              <span className="text-xs font-mono text-primary font-bold">{learningRate.toFixed(4)}</span>
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
            <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest px-1">Batch Size</label>
            <div className="relative">
              <select className="w-full bg-surface-container-high border border-outline-variant/20 rounded-xl p-4 text-xs text-on-surface focus:outline-none focus:border-primary transition-colors appearance-none cursor-pointer">
                <option>64</option>
                <option>16</option>
                <option>32</option>
                <option>128</option>
              </select>
              <ChevronDown className="absolute right-4 top-1/2 -translate-y-1/2 w-4 h-4 text-outline-variant pointer-events-none" />
            </div>
          </div>

          {/* Epochs */}
          <div className="space-y-3">
            <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest px-1">Epochs</label>
            <input 
              type="number" 
              defaultValue={100}
              className="w-full bg-surface-container-high border border-outline-variant/20 rounded-xl p-4 text-xs font-mono text-on-surface focus:outline-none focus:border-primary transition-colors"
            />
          </div>

          {/* Optimizer */}
          <div className="space-y-4">
            <label className="text-[10px] font-bold text-on-surface-variant uppercase tracking-widest px-1">Optimizer</label>
            <div className="grid grid-cols-1 gap-2">
              {['Adam', 'SGD', 'RMSprop'].map((opt) => (
                <label 
                  key={opt}
                  className={cn(
                    "flex items-center justify-between p-4 rounded-xl border border-outline-variant/20 cursor-pointer transition-all",
                    optimizer === opt ? "bg-primary/10 border-primary/50 text-primary" : "bg-surface-container-high hover:bg-surface-container-highest"
                  )}
                >
                  <span className="text-xs font-bold">{opt}</span>
                  <input 
                    type="radio" 
                    name="optimizer" 
                    value={opt}
                    checked={optimizer === opt}
                    onChange={() => setOptimizer(opt)}
                    className="w-4 h-4 border-outline-variant text-primary focus:ring-primary"
                  />
                </label>
              ))}
            </div>
          </div>

          <div className="pt-6 border-t border-outline-variant/10">
            <button className="w-full py-4 bg-gradient-to-r from-primary to-primary/80 text-on-primary font-black text-xs uppercase tracking-[0.2em] rounded-xl flex items-center justify-center gap-3 shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all group">
              <Play className="w-5 h-5 fill-current group-hover:scale-110 transition-transform" />
              INITIATE TRAINING
            </button>
            <p className="text-[9px] text-center text-on-surface-variant mt-4 font-semibold uppercase tracking-widest opacity-60 italic">Ensure GPU cluster is in idle state before starting.</p>
          </div>
        </div>
      </aside>
    </div>
  );
}
