import java.util.ArrayList;
import java.util.HashSet;

public class ReductionRules {
    private static HashSet<Vertex> empty = new HashSet<>();


    public static Graph removeNoneCycleVertex(Graph g) {
        Graph h = new Graph(g);
        for (Vertex v : h.getVertices()) {
            ArrayList<Vertex> cycle = new Cycle(h, v, SearchType.CONTAINS_VERTEX).cycle();
            if (cycle.isEmpty()) {
                h.removeVertex(v, false, false);
                //System.out.println("deleted Vertex " + v);
            }
        }
        return h;
    }

    public static void removeNoneCycleEdge(Graph g) {

    }

    public static void chainingRule(Graph g) {
        for (Vertex v : g.getVertices()) {
            if(v.isForbidden()) return;
            if (g.getInEdges().getOrDefault(v, empty).size() == 1 && !g.getInEdges().get(v).contains(v)) {
                Vertex u = g.getInEdges().get(v).iterator().next();
                for (Vertex w : g.getOutEdges().getOrDefault(v, empty)) {
                    g.getOutEdges().get(u).add(w);
                    g.getInEdges().get(w).add(u);
                }
                g.removeVertex(v, false, false);
                //System.out.println("Chaindelete " + v);
            } else if (g.getOutEdges().getOrDefault(v, empty).size() == 1 && !g.getOutEdges().get(v).contains(v)) {
                Vertex w = g.getOutEdges().get(v).iterator().next();
                for (Vertex u : g.getInEdges().getOrDefault(v, empty)) {
                    g.getOutEdges().get(u).add(w);
                    g.getInEdges().get(w).add(u);
                }
                //System.out.println("Chaindelete " + v);
                g.removeVertex(v, false, false);
            }
        }

    }

    public static HashSet<Vertex> chainingClean(Graph g) {
        HashSet<Vertex> s = new HashSet<>();
        for (Vertex v : g.getVertices()) {
            if (g.getInEdges().getOrDefault(v, empty).contains(v) && !v.isForbidden()) { //do not delete forbidden nodes
                g.removeVertex(v, false, false);
                s.add(v);
                //System.out.println("ChainClean! " + v);
            }
        }
        return s;
    }
}
