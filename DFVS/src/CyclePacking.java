import java.util.ArrayList;

public class CyclePacking {
    public static int cancelCounter = 0;
    public static int localCounter = 0;
    public static int localSearchFoundMore = 0;
    public static ArrayList<ArrayList<Vertex>> packing = new ArrayList<>();

    public static boolean greedyPacking(Graph g, int k) {
        //long start = System.currentTimeMillis();
        packing = new ArrayList<>();
        Graph h = new Graph(g);
        int counter = 0;
        while (counter <= k || k == -1) {
            Cycle cycle = new Cycle(h, SearchType.SHORT_CYCLE, false, false);
            ArrayList<Vertex> c = cycle.cycle();
            if (c.isEmpty()) {
                //System.out.println("#time:" + (System.currentTimeMillis() - start));
                return false;
            }
            packing.add(c);
            c.forEach(x -> h.removeVertex(x, false, false));
            counter++;
        }
        cancelCounter++;
        //System.out.println("#time:" + (System.currentTimeMillis() - start));
        return true;
    }

    public static boolean localSearchPacking(Graph g, int k) {
        ArrayList<ArrayList<Vertex>> p = new ArrayList<>(packing);
        for (ArrayList<Vertex> cycle :
                packing) {
            Graph h = new Graph(g);
            p.remove(cycle);
            p.forEach(x -> x.forEach(v -> h.removeVertex(v, false, false)));
            Cycle cycle1 = new Cycle(h, SearchType.SHORT_CYCLE, true, false);
            ArrayList<ArrayList<Vertex>> cycles = cycle1.getCycles();
            while (!cycles.isEmpty()) {
                ArrayList<ArrayList<Vertex>> indCycles = cycle1.getIndependentCycles(cycles);
                if (indCycles.size() > 1) {
                    localSearchFoundMore++;
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
        if (packing.size() > k && k != -1) {
            localCounter++;
            return true;
        } else {
            return false;
        }
    }


    public static boolean optimalPacking(Graph g, int k) {
        Graph h = new Graph(g);
        int counter = 0;
        while (counter <= k)
            return true;
        return true;
    }
}
