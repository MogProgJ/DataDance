package structlab.app.ui;

import structlab.app.session.SessionManager;
import structlab.app.session.StructureSession;
import java.util.Optional;

public class PromptBuilder {

    public static String build(SessionManager sessionManager) {
        Optional<StructureSession> session = sessionManager.getActiveSession();

        if (session.isEmpty()) {
            // Default prompt
            return TerminalTheme.BOLD + TerminalTheme.MAGENTA + "structlab" + TerminalTheme.RESET + "> ";
        } else {
            // Context aware prompt mapping to the active simulator session
            // e.g., structlab[stack-impl]>
            String impl = session.get().getImplementationId().replace("impl-", "");
            return TerminalTheme.BOLD + TerminalTheme.MAGENTA + "structlab"
                 + TerminalTheme.GRAY + "[" + TerminalTheme.CYAN + impl + TerminalTheme.GRAY + "]"
                 + TerminalTheme.RESET + "> ";
        }
    }
}
