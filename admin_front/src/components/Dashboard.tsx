import { 
  X, 
  CheckCircle2,
  AlertCircle,
  Clock,
  History as HistoryIcon,
  MousePointer2,
  Square,
  Crosshair,
  Database,
  Trash2,
  Inbox as InboxIcon,
  ChevronRight,
  Info
} from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import { useState, useRef, useEffect, MouseEvent } from 'react';
import { cn } from '@/src/lib/utils';

interface PillData {
  id: string;
  filename: string;
  imageUrl: string;
  label: string;
  coords: { x: number; y: number; w: number; h: number };
  status: 'pending' | 'approved' | 'rejected';
}

const initialInbox: PillData[] = [
  { id: '1', filename: 'pill_det_421.jpg', imageUrl: 'https://picsum.photos/seed/p1/800/600', label: 'Tylenol ER', coords: { x: 120, y: 80, w: 200, h: 100 }, status: 'pending' },
  { id: '2', filename: 'pill_det_422.jpg', imageUrl: 'https://picsum.photos/seed/p2/800/600', label: 'Aspirin', coords: { x: 150, y: 120, w: 180, h: 90 }, status: 'pending' },
  { id: '3', filename: 'pill_det_423.jpg', imageUrl: 'https://picsum.photos/seed/p3/800/600', label: 'Ibuprofen', coords: { x: 90, y: 200, w: 220, h: 110 }, status: 'pending' },
  { id: '4', filename: 'pill_det_424.jpg', imageUrl: 'https://picsum.photos/seed/p4/800/600', label: 'Unknown', coords: { x: 50, y: 50, w: 300, h: 200 }, status: 'pending' },
  { id: '5', filename: 'pill_det_425.jpg', imageUrl: 'https://picsum.photos/seed/p5/800/600', label: 'Magnesium', coords: { x: 200, y: 100, w: 150, h: 100 }, status: 'pending' },
  { id: '6', filename: 'pill_det_426.jpg', imageUrl: 'https://picsum.photos/seed/p6/800/600', label: 'Vitamin C', coords: { x: 100, y: 300, w: 180, h: 150 }, status: 'pending' },
];

