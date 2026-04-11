package structlab.core.graph;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphPresetsTest {

    @Test
    void allPresetsNonNull() {
        List<GraphPresets.Preset> all = GraphPresets.all();
        assertNotNull(all);
        assertFalse(all.isEmpty());
    }

    @Test
    void allPresetsHaveValidGraph() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            assertNotNull(p.graph(), "Preset '" + p.name() + "' has null graph");
            assertTrue(p.graph().nodeCount() > 0, "Preset '" + p.name() + "' has no nodes");
        }
    }

    @Test
    void allPresetsHaveValidSource() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            String src = p.suggestedSource();
            assertNotNull(src, "Preset '" + p.name() + "' has null suggestedSource");
            assertTrue(p.graph().neighbors(src) != null || p.graph().nodeCount() > 0,
                    "Preset '" + p.name() + "': suggestedSource '" + src + "' not in graph");
        }
    }

    @Test
    void allPresetsHaveNameAndDescription() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            assertNotNull(p.name());
            assertFalse(p.name().isBlank(), "Preset has blank name");
            assertNotNull(p.description());
            assertFalse(p.description().isBlank(), "Preset '" + p.name() + "' has blank description");
        }
    }

    @Test
    void presetsListIsUnmodifiable() {
        List<GraphPresets.Preset> all = GraphPresets.all();
        assertThrows(UnsupportedOperationException.class, () -> all.add(null));
    }

    @Test
    void eightPresetsExist() {
        assertEquals(8, GraphPresets.all().size());
    }

    @Test
    void bfsRunsOnAllPresets() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            List<AlgorithmFrame> frames = BfsRunner.run(p.graph(), p.suggestedSource());
            assertFalse(frames.isEmpty(), "BFS produced no frames for preset '" + p.name() + "'");
        }
    }

    @Test
    void dfsRunsOnAllPresets() {
        for (GraphPresets.Preset p : GraphPresets.all()) {
            List<AlgorithmFrame> frames = DfsRunner.run(p.graph(), p.suggestedSource());
            assertFalse(frames.isEmpty(), "DFS produced no frames for preset '" + p.name() + "'");
        }
    }
}
