import {
  CheckCircle2,
  MousePointer2,
  Square,
  RotateCcw,
  Crosshair,
  Database,
  Trash2,
  Inbox as InboxIcon,
  Info,
} from "lucide-react";
import { motion, AnimatePresence } from "motion/react";
import { useState, useEffect, MouseEvent, useRef } from "react";
import { cn } from "@/src/lib/utils";

interface PillData {
  id: number;
  imagePath: string;
  status: "INBOX" | "TRAINING_SET" | "TRASH";
  boxCount: number;
}

type LabelingPageResponse = {
  content: PillData[];
  totalElements: number;
};

type LabelingItemDetail = {
  id: number;
  imagePath: string;
  status: "INBOX" | "TRAINING_SET" | "TRASH";
  boxes: Array<{
    id: number;
    boxIndex: number;
    xMin: number;
    yMin: number;
    xMax: number;
    yMax: number;
  }>;
};

type EditableBox = {
  id: number;
  boxIndex: number;
  xMin: number;
  yMin: number;
  xMax: number;
  yMax: number;
};

type DeleteMenuState = {
  boxId: number;
};

type BulkActionToastState = {
  ids: number[];
  message: string;
  actions: Array<{ label: string; status: PillData["status"] }>;
};

export default function Dashboard() {
  const [data, setData] = useState<PillData[]>([]);
  const [activeTab, setActiveTab] = useState<
    "Inbox" | "Training Set" | "Trash"
  >("Inbox");
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [selectedIds, setSelectedIds] = useState<number[]>([]);
  const [selectedDetail, setSelectedDetail] =
    useState<LabelingItemDetail | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isSaving, setIsSaving] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [interactionMode, setInteractionMode] = useState<"select" | "draw">(
    "select",
  );
  const [editableBoxes, setEditableBoxes] = useState<EditableBox[]>([]);
  const [selectedBoxId, setSelectedBoxId] = useState<number | null>(null);
  const [hoveredBoxId, setHoveredBoxId] = useState<number | null>(null);
  const [deleteMenu, setDeleteMenu] = useState<DeleteMenuState | null>(null);
  const [draftBox, setDraftBox] = useState<EditableBox | null>(null);
  const [draggingBoxId, setDraggingBoxId] = useState<number | null>(null);
  const [bulkActionToast, setBulkActionToast] = useState<BulkActionToastState | null>(null);
  const previewRef = useRef<HTMLDivElement | null>(null);
  const dragStartRef = useRef<{ point: { x: number; y: number }; box: EditableBox } | null>(null);

  const apiBaseUrl =
    import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
  const token = localStorage.getItem("admin_access_token");

  const resolveImageUrl = (imagePath: string) => {
    if (!imagePath) return "";
    if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
      return imagePath;
    }
    return `${apiBaseUrl}${imagePath}`;
  };

  const statusFromTab = (
    tab: "Inbox" | "Training Set" | "Trash",
  ): PillData["status"] => {
    if (tab === "Inbox") return "INBOX";
    if (tab === "Training Set") return "TRAINING_SET";
    return "TRASH";
  };

  const fetchItems = async (tab: "Inbox" | "Training Set" | "Trash") => {
    if (!token) return;
    setIsLoading(true);
    setErrorMessage(null);
    try {
      const status = statusFromTab(tab);
      const response = await fetch(
        `${apiBaseUrl}/api/admin/labeling/items?status=${status}&page=0&pageSize=100`,
        { headers: { Authorization: `Bearer ${token}` } },
      );
      if (!response.ok) throw new Error("라벨링 목록을 불러오지 못했습니다.");
      const pageData = (await response.json()) as LabelingPageResponse;
      setData(pageData.content ?? []);
      setSelectedIndex(0);
      setSelectedIds([]);
      setBulkActionToast(null);
    } catch (error) {
      setErrorMessage(
        error instanceof Error
          ? error.message
          : "목록 조회 중 오류가 발생했습니다.",
      );
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchItems(activeTab);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activeTab]);

  const currentItems = data.filter((item) => {
    if (activeTab === "Inbox") return item.status === "INBOX";
    if (activeTab === "Training Set") return item.status === "TRAINING_SET";
    return item.status === "TRASH";
  });

  const selectedItem = currentItems[selectedIndex] || null;
  const selectedBox = editableBoxes.find((box) => box.id === selectedBoxId) ?? null;
  const hoveredBox = editableBoxes.find((box) => box.id === hoveredBoxId) ?? null;
  const previewInfoBox = hoveredBox ?? selectedBox;

  useEffect(() => {
    const fetchItemDetail = async () => {
      if (!selectedItem || !token) {
        setSelectedDetail(null);
        return;
      }
      try {
        const response = await fetch(
          `${apiBaseUrl}/api/admin/labeling/items/${selectedItem.id}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          },
        );
        if (!response.ok) throw new Error("라벨링 상세를 불러오지 못했습니다.");
        const detail = (await response.json()) as LabelingItemDetail;
        console.log("[LabelingDetail] selectedDetail?.boxes", detail?.boxes);
        setSelectedDetail(detail);
      } catch (error) {
        setErrorMessage(
          error instanceof Error
            ? error.message
            : "상세 조회 중 오류가 발생했습니다.",
        );
      }
    };
    fetchItemDetail();
  }, [selectedItem, token, apiBaseUrl]);

  useEffect(() => {
    console.log("[LabelingDetail] selectedDetail?.boxes(state)", selectedDetail?.boxes);
  }, [selectedDetail]);

  useEffect(() => {
    const boxes =
      selectedDetail?.boxes
        ?.slice()
        .sort((a, b) => a.boxIndex - b.boxIndex)
        .map((box) => ({
          id: box.id,
          boxIndex: box.boxIndex,
          xMin: box.xMin,
          yMin: box.yMin,
          xMax: box.xMax,
          yMax: box.yMax,
        })) ?? [];
    setEditableBoxes(boxes);
    setSelectedBoxId(boxes[0]?.id ?? null);
    setHoveredBoxId(null);
    setDeleteMenu(null);
    setDraftBox(null);
    setDraggingBoxId(null);
    dragStartRef.current = null;
  }, [selectedDetail]);

  const clamp01 = (value: number) => Math.max(0, Math.min(1, value));

  const normalizeBox = (box: EditableBox): EditableBox => {
    const xMin = clamp01(Math.min(box.xMin, box.xMax));
    const yMin = clamp01(Math.min(box.yMin, box.yMax));
    const xMax = clamp01(Math.max(box.xMin, box.xMax));
    const yMax = clamp01(Math.max(box.yMin, box.yMax));
    return { ...box, xMin, yMin, xMax, yMax };
  };

  const toNormalizedPoint = (event: MouseEvent<HTMLDivElement>) => {
    const rect = previewRef.current?.getBoundingClientRect();
    if (!rect) return null;
    const x = clamp01((event.clientX - rect.left) / rect.width);
    const y = clamp01((event.clientY - rect.top) / rect.height);
    return { x, y };
  };

  const moveBox = (box: EditableBox, dx: number, dy: number): EditableBox => {
    const width = box.xMax - box.xMin;
    const height = box.yMax - box.yMin;
    let xMin = box.xMin + dx;
    let yMin = box.yMin + dy;

    if (xMin < 0) xMin = 0;
    if (yMin < 0) yMin = 0;
    if (xMin + width > 1) xMin = 1 - width;
    if (yMin + height > 1) yMin = 1 - height;

    return {
      ...box,
      xMin: clamp01(xMin),
      yMin: clamp01(yMin),
      xMax: clamp01(xMin + width),
      yMax: clamp01(yMin + height),
    };
  };

  const handlePreviewMouseDown = (event: MouseEvent<HTMLDivElement>) => {
    if (event.button !== 0) return;
    event.preventDefault();
    setDeleteMenu(null);
    if (interactionMode !== "draw") return;
    const point = toNormalizedPoint(event);
    if (!point) return;
    const tempId = -Date.now();
    setDraftBox({
      id: tempId,
      boxIndex: editableBoxes.length,
      xMin: point.x,
      yMin: point.y,
      xMax: point.x,
      yMax: point.y,
    });
  };

  const handlePreviewMouseMove = (event: MouseEvent<HTMLDivElement>) => {
    const point = toNormalizedPoint(event);
    if (!point) return;
    if (interactionMode === "draw" && draftBox) {
      setDraftBox({
        ...draftBox,
        xMax: point.x,
        yMax: point.y,
      });
      return;
    }
    if (interactionMode === "select" && draggingBoxId && dragStartRef.current) {
      const dx = point.x - dragStartRef.current.point.x;
      const dy = point.y - dragStartRef.current.point.y;
      const baseBox = dragStartRef.current.box;
      setEditableBoxes((prev) =>
        prev.map((box) => (box.id === draggingBoxId ? moveBox(baseBox, dx, dy) : box)),
      );
    }
  };

  const handlePreviewMouseUp = () => {
    if (draggingBoxId) {
      setDraggingBoxId(null);
      dragStartRef.current = null;
      return;
    }
    if (!draftBox) return;
    const normalized = normalizeBox(draftBox);
    const width = normalized.xMax - normalized.xMin;
    const height = normalized.yMax - normalized.yMin;
    setDraftBox(null);
    if (width < 0.005 || height < 0.005) return;
    setEditableBoxes((prev) => {
      const next = [...prev, normalized].map((box, index) => ({
        ...box,
        boxIndex: index,
      }));
      return next;
    });
    setSelectedBoxId(normalized.id);
  };

  const handleBoxMouseDown = (event: MouseEvent<HTMLDivElement>, box: EditableBox) => {
    event.stopPropagation();
    event.preventDefault();
    setSelectedBoxId(box.id);
    setDeleteMenu(null);
    if (interactionMode !== "select" || event.button !== 0) return;
    const point = toNormalizedPoint(event);
    if (!point) return;
    setDraggingBoxId(box.id);
    dragStartRef.current = { point, box };
  };

  const handleBoxContextMenu = (event: MouseEvent<HTMLDivElement>, box: EditableBox) => {
    event.preventDefault();
    event.stopPropagation();
    setSelectedBoxId(box.id);
    setDeleteMenu({ boxId: box.id });
  };

  const handleBoxCoordChange = (
    field: "xMin" | "yMin" | "xMax" | "yMax",
    value: number,
  ) => {
    if (!selectedBoxId || Number.isNaN(value)) return;
    setEditableBoxes((prev) =>
      prev.map((box) =>
        box.id === selectedBoxId
          ? normalizeBox({
              ...box,
              [field]: value,
            })
          : box,
      ),
    );
  };

  const persistBoxes = async () => {
    if (!selectedItem || !token) return;
    const response = await fetch(
      `${apiBaseUrl}/api/admin/labeling/items/${selectedItem.id}/boxes`,
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          boxes: editableBoxes.map((box, index) => ({
            boxIndex: index,
            xMin: box.xMin,
            yMin: box.yMin,
            xMax: box.xMax,
            yMax: box.yMax,
          })),
        }),
      },
    );
    if (!response.ok) throw new Error("박스 저장에 실패했습니다.");
    const detail = (await response.json()) as LabelingItemDetail;
    setSelectedDetail(detail);
  };

  const handleDeleteBoxById = (targetBoxId: number) => {
    setEditableBoxes((prev) =>
      prev
        .filter((box) => box.id !== targetBoxId)
        .map((box, index) => ({ ...box, boxIndex: index })),
    );
    setSelectedBoxId(null);
    setDeleteMenu(null);
  };

  const handleApprove = async () => {
    if (!selectedItem || !token) return;
    setIsSaving(true);
    setErrorMessage(null);
    try {
      await persistBoxes();
      const response = await fetch(
        `${apiBaseUrl}/api/admin/labeling/items/${selectedItem.id}/approve`,
        {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        },
      );
      if (!response.ok) throw new Error("승인 처리에 실패했습니다.");
      await fetchItems(activeTab);
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : "저장/승인 중 오류가 발생했습니다.",
      );
    } finally {
      setIsSaving(false);
    }
  };

  const handleReject = async () => {
    if (!selectedItem) return;
    try {
      const response = await fetch(
        `${apiBaseUrl}/api/admin/labeling/items/${selectedItem.id}/reject`,
        {
          method: "POST",
          headers: { Authorization: `Bearer ${token}` },
        },
      );
      if (!response.ok) throw new Error("반려 처리에 실패했습니다.");
      await fetchItems(activeTab);
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : "반려 중 오류가 발생했습니다.",
      );
    }
  };

  const toggleSelectAll = () => {
    if (selectedIds.length === currentItems.length) {
      setSelectedIds([]);
      setBulkActionToast(null);
    } else {
      const allIds = currentItems.map((i) => i.id);
      setSelectedIds(allIds);
      const actions =
        activeTab === "Inbox"
          ? [
              { label: "전체를 Training Set으로 보내기", status: "TRAINING_SET" as const },
              { label: "전체를 Trash로 보내기", status: "TRASH" as const },
            ]
          : activeTab === "Training Set"
            ? [
                { label: "전체를 Inbox로 보내기", status: "INBOX" as const },
                { label: "전체를 Trash로 보내기", status: "TRASH" as const },
              ]
            : [
                { label: "전체를 Inbox로 보내기", status: "INBOX" as const },
                { label: "전체를 Training Set으로 보내기", status: "TRAINING_SET" as const },
              ];
      setBulkActionToast({
        ids: allIds,
        message: `선택한 ${allIds.length}개 항목을 어디로 이동할까요?`,
        actions,
      });
    }
  };

  const toggleSelect = (id: number, e: MouseEvent) => {
    e.stopPropagation();
    setSelectedIds((prev) => {
      const next = prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id];
      if (next.length !== currentItems.length) {
        setBulkActionToast(null);
      }
      return next;
    });
  };

  const handleBulkMove = async (targetStatus: PillData["status"]) => {
    if (!token || !bulkActionToast || bulkActionToast.ids.length === 0) return;
    setIsSaving(true);
    setErrorMessage(null);
    try {
      const response = await fetch(`${apiBaseUrl}/api/admin/labeling/items/bulk/status`, {
        method: "PATCH",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ids: bulkActionToast.ids,
          status: targetStatus,
        }),
      });
      if (!response.ok) throw new Error("일괄 상태 변경에 실패했습니다.");
      setSelectedIds([]);
      setBulkActionToast(null);
      await fetchItems(activeTab);
    } catch (error) {
      setErrorMessage(
        error instanceof Error ? error.message : "일괄 상태 변경 중 오류가 발생했습니다.",
      );
    } finally {
      setIsSaving(false);
    }
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
            {(["Inbox", "Training Set", "Trash"] as const).map((tab) => (
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
                    : "text-on-surface-variant hover:text-on-surface",
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
            <span className="text-[10px] font-black text-primary uppercase tracking-[0.15em]">
              Remaining Tasks: {data.filter((i) => i.status === "INBOX").length}
            </span>
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
              <div
                className={cn(
                  "w-3 h-3 border rounded-sm flex items-center justify-center",
                  selectedIds.length === currentItems.length
                    ? "bg-primary border-primary"
                    : "border-outline",
                )}
              >
                {selectedIds.length === currentItems.length && (
                  <CheckCircle2 className="w-2.5 h-2.5 text-black" />
                )}
              </div>
              Select All
            </button>
            <span className="text-[10px] text-on-surface-variant uppercase font-bold tracking-widest opacity-50">
              {currentItems.length} items found
            </span>
          </div>
          {isLoading && (
            <span className="text-[10px] text-on-surface-variant uppercase tracking-widest">
              Loading...
            </span>
          )}
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-6">
          <AnimatePresence mode="popLayout">
            {currentItems.map((item, index) => (
              <motion.div
                layout
                initial={{ opacity: 0, scale: 0.9 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.8, filter: "blur(10px)" }}
                key={item.id}
                onClick={() => setSelectedIndex(index)}
                className={cn(
                  "group relative aspect-square rounded-3xl overflow-hidden cursor-pointer border transition-all duration-300",
                  selectedIndex === index
                    ? "ring-2 ring-primary border-transparent shadow-2xl shadow-primary/10"
                    : "border-outline-variant/10 bg-surface-container hover:border-outline-variant/30",
                )}
              >
                <img
                  src={resolveImageUrl(item.imagePath)}
                  alt={`labeling-item-${item.id}`}
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
                      : "bg-surface/20 border-white/20 opacity-0 group-hover:opacity-100",
                  )}
                >
                  {selectedIds.includes(item.id) && (
                    <CheckCircle2 className="w-4 h-4 text-black" />
                  )}
                </div>

                <div className="absolute bottom-4 left-4 right-4">
                  <p className="text-[10px] font-mono text-white/50 truncate mb-1">
                    {item.imagePath.split("/").pop()}
                  </p>
                  <p className="text-xs font-black text-white uppercase tracking-widest">
                    BOXES {item.boxCount}
                  </p>
                </div>

                {item.status === "TRAINING_SET" && (
                  <div className="absolute top-4 right-4 bg-green-500 text-black px-2 py-0.5 rounded text-[8px] font-black uppercase">
                    Approved
                  </div>
                )}
              </motion.div>
            ))}
          </AnimatePresence>
        </div>

        {currentItems.length === 0 && (
          <div className="flex flex-col items-center justify-center h-[60vh] text-center">
            <Database className="w-14 h-14 mb-4 text-on-surface-variant/50" />
            <p className="text-lg font-black uppercase tracking-widest text-on-surface">
              {errorMessage
                ? "라벨링 목록을 가져오지 못했습니다"
                : "Inbox Dynamic Empty"}
            </p>
            <p className="text-xs text-on-surface-variant mt-2">
              {errorMessage
                ? "네트워크 또는 서버 상태를 확인한 뒤 다시 시도해 주세요."
                : "검토 대기 항목이 없습니다."}
            </p>
            <button
              onClick={() => fetchItems(activeTab)}
              disabled={isLoading}
              className="mt-5 inline-flex items-center gap-2 px-4 py-2 rounded-xl bg-surface-container-high border border-outline-variant/20 text-xs font-bold text-on-surface hover:border-primary/40 disabled:opacity-50 transition-all"
            >
              <RotateCcw className="w-4 h-4" />
              다시 불러오기
            </button>
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
            <h3 className="text-sm font-black uppercase tracking-widest text-on-surface">
              Labeling Inspector
            </h3>
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
                <div
                  ref={previewRef}
                  onMouseDown={handlePreviewMouseDown}
                  onMouseMove={handlePreviewMouseMove}
                  onMouseUp={handlePreviewMouseUp}
                  onMouseLeave={handlePreviewMouseUp}
                  onContextMenu={(event) => event.preventDefault()}
                  className={cn(
                    "relative aspect-video bg-black rounded-3xl overflow-hidden border border-outline-variant/20 group",
                    interactionMode === "draw" ? "cursor-crosshair" : "cursor-default",
                  )}
                >
                  <img
                    src={resolveImageUrl(
                      selectedDetail?.imagePath ?? selectedItem.imagePath,
                    )}
                    draggable={false}
                    onDragStart={(event) => event.preventDefault()}
                    className="w-full h-full object-fill opacity-80 z-0 pointer-events-none select-none"
                  />
                  {editableBoxes.map((box) => (
                    <motion.div
                      key={box.id}
                      style={{
                        zIndex:
                          selectedBoxId === box.id
                            ? 30
                            : hoveredBoxId === box.id
                              ? 25
                              : 20,
                      }}
                      initial={false}
                      animate={{
                        left: `${box.xMin * 100}%`,
                        top: `${box.yMin * 100}%`,
                        width: `${(box.xMax - box.xMin) * 100}%`,
                        height: `${(box.yMax - box.yMin) * 100}%`,
                      }}
                      onMouseEnter={() => setHoveredBoxId(box.id)}
                      onMouseLeave={() =>
                        setHoveredBoxId((prev) => (prev === box.id ? null : prev))
                      }
                      onMouseDown={(event) => handleBoxMouseDown(event, box)}
                      onContextMenu={(event) => handleBoxContextMenu(event, box)}
                      className={cn(
                        "absolute border-2 shadow-[0_0_15px_rgba(123,208,255,0.4)]",
                        selectedBoxId === box.id
                          ? "border-primary"
                          : "border-primary/50",
                        hoveredBoxId === box.id && "bg-primary/20",
                        interactionMode === "select"
                          ? "cursor-move pointer-events-auto"
                          : "pointer-events-none",
                      )}
                    >
                      <div className="absolute -top-6 left-0 bg-primary px-1.5 py-0.5 rounded text-[8px] font-black text-black uppercase">
                        BOX {box.boxIndex}
                      </div>
                    </motion.div>
                  ))}
                  {draftBox && (
                    <div
                      className="absolute border-2 border-primary/70 border-dashed pointer-events-none"
                      style={{
                        left: `${Math.min(draftBox.xMin, draftBox.xMax) * 100}%`,
                        top: `${Math.min(draftBox.yMin, draftBox.yMax) * 100}%`,
                        width: `${Math.abs(draftBox.xMax - draftBox.xMin) * 100}%`,
                        height: `${Math.abs(draftBox.yMax - draftBox.yMin) * 100}%`,
                      }}
                    />
                  )}

                  {/* Drawing overlays */}
                  <div className="absolute inset-0 pointer-events-none opacity-0 group-hover:opacity-100 transition-opacity z-40">
                    <div className="absolute top-4 right-4 flex gap-2 pointer-events-auto">
                      <button
                        onClick={() => {
                          setInteractionMode("select");
                          setDeleteMenu(null);
                        }}
                        className={cn(
                          "p-2 backdrop-blur rounded-lg",
                          interactionMode === "select"
                            ? "bg-surface-container-highest/80 text-primary"
                            : "bg-surface/20 text-white",
                        )}
                      >
                        <MousePointer2 className="w-4 h-4" />
                      </button>
                      <button
                        onClick={() => {
                          setInteractionMode("draw");
                          setDraggingBoxId(null);
                          setHoveredBoxId(null);
                          setDeleteMenu(null);
                        }}
                        className={cn(
                          "p-2 backdrop-blur rounded-lg",
                          interactionMode === "draw"
                            ? "bg-surface-container-highest/80 text-primary"
                            : "bg-surface/20 text-white",
                        )}
                      >
                        <Square className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                  {deleteMenu && interactionMode === "select" && (
                    (() => {
                      const targetBox = editableBoxes.find(
                        (box) => box.id === deleteMenu.boxId,
                      );
                      if (!targetBox) return null;
                      const centerX = ((targetBox.xMin + targetBox.xMax) / 2) * 100;
                      const centerY = ((targetBox.yMin + targetBox.yMax) / 2) * 100;
                      return (
                        <button
                          onMouseDown={(event) => event.stopPropagation()}
                          onClick={(event) => {
                            event.stopPropagation();
                            handleDeleteBoxById(deleteMenu.boxId);
                          }}
                          className="absolute z-50 -translate-x-1/2 -translate-y-1/2 px-2 py-1 rounded-md border border-error/40 bg-white text-error text-[10px] font-black uppercase tracking-wider shadow-lg hover:bg-red-50"
                          style={{ left: `${centerX}%`, top: `${centerY}%` }}
                        >
                          삭제
                        </button>
                      );
                    })()
                  )}
                </div>
                <p className="text-[9px] text-center text-on-surface-variant italic">
                  마우스로 영역을 드래그하여 바운딩 박스를 수정할 수 있습니다.
                </p>
                {interactionMode === "select" && previewInfoBox && (
                  <div className="grid grid-cols-2 gap-2 rounded-xl border border-outline-variant/20 bg-surface-container-high p-3">
                    <div className="text-[10px] font-bold text-on-surface-variant">
                      X Min
                      <p className="text-on-surface">{previewInfoBox.xMin.toFixed(6)}</p>
                      <p className="text-[8px] opacity-60">NORM</p>
                    </div>
                    <div className="text-[10px] font-bold text-on-surface-variant">
                      Y Min
                      <p className="text-on-surface">{previewInfoBox.yMin.toFixed(6)}</p>
                      <p className="text-[8px] opacity-60">NORM</p>
                    </div>
                    <div className="text-[10px] font-bold text-on-surface-variant">
                      X Max
                      <p className="text-on-surface">{previewInfoBox.xMax.toFixed(6)}</p>
                      <p className="text-[8px] opacity-60">NORM</p>
                    </div>
                    <div className="text-[10px] font-bold text-on-surface-variant">
                      Y Max
                      <p className="text-on-surface">{previewInfoBox.yMax.toFixed(6)}</p>
                      <p className="text-[8px] opacity-60">NORM</p>
                    </div>
                  </div>
                )}
              </section>             

              {/* Action Section */}
              <section className="pt-8 border-t border-outline-variant/10 space-y-4">
                <div className="bg-primary/5 p-4 rounded-2xl border border-primary/20">
                  <p className="text-[10px] text-primary font-bold uppercase tracking-widest flex items-center gap-2 mb-1">
                    <CheckCircle2 className="w-3 h-3" />
                    Security Notice
                  </p>
                  <p className="text-[10px] text-on-surface-variant leading-relaxed">
                    승인 시 해당 데이터는 실시간으로{" "}
                    <span className="text-on-surface font-bold">
                      'Training Set'
                    </span>
                    으로 이동되어 다음 모델 가중치 학습에 반영됩니다.
                  </p>
                </div>

                <div className="grid grid-cols-1 gap-3">
                  <button
                    onClick={handleApprove}
                    disabled={isSaving}
                    className="w-full py-4 bg-primary text-on-primary font-black text-xs uppercase tracking-[0.2em] rounded-2xl flex items-center justify-center gap-3 shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all"
                  >
                    <CheckCircle2 className="w-5 h-5" />
                    {isSaving ? "Saving & Approving..." : "Update Label & Approve"}
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
              <p className="text-xs font-black uppercase tracking-widest text-on-surface">
                No Item Selected
              </p>
              <p className="text-[10px] mt-2">
                왼쪽 그리드에서 이미지를 선택하여
                <br />
                인스펙팅을 시작하세요.
              </p>
            </div>
          )}
        </div>
      </aside>

      {bulkActionToast && (
        <div className="fixed left-1/2 bottom-8 -translate-x-1/2 z-50 w-[min(92vw,640px)]">
          <div className="bg-surface-container-high/95 backdrop-blur-xl border border-outline-variant/20 rounded-2xl shadow-2xl px-4 py-3 flex items-center justify-between gap-4">
            <p className="text-[11px] text-on-surface font-bold">{bulkActionToast.message}</p>
            <div className="flex items-center gap-2">
              {bulkActionToast.actions.map((action) => (
                <button
                  key={action.status}
                  disabled={isSaving}
                  onClick={() => handleBulkMove(action.status)}
                  className="px-3 py-2 rounded-xl bg-primary/15 border border-primary/30 text-primary text-[10px] font-black uppercase tracking-widest hover:bg-primary/25 transition-all disabled:opacity-60"
                >
                  {action.label}
                </button>
              ))}
              <button
                onClick={() => setBulkActionToast(null)}
                className="px-3 py-2 rounded-xl bg-surface-container border border-outline-variant/20 text-on-surface-variant text-[10px] font-black uppercase tracking-widest hover:text-on-surface transition-all"
              >
                닫기
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function CoordInput({
  label,
  value,
  onChange,
}: {
  label: string;
  value: number;
  onChange: (value: number) => void;
}) {
  return (
    <div>
      <label className="text-[9px] font-bold text-on-surface-variant/50 uppercase tracking-widest pl-1">
        {label}
      </label>
      <div className="relative">
        <input
          type="number"
          value={Number.isFinite(value) ? value : 0}
          step="0.000001"
          min={0}
          max={1}
          onChange={(event) => onChange(Number(event.target.value))}
          className="w-full bg-surface-container border border-outline-variant/10 rounded-xl px-4 py-2.5 text-[10px] font-mono text-on-surface focus:outline-none focus:border-primary transition-colors"
        />
        <div className="absolute right-3 top-1/2 -translate-y-1/2 text-[8px] font-mono opacity-20">
          NORM
        </div>
      </div>
    </div>
  );
}
