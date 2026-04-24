import {
  BarChart3,
  BrainCircuit,
  Cpu,
  Database,
  HelpCircle,
  History as HistoryIcon,
  LayoutDashboard,
  LogOut,
  ShieldCheck,
  Terminal,
} from "lucide-react";
import { cn } from "@/src/lib/utils";
import { motion } from "motion/react";
import { ViewType } from "../App";

const navItems: { icon: any; label: ViewType; displayLabel: string }[] = [
  { icon: LayoutDashboard, label: "Overview", displayLabel: "Overview" },
  { icon: BrainCircuit, label: "Training", displayLabel: "Training" },
  { icon: Terminal, label: "TrainingLogs", displayLabel: "Live Training Logs" },
  { icon: HistoryIcon, label: "Archives", displayLabel: "Archives" },
];

const secondaryItems = [{ icon: HelpCircle, label: "Support" }];

interface SidebarProps {
  onLogout: () => void;
  currentView: ViewType;
  onViewChange: (view: ViewType) => void;
}

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

export default function Sidebar({
  onLogout,
  currentView,
  onViewChange,
}: SidebarProps) {
  return (
    <aside className="fixed left-0 top-0 h-screen w-64 z-40 bg-surface-container-lowest border-r border-outline-variant/10 flex flex-col p-4 gap-2">
      <div className="mt-20 mb-8 px-2 flex items-center gap-3">
        <div className="w-10 h-10 rounded-xl bg-primary/20 border border-primary/30 flex items-center justify-center shadow-[0_0_15px_rgba(123,208,255,0.2)]">
          <BrainCircuit className="w-6 h-6 text-primary" />
        </div>
        <div>
          <h2 className="text-sm font-black text-primary leading-none uppercase tracking-tighter">
            Pill RL Engine
          </h2>
          <p className="text-[10px] text-on-surface font-black uppercase tracking-widest mt-1 bg-primary/10 px-1.5 py-0.5 rounded border border-primary/20 inline-block">
            v2.1.0-ltyk
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
                <div className="w-1.5 h-1.5 rounded-full bg-primary ai-pulse" />
                <span className="text-[8px] opacity-40">45%</span>
              </div>
            )}
          </button>
        ))}
      </nav>
      {/* Server Infrastructure Widget
      <div className="px-2 mb-6 space-y-4">
        <div className="bg-surface-container-low/40 backdrop-blur-xl border border-outline-variant/10 rounded-2xl p-4 shadow-xl">
          <h3 className="text-[9px] font-black text-on-surface-variant uppercase tracking-[0.2em] mb-4 flex items-center gap-2">
            <Cpu className="w-3 h-3 text-primary" />
            Server Infrastructure
          </h3>
          <div className="space-y-3">
            <ResourceItem
              icon={Cpu}
              label="CPU"
              detail="EPYC 7763"
              usage={42}
            />
            <ResourceItem
              icon={Database}
              label="RAM"
              detail="512GB DDR4"
              usage={68}
            />
            <div className="relative group">
              <ResourceItem
                icon={ShieldCheck}
                label="GPU"
                detail="H100 x 4"
                usage={85}
              />
              <div className="absolute -right-1 -top-1">
                <div className="w-1.5 h-1.5 rounded-full bg-green-500 shadow-[0_0_8px_rgba(34,197,94,0.6)]" />
              </div>
            </div>
          </div>
        </div>
      </div> */}
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
