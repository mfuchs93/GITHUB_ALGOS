import java.util.ArrayList;
import java.util.HashSet;

public class CyclePacking {
    public static int cancelCounter = 0;
    public static int localCounter = 0;
    public static ArrayList<HashSet<Vertex>> packing = new ArrayList<>();

    public static boolean greedyPacking(Graph g, int k) {
        packing = new ArrayList<>();
        Graph h = new Graph(g);
        int counter = 0;
        while(counter <= k) {
            Cycle cycle = new Cycle(h, SearchType.SHORT_CYCLE, false);
            HashSet<Vertex> c = cycle.cycle();
            if (c.isEmpty()) return false;
            packing.add(c);
            c.forEach(x->h.removeVertex(x,false,false));
            counter++;
        }
        cancelCounter++;
        return true;
    }
    
    public static boolean localSearchPacking(Graph g, int k) {
        ArrayList<HashSet<Vertex>> newCycles = new ArrayList<>();
        ArrayList<HashSet<Vertex>> p = new ArrayList<>(packing);
        for (HashSet<Vertex> cycle :
                packing) {
            Graph h = new Graph(g);
            p.remove(cycle);
            p.forEach(x -> x.forEach(v -> h.removeVertex(v, false, false)));
            Cycle cycle1 = new Cycle(h, SearchType.SHORT_CYCLE,true);
            ArrayList<HashSet<Vertex>> cycles = cycle1.getCycles();
            while (!cycles.isEmpty()) {
                ArrayList<HashSet<Vertex>> indCycles = cycle1.getIndependentCycles(cycles);
                if (indCycles.size() > 1) {
                    p.addAll(indCycles);
                    break;
                } else {
                    cycles.remove(indCycles.get(0));
                }
            }
            if (cycles.isEmpty()) {
                //newCycles.add(cycle);
                p.add(cycle);
            }
        }
        packing = p;
        if (packing.size() > k){
            localCounter++;
            return true;
        } else return false;
    }



    public static boolean optimalPacking(Graph g, int k) {
        Graph h = new Graph(g);
        int counter = 0;
        while (counter <= k)
            return true;
        return true;
    }
}
