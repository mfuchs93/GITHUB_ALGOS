import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class DFVS {
    public static ArrayList<Vertex> empty = new ArrayList<>();

    public static HashSet<Vertex> branch(Graph g, int k, ArrayList<Vertex> verticesToDelete) {
        Main.countStep();
        if (Thread.interrupted()) {
            System.out.println("Timeout");
            System.exit(1);
        }
        //if (!verticesToDelete.isEmpty()) System.out.println(verticesToDelete);
        HashSet<Vertex> solution = new HashSet<>();
        solution.addAll(verticesToDelete);
        verticesToDelete.forEach(v -> g.removeVertex(v, false, false));
//        ReductionRules.removeNoneCycleVertex(g);
//        HashSet<Vertex> chainingCleanSet = ReductionRules.chainingRule(g);
//        solution.addAll(chainingCleanSet);
//        k -= chainingCleanSet.size();
        HashSet<Vertex> s = new HashSet<>();
        if (k < 0) return null;

        ArrayList<Vertex> cycle = new Cycle(g, SearchType.SHORTEST_CYCLE).cycle();
        if (cycle.isEmpty()) return solution;
        cycle.removeIf(Vertex::isForbidden);
        if (cycle.isEmpty()) return null;
        for (Vertex vertex : cycle) {
            if (!vertex.isForbidden()) { //nur löschen wenn nicht verboten
                s = branch(g.removeVertex(vertex, true, true), k - 1, empty);
                if (s != null) {
                    s.add(vertex);
                    break;
                }
            }
        }
        cycle.forEach(v -> v.setForbidden(false));
        if (s == null) {
            return null;
        }
        solution.addAll(s);


//        HashSet<Graph> subGraphz = new Tarjan(g).SCC();
//        if (subGraphz.isEmpty())
//            return solution; //return solution, because we may have deleted one in chaining
//        if (subGraphz.size() > k) return null;
//        int counter = 1; // wieviele wurden gelöscht
//        int restK = k; // wieviele können wir in den restlichen SubGraphen noch löschen
//        ArrayList<Graph> subGraphs = new ArrayList<>(subGraphz);
//        Collections.sort(subGraphs);
//        for (Graph subGraph :
//                subGraphs) {
//            ArrayList<Vertex> cycle = new Cycle(subGraph, SearchType.SHORTEST_CYCLE).cycle();
//            cycle.removeIf(Vertex::isForbidden);
//            if (cycle.isEmpty()) return null; // wenn alle verboten return
//            while (restK - counter >= 0) {
//                for (Vertex vertex : cycle) {
//                    if (!vertex.isForbidden()) { //nur löschen wenn nicht verboten
//                        s = branch(subGraph.removeVertex(vertex, true, true), counter - 1, verticesToDelete);
//                        if (s != null) {
//                            s.add(vertex);
//                            break;
//                        }
//                    }
//                }
//                cycle.forEach(v -> v.setForbidden(false));
//                if (s != null) {
//                    restK -= counter;
//                    counter = 1;
//                    break;
//                } else {
//                    counter++;
//                }
//            }
//            if (s == null) {
//                return null;
//            } else {
//                solution.addAll(s);
//                s = null;
//            }
//        }
        return solution;
    }

    public static HashSet<Vertex> solveSubGraph(Graph subGraph) {
        HashSet<Vertex> s = null;
        HashSet<Vertex> solution;
        solution = ReductionRules.chainingRule(subGraph);
        //System.out.println(solution);
        // HashSet<HashSet<Vertex>> cycles = new Cycle(g,SearchType.SHORTEST_CYCLE).getCycles();
        Flower flower = new Flower(subGraph);
        ArrayList<Vertex> verticesToDelete = new ArrayList<>();
        int k = 0;
        while (s == null) {
            if (k == 0 || verticesToDelete.size() > 0) {
                verticesToDelete = flower.petalRule(k);
            }
            if (verticesToDelete.size() <= k){
                s = branch(new Graph(subGraph), k - verticesToDelete.size(), verticesToDelete);
            }
            flower.resetPetals();
            k = k + 1;
        }
        solution.addAll(s);
        return solution;
    }

    public static HashSet<Vertex> solve(Graph g) {
        HashSet<Vertex> s = null;
        HashSet<Vertex> solution = null;
        solution = ReductionRules.chainingRule(g);
        HashSet<Graph> subGraphz = new Tarjan(g).SCC();
        if (subGraphz.isEmpty())
            return solution;
        ArrayList<Graph> subGraphs = new ArrayList<>(subGraphz);
        Collections.sort(subGraphs);
        for (Graph subGraph :
                subGraphs) {
            s = solveSubGraph(subGraph);
            solution.addAll(s);
        }
        return solution;
    }
}
