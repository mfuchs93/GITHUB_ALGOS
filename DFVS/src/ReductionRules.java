import java.util.ArrayList;

public class ReductionRules {


    public static void removeNoneCycleVertex(Graph g) {
        for (Vertex v : g.getVertices()) {
            ArrayList<Vertex> cycle = new Cycle(g, v).cycle();
            if (!cycle.isEmpty()) {
                g.removeVertex(v);
            }
        }

    }

    public static void removeNoneCycleEdge(Graph g) {

    }

    public static void chainingRule(Graph g) {
        for (Vertex v : g.getVertices()) {
            if (g.getInEdges().get(v).size() == 1) {
                Vertex u = g.getInEdges().get(v).iterator().next();
                for (Vertex w : g.getOutEdges().get(v)) {
                    g.getOutEdges().get(u).add(w);
                    g.getInEdges().get(w).add(u);
                }
                g.removeVertex(v);
            } else if (g.getOutEdges().get(v).size() == 1) {
                Vertex w = g.getOutEdges().get(v).iterator().next();
                for (Vertex u : g.getInEdges().get(v)) {
                    g.getOutEdges().get(u).add(w);
                    g.getInEdges().get(w).add(u);
                    g.removeVertex(v);
                }
            }
        }

    }

    public static int chainingClean(Graph g, int k) {
        for (Vertex v : g.getVertices()) {
            if (g.getInEdges().get(v).contains(v)) {
                g.removeVertex(v);
                k--;
            }
        }
        return k;
    }
}
