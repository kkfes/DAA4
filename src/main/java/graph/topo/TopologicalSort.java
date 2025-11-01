package graph.topo;

import java.util.*;

/**
 * Topological sort (Kahn's algorithm) for DAGs.
 */
public class TopologicalSort {
    private final List<List<Integer>> adj;

    public TopologicalSort(List<List<Integer>> adj) {
        this.adj = adj;
    }

    public List<Integer> kahn() {
        int n = adj.size();
        int[] indeg = new int[n];
        for (int u = 0; u < n; u++) for (int v : adj.get(u)) indeg[v]++;
        Deque<Integer> q = new ArrayDeque<>();
        for (int i = 0; i < n; i++) if (indeg[i] == 0) q.add(i);
        List<Integer> order = new ArrayList<>();
        while (!q.isEmpty()) {
            int u = q.removeFirst();
            order.add(u);
            for (int v : adj.get(u)) {
                indeg[v]--;
                if (indeg[v] == 0) q.add(v);
            }
        }
        if (order.size() != n) throw new IllegalArgumentException("Graph has cycles");
        return order;
    }
}

