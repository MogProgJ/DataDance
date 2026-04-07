package structlab.gui.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import structlab.app.service.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindowController {

    // ── Discovery panel ─────────────────────────────────────────
    @FXML private ListView<StructureSummary> structureListView;
    @FXML private ListView<ImplementationSummary> implementationListView;
    @FXML private Button openSessionButton;

    // ── Center panel ────────────────────────────────────────────
    @FXML private TextArea stateArea;
    @FXML private TextArea traceArea;

    // ── Right panel: session info ───────────────────────────────
    @FXML private Label sessionStructureLabel;
    @FXML private Label sessionImplLabel;
    @FXML private Label sessionOpsCountLabel;
    @FXML private Button resetButton;
    @FXML private Button closeSessionButton;

    // ── Right panel: operations ─────────────────────────────────
    @FXML private ListView<OperationInfo> operationListView;
    @FXML private TextField argField;
    @FXML private Button executeButton;

    // ── Right panel: history ────────────────────────────────────
    @FXML private ListView<String> historyListView;

    // ── Status ──────────────────────────────────────────────────
    @FXML private Label statusLabel;
    @FXML private Label bottomStatusLabel;

    private StructLabService service;

    public void initService(StructLabService service) {
        this.service = service;
        loadStructures();
    }

    @FXML
    public void initialize() {
        // Structure list: display name + category
        structureListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(StructureSummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name() + "  [" + item.category() + "]");
            }
        });

        // Implementation list: display name
        implementationListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(ImplementationSummary item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.name());
            }
        });

        // Operations list: display name + complexity
        operationListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(OperationInfo item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    String aliases = item.aliases().isEmpty() ? "" : " (" + String.join(", ", item.aliases()) + ")";
                    String mutatesMark = item.mutates() ? " [mut]" : "";
                    setText(item.name() + aliases + "  " + item.complexityNote() + mutatesMark);
                    setTooltip(new Tooltip(
                            item.description() + "\nUsage: " + item.usage() +
                            "\nMutates: " + (item.mutates() ? "Yes" : "No") +
                            "\nComplexity: " + item.complexityNote()
                    ));
                }
            }
        });

        // Selection listeners
        structureListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> onStructureSelected(selected));
        implementationListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> openSessionButton.setDisable(selected == null || service.hasActiveSession()));
    }

    // ── Discovery ───────────────────────────────────────────────

    private void loadStructures() {
        if (service == null) return;
        List<StructureSummary> structures = service.getAllStructures();
        structureListView.setItems(FXCollections.observableArrayList(structures));
    }

    private void onStructureSelected(StructureSummary selected) {
        implementationListView.getItems().clear();
        if (selected == null) return;
        List<ImplementationSummary> impls = service.getImplementations(selected.id());
        implementationListView.setItems(FXCollections.observableArrayList(impls));
        openSessionButton.setDisable(service.hasActiveSession());
    }

    // ── Session ─────────────────────────────────────────────────

    @FXML
    private void onOpenSession() {
        ImplementationSummary impl = implementationListView.getSelectionModel().getSelectedItem();
        if (impl == null) return;

        try {
            SessionSnapshot snapshot = service.openSession(impl.parentStructureId(), impl.id());
            updateSessionUI(snapshot);
            refreshState();
            loadOperations();
            historyListView.getItems().clear();

            openSessionButton.setDisable(true);
            resetButton.setDisable(false);
            closeSessionButton.setDisable(false);
            executeButton.setDisable(false);

            statusLabel.setText("Session: " + snapshot.structureName() + " / " + snapshot.implementationName());
            bottomStatusLabel.setText("Session opened for " + snapshot.implementationName());
        } catch (Exception e) {
            showError("Failed to open session", e.getMessage());
        }
    }

    @FXML
    private void onCloseSession() {
        service.closeSession();
        clearSessionUI();
        bottomStatusLabel.setText("Session closed.");
    }

    @FXML
    private void onReset() {
        try {
            service.resetSession();
            refreshState();
            refreshHistory();
            traceArea.clear();
            updateSessionOpsCount();
            bottomStatusLabel.setText("Session reset to empty state.");
        } catch (Exception e) {
            showError("Reset failed", e.getMessage());
        }
    }

    // ── Operations ──────────────────────────────────────────────

    private void loadOperations() {
        List<OperationInfo> ops = service.getAvailableOperations();
        operationListView.setItems(FXCollections.observableArrayList(ops));
    }

    @FXML
    private void onExecuteOperation() {
        OperationInfo selectedOp = operationListView.getSelectionModel().getSelectedItem();
        if (selectedOp == null) {
            bottomStatusLabel.setText("Select an operation first.");
            return;
        }

        String rawArgs = argField.getText().trim();
        List<String> args = rawArgs.isEmpty()
                ? List.of()
                : Arrays.stream(rawArgs.split("\\s+")).collect(Collectors.toList());

        try {
            ExecutionResult result = service.executeOperation(selectedOp.name(), args);
            refreshState();
            refreshHistory();
            refreshTrace();
            updateSessionOpsCount();
            argField.clear();

            if (result.success()) {
                String msg = "Executed " + result.operationName();
                if (result.returnedValue() != null && !"null".equals(result.returnedValue())) {
                    msg += " -> " + result.returnedValue();
                }
                bottomStatusLabel.setText(msg);
            } else {
                bottomStatusLabel.setText("Failed: " + result.message());
            }
        } catch (Exception e) {
            showError("Execution error", e.getMessage());
        }
    }

    // ── Refresh helpers ─────────────────────────────────────────

    private void refreshState() {
        if (!service.hasActiveSession()) return;
        stateArea.setText(service.getRenderedState());
    }

    private void refreshHistory() {
        if (!service.hasActiveSession()) return;
        List<ExecutionResult> history = service.getHistory();
        List<String> items = new java.util.ArrayList<>();
        for (int i = 0; i < history.size(); i++) {
            ExecutionResult r = history.get(i);
            String status = r.success() ? "[OK]" : "[FAIL]";
            String entry = status + " " + (i + 1) + ". " + r.operationName();
            if (r.success() && r.returnedValue() != null && !"null".equals(r.returnedValue())) {
                entry += " -> " + r.returnedValue();
            } else if (!r.success()) {
                entry += " — " + r.message();
            }
            items.add(entry);
        }
        historyListView.setItems(FXCollections.observableArrayList(items));
    }

    private void refreshTrace() {
        if (!service.hasActiveSession()) return;
        traceArea.setText(service.getLastTraceRendered());
    }

    private void updateSessionUI(SessionSnapshot snapshot) {
        sessionStructureLabel.setText("Structure: " + snapshot.structureName());
        sessionImplLabel.setText("Implementation: " + snapshot.implementationName());
        sessionOpsCountLabel.setText("Operations: " + snapshot.operationCount());
    }

    private void updateSessionOpsCount() {
        service.getSessionSnapshot().ifPresent(s ->
                sessionOpsCountLabel.setText("Operations: " + s.operationCount()));
    }

    private void clearSessionUI() {
        sessionStructureLabel.setText("No active session");
        sessionImplLabel.setText("");
        sessionOpsCountLabel.setText("");
        stateArea.clear();
        traceArea.clear();
        operationListView.getItems().clear();
        historyListView.getItems().clear();
        argField.clear();

        statusLabel.setText("Discovery Mode");
        openSessionButton.setDisable(false);
        resetButton.setDisable(true);
        closeSessionButton.setDisable(true);
        executeButton.setDisable(true);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
