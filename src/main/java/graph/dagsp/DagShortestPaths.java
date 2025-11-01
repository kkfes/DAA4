package graph.dagsp;

import java.util.*;

/**
 * Shortest and longest paths on DAGs.
 * Assumes edge-weighted DAG. For node-duration model, transform nodes to edges externally.
 */
public class DagShortestPaths {
    private final List<List<Edge>> adj;

    public static class Edge {
        public final int to;
        public final long w;
        public Edge(int to, long w) { this.to = to; this.w = w; }
    }

    public DagShortestPaths(List<List<Edge>> adj) {
        this.adj = adj;
    }

    /**
     * Single-source shortest paths on DAG using topological order.
     * Returns distances and parent pointers.
     */
    public Result shortestFrom(int src, List<Integer> topoOrder) {
        int n = adj.size();
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MAX_VALUE / 4);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        dist[src] = 0;
        boolean started = false;
        for (int u : topoOrder) {
            if (u == src) started = true;
            if (!started && dist[u] == Long.MAX_VALUE / 4) continue;
            if (dist[u] >= Long.MAX_VALUE / 4) continue;
            for (Edge e : adj.get(u)) {
                if (dist[u] + e.w < dist[e.to]) {
                    dist[e.to] = dist[u] + e.w;
                    parent[e.to] = u;
                }
            }
        }
        return new Result(dist, parent);
    }

    /**
     * Longest path on DAG (critical path) - returns distances and parents.
     */
    public Result longestFrom(int src, List<Integer> topoOrder) {
        int n = adj.size();
        long[] dist = new long[n];
        Arrays.fill(dist, Long.MIN_VALUE / 4);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        dist[src] = 0;
        boolean started = false;
        for (int u : topoOrder) {
            if (u == src) started = true;
            if (!started && dist[u] == Long.MIN_VALUE / 4) continue;
            if (dist[u] <= Long.MIN_VALUE / 4) continue;
            for (Edge e : adj.get(u)) {
                if (dist[u] + e.w > dist[e.to]) {
                    dist[e.to] = dist[u] + e.w;
                    parent[e.to] = u;
                }
            }
        }
        return new Result(dist, parent);
    }

    public static class Result {
        public final long[] dist;
        public final int[] parent;
        public Result(long[] dist, int[] parent) { this.dist = dist; this.parent = parent; }

        public List<Integer> reconstructPath(int target) {
            if (dist[target] == Long.MAX_VALUE / 4 || dist[target] == Long.MIN_VALUE / 4) return Collections.emptyList();
            List<Integer> path = new ArrayList<>();
            for (int v = target; v != -1; v = parent[v]) path.add(v);
            Collections.reverse(path);
            return path;
        }
    }
}

