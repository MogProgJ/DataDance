package structlab.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.prefs.Preferences;

public class AppSettings {

    private static final String PREF_MOTION = "motionEnabled";
    private static final String PREF_COMPACT = "compactMode";
    private static final String PREF_RAW_TRACES = "showRawTraces";
    private static final String PREF_HIGH_DENSITY = "highDensity";

    private final Preferences prefs = Preferences.userNodeForPackage(AppSettings.class);

    private final BooleanProperty motionEnabled = new SimpleBooleanProperty(prefs.getBoolean(PREF_MOTION, true));
    private final BooleanProperty compactMode = new SimpleBooleanProperty(prefs.getBoolean(PREF_COMPACT, false));
    private final BooleanProperty showRawTraces = new SimpleBooleanProperty(prefs.getBoolean(PREF_RAW_TRACES, true));
    private final BooleanProperty highDensity = new SimpleBooleanProperty(prefs.getBoolean(PREF_HIGH_DENSITY, false));

    public AppSettings() {
        motionEnabled.addListener((obs, o, n) -> prefs.putBoolean(PREF_MOTION, n));
        compactMode.addListener((obs, o, n) -> prefs.putBoolean(PREF_COMPACT, n));
        showRawTraces.addListener((obs, o, n) -> prefs.putBoolean(PREF_RAW_TRACES, n));
        highDensity.addListener((obs, o, n) -> prefs.putBoolean(PREF_HIGH_DENSITY, n));
    }

    public BooleanProperty motionEnabledProperty() { return motionEnabled; }
    public boolean isMotionEnabled() { return motionEnabled.get(); }
    public void setMotionEnabled(boolean v) { motionEnabled.set(v); }

    public BooleanProperty compactModeProperty() { return compactMode; }
    public boolean isCompactMode() { return compactMode.get(); }
    public void setCompactMode(boolean v) { compactMode.set(v); }

    public BooleanProperty showRawTracesProperty() { return showRawTraces; }
    public boolean isShowRawTraces() { return showRawTraces.get(); }
    public void setShowRawTraces(boolean v) { showRawTraces.set(v); }

    public BooleanProperty highDensityProperty() { return highDensity; }
    public boolean isHighDensity() { return highDensity.get(); }
    public void setHighDensity(boolean v) { highDensity.set(v); }
}
