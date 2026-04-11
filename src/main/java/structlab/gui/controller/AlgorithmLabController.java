package structlab.gui.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import structlab.core.graph.*;
import structlab.gui.visual.GraphVisualPane;

import java.util.List;

/**
 * Explore-mode graph algorithm workspace.
 * Self-contained UI module that provides preset selection, algorithm selection,
 * source node selection, and playback controls for BFS/DFS step-by-step execution.
 */
public class AlgorithmLabController {

    private final GraphVisualPane graphPane;
    private final PlaybackController playback = new PlaybackController();

    // Controls
    private ComboBox<String> presetCombo;
    private ComboBox<String> algorithmCombo;
    private ComboBox<String> sourceCombo;
    private Button runBtn, resetBtn;
    private Button playBtn, pauseBtn, nextBtn, prevBtn, toStartBtn, toEndBtn;
    private Slider speedSlider;
    private Label speedLabel;
    private Label frameLabel;

    // Info panel
    private Label discoveryLabel;
    private Label frontierLabel;
    private Label visitedLabel;
    private Label depthLabel;
    private Label statusMessageLabel;

    // State
    private Graph currentGraph;
    private GraphPresets.Preset currentPreset;
    private Timeline autoPlayTimeline;
    private boolean isPlaying = false;

    public AlgorithmLabController() {
        this.graphPane = new GraphVisualPane();
    }

    /** Builds and returns the complete Algorithm Lab workspace node. */
    public Node buildWorkspace() {
        // ── Left control panel ──────────────────────────────
        VBox controlPanel = new VBox(8);
        controlPanel.getStyleClass().add("algo-control-panel");
        controlPanel.setPrefWidth(260);
        controlPanel.setMinWidth(260);
        controlPanel.setMaxWidth(260);
        controlPanel.setPadding(new Insets(16, 12, 16, 12));

        // Preset selection
        VBox presetSection = buildSection("GRAPH PRESET");
        presetCombo = new ComboBox<>();
        presetCombo.setMaxWidth(Double.MAX_VALUE);
        presetCombo.getStyleClass().add("algo-combo");
        presetCombo.setPromptText("Select a graph...");

        List<GraphPresets.Preset> presets = GraphPresets.all();
        presetCombo.setItems(FXCollections.observableArrayList(
                presets.stream().map(GraphPresets.Preset::name).toList()));
        presetCombo.setOnAction(e -> onPresetSelected());

        Label presetDesc = new Label("");
        presetDesc.getStyleClass().add("algo-preset-desc");
        presetDesc.setWrapText(true);

        sectionBody(presetSection).getChildren().addAll(presetCombo, presetDesc);
        this.presetDescLabel = presetDesc;

        // Algorithm selection
        VBox algoSection = buildSection("ALGORITHM");
        algorithmCombo = new ComboBox<>();
        algorithmCombo.setMaxWidth(Double.MAX_VALUE);
        algorithmCombo.getStyleClass().add("algo-combo");
        algorithmCombo.setItems(FXCollections.observableArrayList("BFS", "DFS"));
        algorithmCombo.getSelectionModel().selectFirst();
        sectionBody(algoSection).getChildren().add(algorithmCombo);

        // Source selection
        VBox sourceSection = buildSection("SOURCE NODE");
        sourceCombo = new ComboBox<>();
        sourceCombo.setMaxWidth(Double.MAX_VALUE);
        sourceCombo.getStyleClass().add("algo-combo");
        sourceCombo.setPromptText("Select source...");
        sectionBody(sourceSection).getChildren().add(sourceCombo);

        // Run / Reset
        VBox actionSection = new VBox(6);
        actionSection.setPadding(new Insets(8, 0, 0, 0));
        runBtn = new Button("Run Algorithm");
        runBtn.getStyleClass().add("primary-button");
        runBtn.setMaxWidth(Double.MAX_VALUE);
        runBtn.setDisable(true);
        runBtn.setOnAction(e -> onRun());

        resetBtn = new Button("Reset");
        resetBtn.getStyleClass().add("secondary-button");
        resetBtn.setMaxWidth(Double.MAX_VALUE);
        resetBtn.setDisable(true);
        resetBtn.setOnAction(e -> onReset());

        actionSection.getChildren().addAll(runBtn, resetBtn);

        controlPanel.getChildren().addAll(presetSection, new Separator(), algoSection,
                new Separator(), sourceSection, new Separator(), actionSection);

        // ── Center workspace ────────────────────────────────
        VBox workspace = new VBox();
        workspace.getStyleClass().add("workspace");
        HBox.setHgrow(workspace, Priority.ALWAYS);

        ScrollPane graphScroll = new ScrollPane(graphPane);
        graphScroll.setFitToWidth(true);
        graphScroll.getStyleClass().add("visual-scroll");
        VBox.setVgrow(graphScroll, Priority.ALWAYS);

        // Playback controls bar
        HBox playbackBar = buildPlaybackBar();

        VBox wsContent = new VBox(0, graphScroll, playbackBar);
        wsContent.getStyleClass().add("algo-workspace-content");
        VBox.setVgrow(wsContent, Priority.ALWAYS);
        workspace.getChildren().add(wsContent);

        // ── Right info panel ────────────────────────────────
        VBox infoPanel = buildInfoPanel();

        return new HBox(controlPanel, workspace, infoPanel);
    }

