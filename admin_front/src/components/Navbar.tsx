import { Bell, Settings, X, CheckCircle2, History } from 'lucide-react';
import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { cn } from '@/src/lib/utils';
import { ViewType } from '../App';

interface NavbarProps {
  onViewChange: (view: ViewType) => void;
  trainingComplete?: boolean;
}

export default function Navbar({ onViewChange, trainingComplete }: NavbarProps) {
  const [showNotifications, setShowNotifications] = useState(false);
  const [notifications, setNotifications] = useState([
    { id: 1, text: 'Model v5.1 Training Complete', time: '2 mins ago', type: 'success', view: 'Archives' as ViewType },
    { id: 2, text: 'Dataset Alyak_Full_v4 imported', time: '1 hour ago', type: 'info', view: 'Training' as ViewType },
    { id: 3, text: 'New login detected from IP: 192.168.1.1', time: '3 hours ago', type: 'warning', view: 'Overview' as ViewType },
  ]);

  const unreadCount = notifications.length;

  return (
    <nav className="fixed top-0 w-full z-50 bg-surface/70 backdrop-blur-xl flex justify-between items-center h-16 px-6 border-b border-outline-variant/10">
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2">
          <img src="https://picsum.photos/seed/pill-logo/50/50" alt="Logo" className="w-8 h-8 rounded" />
          <span className="text-xl font-bold tracking-tighter text-primary">Alyak Admin</span>
        </div>
      </div>

      {/* Center Model Info Bar */}
      <div className="hidden lg:flex items-center gap-3 px-6 py-2 bg-surface-container-low/40 backdrop-blur-md rounded-full border border-outline-variant/10">
        <div className="w-1.5 h-1.5 rounded-full bg-primary ai-pulse" />
        <p className="text-[10px] font-mono font-bold tracking-wider text-on-surface-variant flex items-center gap-4">
          <span className="text-on-surface">CURRENT ACTIVE MODEL: <span className="text-primary tracking-widest text-[11px] font-black">V2.1.0-LTYK</span></span>
          <span className="opacity-20">|</span>
          <span>VISION-TRANSFORMER (VIT)</span>
          <span className="opacity-20">|</span>
          <span>STABLE READY</span>
        </p>
      </div>

      <div className="flex items-center gap-4">
        <div className="relative">
          <button 
            onClick={() => setShowNotifications(!showNotifications)}
            className="p-2 text-outline hover:text-on-surface transition-colors hover:bg-surface-container/50 rounded-lg relative group"
          >
            <motion.div
              animate={trainingComplete && unreadCount > 0 ? {
                rotate: [0, -10, 10, -10, 10, 0],
                scale: [1, 1.1, 1],
              } : {}}
              transition={{ 
                duration: 0.5, 
                repeat: trainingComplete ? Infinity : 0, 
                repeatDelay: 2 
              }}
            >
              <Bell className="w-5 h-5" />
            </motion.div>
            
            {unreadCount > 0 && (
              <span className="absolute top-1.5 right-1.5 w-4 h-4 bg-error text-[10px] font-black text-white rounded-full flex items-center justify-center border-2 border-surface">
                {unreadCount}
              </span>
            )}

            {trainingComplete && unreadCount > 0 && (
              <motion.div
                initial={{ scale: 0.8, opacity: 0 }}
                animate={{ scale: 1.5, opacity: 0 }}
                transition={{ duration: 1.5, repeat: Infinity }}
                className="absolute inset-0 rounded-full border-2 border-primary/50"
              />
            )}
          </button>

          <AnimatePresence>
            {showNotifications && (
              <>
                <div 
                  className="fixed inset-0 z-40" 
                  onClick={() => setShowNotifications(false)} 
                />
                <motion.div
                  initial={{ opacity: 0, y: 10, scale: 0.95 }}
                  animate={{ opacity: 1, y: 0, scale: 1 }}
                  exit={{ opacity: 0, y: 10, scale: 0.95 }}
                  className="absolute right-0 mt-3 w-80 bg-surface-container-high border border-outline-variant/20 rounded-2xl shadow-2xl z-50 overflow-hidden"
                >
                  <header className="p-4 border-b border-outline-variant/10 flex justify-between items-center bg-surface-container-highest/30">
                    <h3 className="text-xs font-black text-on-surface uppercase tracking-widest">Notifications</h3>
                    <button 
                      onClick={() => setShowNotifications(false)}
                      className="text-on-surface-variant hover:text-on-surface"
                    >
                      <X className="w-4 h-4" />
                    </button>
                  </header>

                  <div className="max-h-96 overflow-y-auto">
                    {notifications.length > 0 ? (
                      notifications.map((notif) => (
                        <button
                          key={notif.id}
                          onClick={() => {
                            onViewChange(notif.view);
                            setShowNotifications(false);
                          }}
                          className="w-full p-4 flex gap-4 hover:bg-surface-container-highest transition-colors text-left border-b border-outline-variant/5 last:border-0"
                        >
                          <div className={cn(
                            "w-8 h-8 rounded-full flex items-center justify-center shrink-0",
                            notif.type === 'success' ? "bg-green-500/10 text-green-400" :
                            notif.type === 'warning' ? "bg-yellow-500/10 text-yellow-400" : "bg-primary/10 text-primary"
                          )}>
                            {notif.type === 'success' ? <CheckCircle2 className="w-4 h-4" /> : <Settings className="w-4 h-4" />}
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className="text-xs font-bold text-on-surface truncate">{notif.text}</p>
                            <p className="text-[10px] text-on-surface-variant mt-0.5">{notif.time}</p>
                          </div>
                          <ChevronRight className="w-3 h-3 text-outline-variant mt-1" />
                        </button>
                      ))
                    ) : (
                      <div className="p-8 text-center">
                        <Bell className="w-8 h-8 text-outline-variant mx-auto mb-2 opacity-20" />
                        <p className="text-xs text-on-surface-variant opacity-50 italic">No new notifications</p>
                      </div>
                    )}
                  </div>

                  <footer className="p-3 bg-surface-container-highest/20 border-t border-outline-variant/10 flex gap-2">
                    <button 
                      onClick={() => setNotifications([])}
                      className="flex-1 py-2 text-[9px] font-black uppercase tracking-widest text-on-surface-variant hover:text-on-surface hover:bg-surface-container rounded-lg transition-all"
                    >
                      Mark all as read
                    </button>
                    <button 
                      onClick={() => setNotifications([])}
                      className="flex-1 py-2 text-[9px] font-black uppercase tracking-widest text-error/70 hover:text-error hover:bg-error/5 rounded-lg transition-all"
                    >
                      Clear all
                    </button>
                  </footer>
                </motion.div>
              </>
            )}
          </AnimatePresence>
        </div>
        
        <button className="p-2 text-outline hover:text-on-surface transition-colors hover:bg-surface-container/50 rounded-lg">
          <Settings className="w-5 h-5" />
        </button>
        
        <div className="w-8 h-8 rounded-full overflow-hidden border border-outline-variant/20 hover:border-primary transition-colors cursor-pointer">
          <img 
            alt="Admin profile" 
            className="w-full h-full object-cover" 
            src="https://picsum.photos/seed/admin/100/100"
            referrerPolicy="no-referrer"
          />
        </div>
      </div>
    </nav>
  );
}

function ChevronRight({ className }: { className?: string }) {
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
      <path d="m9 18 6-6-6-6" />
    </svg>
  );
}
