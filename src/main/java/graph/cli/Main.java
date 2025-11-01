package graph.cli;

import graph.scc.TarjanSCC;
import graph.scc.Condensation;
import graph.topo.TopologicalSort;
import graph.dagsp.DagShortestPaths;
import graph.dagsp.DagShortestPaths.Edge;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.fasterxml.jackson.databind.*;

/**
 * Simple runner: reads tasks.json, runs SCC, builds condensation DAG, topological order,
 * computes shortest and longest paths on condensation DAG (edge-weight model).
 */
public class Main {
    static class InputEdge { public int u; public int v; public long w; }
    static class Input {
        public boolean directed;
        public int n;
        public List<InputEdge> edges;
        public int source;
        public String weight_model;
    }

    public static void main(String[] args) throws Exception {
        Path path = Paths.get("data/tasks.json");
        if (!Files.exists(path)) {
            System.err.println("data/tasks.json not found. Put input in data/tasks.json or pass path as arg.");
            return;
        }
        ObjectMapper om = new ObjectMapper();
        Input in = om.readValue(path.toFile(), Input.class);
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < in.n; i++) adj.add(new ArrayList<>());
        for (InputEdge e : in.edges) adj.get(e.u).add(e.v);

        System.out.println("Running Tarjan SCC...");
        TarjanSCC scc = new TarjanSCC(adj);
        List<List<Integer>> comps = scc.getComponents();
        System.out.println("Found " + comps.size() + " components");
        for (int i = 0; i < comps.size(); i++) {
            System.out.println("C" + i + ": " + comps.get(i));
        }

        System.out.println("Building condensation DAG...");
        Condensation cond = new Condensation(adj, scc.getComponentIds());
        List<List<Integer>> dag = cond.getDagAdj();

        System.out.println("Topological order of components (Kahn):");
        TopologicalSort topo = new TopologicalSort(dag);
        List<Integer> order = topo.kahn();
        System.out.println(order);

        // Build weighted DAG for condensation using minimum edge weight between components
        int m = dag.size();
        List<List<Edge>> wadj = new ArrayList<>();
        for (int i = 0; i < m; i++) wadj.add(new ArrayList<>());
        Map<Long, Long> best = new HashMap<>();
        for (InputEdge e : in.edges) {
            int cu = scc.getComponentIds()[e.u];
            int cv = scc.getComponentIds()[e.v];
            if (cu == cv) continue;
            long key = ((long)cu << 32) | (cv & 0xffffffffL);
            long w = e.w;
            best.put(key, Math.min(best.getOrDefault(key, Long.MAX_VALUE / 4), w));
        }
        for (Map.Entry<Long, Long> en : best.entrySet()) {
            int cu = (int)(en.getKey() >> 32);
            int cv = (int)(en.getKey().longValue());
            wadj.get(cu).add(new Edge(cv, en.getValue()));
        }

        DagShortestPaths dsp = new DagShortestPaths(wadj);
        int srcComp = scc.getComponentIds()[in.source];
        System.out.println("Source component: "+srcComp);
        DagShortestPaths.Result shortest = dsp.shortestFrom(srcComp, order);
        System.out.println("Shortest distances from component " + srcComp + ":");
        for (int i = 0; i < shortest.dist.length; i++) System.out.println(i + ": " + shortest.dist[i]);

        DagShortestPaths.Result longest = dsp.longestFrom(srcComp, order);
        System.out.println("Longest distances from component " + srcComp + ":");
        for (int i = 0; i < longest.dist.length; i++) System.out.println(i + ": " + longest.dist[i]);

        // reconstruct longest path to the furthest node
        long bestDist = Long.MIN_VALUE;
        int bestNode = -1;
        for (int i = 0; i < longest.dist.length; i++) if (longest.dist[i] > bestDist) { bestDist = longest.dist[i]; bestNode = i; }
        System.out.println("Critical path length: " + bestDist);
        System.out.println("Critical path (components): " + longest.reconstructPath(bestNode));

        // Map back to original nodes: just show representative nodes in each component
        System.out.println("Derived order of original tasks after SCC compression:");
        for (int comp : order) {
            System.out.println(comp + ": " + comps.get(comp));
        }
    }
}