    // ── UI builders ─────────────────────────────────────────

    private Label presetDescLabel;

    private HBox buildPlaybackBar() {
        HBox bar = new HBox(8);
        bar.getStyleClass().add("algo-playback-bar");
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(10, 16, 10, 16));

        toStartBtn = iconButton("⏮", "Reset to start");
        toStartBtn.setOnAction(e -> onToStart());
        prevBtn = iconButton("◀", "Previous step");
        prevBtn.setOnAction(e -> onPrev());
        playBtn = iconButton("▶", "Play");
        playBtn.setOnAction(e -> onPlay());
        pauseBtn = iconButton("⏸", "Pause");
        pauseBtn.setOnAction(e -> onPause());
        pauseBtn.setVisible(false);
        pauseBtn.setManaged(false);
        nextBtn = iconButton("▶▶", "Next step");
        nextBtn.setText("▶|");
        nextBtn.setOnAction(e -> onNext());
        toEndBtn = iconButton("⏭", "Jump to end");
        toEndBtn.setOnAction(e -> onToEnd());

        frameLabel = new Label("—");
        frameLabel.getStyleClass().add("algo-frame-label");

        Region spacerL = new Region();
        HBox.setHgrow(spacerL, Priority.ALWAYS);
        Region spacerR = new Region();
        HBox.setHgrow(spacerR, Priority.ALWAYS);

        speedSlider = new Slider(0.25, 3.0, 1.0);
        speedSlider.getStyleClass().add("algo-speed-slider");
        speedSlider.setPrefWidth(100);
        speedSlider.setMajorTickUnit(0.5);
        speedSlider.valueProperty().addListener((obs, o, n) -> {
            speedLabel.setText(String.format("%.1fx", n.doubleValue()));
            if (autoPlayTimeline != null && isPlaying) {
                restartAutoPlay();
            }
        });

        speedLabel = new Label("1.0x");
        speedLabel.getStyleClass().add("algo-speed-label");

        bar.getChildren().addAll(
                toStartBtn, prevBtn, playBtn, pauseBtn, nextBtn, toEndBtn,
                spacerL, frameLabel, spacerR,
                new Label("Speed:"), speedSlider, speedLabel);

