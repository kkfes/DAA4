Smart City / Smart Campus Scheduling - Assignment 4

What is implemented
- SCC detection using Tarjan's algorithm (graph.scc.TarjanSCC)
- Condensation graph builder (graph.scc.Condensation)
- Topological order on condensation DAG (graph.topo.TopologicalSort via Kahn)
- Shortest and longest paths on DAG (graph.dagsp.DagShortestPaths) - edge-weight model
- Simple CLI runner: reads data/tasks.json and runs the pipeline (graph.cli.Main)
- Dataset files under /data (9 datasets: small/medium/large)
- Basic Metrics class (graph.metrics.Metrics)

Weight model
- I used edge-weight model ("edge") for DAG shortest/longest paths. For node-duration model transform nodes to edges externally or modify DagShortestPaths accordingly.

Data summary
- The repository includes 9 datasets under `data/` (3 small, 3 medium, 3 large). Below is a summary (name, n, m, type):

name, n, m, type
- tasks.json, 8, 7, cyclic
- dataset_small_1.json, 6, 6, cyclic
- dataset_small_2.json, 7, 6, acyclic
- dataset_small_3.json, 10, 10, cyclic
- dataset_medium_1.json, 12, 12, cyclic
- dataset_medium_2.json, 15, 14, acyclic
- dataset_medium_3.json, 18, 17, cyclic
- dataset_large_1.json, 22, 19, mixed
- dataset_large_2.json, 30, 29, acyclic
- dataset_large_3.json, 40, 36, mixed

(Notes: "mixed" indicates presence of both cycles and long acyclic chains.)

Results / Metrics
- The code contains a `Metrics` class (graph.metrics.Metrics) with fields:
  - timeNs: elapsed time in nanoseconds
  - dfsVisits, dfsEdges: counters for SCC DFS
  - kahnOps: counter for Kahn operations (push/pop)
  - relaxations: relaxations in DAG-SP

How to collect results (recommended)
1) Build the project:

```cmd
mvn -DskipTests package
```

2) Run the main runner (it reads `data/tasks.json` by default). For a custom dataset, replace the file or modify `Main` to accept a path.

```cmd
java -cp target\DAA4-1.0-SNAPSHOT.jar graph.cli.Main
```

3) Record the printed outputs and augment with timing/metrics by integrating `Metrics` into the algorithms (I can add this if you want). Use `System.nanoTime()` at start/end of each algorithm phase.

Analysis
- SCC/Condensation/Topo
  - Tarjan's SCC is linear in edges+vertices (O(n+m)). Bottlenecks appear when the graph is dense (m ~ n^2) because DFS visits many edges; memory for recursion/stack is proportional to n, and component sizes may be large which affects subsequent condensation creation.
  - Building the condensation graph requires scanning all edges and mapping endpoints to component ids — linear in m but with overhead for deduplicating edges between components (I used a HashSet of long keys) which adds memory and hashing cost.
  - Topological sort (Kahn) is linear; bottleneck is maintaining indegrees and queue operations — cost increases with graph density but remains O(n+m).

- DAG Shortest/Longest Paths
  - Using topological order gives O(n+m) relaxation complexity. For edge-weight model, each edge relaxed once. For node-duration model, a transformation increases graph size (split nodes) and may double number of vertices/edges.
  - Longest path is computed by max-DP in topo order; same complexity as shortest. Numerical range: use long for durations/weights; detect unreachable nodes (INF) and negative cycles (not possible in DAG).

Effect of structure
- Density: dense graphs increase m and thus runtime linearly in m; SCC detection and condensation construction feel the biggest impact from density.
- SCC sizes: large SCCs compress to fewer nodes in condensation, reducing DAG size; but forming those SCCs requires exploring many internal edges. If SCCs are huge, the condensation DAG may be small and DAG-SP runs faster.
- Mixed graphs: if many small SCCs connected by many inter-component edges, condensation might still be large and relatively dense.

Conclusions & practical recommendations
- Use Tarjan (or Kosaraju) for SCC detection depending on memory model; Tarjan is single-pass and memory-frugal.
- For scheduling with task durations, prefer edge-weight model if durations are associated with transitions; for node durations, split nodes or adapt DP carefully.
- For single-source path planning on compressed DAG, always compress SCCs first to avoid cycles and then run topo-based DP for shortest/longest.
- Instrumentation: measure both operation counts and wall-clock time; analyze effects of density and SCC size separately.
