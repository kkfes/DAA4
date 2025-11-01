package graph.scc;

import java.util.*;

/**
 * Tarjan's algorithm for Strongly Connected Components (SCC).
 */
public class TarjanSCC {
    private final List<List<Integer>> adj;
    private final int n;

    // results
    private final int[] compId; // component id for each vertex
    private final List<List<Integer>> components = new ArrayList<>();

    // internal
    private int time = 0;
    private final int[] disc;
    private final int[] low;
    private final boolean[] inStack;
    private final Deque<Integer> stack = new ArrayDeque<>();

    public TarjanSCC(List<List<Integer>> adj) {
        this.adj = adj;
        this.n = adj.size();
        this.compId = new int[n];
        Arrays.fill(compId, -1);
        this.disc = new int[n];
        Arrays.fill(disc, -1);
        this.low = new int[n];
        this.inStack = new boolean[n];
        run();
    }

    private void run() {
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) dfs(i);
        }
    }

    private void dfs(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        inStack[u] = true;
        for (int v : adj.get(u)) {
            if (disc[v] == -1) {
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (inStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
        if (low[u] == disc[u]) {
            List<Integer> comp = new ArrayList<>();
            while (true) {
                int w = stack.pop();
                inStack[w] = false;
                compId[w] = components.size();
                comp.add(w);
                if (w == u) break;
            }
            components.add(comp);
        }
    }

    public List<List<Integer>> getComponents() {
        return components;
    }

    public int[] getComponentIds() {
        return compId;
    }
}

