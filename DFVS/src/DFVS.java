import java.util.*;
import java.util.stream.Collectors;

public class DFVS {

    public static HashSet<Vertex> branch(Graph g, int k, LinkedList<HashSet<Vertex>> independentCliques, int level) {
        Main.countStep();
        if (Thread.interrupted()) {
            Main.log(Main.path, 0);
            System.out.println("#Timeout");
            //System.exit(1);
        }
        HashSet<Vertex> solution = new HashSet<>();
        if (CyclePacking.greedyPacking(g, k)) return null;
        HashSet<Vertex> chainingCleanSet = ReductionRules.chainingRule(g);
        solution.addAll(chainingCleanSet);
        k -= chainingCleanSet.size();
        HashSet<Vertex> s = new HashSet<>();
        if (k < 0) return null;
        //no cliques => branch on short cycle
        if (independentCliques == null) {
            ArrayList<Vertex> cycle = new Cycle(g, SearchType.SHORT_CYCLE, false, false).cycle();
            if (cycle.isEmpty()) return solution;
            cycle.removeIf(Vertex::isForbidden);
            if (cycle.isEmpty()) return null;
            for (Vertex vertex : cycle) {
                if (!vertex.isForbidden()) { //only delete if not forbidden
                    s = branch(g.removeVertex(vertex, true, true), k - 1, null, level + 1);
                    if (s != null) {
                        s.add(vertex);
                        break;
                    }
                }
            }
            cycle.forEach(v -> v.setForbidden(false));
        } else if (independentCliques.isEmpty()) {
            s = branch(g, k, null, level + 1);
        } else {
            HashSet<Vertex> clique = independentCliques.poll();
            ArrayList<HashSet<Vertex>> subSets = getSubsets(new ArrayList<>(clique), clique.size() - 1);
            for (HashSet<Vertex> set : subSets) {
                Graph h = new Graph(g);
                set.forEach(x -> h.removeVertex(x, false, false));
                s = branch(h, k - set.size(), new LinkedList<>(independentCliques), level + 1);
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
        long startTime = System.currentTimeMillis();
        HashSet<Vertex> heuristicSol = DFVSHeuristic.solve(new Graph(subGraph));
        int upperBound = heuristicSol.size();
        Main.upperBound += upperBound;
        Main.upperBoundTime += System.currentTimeMillis() - startTime;
        HashSet<Vertex> s = null;
        HashSet<Vertex> solution = new HashSet<>(ReductionRules.chainingRule(subGraph));
        Main.chaining3 += ReductionRules.removed;
        Main.preK += solution.size();
        if (subGraph.getVertices().isEmpty()) return solution;
        Flower flower = new Flower(subGraph, null);
        //flower.petalOneRule(subGraph);
        ArrayList<Vertex> verticesToDelete = new ArrayList<>();
        Clique clique = new Clique(subGraph, 10);
        ArrayList<HashSet<Vertex>> independentCliques = clique.getIndependentCliques();
        Main.indCliques += independentCliques.size();
        CyclePacking.greedyPacking(subGraph, -1);
        //CyclePacking.localSearchPacking(subGraph, -1);
        ArrayList<ArrayList<Vertex>> independentCycles = CyclePacking.packing;
        for (HashSet<Vertex> c : independentCliques) {
            independentCycles.removeIf(x -> x.stream().anyMatch(c::contains));
        }
        Main.indCycles += independentCycles.size();
        int lowerK = 0;

        lowerK = independentCliques.stream().map(x -> x.size() - 1).reduce(0, Integer::sum);
        lowerK += independentCycles.size();

        Main.lowerBound += lowerK;
        int subLBFlower = 0;

        int left = lowerK;
        int right = upperBound;
        HashSet<Vertex> currentSolution = new HashSet<>();
        while (left <= right) {
            System.out.println("#(" + left + ", " + right + ")");
            if (left == upperBound) {
                currentSolution = heuristicSol;
                solution.clear();
                break;
            }
            int k = left + ((right - left) / 2);
            verticesToDelete = flower.petalRule(k);

            if (verticesToDelete.size() <= k) {
                Graph h = new Graph(subGraph);
                verticesToDelete.forEach(x -> h.removeVertex(x, false, false));
                HashSet<Vertex> set = (HashSet<Vertex>) h.getVertices().stream().filter(x -> x.getPetal() > flower.getAverageFlow()).collect(Collectors.toSet());
                for (Vertex v :
                        set) {
                    if (Thread.interrupted()) {
                        Main.log(Main.path, s==null? 0: s.size());
                        System.out.println("#Timeout");
                        //System.exit(1);
                    }
                    Graph i = new Graph(h);
                    v.getPetalNodes().forEach(x -> i.removeVertex(x, false, false));
                    if (CyclePacking.greedyPacking(i, k - v.getPetal() - verticesToDelete.size())) {
                        subLBFlower += 1;
                        h.removeVertex(v, false, false);
                        h.getVertices().forEach(x -> x.setPetal(x.getPetal() - 1));
                        verticesToDelete.add(v);
                        //Main.packingFlowers.add(v);
                    }
                }
                if (independentCliques.isEmpty()) {
                    s = branch(h, k - verticesToDelete.size(), null, 1);
                } else {
                    ArrayList<HashSet<Vertex>> iC = new ArrayList<>(independentCliques);
                    ArrayList<Vertex> finalVerticesToDelete = verticesToDelete;
                    iC.forEach(x -> finalVerticesToDelete.forEach(x::remove));
                    iC.removeIf(x -> x.size() < 2);
                    s = branch(h, k - verticesToDelete.size(), new LinkedList<>(iC), 1);
                }
                if (s != null) {
                    currentSolution.clear();
                    currentSolution.addAll(verticesToDelete);
                    currentSolution.addAll(s);
                    Main.flowers += verticesToDelete.size();
                    Main.lbFlower += subLBFlower;
                }
            }
            if (s != null) {
                right = k - 1;
            } else {
                left = k + 1;
            }
            flower.resetPetals();
            subLBFlower = 0;
        }
        solution.addAll(currentSolution);
        return solution;
    }

    public static HashSet<Vertex> solve(Graph g) {
        HashSet<Vertex> s;
        HashSet<Vertex> solution = new HashSet<>(ReductionRules.chainingRule(g));
        Main.chaining1 = ReductionRules.removed;
        if (g.getVertices().isEmpty()) return solution;
        Clique clique = new Clique(g, 10);
        while (true) {
            HashSet<Vertex> verticesToDelete = clique.cliqueRule();
            Main.cliqueRule += verticesToDelete.size();
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
        Main.preK += solution.size();
        Main.chaining2 = ReductionRules.removed;
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
