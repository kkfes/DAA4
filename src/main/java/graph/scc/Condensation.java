package graph.scc;

import java.util.*;

/**
 * Builds condensation graph (DAG) from original graph and component ids.
 */
public class Condensation {
    private final List<List<Integer>> dagAdj;
    private final int comps;

    public Condensation(List<List<Integer>> adj, int[] compId) {
        this.comps = Arrays.stream(compId).max().orElse(-1) + 1;
        this.dagAdj = new ArrayList<>();
        for (int i = 0; i < comps; i++) dagAdj.add(new ArrayList<>());
        build(adj, compId);
    }

    private void build(List<List<Integer>> adj, int[] compId) {
        int n = adj.size();
        Set<Long> seen = new HashSet<>();
        for (int u = 0; u < n; u++) {
            for (int v : adj.get(u)) {
                int cu = compId[u], cv = compId[v];
                if (cu != cv) {
                    long key = ((long)cu << 32) | (cv & 0xffffffffL);
                    if (!seen.contains(key)) {
                        dagAdj.get(cu).add(cv);
                        seen.add(key);
                    }
                }
            }
        }
    }

    public List<List<Integer>> getDagAdj() {
        return dagAdj;
    }

    public int components() { return comps; }
}

