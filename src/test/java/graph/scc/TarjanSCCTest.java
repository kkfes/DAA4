package graph.scc;

import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class TarjanSCCTest {
    @Test
    public void testSimpleSCC() {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < 4; i++) adj.add(new ArrayList<>());
        adj.get(0).add(1);
        adj.get(1).add(2);
        adj.get(2).add(0);
        adj.get(2).add(3);
        TarjanSCC scc = new TarjanSCC(adj);
        List<List<Integer>> comps = scc.getComponents();
        // expect one SCC of {0,1,2} and one {3}
        assertEquals(2, comps.size());
        boolean found3 = false;
        boolean found012 = false;
        for (List<Integer> c : comps) {
            if (c.size() == 1 && c.contains(3)) found3 = true;
            if (c.size() == 3 && c.containsAll(Arrays.asList(0,1,2))) found012 = true;
        }
        assertTrue(found3 && found012);
    }
}

