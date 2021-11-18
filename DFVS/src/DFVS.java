import java.util.ArrayList;
import java.util.HashSet;

public class DFVS {

    public static HashSet<Vertex> branch(Graph g, int k) {
        if (Thread.interrupted()) {
            System.out.println("Timeout");
            System.exit(1);
        }
        HashSet<Vertex> solution = new HashSet<>();
        if (k % 2 == 0) {
                g = ReductionRules.removeNoneCycleVertex(g);
            }
        ReductionRules.chainingRule(g);
        HashSet<Vertex> s = new HashSet<>();
        HashSet<Vertex> chainingCleanSet = ReductionRules.chainingClean(g);
        solution.addAll(chainingCleanSet);
        k -= chainingCleanSet.size();
        if (k < 0) return null;
        HashSet<Graph> subGraphs = new Tarjan(g).SCC();
        if (subGraphs.isEmpty()) return s; //maybe return solution ??
        if (subGraphs.size() > k) return null;
        int counter = 1; // wieviele wurden gelöscht
        int restK = k; // wieviele können wir in den restlichen SubGraphen noch löschen
            for (Graph subGraph :
                    subGraphs) {
                ArrayList<Vertex> cycle = new Cycle(subGraph, SearchType.DFS).cycle();
                cycle.removeIf(Vertex::isForbidden);
                if (cycle.isEmpty()) return null; // wenn alle verboten return
                while (restK - counter >= 0) {
                    for (Vertex vertex : cycle) {
                        if (!vertex.isForbidden()) { //nur löschen wenn nicht verboten
                            s = branch(subGraph.removeVertex(vertex, true, true), counter - 1);
                            if (s != null) {
                                s.add(vertex);
                                break;
                            }
                        }
                    }
                    cycle.forEach(v -> v.setForbidden(false));
                    if (s != null) {
                        restK -= counter;
                        counter = 1;
                        break;
                    } else {
                        counter++;
                    }
                }
                if (s == null) {
                    return null;
                } else {
                    solution.addAll(s);
                    s = null;
                }
            }
            return solution;
    }

    public static HashSet<Vertex> solve(Graph g) {
        int k = 0; //doing SCC in the first branch will filter all components <=1, so if a component remains,
        // we have to delete at least one.
        HashSet<Vertex> s = null;
        while (s == null) {
            System.out.println(k);
            s = branch(new Graph(g), k);
            k = k + 1;
        }
//        for (Vertex v :
//                s) {
//            g = g.removeVertex(v);
//        }
//        HashSet<Graph> subGraphs = new Tarjan(g).SCC();
        return s;
    }
}
