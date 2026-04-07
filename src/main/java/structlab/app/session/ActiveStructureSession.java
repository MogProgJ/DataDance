package structlab.app.session;

import structlab.app.runtime.StructureRuntime;
import structlab.app.runtime.OperationExecutionResult;

import java.util.ArrayList;
import java.util.List;

public class ActiveStructureSession implements StructureSession {

    private final String structureId;
    private final String implementationId;
    private final StructureRuntime runtime;
    private final List<OperationExecutionResult> history;

    public ActiveStructureSession(String structureId, String implementationId, StructureRuntime runtime) {
        this.structureId = structureId;
        this.implementationId = implementationId;
        this.runtime = runtime;
        this.history = new ArrayList<>();
    }

    @Override
    public String getStructureId() {
        return structureId;
    }

    @Override
    public String getImplementationId() {
        return implementationId;
    }

    public StructureRuntime getRuntime() {
        return runtime;
    }

    public void addHistory(OperationExecutionResult result) {
        history.add(result);
    }

    public List<OperationExecutionResult> getHistory() {
        return new ArrayList<>(history);
    }

    @Override
    public void close() {
        // Run cleanup if needed (Phase 5 prep)
    }
}
