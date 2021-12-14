import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class DFVS {

    public static HashSet<Vertex> branch(Graph g, int k, LinkedList<HashSet<Vertex>> independentCliques) {
        Main.countStep();
        if (Thread.interrupted()) {
            System.out.println("#No. of canceled branches: " + CyclePacking.cancelCounter);
            System.out.println("#No. of canceled branches Local: " + CyclePacking.localCounter);
            System.out.println("Timeout");
            System.exit(1);
        }
        if (CyclePacking.greedyPacking(g,k)) return null;
        if (CyclePacking.localSearchPacking(g,k)) return null;
        HashSet<Vertex> solution = new HashSet<>();
        HashSet<Vertex> chainingCleanSet = ReductionRules.chainingRule(g);
        solution.addAll(chainingCleanSet);
        //System.out.println("#chaining: " + chainingCleanSet.size());
        k -= chainingCleanSet.size();
        HashSet<Vertex> s = new HashSet<>();
        if (k < 0) return null;
        //no cliques => branch on short cycle
        if (independentCliques == null) {
            HashSet<Vertex> cycle = new Cycle(g, SearchType.SHORT_CYCLE, false).cycle();
            if (cycle.isEmpty()) return solution;
            cycle.removeIf(Vertex::isForbidden);
            if (cycle.isEmpty()) return null;
            for (Vertex vertex : cycle) {
                if (!vertex.isForbidden()) { //only delete if not forbidden
                    s = branch(g.removeVertex(vertex, true, true), k - 1, null);
                    if (s != null) {
                        s.add(vertex);
                        break;
                    }
                }
            }
            cycle.forEach(v -> v.setForbidden(false));
        } else if (independentCliques.isEmpty()) {
            s = branch(g, k, null);
        } else {
            HashSet<Vertex> clique = independentCliques.poll();
            ArrayList<HashSet<Vertex>> subSets = getSubsets(new ArrayList<>(clique), clique.size() - 1);
            for (HashSet<Vertex> set : subSets) {
                Graph h = new Graph(g);
                set.forEach(x -> h.removeVertex(x, false, false));
                s = branch(h, k - set.size(), new LinkedList<>(independentCliques));
                if (s != null) {
                    s.addAll(set);
                    break;
                }
            }
        }
        if (s == null) {
            return null;
        }
        solution.addAll(s);
        return solution;
    }

    public static HashSet<Vertex> solveSubGraph(Graph subGraph) {
        HashSet<Vertex> s = null;
        HashSet<Vertex> solution = new HashSet<>(ReductionRules.chainingRule(subGraph));
        if (subGraph.getVertices().isEmpty()) return solution;
        Flower flower = new Flower(subGraph);
        flower.petalOneRule(subGraph);
        ArrayList<Vertex> verticesToDelete = new ArrayList<>();
        Clique clique = new Clique(subGraph, 10);
        ArrayList<HashSet<Vertex>> independentCliques = clique.getIndependentCliques();
        ArrayList<HashSet<Vertex>> independentCycles = new Cycle(subGraph, SearchType.SHORT_CYCLE, true).getIndependentCycles(null);
        for (HashSet<Vertex> c : independentCliques) {
            independentCycles.removeIf(x -> x.stream().anyMatch(c::contains));
        }
        int k = 0;
        int lowerK = 0;

        lowerK = independentCliques.stream().map(x -> x.size() - 1).reduce(0, Integer::sum);
        lowerK += independentCycles.size();
        System.out.println("#lower-bound k through cliques and independent cycles: " + lowerK);

        k = lowerK;
        while (s == null) {
            System.out.println("#k: " + k);
            if (k == 0 || k == lowerK || verticesToDelete.size() > 0) {
                verticesToDelete = flower.petalRule(k);
            }
            if (verticesToDelete.size() <= k) {
                Graph h = new Graph(subGraph);
                verticesToDelete.forEach(x -> h.removeVertex(x, false, false));
                if (independentCliques.isEmpty()) {
                    s = branch(h, k - verticesToDelete.size(), null);
                } else {
                    ArrayList<Vertex> finalVerticesToDelete = verticesToDelete;
                    independentCliques.forEach(x -> finalVerticesToDelete.forEach(x::remove));
                    s = branch(h, k - verticesToDelete.size(), new LinkedList<>(independentCliques));
                }
                if (s != null) {
                    solution.addAll(verticesToDelete);
                    System.out.println("#deleted through flowers: " + verticesToDelete.size());
                }
            }
            flower.resetPetals();
            k = k + 1;
        }
        solution.addAll(s);
        return solution;
    }

    public static HashSet<Vertex> solve(Graph g) {
        HashSet<Vertex> s;
        HashSet<Vertex> solution = new HashSet<>(ReductionRules.chainingRule(g));
        if (g.getVertices().isEmpty()) return solution;
        Clique clique = new Clique(g, 10);
        while (true) {
            HashSet<Vertex> verticesToDelete = clique.cliqueRule();
            if (verticesToDelete.isEmpty()) {
                break;
            }
            for (Vertex v :
                    verticesToDelete) {
                g.removeVertex(v, false, false);
            }
            solution.addAll(verticesToDelete);
            System.out.println("#removed by cliqueRule: " + verticesToDelete.size());
        }
        solution.addAll(ReductionRules.chainingRule(g));
        if (g.getVertices().isEmpty()) return solution;
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


    private static void getSubsets(ArrayList<Vertex> superSet, int k, int idx, HashSet<Vertex> current, ArrayList<HashSet<Vertex>> solution) {
        //successful stop clause
        if (current.size() == k) {
            solution.add(new HashSet<>(current));
            return;
        }
        //unseccessful stop clause
        if (idx == superSet.size()) return;
        Vertex x = superSet.get(idx);
        current.add(x);
        //"guess" x is in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
        current.remove(x);
        //"guess" x is not in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
    }

    public static ArrayList<HashSet<Vertex>> getSubsets(ArrayList<Vertex> superSet, int k) {
        ArrayList<HashSet<Vertex>> res = new ArrayList<>();
        getSubsets(superSet, k, 0, new HashSet<>(), res);
        return res;
    }

}
