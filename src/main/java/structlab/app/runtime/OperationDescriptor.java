package structlab.app.runtime;

import java.util.List;

public record OperationDescriptor(
    String name,
    String description,
    int argCount,
    String usage,
    boolean mutates
) {}
