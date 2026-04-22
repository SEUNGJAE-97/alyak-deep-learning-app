/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useEffect } from 'react';
import { AnimatePresence, motion } from 'motion/react';
import { CheckCircle2, ChevronRight, X, History as HistoryIcon } from 'lucide-react';
import Login from './components/Login';
import Dashboard from './components/Dashboard';
import Training from './components/Training';
import TrainingLogs from './components/TrainingLogs';
import Archives from './components/Archives';
import Sidebar from './components/Sidebar';
import Navbar from './components/Navbar';
import WorkflowStepper from './components/WorkflowStepper';

export type ViewType = 'Overview' | 'TrainingLogs' | 'Training' | 'Archives';
type LoginSuccessPayload = {
  accessToken: string;
  userId: number;
};

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentView, setCurrentView] = useState<ViewType>('Overview');
  const [showToast, setShowToast] = useState(false);

  // Simple persistence for demo purposes
  useEffect(() => {
    const auth = localStorage.getItem('observatory_auth');
    const token = localStorage.getItem('admin_access_token');
    if (auth === 'true' && token) {
      setIsAuthenticated(true);
    }

    // Simulate training completion after 10 seconds for demo
    const timer = setTimeout(() => {
      setShowToast(true);
    }, 10000);

    return () => clearTimeout(timer);
  }, [isAuthenticated]);

  const handleLogin = ({ accessToken, userId }: LoginSuccessPayload) => {
    localStorage.setItem('observatory_auth', 'true');
    localStorage.setItem('admin_access_token', accessToken);
    localStorage.setItem('admin_user_id', String(userId));
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem('observatory_auth');
    localStorage.removeItem('admin_access_token');
    localStorage.removeItem('admin_user_id');
    setIsAuthenticated(false);
  };

  if (!isAuthenticated) {
    return <Login onLogin={handleLogin} />;
  }

  return (
    <div className="min-h-screen bg-background text-on-surface scroll-smooth">
      <Navbar 
        onViewChange={setCurrentView} 
        trainingComplete={showToast} 
      />
      <WorkflowStepper currentView={currentView} />
      <Sidebar 
        onLogout={handleLogout} 
        currentView={currentView} 
        onViewChange={setCurrentView} 
      />
      
      <AnimatePresence>
        {showToast && (
          <motion.div 
            initial={{ opacity: 0, y: -20, x: '-50%' }}
            animate={{ opacity: 1, y: 0, x: '-50%' }}
            exit={{ opacity: 0, y: -20, x: '-50%' }}
            className="fixed top-24 left-1/2 z-[100] w-full max-w-sm"
          >
            <div className="bg-surface-container-high/90 backdrop-blur-2xl border border-primary/30 rounded-2xl p-4 shadow-2xl flex items-center gap-4 group">
              <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center text-primary shrink-0">
                <CheckCircle2 className="w-6 h-6" />
              </div>
              <div className="flex-1">
                <p className="text-sm font-black text-on-surface uppercase tracking-tight">Training Complete!</p>
                <p className="text-xs text-on-surface-variant">Model v2.1.0-LTYK weights stabilized.</p>
              </div>
              <div className="flex items-center gap-2">
                <button 
                  onClick={() => {
                    setCurrentView('Archives');
                    setShowToast(false);
                  }}
                  className="bg-primary text-on-primary text-[10px] font-black py-1 px-3 rounded uppercase tracking-widest hover:scale-105 active:scale-95 transition-all"
                >
                  View Results
                </button>
                <button 
                  onClick={() => setShowToast(false)}
                  className="text-on-surface-variant hover:text-on-surface transition-colors"
                >
                  <X className="w-4 h-4" />
                </button>
              </div>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      <main className="relative z-0">
        <AnimatePresence mode="wait">
          <motion.div
            key={currentView}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.3 }}
          >
            {currentView === 'Overview' && <Dashboard />}
            {currentView === 'TrainingLogs' && <TrainingLogs />}
            {currentView === 'Training' && <Training />}
            {currentView === 'Archives' && <Archives />}
          </motion.div>
        </AnimatePresence>
      </main>
    </div>
  );
}

