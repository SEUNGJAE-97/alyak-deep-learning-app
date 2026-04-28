/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import { useState, useEffect } from "react";
import { AnimatePresence, motion } from "motion/react";
import { CheckCircle2, ChevronRight, History as HistoryIcon, X } from "lucide-react";
import Login from "./components/Login";
import Dashboard from "./components/Dashboard";
import Training from "./components/Training";
import TrainingLogs from "./components/TrainingLogs";
import Archives from "./components/Archives";
import Sidebar from "./components/Sidebar";
import Navbar from "./components/Navbar";
import WorkflowStepper from "./components/WorkflowStepper";
import {
  TrainingStreamProvider,
  useTrainingStream,
} from "./components/TrainingStreamContext";

export type ViewType = "Overview" | "TrainingLogs" | "Training" | "Archives";
type LoginSuccessPayload = {
  accessToken: string;
  userId: number;
};

export default function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [currentView, setCurrentView] = useState<ViewType>("Overview");

  // Simple persistence for demo purposes
  useEffect(() => {
    const auth = localStorage.getItem("observatory_auth");
    const token = localStorage.getItem("admin_access_token");
    if (auth === "true" && token) {
      setIsAuthenticated(true);
    }

  }, [isAuthenticated]);

  const handleLogin = ({ accessToken, userId }: LoginSuccessPayload) => {
    localStorage.setItem("observatory_auth", "true");
    localStorage.setItem("admin_access_token", accessToken);
    localStorage.setItem("admin_user_id", String(userId));
    setIsAuthenticated(true);
  };

  const handleLogout = () => {
    localStorage.removeItem("observatory_auth");
    localStorage.removeItem("admin_access_token");
    localStorage.removeItem("admin_user_id");
    setIsAuthenticated(false);
  };

  if (!isAuthenticated) {
    return <Login onLogin={handleLogin} />;
  }

  return (
    <TrainingStreamProvider>
      <AppShell
        currentView={currentView}
        setCurrentView={setCurrentView}
        handleLogout={handleLogout}
      />
    </TrainingStreamProvider>
  );
}

function AppShell({
  currentView,
  setCurrentView,
  handleLogout,
}: {
  currentView: ViewType;
  setCurrentView: (view: ViewType) => void;
  handleLogout: () => void;
}) {
  const { toast, dismissToast } = useTrainingStream();

  useEffect(() => {
    if (!toast) return;
    const timer = window.setTimeout(() => dismissToast(), 10000);
    return () => window.clearTimeout(timer);
  }, [toast, dismissToast]);

  return (
    <>
      <div className="min-h-screen bg-background text-on-surface scroll-smooth">
        <Navbar onViewChange={setCurrentView} />
        <WorkflowStepper currentView={currentView} />
        <Sidebar
          onLogout={handleLogout}
          currentView={currentView}
          onViewChange={setCurrentView}
        />

        <main className="relative z-0">
          <AnimatePresence mode="wait">
            <motion.div
              key={currentView}
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
              transition={{ duration: 0.3 }}
            >
              {currentView === "Overview" && <Dashboard />}
              {currentView === "TrainingLogs" && <TrainingLogs />}
              {currentView === "Training" && <Training />}
              {currentView === "Archives" && <Archives />}
            </motion.div>
          </AnimatePresence>
        </main>
      </div>
      {toast && (
        <div className="fixed top-24 left-1/2 -translate-x-1/2 z-[9999] w-full max-w-sm">
          <div
            className={
              toast.type === "success"
                ? "relative backdrop-blur-2xl border rounded-2xl p-4 shadow-2xl flex items-center gap-4 bg-green-800 border-green-600 text-white"
                : "backdrop-blur-2xl border rounded-2xl p-4 shadow-2xl flex items-center gap-4 bg-red-700 border-red-500 text-white"
            }
          >
            <div
              className={
                toast.type === "success"
                  ? "w-10 h-10 rounded-full flex items-center justify-center shrink-0 bg-green-900 text-white border border-green-400"
                  : "w-10 h-10 rounded-full flex items-center justify-center shrink-0 bg-red-900 text-white border border-red-400"
              }
            >
              {toast.type === "success" ? (
                <CheckCircle2 className="w-6 h-6" />
              ) : (
                <X className="w-6 h-6" />
              )}
            </div>
            <div className="flex-1">
              <p className="text-sm font-black uppercase tracking-tight">
                {toast.title}
              </p>
              {toast.description && (
                <p
                  className={
                    toast.type === "success"
                      ? "text-xs mt-0.5 text-white/90"
                      : "text-xs mt-0.5 text-white/90"
                  }
                >
                  {toast.description}
                </p>
              )}
            </div>
            <button
              onClick={dismissToast}
              className={
                toast.type === "success"
                  ? "absolute top-3 right-3 text-white/70 hover:text-white transition-colors"
                  : "absolute top-3 right-3 text-white/70 hover:text-white transition-colors"
              }
            >
              <X className="w-4 h-4" />
            </button>
            <div className="flex items-center gap-2">
              {toast.type === "success" && (
                <button
                  onClick={() => {
                    setCurrentView("Archives");
                    dismissToast();
                  }}
                  className="bg-green-950 text-white text-[10px] font-black py-1 px-3 rounded uppercase tracking-widest border border-green-500 hover:scale-105 active:scale-95 transition-all"
                >
                  View Results
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </>
  );
}
