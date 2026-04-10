package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Visual state component for HeapPriorityQueue — the ADT/behavior lens.
 * Emphasizes queue semantics: next-out item, priority ordering, enqueue/dequeue.
 * Shows the underlying heap implementation as a secondary, collapsed detail.
 */
public class PriorityQueueVisualPane extends VBox {

    private final Label sizeLabel;
    private final Label nextOutValue;
    private final HBox priorityStrip;
    private final VBox heapDetailSection;
    private final VBox heapDetailContent;
    private final Label heapDetailToggle;
    private final Label emptyLabel;
    private boolean heapDetailExpanded = false;

    public PriorityQueueVisualPane() {
        setSpacing(10);
        setPadding(new Insets(12));
        getStyleClass().add("visual-state-pane");

        // ── Header ──────────────────────────────────
        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Priority Queue");
        title.getStyleClass().add("visual-state-title");
        sizeLabel = new Label("Size: 0");
        sizeLabel.getStyleClass().add("visual-state-meta");
        Label implBadge = new Label("heap-backed");
        implBadge.getStyleClass().add("pq-impl-badge");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(title, implBadge, spacer, sizeLabel);

        // ── Next Out hero section ───────────────────
        VBox nextOutSection = new VBox(2);
        nextOutSection.setAlignment(Pos.CENTER);
        nextOutSection.getStyleClass().add("pq-next-out-section");

        Label nextOutLabel = new Label("Next Out");
        nextOutLabel.getStyleClass().add("pq-next-out-label");

        nextOutValue = new Label("—");
        nextOutValue.getStyleClass().add("pq-next-out-value");

        nextOutSection.getChildren().addAll(nextOutLabel, nextOutValue);

        // ── Priority order strip ────────────────────
        VBox stripWrapper = new VBox(4);
        Label stripLabel = new Label("Priority Order");
        stripLabel.getStyleClass().add("pq-strip-label");

        priorityStrip = new HBox(4);
        priorityStrip.setAlignment(Pos.CENTER_LEFT);
        priorityStrip.getStyleClass().add("pq-priority-strip");

        stripWrapper.getChildren().addAll(stripLabel, priorityStrip);

        // ── Heap detail (collapsed by default) ──────
        heapDetailToggle = new Label("▸ Heap internals");
        heapDetailToggle.getStyleClass().add("pq-heap-detail-toggle");
        heapDetailToggle.setOnMouseClicked(e -> toggleHeapDetail());

        heapDetailContent = new VBox(4);
        heapDetailContent.getStyleClass().add("pq-heap-detail-content");
        heapDetailContent.setVisible(false);
        heapDetailContent.setManaged(false);

        heapDetailSection = new VBox(2, heapDetailToggle, heapDetailContent);
        heapDetailSection.getStyleClass().add("pq-heap-detail-section");

        // ── Empty state ─────────────────────────────
        emptyLabel = new Label("Empty priority queue");
        emptyLabel.getStyleClass().add("visual-empty-state");
        emptyLabel.setMaxWidth(Double.MAX_VALUE);
        emptyLabel.setAlignment(Pos.CENTER);

        getChildren().addAll(header, nextOutSection, stripWrapper, heapDetailSection);
    }

    public void update(HeapStateModel model) {
        priorityStrip.getChildren().clear();
        heapDetailContent.getChildren().clear();
        sizeLabel.setText("Size: " + model.size());

        if (model.isEmpty()) {
            nextOutValue.setText("—");
            priorityStrip.getChildren().add(emptyLabel);
            collapseHeapDetail();
            return;
        }

        // ── Next Out ────────────────────────────────
        nextOutValue.setText(model.minValue());

        // ── Build priority order strip ──────────────
        // Sort a copy of the elements to show priority ordering
        List<String> sorted = sortedByPriority(model.elements());
        for (int i = 0; i < sorted.size(); i++) {
            String value = sorted.get(i);
            StackPane chip = new StackPane();
            chip.getStyleClass().add("pq-chip");
            if (i == 0) {
                chip.getStyleClass().add("pq-chip-front");
            }
            chip.setMinWidth(34);
            chip.setMinHeight(26);

            Label valLabel = new Label(value);
            valLabel.getStyleClass().add("pq-chip-value");
            chip.getChildren().add(valLabel);

            priorityStrip.getChildren().add(chip);

            // Add arrow between chips
            if (i < sorted.size() - 1) {
                Label arrow = new Label("›");
                arrow.getStyleClass().add("pq-chip-arrow");
                priorityStrip.getChildren().add(arrow);
            }
        }

        // ── Build heap detail (array in heap order) ─
        buildHeapDetail(model);
    }

    /**
     * Sorts elements by numeric value for priority display.
     * Falls back to string comparison if not numeric.
     */
    static List<String> sortedByPriority(List<String> elements) {
        List<String> sorted = new ArrayList<>(elements);
        sorted.sort((a, b) -> {
            try {
                return Integer.compare(Integer.parseInt(a), Integer.parseInt(b));
            } catch (NumberFormatException e) {
                return a.compareTo(b);
            }
        });
        return Collections.unmodifiableList(sorted);
    }

    private void buildHeapDetail(HeapStateModel model) {
        // Backing array in heap order
        HBox arrayRow = new HBox(2);
        arrayRow.setAlignment(Pos.CENTER_LEFT);

        Label arrLabel = new Label("Backing array");
        arrLabel.getStyleClass().add("pq-detail-label");
        arrLabel.setMinWidth(90);
        arrayRow.getChildren().add(arrLabel);

        for (int i = 0; i < model.elements().size(); i++) {
            VBox slot = new VBox(1);
            slot.setAlignment(Pos.CENTER);

            Label idxLabel = new Label(String.valueOf(i));
            idxLabel.getStyleClass().add("heap-array-index");

            StackPane cell = new StackPane();
            cell.getStyleClass().add("heap-array-cell");
            if (i == 0) {
                cell.getStyleClass().add("heap-array-cell-root");
            }
            cell.setMinWidth(30);
            cell.setMinHeight(22);

            Label valLabel = new Label(model.elements().get(i));
            valLabel.getStyleClass().add("heap-array-cell-value");
            cell.getChildren().add(valLabel);

            slot.getChildren().addAll(idxLabel, cell);
            arrayRow.getChildren().add(slot);
        }

        heapDetailContent.getChildren().add(arrayRow);
    }

    private void toggleHeapDetail() {
        heapDetailExpanded = !heapDetailExpanded;
        heapDetailContent.setVisible(heapDetailExpanded);
        heapDetailContent.setManaged(heapDetailExpanded);
        heapDetailToggle.setText(heapDetailExpanded
                ? "▾ Heap internals" : "▸ Heap internals");
    }

    private void collapseHeapDetail() {
        heapDetailExpanded = false;
        heapDetailContent.setVisible(false);
        heapDetailContent.setManaged(false);
        heapDetailToggle.setText("▸ Heap internals");
    }
}
