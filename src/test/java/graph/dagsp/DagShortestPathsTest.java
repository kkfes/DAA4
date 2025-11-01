package graph.dagsp;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class DagShortestPathsTest {
    @Test
    public void testShortestAndLongest() {
        // build simple DAG: 0 -> 1 (w=2), 0 -> 2 (w=5), 1 -> 2 (w=1)
        List<List<DagShortestPaths.Edge>> adj = new ArrayList<>();
        for (int i = 0; i < 3; i++) adj.add(new ArrayList<>());
        adj.get(0).add(new DagShortestPaths.Edge(1, 2));
        adj.get(0).add(new DagShortestPaths.Edge(2, 5));
        adj.get(1).add(new DagShortestPaths.Edge(2, 1));

        DagShortestPaths dsp = new DagShortestPaths(adj);
        // topological order can be [0,1,2]
        List<Integer> topo = Arrays.asList(0,1,2);
        DagShortestPaths.Result sres = dsp.shortestFrom(0, topo);
        assertEquals(0L, sres.dist[0]);
        assertEquals(2L, sres.dist[1]);
        assertEquals(3L, sres.dist[2]); // via 0->1->2

        DagShortestPaths.Result lres = dsp.longestFrom(0, topo);
        assertEquals(0L, lres.dist[0]);
        assertEquals(2L, lres.dist[1]);
        assertEquals(5L, lres.dist[2]); // via 0->2 direct is longest (5) vs via 1 is 3

        List<Integer> pathTo2 = lres.reconstructPath(2);
        assertTrue(pathTo2.size() >= 2);
        assertEquals(Integer.valueOf(0), pathTo2.get(0));
    }
}

