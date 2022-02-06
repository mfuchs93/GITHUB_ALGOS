import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class DFASHeuristic {

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
        HashSet<Vertex> s = null;
        HashSet<Vertex> solution = new HashSet<>(ReductionRules.chainingRule(subGraph));
        Main.chaining3 += ReductionRules.removed;
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
        int k = 0;
        int lowerK = 0;

        lowerK = independentCliques.stream().map(x -> x.size() - 1).reduce(0, Integer::sum);
        lowerK += independentCycles.size();

        k = lowerK;
        Main.preK += lowerK;
        int subLBFlower = 0;
        while (s == null) {
            if (k == 0 || k == lowerK || verticesToDelete.size() > 0) {
                verticesToDelete = flower.petalRule(k);
            }
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
                    solution.addAll(verticesToDelete);
                    Main.flowers += verticesToDelete.size();
                    Main.lbFlower += subLBFlower;
                }
            }
            flower.resetPetals();
            subLBFlower = 0;
            k = k + 1;
        }
        solution.addAll(s);
        return solution;
    }

    public static HashSet<Vertex> solve(Graph g) {
//        ArrayList<Vertex> topologicalOrder = new ArrayList<>(g.getVertices());
//        AtomicInteger j = new AtomicInteger();
//        topologicalOrder.forEach(v -> v.setOrderIndex(j.getAndIncrement()));
        Graph h = new Graph(g);

        //greedy
        Vertex[] tO = new Vertex[g.getVertices().size()];
        int i = 0;
        while(!h.getVertices().isEmpty()) {
            Vertex source = null;
            for (Vertex v: h.getVertices()) {
                if (source == null || h.getInDegree(v) < h.getInDegree(source) || h.getInDegree(v) == h.getInDegree(source) && v.getPolarity() == '-' && source.getPolarity() == '+') {
                    source = v;
                }
            }
            //System.out.println("#" + h.getInDegree(source));
            h.removeVertex(source, false, false);
            tO[i] = source;
            Vertex sink = null;
            for (Vertex v: h.getVertices()) {
                if (sink == null || g.getOutDegree(v) < g.getOutDegree(sink)) {
                    sink = v;
                }
            }
            h.removeVertex(sink, false, false);
            tO[g.getVertices().size() -1 - i] = sink;
            i++;
        }
        ArrayList<Vertex> topologicalOrder = new ArrayList<>(List.of(tO));
        Collections.shuffle(topologicalOrder);
        AtomicInteger j = new AtomicInteger();
        topologicalOrder.forEach(v -> v.setOrderIndex(j.getAndIncrement()));

        HashSet<Vertex> optimalSolution = null;
        for (int k = 0; k < 100; k++) {
            HashSet<Vertex> s = localSearchRandom(new Graph(g), new ArrayList<>(topologicalOrder));
            System.out.println("#solSize: " + s.size());
            if (optimalSolution == null || s.size() < optimalSolution.size()) {
                optimalSolution = s;
            }
        }
        System.out.println("#End: " + optimalSolution.size());
        return optimalSolution;
    }

    private static HashSet<Vertex> localSearchRandom(Graph g, ArrayList<Vertex> topologicalOrder) {
        HashSet<Vertex> s = new HashSet<>();
        boolean run = true;
        int length =  (int) (topologicalOrder.size() * 0.05);
        int failCounter = 0;
        while (failCounter < 10000) {
            int startIndex = new Random().nextInt(topologicalOrder.size() - length);
            int backEdges = 0;
            for (int i = startIndex; i < startIndex + length; i++) {
                Vertex v = topologicalOrder.get(i);
                backEdges += g.getOutEdges().get(v).stream().filter(w -> w.getOrderIndex() < v.getOrderIndex() && w.getOrderIndex() >= startIndex).count();
            }
            ArrayList<Vertex> topoCopy = new ArrayList<>(topologicalOrder);
            Collections.shuffle(topoCopy.subList(startIndex, startIndex + length));
            for (int i = startIndex; i < startIndex + length; i++) {
                topoCopy.get(i).setOrderIndex(i);
            }
            int backEdgesShuffle = 0;
            for (int i = startIndex; i < startIndex + length; i++) {
                Vertex v = topoCopy.get(i);
                backEdgesShuffle += g.getOutEdges().get(v).stream().filter(w -> w.getOrderIndex() < v.getOrderIndex() && w.getOrderIndex() >= startIndex).count();
            }
            if (backEdgesShuffle < backEdges){
                topologicalOrder = topoCopy;
            } else {
                for (int i = startIndex; i < startIndex + length; i++) {
                    topologicalOrder.get(i).setOrderIndex(i);
                }
                failCounter++;
            }
        }

//        int swapCounter = swapArbitrary(g, topologicalOrder);
//        while (run) {
//            run = false;
//            for (int c = 0; c < topologicalOrder.size() - 1; c++) {
//                if (!g.getOutEdges().get(topologicalOrder.get(c)).contains(topologicalOrder.get(c + 1)) &&
//                        g.getOutEdges().get(topologicalOrder.get(c + 1)).contains(topologicalOrder.get(c))) {
//                    Collections.swap(topologicalOrder, c, c + 1);
//                    topologicalOrder.get(c).setOrderIndex(c);
//                    topologicalOrder.get(c + 1).setOrderIndex(c + 1);
//                    run = true;
//                    swapCounter++;
//                }
//            }
//        }
        //System.out.println("#" + swapCounter);
        topologicalOrder.forEach(source -> g.getOutEdges().get(source).stream().filter(t -> source.getOrderIndex() < t.getOrderIndex()).collect(Collectors.toList()).forEach(target -> g.removeEdge(source, target)));
        for (Vertex v :
                topologicalOrder) {
            if (v.getPolarity() == '+') {
                Vertex minus = g.getVertices().stream().filter(w -> w.toString().equals(v.getName()+ '-')).findFirst().get();
                if(v.getOrderIndex() < minus.getOrderIndex()) {
                    s.add(v);
                    g.removeVertex(v, false, false);
                    g.removeVertex(minus, false, false);
                }
            }
        }
        while (true) {
            Vertex vertexToDelete = null;
            for (Vertex v :
                    g.getVertices()) {
                if (vertexToDelete == null && g.getDegree(v) > 0 || g.getDegree(v) > g.getDegree(vertexToDelete)) {
                    vertexToDelete = v;
                }
            }
            if (vertexToDelete == null) break;
            //System.out.println("#" + h.getDegree(vertexToDelete));
            s.add(vertexToDelete);
            g.removeVertex(vertexToDelete, false, false);
            if (vertexToDelete.getPolarity() == '+') {
                Vertex finalVertexToDelete = vertexToDelete;
                g.removeVertex(g.getVertices().stream().filter(v -> v.toString().equals(finalVertexToDelete.getName() + "-")).findFirst().get(),false, false);
            } else {
                Vertex finalVertexToDelete = vertexToDelete;
                g.removeVertex(g.getVertices().stream().filter(v -> v.toString().equals(finalVertexToDelete.getName() + "+")).findFirst().get(),false, false);
            }
        }
        return s;
    }

    private static int swapArbitrary(Graph g, ArrayList<Vertex> topologicalOrder) {
        int swapCounter = 0;
        int failCounter = 0;
        int lower = 0;
        int upper = 0;
        long startTime = System.currentTimeMillis();
        while(failCounter < 10000) {
            try {
                lower = min(new Random().nextInt(topologicalOrder.size()), topologicalOrder.size() - 1);
                upper = min(lower + new Random().nextInt(topologicalOrder.size() - lower), topologicalOrder.size() -1);
                int finalLower = lower;
                int finalUpper = upper;
                int finalLower1 = lower;
                int finalUpper1 = upper;
                if(g.getOutEdges().get(topologicalOrder.get(lower)).stream().filter(v -> v.getOrderIndex() > finalLower && v.getOrderIndex() <= finalUpper).count() <
                        g.getOutEdges().get(topologicalOrder.get(upper)).stream().filter(v -> v.getOrderIndex() >= finalLower1 && v.getOrderIndex() < finalUpper1).count()) {
                    Collections.swap(topologicalOrder, lower, upper);
                    topologicalOrder.get(lower).setOrderIndex(lower);
                    topologicalOrder.get(upper).setOrderIndex(upper);
                    swapCounter++;
                    failCounter = 0;
                } else {
                    failCounter++;
                }

            } catch (NullPointerException e) {
                System.out.println(lower);
                System.out.println(upper);
                System.out.println(topologicalOrder.size());
                System.exit(0);
            }
        }
        return swapCounter;
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
