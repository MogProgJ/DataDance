package structlab.app.runtime.adapters;

import structlab.app.runtime.OperationDescriptor;
import structlab.app.runtime.OperationExecutionResult;
import structlab.trace.TracedCircularArrayQueue;
import structlab.trace.TracedLinkedQueue;
import structlab.trace.TracedTwoStackQueue;

import java.util.List;

public class QueueRuntimeAdapter extends AbstractRuntimeAdapter {

    private final Object activeQueue; // Holds one of the traced queues

    public QueueRuntimeAdapter(String implName, Object queue) {
        super("Queue", implName);
        this.activeQueue = queue;
    }

    @Override
    public List<OperationDescriptor> getAvailableOperations() {
        return List.of(
                new OperationDescriptor("enqueue", "Enqueue an element to the rear of the queue", 1, "enqueue <value>", true),
                new OperationDescriptor("dequeue", "Dequeue an element from the front of the queue", 0, "dequeue", true),
                new OperationDescriptor("peek", "Look at the front element without removing it", 0, "peek", false)
        );
    }

    @Override
    public OperationExecutionResult execute(String operation, List<String> args) {
        try {
            switch (operation.toLowerCase()) {
                case "enqueue":
                    if (args.isEmpty()) throw new IllegalArgumentException("Usage: enqueue <value>");
                    int val = parseArg(args.get(0));
                    if (activeQueue instanceof TracedCircularArrayQueue tcaq) {
                        tcaq.enqueue(val);
                        return success("enqueue", null, tcaq.traceLog());
                    } else if (activeQueue instanceof TracedLinkedQueue tlq) {
                        tlq.enqueue(val);
                        return success("enqueue", null, tlq.traceLog());
                    } else if (activeQueue instanceof TracedTwoStackQueue ttsq) {
                        ttsq.enqueue(val);
                        return success("enqueue", null, ttsq.traceLog());
                    }
                    break;
                case "dequeue":
                    if (activeQueue instanceof TracedCircularArrayQueue tcaq) {
                        return success("dequeue", tcaq.dequeue(), tcaq.traceLog());
                    } else if (activeQueue instanceof TracedLinkedQueue tlq) {
                        return success("dequeue", tlq.dequeue(), tlq.traceLog());
                    } else if (activeQueue instanceof TracedTwoStackQueue ttsq) {
                        return success("dequeue", ttsq.dequeue(), ttsq.traceLog());
                    }
                    break;
                case "peek":
                    if (activeQueue instanceof TracedCircularArrayQueue tcaq) {
                        return success("peek", tcaq.peek(), tcaq.traceLog());
                    } else if (activeQueue instanceof TracedLinkedQueue tlq) {
                        return success("peek", tlq.peek(), tlq.traceLog());
                    } else if (activeQueue instanceof TracedTwoStackQueue ttsq) {
                        return success("peek", ttsq.peek(), ttsq.traceLog());
                    }
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown queue operation: " + operation);
            }
        } catch (Exception e) {
            return error(operation, e);
        }
        return error(operation, new IllegalStateException("Invalid active queue state"));
    }

    @Override
    public String getCurrentState() {
        if (activeQueue instanceof TracedCircularArrayQueue tcaq) return tcaq.unwrap().snapshot();
        if (activeQueue instanceof TracedLinkedQueue tlq) return tlq.unwrap().snapshot();
        if (activeQueue instanceof TracedTwoStackQueue ttsq) return ttsq.unwrap().snapshot();
        return "[]";
    }

    @Override
    public void reset() {
        if (activeQueue instanceof TracedCircularArrayQueue tcaq) {
            try {
                while (!tcaq.unwrap().isEmpty()) tcaq.unwrap().dequeue();
            } catch(Exception ignored) {}
        }
        if (activeQueue instanceof TracedLinkedQueue tlq) {
             try {
                while (!tlq.unwrap().isEmpty()) tlq.unwrap().dequeue();
             } catch(Exception ignored) {}
        }
        if (activeQueue instanceof TracedTwoStackQueue ttsq) {
             try {
                while (!ttsq.unwrap().isEmpty()) ttsq.unwrap().dequeue();
             } catch(Exception ignored) {}
        }
    }
}
