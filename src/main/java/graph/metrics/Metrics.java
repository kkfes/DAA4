package graph.metrics;

/**
 * Simple metrics collector.
 */
public class Metrics {
    public long timeNs = 0;
    public long dfsVisits = 0;
    public long dfsEdges = 0;
    public long kahnOps = 0;
    public long relaxations = 0;

    public void reset() {
        timeNs = 0; dfsVisits = 0; dfsEdges = 0; kahnOps = 0; relaxations = 0;
    }
}

