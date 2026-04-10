package structlab.gui.visual;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

/**
 * A compact summary header for the Compare workspace.
 * Shows structure name, implementation count, operation count,
 * and an overall status indicator.
 */
public class ComparisonSummaryPane extends HBox {

    private final Label structureLabel;
    private final Label implCountLabel;
    private final Label opsCountLabel;
    private final Label overallStatus;

    public ComparisonSummaryPane() {
        setSpacing(20);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("comparison-summary");
        setPadding(new Insets(14, 20, 14, 20));

        structureLabel = new Label("No active comparison");
        structureLabel.getStyleClass().add("comparison-summary-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        implCountLabel = new Label("");
        implCountLabel.getStyleClass().add("comparison-summary-metric");

        opsCountLabel = new Label("");
        opsCountLabel.getStyleClass().add("comparison-summary-metric");

        overallStatus = new Label("");
        overallStatus.getStyleClass().addAll("comparison-status-badge", "comparison-status-idle");

        getChildren().addAll(structureLabel, spacer, implCountLabel, opsCountLabel, overallStatus);
    }

    public void updateIdle() {
        structureLabel.setText("No active comparison");
        implCountLabel.setText("");
        opsCountLabel.setText("");
        overallStatus.setText("");
        overallStatus.getStyleClass().setAll("comparison-status-badge", "comparison-status-idle");
    }

    public void updateSession(String structureName, int implCount) {
        structureLabel.setText(structureName);
        implCountLabel.setText(implCount + " implementations");
        opsCountLabel.setText("0 operations");
        overallStatus.setText("READY");
        overallStatus.getStyleClass().setAll("comparison-status-badge", "comparison-status-ready");
    }

    public void updateAfterOperation(int opsCount, boolean allSucceeded) {
        opsCountLabel.setText(opsCount + " operation" + (opsCount == 1 ? "" : "s"));
        if (allSucceeded) {
            overallStatus.setText("ALL OK");
            overallStatus.getStyleClass().setAll("comparison-status-badge", "comparison-status-ok");
        } else {
            overallStatus.setText("PARTIAL");
            overallStatus.getStyleClass().setAll("comparison-status-badge", "comparison-status-fail");
        }
    }
}
