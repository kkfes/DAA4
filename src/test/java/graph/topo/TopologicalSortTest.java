package graph.topo;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TopologicalSortTest {
    @Test
    public void testKahnSimple() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(2);
        TopologicalSort ts = new TopologicalSort(adj);
        List<Integer> order = ts.kahn();
        assertEquals(Arrays.asList(0,1,2), order);
    }
}

