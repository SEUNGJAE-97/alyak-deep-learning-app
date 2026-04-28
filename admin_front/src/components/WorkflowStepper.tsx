import { Check, Circle } from 'lucide-react';
import { cn } from '@/src/lib/utils';
import { motion } from 'motion/react';
import { ViewType } from '../App';

interface Step {
  id: number;
  label: string;
  views: ViewType[];
}

const steps: Step[] = [
  { id: 1, label: 'Data Labeling', views: ['Overview'] },
  { id: 2, label: 'Model Training', views: ['Training', 'TrainingLogs'] },
  { id: 3, label: 'Evaluation & Archive', views: ['Archives'] },
];

interface WorkflowStepperProps {
  currentView: ViewType;
}

export default function WorkflowStepper({ currentView }: WorkflowStepperProps) {
  const currentStepIndex = steps.findIndex(step => step.views.includes(currentView));

  return (
    <div className="fixed top-16 left-64 right-0 z-40 bg-background/40 backdrop-blur-md border-b border-outline-variant/10 px-8 py-3 translate-y-[0px]">
      <div className="max-w-4xl mx-auto flex items-center justify-between">
        {steps.map((step, index) => {
          const isCompleted = index < currentStepIndex;
          const isCurrent = index === currentStepIndex;
          const isUpcoming = index > currentStepIndex;

          return (
            <div key={step.id} className="flex items-center flex-1 last:flex-none">
              <div className="flex items-center gap-3">
                <div className="relative">
                  {isCompleted ? (
                    <div className="w-6 h-6 rounded-full bg-primary flex items-center justify-center">
                      <Check className="w-4 h-4 text-black stroke-[3px]" />
                    </div>
                  ) : isCurrent ? (
                    <motion.div
                      animate={{ boxShadow: ['0 0 0px #7bd0ff', '0 0 12px #7bd0ff', '0 0 0px #7bd0ff'] }}
                      transition={{ duration: 2, repeat: Infinity }}
                      className="w-6 h-6 rounded-full border-2 border-primary flex items-center justify-center bg-primary/10"
                    >
                      <div className="w-1.5 h-1.5 rounded-full bg-primary" />
                    </motion.div>
                  ) : (
                    <div className="w-6 h-6 rounded-full border-2 border-on-surface-variant/20 border-dashed flex items-center justify-center">
                      <div className="w-1 h-1 rounded-full bg-on-surface-variant/20" />
                    </div>
                  )}
                </div>
                <span className={cn(
                  "text-[10px] font-black uppercase tracking-[0.15em] transition-all",
                  isCurrent ? "text-primary tracking-widest" :
                  isCompleted ? "text-on-surface" : "text-on-surface-variant opacity-40"
                )}>
                  {step.label}
                </span>
              </div>

              {index < steps.length - 1 && (
                <div className="flex-1 mx-6 h-px relative">
                  <div className={cn(
                    "absolute inset-0",
                    isCompleted ? "bg-primary" : "bg-outline-variant/20 border-t border-dashed border-on-surface-variant/20"
                  )} />
                </div>
              )}
            </div>
          );
        })}
      </div>
    </div>
  );
}
