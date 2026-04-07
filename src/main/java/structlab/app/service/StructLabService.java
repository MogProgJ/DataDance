package structlab.app.service;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.app.runtime.RuntimeFactory;
import structlab.app.runtime.StructureRuntime;
import structlab.app.session.ActiveStructureSession;
import structlab.registry.ImplementationMetadata;
import structlab.registry.InMemoryStructureRegistry;
import structlab.registry.RegistrySeeder;
import structlab.registry.StructureMetadata;
import structlab.registry.StructureRegistry;
import structlab.trace.TraceStep;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Facade layer for GUI and programmatic consumers.
 * Wraps the registry, session, and runtime model into clean method calls
 * without depending on shell command parsing.
 */
public class StructLabService {

    private final StructureRegistry registry;
    private ActiveStructureSession activeSession;

    public StructLabService(StructureRegistry registry) {
        this.registry = registry;
    }

    public static StructLabService createDefault() {
        InMemoryStructureRegistry registry = new InMemoryStructureRegistry();
        RegistrySeeder.seed(registry);
        return new StructLabService(registry);
    }

    // ── Discovery ──────────────────────────────────────────────

    public List<StructureSummary> getAllStructures() {
        return registry.getAllStructures().stream()
                .map(m -> new StructureSummary(m.id(), m.name(), m.category(), m.keywords(), m.description()))
                .toList();
    }

    public Optional<StructureSummary> getStructure(String structureId) {
        String id = normalizeStructureId(structureId);
        return registry.getStructureById(id)
                .map(m -> new StructureSummary(m.id(), m.name(), m.category(), m.keywords(), m.description()));
    }

    public List<ImplementationSummary> getImplementations(String structureId) {
        String id = normalizeStructureId(structureId);
        return registry.getImplementationsFor(id).stream()
                .map(im -> new ImplementationSummary(
                        im.id(), im.name(), im.parentStructureId(),
                        im.description(), im.timeComplexity(), im.spaceComplexity()))
                .toList();
    }

    // ── Session lifecycle ──────────────────────────────────────

    public boolean hasActiveSession() {
        return activeSession != null;
    }

    public SessionSnapshot openSession(String structureId, String implementationId) {
        String sId = normalizeStructureId(structureId);
        String iId = normalizeImplementationId(implementationId);

        Optional<StructureMetadata> smOpt = registry.getStructureById(sId);
        if (smOpt.isEmpty()) {
            throw new IllegalArgumentException("Structure not found: " + sId);
        }

        List<ImplementationMetadata> impls = registry.getImplementationsFor(sId);
        Optional<ImplementationMetadata> imOpt = impls.stream()
                .filter(im -> im.id().equals(iId))
                .findFirst();
        if (imOpt.isEmpty()) {
            throw new IllegalArgumentException("Implementation not found: " + iId);
        }

        StructureRuntime runtime = RuntimeFactory.createRuntime(smOpt.get(), imOpt.get());
        activeSession = new ActiveStructureSession(sId, iId, runtime);
        return getSessionSnapshot().orElseThrow();
    }

    public void closeSession() {
        if (activeSession != null) {
            activeSession.close();
            activeSession = null;
        }
    }

    public Optional<SessionSnapshot> getSessionSnapshot() {
        if (activeSession == null) return Optional.empty();
        return Optional.of(new SessionSnapshot(
                activeSession.getStructureId(),
                activeSession.getImplementationId(),
                activeSession.getRuntime().getStructureName(),
                activeSession.getRuntime().getImplementationName(),
                activeSession.historySize()
        ));
    }

    // ── Operations ─────────────────────────────────────────────

    public List<OperationInfo> getAvailableOperations() {
        requireSession();
        return activeSession.getRuntime().getAvailableOperations().stream()
                .map(o -> new OperationInfo(
                        o.name(), o.aliases(), o.description(),
                        o.argCount(), o.usage(), o.mutates(), o.complexityNote()))
                .toList();
    }

    public ExecutionResult executeOperation(String operation, List<String> args) {
        requireSession();
        OperationExecutionResult result = activeSession.getRuntime().execute(operation, args);
        activeSession.addHistory(result);
        return toExecutionResult(result);
    }

    // ── State and history ──────────────────────────────────────

    public String getRenderedState() {
        requireSession();
        return activeSession.getRuntime().renderCurrentState();
    }

    public List<ExecutionResult> getHistory() {
        requireSession();
        return activeSession.getHistory().stream()
                .map(this::toExecutionResult)
                .toList();
    }

    public Optional<ExecutionResult> getLastResult() {
        requireSession();
        return activeSession.getLastResult().map(this::toExecutionResult);
    }

    public List<TraceStep> getLastTraceSteps() {
        requireSession();
        return activeSession.getLastResult()
                .filter(r -> r.traceSteps() != null)
                .map(r -> List.copyOf(r.traceSteps()))
                .orElse(List.of());
    }

    public String getLastTraceRendered() {
        requireSession();
        List<TraceStep> steps = getLastTraceSteps();
        if (steps.isEmpty()) return "No trace steps available.";
        StringBuilder sb = new StringBuilder();
        for (TraceStep step : steps) {
            sb.append(structlab.render.ConsoleTraceRenderer.render(step)).append("\n");
        }
        return sb.toString().trim();
    }

    // ── Reset ──────────────────────────────────────────────────

    public void resetSession() {
        requireSession();
        activeSession.getRuntime().reset();
        activeSession.clearHistory();
    }

    // ── Internal ───────────────────────────────────────────────

    private void requireSession() {
        if (activeSession == null) {
            throw new IllegalStateException("No active session. Open a session first.");
        }
    }

    private ExecutionResult toExecutionResult(OperationExecutionResult r) {
        return new ExecutionResult(
                r.success(), r.operationName(), r.message(),
                r.returnedValue(), r.traceSteps() != null ? List.copyOf(r.traceSteps()) : List.of());
    }

    private String normalizeStructureId(String id) {
        return id.startsWith("struct-") ? id : "struct-" + id;
    }

    private String normalizeImplementationId(String id) {
        return id.startsWith("impl-") ? id : "impl-" + id;
    }
}
