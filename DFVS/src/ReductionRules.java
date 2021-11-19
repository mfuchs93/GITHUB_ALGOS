import java.util.ArrayList;
import java.util.HashSet;

public class ReductionRules {
    private static HashSet<Vertex> empty = new HashSet<>();


    public static void removeNoneCycleVertex(Graph g) {
        for (Vertex v : g.getVertices()) {
            ArrayList<Vertex> cycle = new Cycle(g, v, SearchType.CONTAINS_VERTEX).cycle();
            if (cycle.isEmpty()) {
                g.removeVertex(v, false, false);
            }
        }
    }

    public static void removeNoneCycleEdge(Graph g) {

    }

    public static HashSet<Vertex> chainingRule(Graph g) {
        int removed = 0;
        for (Vertex v : g.getVertices()) {
            if(v.isForbidden()) break; // return or break??
            if (g.getInEdges().getOrDefault(v, empty).size() == 1 && !g.getInEdges().get(v).contains(v)) {
                Vertex u = g.getInEdges().get(v).iterator().next();
                for (Vertex w : g.getOutEdges().getOrDefault(v, empty)) {
                    g.getOutEdges().get(u).add(w);
                    g.getInEdges().get(w).add(u);
                }
                g.removeVertex(v, false, false);
                removed++;
            } else if (g.getOutEdges().getOrDefault(v, empty).size() == 1 && !g.getOutEdges().get(v).contains(v)) {
                Vertex w = g.getOutEdges().get(v).iterator().next();
                for (Vertex u : g.getInEdges().getOrDefault(v, empty)) {
                    g.getOutEdges().get(u).add(w);
                    g.getInEdges().get(w).add(u);
                }
                g.removeVertex(v, false, false);
                removed++;
            }
        }
        return removed > 0? chainingClean(g): empty;
    }

    public static HashSet<Vertex> chainingClean(Graph g) {
        HashSet<Vertex> s = new HashSet<>();
        for (Vertex v : g.getVertices()) {
            if (g.getInEdges().getOrDefault(v, empty).contains(v) && !v.isForbidden()) { //do not delete forbidden nodes
                g.removeVertex(v, false, false);
                s.add(v);
            }
        }
        return s;
    }
}
