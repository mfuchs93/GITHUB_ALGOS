import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Collectors;

public class DFVS {

    public static HashSet<Vertex> branch(Graph g, int k) {
        Main.countStep();
        if (Thread.interrupted()) {
            System.out.println("Timeout");
            System.exit(1);
        }
        HashSet<Vertex> solution = new HashSet<>();
        ReductionRules.removeNoneCycleVertex(g);
        HashSet<Vertex> chainingCleanSet = ReductionRules.chainingRule(g);
        solution.addAll(chainingCleanSet);
        k -= chainingCleanSet.size();
        HashSet<Vertex> s = new HashSet<>();
        if (k < 0) return null;
        HashSet<Graph> subGraphz = new Tarjan(g).SCC();
        if (subGraphz.isEmpty())
            return solution; //return solution, because we may have deleted one in chaining
        if (subGraphz.size() > k) return null;
        int counter = 1; // wieviele wurden gelöscht
        int restK = k; // wieviele können wir in den restlichen SubGraphen noch löschen
        ArrayList<Graph> subGraphs = new ArrayList<>(subGraphz);
        Collections.sort(subGraphs);
        for (Graph subGraph :
                subGraphs) {
            ArrayList<Vertex> cycle = new Cycle(subGraph, SearchType.SHORTEST_CYCLE).cycle();
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
        HashSet<Vertex> s = null;
        HashSet<Vertex> solution;
        ReductionRules.removeNoneCycleVertex(g);
        solution = ReductionRules.chainingRule(g);
       // HashSet<HashSet<Vertex>> cycles = new Cycle(g,SearchType.SHORTEST_CYCLE).getCycles();

        int k = 0;
        //Graph h = new Graph(g);
        while (s == null) {
            s = branch(new Graph(g), k);
            k = k + 1;
        }
        solution.addAll(s);
        return solution;
    }
}