export default function Dashboard() {
  const [data, setData] = useState<PillData[]>(initialInbox);
  const [activeTab, setActiveTab] = useState<'Inbox' | 'Training Set' | 'Trash'>('Inbox');
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [selectedIds, setSelectedIds] = useState<string[]>([]);
  
  const currentItems = data.filter(item => {
    if (activeTab === 'Inbox') return item.status === 'pending';
    if (activeTab === 'Training Set') return item.status === 'approved';
    return item.status === 'rejected';
  });

  const selectedItem = currentItems[selectedIndex] || null;

  const handleApprove = () => {
    if (!selectedItem) return;
    setData(prev => prev.map(item => item.id === selectedItem.id ? { ...item, status: 'approved' } : item));
    // Auto-select next if available
    if (selectedIndex >= currentItems.length - 1) {
      setSelectedIndex(Math.max(0, currentItems.length - 2));
    }
  };

  const handleReject = () => {
    if (!selectedItem) return;
    setData(prev => prev.map(item => item.id === selectedItem.id ? { ...item, status: 'rejected' } : item));
    if (selectedIndex >= currentItems.length - 1) {
      setSelectedIndex(Math.max(0, currentItems.length - 2));
    }
  };

  const toggleSelectAll = () => {
    if (selectedIds.length === currentItems.length) {
      setSelectedIds([]);
    } else {
      setSelectedIds(currentItems.map(i => i.id));
    }
  };

  const toggleSelect = (id: string, e: MouseEvent) => {
    e.stopPropagation();
    setSelectedIds(prev => prev.includes(id) ? prev.filter(i => i !== id) : [...prev, id]);
  };

  return (
    <div className="ml-64 mt-[112px] p-8 pr-[420px] min-h-screen bg-background">
      {/* Top Header Section */}
      <header className="fixed top-28 left-64 right-[420px] z-40 bg-background/80 backdrop-blur-xl border-b border-outline-variant/10 p-4 flex justify-between items-center">
        <div className="flex items-center gap-6">
          <h2 className="text-sm font-black text-on-surface uppercase tracking-widest flex items-center gap-2">
            <InboxIcon className="w-4 h-4 text-primary" />
            Pill Detection Inbox <span className="opacity-30">(Pending)</span>
          </h2>
          <div className="h-4 w-px bg-outline-variant/20" />
          <nav className="flex gap-1 bg-surface-container rounded-lg p-1">
            {(['Inbox', 'Training Set', 'Trash'] as const).map((tab) => (
              <button
                key={tab}
                onClick={() => {
                  setActiveTab(tab);
                  setSelectedIndex(0);
                }}
                className={cn(
                  "px-4 py-1.5 rounded-md text-[10px] font-black uppercase tracking-widest transition-all",
                  activeTab === tab 
                    ? "bg-surface-container-highest text-primary shadow-sm" 
                    : "text-on-surface-variant hover:text-on-surface"
                )}
              >
                {tab}
              </button>
            ))}
          </nav>
        </div>
        
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2 px-3 py-1.5 bg-primary/10 border border-primary/20 rounded-full">
            <div className="w-1.5 h-1.5 rounded-full bg-primary ai-pulse" />
            <span className="text-[10px] font-black text-primary uppercase tracking-[0.15em]">Remaining Tasks: {data.filter(i => i.status === 'pending').length}</span>
          </div>
        </div>
      </header>

      {/* Main Grid Area */}
      <div className="mt-16">
        <div className="mb-6 flex justify-between items-center">
          <div className="flex items-center gap-4">
            <button 
              onClick={toggleSelectAll}
              className="flex items-center gap-2 px-3 py-1.5 bg-surface-container-high rounded-lg text-[10px] font-bold text-on-surface-variant hover:text-on-surface transition-colors"
            >
              <div className={cn("w-3 h-3 border rounded-sm flex items-center justify-center", selectedIds.length === currentItems.length ? "bg-primary border-primary" : "border-outline")}>
                {selectedIds.length === currentItems.length && <CheckCircle2 className="w-2.5 h-2.5 text-black" />}
              </div>
              Select All
            </button>
            <span className="text-[10px] text-on-surface-variant uppercase font-bold tracking-widest opacity-50">{currentItems.length} items found</span>
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-6">
          <AnimatePresence mode="popLayout">
            {currentItems.map((item, index) => (
              <motion.div
                layout
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.8, filter: 'blur(10px)' }}
                key={item.id}
                onClick={() => setSelectedIndex(index)}
                className={cn(
                  "group relative aspect-square rounded-3xl overflow-hidden cursor-pointer border transition-all duration-300",
                  selectedIndex === index 
                    ? "ring-2 ring-primary border-transparent shadow-2xl shadow-primary/10" 
                    : "border-outline-variant/10 bg-surface-container hover:border-outline-variant/30"
                )}
              >
                <img 
                  src={item.imageUrl} 
                  alt={item.filename}
                  className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
                />
                
                <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                
                {/* Checkbox overlay */}
                <div 
                  onClick={(e) => toggleSelect(item.id, e)}
                  className={cn(
                    "absolute top-4 left-4 w-6 h-6 rounded-lg backdrop-blur-md border flex items-center justify-center transition-all",
                    selectedIds.includes(item.id) 
                      ? "bg-primary border-primary" 
                      : "bg-surface/20 border-white/20 opacity-0 group-hover:opacity-100"
                  )}
                >
                  {selectedIds.includes(item.id) && <CheckCircle2 className="w-4 h-4 text-black" />}
                </div>

                <div className="absolute bottom-4 left-4 right-4">
                  <p className="text-[10px] font-mono text-white/50 truncate mb-1">{item.filename}</p>
                  <p className="text-xs font-black text-white uppercase tracking-widest">{item.label}</p>
                </div>

                {item.status === 'approved' && (
                  <div className="absolute top-4 right-4 bg-green-500 text-black px-2 py-0.5 rounded text-[8px] font-black uppercase">Approved</div>
                )}
              </motion.div>
            ))}
          </AnimatePresence>
        </div>
        
        {currentItems.length === 0 && (
          <div className="flex flex-col items-center justify-center h-[60vh] opacity-20">
            <CheckCircle2 className="w-16 h-16 mb-4" />
            <p className="text-xl font-black uppercase tracking-widest">Inbox Dynamic Empty</p>
          </div>
        )}
      </div>

      {/* Right Labeling Inspector */}
      <aside className="fixed right-0 top-28 bottom-0 w-[420px] bg-surface-container-low/80 backdrop-blur-3xl border-l border-outline-variant/10 z-30 flex flex-col p-6 shadow-[0_0_50px_rgba(0,0,0,0.5)]">
        <header className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg bg-primary/10 flex items-center justify-center">
              <Crosshair className="w-4 h-4 text-primary" />
            </div>
            <h3 className="text-sm font-black uppercase tracking-widest text-on-surface">Labeling Inspector</h3>
          </div>
          <button className="text-on-surface-variant hover:text-on-surface transition-colors">
            <Info className="w-5 h-5" />
          </button>
        </header>

        <div className="flex-1 overflow-y-auto space-y-8 pr-2 custom-scrollbar">
          {selectedItem ? (
            <>
              {/* Canvas Preview Area */}
              <section className="space-y-4">
                <div className="relative aspect-video bg-black rounded-3xl overflow-hidden border border-outline-variant/20 group cursor-crosshair">
                  <img src={selectedItem.imageUrl} className="w-full h-full object-cover opacity-80" />
                  {/* Mock Bounding Box */}
                  <motion.div 
                    initial={false}
                    animate={{ 
                      left: selectedItem.coords.x, 
                      top: selectedItem.coords.y, 
                      width: selectedItem.coords.w, 
                      height: selectedItem.coords.h 
                    }}
                    className="absolute border-2 border-primary shadow-[0_0_15px_rgba(123,208,255,0.4)] pointer-events-none"
                  >
                    <div className="absolute -top-6 left-0 bg-primary px-1.5 py-0.5 rounded text-[8px] font-black text-black uppercase">{selectedItem.label}</div>
                    <div className="absolute top-0 left-0 w-2 h-2 bg-primary -translate-x-1/2 -translate-y-1/2 rounded-full ring-4 ring-primary/20" />
                    <div className="absolute bottom-0 right-0 w-2 h-2 bg-primary translate-x-1/2 translate-y-1/2 rounded-full ring-4 ring-primary/20" />
                  </motion.div>
                  
                  {/* Drawing overlays */}
                  <div className="absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity">
                    <div className="absolute top-4 right-4 flex gap-2">
                      <button className="p-2 bg-surface-container-highest/80 backdrop-blur rounded-lg text-primary"><MousePointer2 className="w-4 h-4" /></button>
                      <button className="p-2 bg-surface/20 backdrop-blur rounded-lg text-white"><Square className="w-4 h-4" /></button>
                    </div>
                  </div>
                </div>
                <p className="text-[9px] text-center text-on-surface-variant italic">마우스로 영역을 드래그하여 바운딩 박스를 수정할 수 있습니다.</p>
              </section>

              {/* Manual Verification Section */}
              <section className="space-y-6">
                <h4 className="text-[10px] font-black text-on-surface-variant uppercase tracking-widest border-b border-outline-variant/10 pb-2">Manual Verification</h4>
                
                <div className="space-y-4">
                  <div className="space-y-1.5">
                    <label className="text-[10px] font-bold text-on-surface-variant/70 uppercase tracking-widest pl-1">알약 종류 (Class)</label>
                    <select className="w-full bg-surface-container-high border border-outline-variant/20 rounded-xl p-4 text-xs text-on-surface font-bold focus:outline-none focus:border-primary transition-colors appearance-none cursor-pointer">
                      <option>Tylenol ER</option>
                      <option>Aspirin 500mg</option>
                      <option>Ibuprofen</option>
                      <option>Vitamin C</option>
                      <option>Unknown / Fragment</option>
                    </select>
                  </div>

                  <div className="grid grid-cols-2 gap-4">
                    <CoordInput label="X Coordinate" value={selectedItem.coords.x} />
                    <CoordInput label="Y Coordinate" value={selectedItem.coords.y} />
                    <CoordInput label="Bound Width" value={selectedItem.coords.w} />
                    <CoordInput label="Bound Height" value={selectedItem.coords.h} />
                  </div>
                </div>
              </section>

              {/* Action Section */}
              <section className="pt-8 border-t border-outline-variant/10 space-y-4">
                <div className="bg-primary/5 p-4 rounded-2xl border border-primary/20">
                  <p className="text-[10px] text-primary font-bold uppercase tracking-widest flex items-center gap-2 mb-1">
                    <CheckCircle2 className="w-3 h-3" />
                    Security Notice
                  </p>
                  <p className="text-[10px] text-on-surface-variant leading-relaxed">승인 시 해당 데이터는 실시간으로 <span className="text-on-surface font-bold">'Training Set'</span>으로 이동되어 다음 모델 가중치 학습에 반영됩니다.</p>
                </div>

                <div className="grid grid-cols-1 gap-3">
                  <button 
                    onClick={handleApprove}
                    className="w-full py-4 bg-primary text-on-primary font-black text-xs uppercase tracking-[0.2em] rounded-2xl flex items-center justify-center gap-3 shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all"
                  >
                    <CheckCircle2 className="w-5 h-5" />
                    Update Label & Approve
                  </button>
                  <button 
                    onClick={handleReject}
                    className="w-full py-4 bg-error/10 text-error border border-error/20 font-black text-xs uppercase tracking-[0.15em] rounded-2xl flex items-center justify-center gap-3 hover:bg-error/20 transition-all"
                  >
                    <Trash2 className="w-5 h-5" />
                    Reject & Flag
                  </button>
                </div>
              </section>
            </>
          ) : (
            <div className="h-full flex flex-col items-center justify-center opacity-30 text-center">
              <Database className="w-12 h-12 mb-4" />
              <p className="text-xs font-black uppercase tracking-widest text-on-surface">No Item Selected</p>
              <p className="text-[10px] mt-2">왼쪽 그리드에서 이미지를 선택하여<br/>인스펙팅을 시작하세요.</p>
            </div>
          )}
        </div>
      </aside>
    </div>
  );
}

function CoordInput({ label, value }: { label: string, value: number }) {
  return (
    <div className="space-y-1.5">
      <label className="text-[9px] font-bold text-on-surface-variant/50 uppercase tracking-widest pl-1">{label}</label>
      <div className="relative">
        <input 
          type="number" 
          value={value} 
          readOnly 
          className="w-full bg-surface-container border border-outline-variant/10 rounded-xl px-4 py-2.5 text-[10px] font-mono text-on-surface focus:outline-none focus:border-primary transition-colors" 
        />
        <div className="absolute right-3 top-1/2 -translate-y-1/2 text-[8px] font-mono opacity-20">PX</div>
      </div>
    </div>
  );
}