        setPlaybackDisabled(true);
        return bar;
    }

    private VBox buildInfoPanel() {
        VBox panel = new VBox(8);
        panel.getStyleClass().add("algo-info-panel");
        panel.setPrefWidth(240);
        panel.setMinWidth(240);
        panel.setMaxWidth(240);
        panel.setPadding(new Insets(16, 12, 16, 12));

        VBox stateSection = buildSection("ALGORITHM STATE");
        statusMessageLabel = new Label("No algorithm running");
        statusMessageLabel.getStyleClass().add("algo-status-msg");
        statusMessageLabel.setWrapText(true);
        depthLabel = new Label("");
        depthLabel.getStyleClass().add("algo-info-value");
        discoveryLabel = new Label("");
        discoveryLabel.getStyleClass().add("algo-info-value");
        discoveryLabel.setWrapText(true);
        frontierLabel = new Label("");
        frontierLabel.getStyleClass().add("algo-info-value");
        frontierLabel.setWrapText(true);
        visitedLabel = new Label("");
        visitedLabel.getStyleClass().add("algo-info-value");

        VBox stateBody = sectionBody(stateSection);
        stateBody.getChildren().addAll(
                statusMessageLabel,
                new Separator(),
                styledLabel("DEPTH / LAYER", "algo-info-label"), depthLabel,
                styledLabel("DISCOVERY ORDER", "algo-info-label"), discoveryLabel,
                styledLabel("FRONTIER", "algo-info-label"), frontierLabel,
                styledLabel("VISITED", "algo-info-label"), visitedLabel);

        panel.getChildren().add(stateSection);
        return panel;
    }

    // ── Event handlers ──────────────────────────────────────

    private void onPresetSelected() {
        stopAutoPlay();
        int index = presetCombo.getSelectionModel().getSelectedIndex();
        if (index < 0) return;

        currentPreset = GraphPresets.all().get(index);
        currentGraph = currentPreset.graph();
        presetDescLabel.setText(currentPreset.description());

        // Populate source nodes
        sourceCombo.setItems(FXCollections.observableArrayList(currentGraph.nodes()));
        if (currentGraph.hasNode(currentPreset.suggestedSource())) {
            sourceCombo.getSelectionModel().select(currentPreset.suggestedSource());
        } else if (!currentGraph.nodes().isEmpty()) {
            sourceCombo.getSelectionModel().selectFirst();
        }

        graphPane.setGraph(currentGraph);
        playback.clear();
        runBtn.setDisable(false);
        resetBtn.setDisable(true);
        setPlaybackDisabled(true);
        clearInfoPanel();
        updateFrameLabel();
    }

    private void onRun() {
        if (currentGraph == null) return;
        String source = sourceCombo.getValue();
        if (source == null || source.isEmpty()) return;
        String algo = algorithmCombo.getValue();
        if (algo == null) return;

        stopAutoPlay();

        List<AlgorithmFrame> frames;
        if ("BFS".equals(algo)) {
            frames = BfsRunner.run(currentGraph, source);
        } else {
            frames = DfsRunner.run(currentGraph, source);
        }

        playback.load(frames);
        resetBtn.setDisable(false);
        setPlaybackDisabled(false);
        renderCurrentFrame();
    }

    private void onReset() {
        stopAutoPlay();
        playback.clear();
        graphPane.renderIdle();
        setPlaybackDisabled(true);
        clearInfoPanel();
        updateFrameLabel();
    }

    private void onNext() {
        if (playback.next()) {
            renderCurrentFrame();
        }
    }

    private void onPrev() {
        if (playback.previous()) {
            renderCurrentFrame();
        }
    }

    private void onToStart() {
        stopAutoPlay();
        playback.reset();
        renderCurrentFrame();
    }

    private void onToEnd() {
        stopAutoPlay();
        playback.jumpToEnd();
        renderCurrentFrame();
    }

    private void onPlay() {
        if (!playback.isLoaded()) return;
        isPlaying = true;
        playBtn.setVisible(false);
        playBtn.setManaged(false);
        pauseBtn.setVisible(true);
        pauseBtn.setManaged(true);
        startAutoPlay();
    }

    private void onPause() {
        stopAutoPlay();
    }

    // ── Auto-play ───────────────────────────────────────────

    private void startAutoPlay() {
        double speed = speedSlider.getValue();
        double intervalMs = 800.0 / speed;

        autoPlayTimeline = new Timeline(new KeyFrame(
                Duration.millis(intervalMs), e -> {
            if (!playback.hasNext()) {
                stopAutoPlay();
                return;
            }
            playback.next();
            renderCurrentFrame();
        }));
        autoPlayTimeline.setCycleCount(Timeline.INDEFINITE);
        autoPlayTimeline.play();
    }

    private void stopAutoPlay() {
        isPlaying = false;
        if (autoPlayTimeline != null) {
            autoPlayTimeline.stop();
            autoPlayTimeline = null;
        }
        playBtn.setVisible(true);
        playBtn.setManaged(true);
        pauseBtn.setVisible(false);
        pauseBtn.setManaged(false);
    }

    private void restartAutoPlay() {
        if (autoPlayTimeline != null) {
            autoPlayTimeline.stop();
        }
        startAutoPlay();
    }

    // ── Rendering ───────────────────────────────────────────

    private void renderCurrentFrame() {
        AlgorithmFrame frame = playback.current();
        if (frame == null) return;
        graphPane.renderFrame(frame);
        updateInfoPanel(frame);
        updateFrameLabel();
        updatePlaybackButtons();
    }

    private void updateInfoPanel(AlgorithmFrame frame) {
        statusMessageLabel.setText(frame.statusMessage());
        depthLabel.setText(String.valueOf(frame.depth()));

        List<String> disc = frame.discoveryOrder();
        discoveryLabel.setText(disc.isEmpty() ? "—" : String.join(" → ", disc));

        List<String> front = frame.frontier();
        String frontierType = frame.algorithm() == AlgorithmFrame.AlgorithmType.BFS ? "Queue" : "Stack";
        frontierLabel.setText(front.isEmpty() ? "— (empty " + frontierType + ")"
                : frontierType + ": [" + String.join(", ", front) + "]");

        visitedLabel.setText(frame.visited().size() + " of "
                + (currentGraph != null ? currentGraph.nodeCount() : "?") + " nodes");
    }

    private void clearInfoPanel() {
        statusMessageLabel.setText("No algorithm running");
        depthLabel.setText("");
        discoveryLabel.setText("");
        frontierLabel.setText("");
        visitedLabel.setText("");
    }

    private void updateFrameLabel() {
        if (!playback.isLoaded()) {
            frameLabel.setText("—");
        } else {
            frameLabel.setText((playback.currentIndex() + 1) + " / " + playback.frameCount());
        }
    }

    private void updatePlaybackButtons() {
        prevBtn.setDisable(!playback.hasPrevious());
        nextBtn.setDisable(!playback.hasNext());
        toStartBtn.setDisable(!playback.hasPrevious());
        toEndBtn.setDisable(!playback.hasNext());
    }

    private void setPlaybackDisabled(boolean disabled) {
        playBtn.setDisable(disabled);
        pauseBtn.setDisable(disabled);
        nextBtn.setDisable(disabled);
        prevBtn.setDisable(disabled);
        toStartBtn.setDisable(disabled);
        toEndBtn.setDisable(disabled);
    }

    // ── Helpers ──────────────────────────────────────────────

    private VBox buildSection(String title) {
        VBox section = new VBox(4);
        section.getStyleClass().add("algo-section");
        Label header = new Label(title);
        header.getStyleClass().add("section-header");
        VBox body = new VBox(6);
        body.getStyleClass().add("algo-section-body");
        section.getChildren().addAll(header, body);
        return section;
    }

    private VBox sectionBody(VBox section) {
        return (VBox) section.getChildren().get(1);
    }

    private Button iconButton(String icon, String tooltip) {
        Button btn = new Button(icon);
        btn.getStyleClass().add("algo-playback-btn");
        btn.setTooltip(new Tooltip(tooltip));
        return btn;
    }

    private Label styledLabel(String text, String... classes) {
        Label l = new Label(text);
        l.getStyleClass().addAll(classes);
        return l;
    }
}
