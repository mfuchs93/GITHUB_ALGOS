import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.stream.Collectors;

public class DFVS {
    public static ArrayList<Vertex> empty = new ArrayList<>();

    public static HashSet<Vertex> branch(Graph g, int k, ArrayList<Vertex> verticesToDelete) {
        Main.countStep();
        if (Thread.interrupted()) {
            System.out.println("Timeout");
            System.exit(1);
        }
        //if (!verticesToDelete.isEmpty()) System.out.println(verticesToDelete);
        HashSet<Vertex> solution = new HashSet<>(verticesToDelete);
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
            if (!vertex.isForbidden()) { //only delete if not forbidden
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


//        HashSet<Graph> subGraphs = new Tarjan(g).SCC();
//        if (subGraphs.isEmpty())
//            return solution; //return solution, because we may have deleted one in chaining
//        if (subGraphs.size() > k) return null;
//        int counter = 1; // how many we have deleted for this subGraph
//        int restK = k; // how many we can delete in the remaining subGraphs
//        Collections.sort(subGraphs);
//        for (Graph subGraph :
//                subGraphs) {
//            ArrayList<Vertex> cycle = new Cycle(subGraph, SearchType.SHORTEST_CYCLE).cycle();
//            cycle.removeIf(Vertex::isForbidden);
//            if (cycle.isEmpty()) return null; // if all forbidden return
//            while (restK - counter >= 0) {
//                for (Vertex vertex : cycle) {
//                    if (!vertex.isForbidden()) { //only delete if not forbidden
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
        // HashSet<HashSet<Vertex>> cycles = new Cycle(g,SearchType.SHORTEST_CYCLE).getCycles();
        Flower flower = new Flower(subGraph);
        ArrayList<Vertex> verticesToDelete = new ArrayList<>();
        int k = 0;
        while (s == null) {
            //System.out.println(k);
            if (k == 0 || verticesToDelete.size() > 0) {
                verticesToDelete = flower.petalRule(k);
            }
            if (verticesToDelete.size() <= k) {
                s = branch(new Graph(subGraph), k - verticesToDelete.size(), verticesToDelete);
            }
            flower.resetPetals();
            k = k + 1;
        }
        solution.addAll(s);
        return solution;
    }

    public static HashSet<Vertex> solve(Graph g) {
        HashSet<Vertex> s;
        HashSet<Vertex> solution;
        solution = ReductionRules.chainingRule(g);
        Clique c = new Clique(g, 10);
        while (true) {
            HashMap<Integer, ArrayList<HashSet<Vertex>>> cliques = new HashMap<>();
//            for (int i = 2; i<=10; i++){
//                c.setK(i);
//                c.cliqueRule();
//                if (c.getCliques().size() == 0) break;
//                cliques.put(i, c.getCliques());
//            }
            HashSet<Vertex> verticesToDelete = c.cliqueRule();
            HashSet<Vertex> distinctCliqueVertices = new HashSet<>();
            c.getCliques().forEach(distinctCliqueVertices::addAll);
            System.out.println("#distinct vertices: "+ distinctCliqueVertices.size());
            HashSet<HashSet<Vertex>> noIntersections = new HashSet<>();
            for (HashSet<Vertex> s1:
            c.getCliques()){
                boolean hasIntersection = false;
                for (HashSet<Vertex> s2 :
                     c.getCliques().stream().filter(x -> x != s1).collect(Collectors.toSet())) {
                    if (s1.stream().anyMatch(s2::contains)) {
                        hasIntersection = true;
                        break;
                    }
                }
                if (!hasIntersection) noIntersections.add(s1);
            }
            if (verticesToDelete.isEmpty()) {
                if (c.getK() >2) {
                    c.setK(c.getK()-1);
                    continue;
                }
                break;
            }
            for (Vertex v :
                    verticesToDelete) {
                g.removeVertex(v, false, false);
            }
            solution.addAll(verticesToDelete);
            solution.addAll(ReductionRules.chainingRule(g));
        }
        ArrayList<Graph> subGraphs = new Tarjan(g).SCC();
        if (subGraphs.isEmpty())
            return solution;
        Collections.sort(subGraphs);
        for (Graph subGraph :
                subGraphs) {
            s = solveSubGraph(subGraph);
            solution.addAll(s);
        }
        return solution;
    }
}
