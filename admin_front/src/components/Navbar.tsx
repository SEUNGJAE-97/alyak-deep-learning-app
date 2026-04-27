import { History } from 'lucide-react';
import { ViewType } from '../App';

interface NavbarProps {
  onViewChange: (view: ViewType) => void;
  trainingComplete?: boolean;
}

export default function Navbar({ onViewChange, trainingComplete }: NavbarProps) {
  void onViewChange;
  void trainingComplete;
  return (
    <nav className="fixed top-0 w-full z-50 bg-surface/70 backdrop-blur-xl flex justify-between items-center h-16 px-6 border-b border-outline-variant/10">
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2">
          <img src="https://picsum.photos/seed/pill-logo/50/50" alt="Logo" className="w-8 h-8 rounded" />
          <span className="text-xl font-bold tracking-tighter text-primary">Alyak Admin</span>
        </div>
      </div>

      <div />

      <div />
    </nav>
  );
}
