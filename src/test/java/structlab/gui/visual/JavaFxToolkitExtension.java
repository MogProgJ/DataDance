package structlab.gui.visual;

import javafx.application.Platform;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * JUnit 5 extension that initialises the JavaFX toolkit once for
 * all test classes that need it.  Uses the global store so the
 * toolkit is started at most once per JVM, regardless of how many
 * test classes carry {@code @ExtendWith(JavaFxToolkitExtension.class)}.
 *
 * <p>On headless CI (no display) the {@link UnsupportedOperationException}
 * thrown by {@link Platform#startup} is caught silently so tests that
 * only exercise model / layout logic can still run.</p>
 */
public class JavaFxToolkitExtension implements BeforeAllCallback {

    private static final ExtensionContext.Namespace NS =
            ExtensionContext.Namespace.create(JavaFxToolkitExtension.class);

    @Override
    public void beforeAll(ExtensionContext context) {
        // Use the root store so the toolkit is only started once per JVM.
        ExtensionContext.Store store = context.getRoot().getStore(NS);
        store.getOrComputeIfAbsent("toolkit-init", key -> {
            try {
                Platform.startup(() -> {});
            } catch (IllegalStateException | UnsupportedOperationException ignored) {
                // Already initialised, or running headless — both fine.
            }
            return true;
        }, Boolean.class);
    }
}
