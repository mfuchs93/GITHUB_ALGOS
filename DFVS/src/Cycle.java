import java.util.*;

public class Cycle {
    private final boolean allCycles;
    private HashMap<Vertex, Boolean> marked;
    private HashMap<Vertex, Vertex> edgeTo;
    private HashMap<Vertex, Boolean> onStack;
    private Stack<Vertex> cycle;
    private final Graph g;
    private final HashSet<Vertex> empty = new HashSet<>();
    private HashMap<Vertex, Integer> distTo;
    private final HashSet<ArrayList<Vertex>> cycles = new HashSet<>();

    public Cycle(Graph g, SearchType type, boolean allCycles) {
        this.g = g;
        this.allCycles = allCycles;
        switch (type) {
            case DFS -> {
                onStack = new HashMap<>();
                edgeTo = new HashMap<>();
                marked = new HashMap<>();
                for (Vertex v : g.getVertices()) {
                    if (!marked.getOrDefault(v, false)) {
                        dfs(v);
                    }
                }
            }
            case SHORT_CYCLE -> shortCycle(this.allCycles);
        }
    }

    public Cycle(Graph g, Vertex v, SearchType type, boolean allCycles) {
        this.g = g;
        this.allCycles = allCycles;

        switch (type) {
            case DFS -> {
                onStack = new HashMap<>();
                edgeTo = new HashMap<>();
                marked = new HashMap<>();
                dfs(v);
            }
            case CONTAINS_VERTEX -> bfs(g, v, v);
        }
    }

    private void dfs(Vertex v) {
        onStack.put(v, true);
        marked.put(v, true);
        for (Vertex w : g.getOutEdges().getOrDefault(v, empty)) {
            if (hasCycle()) {
                return;
            } else if (!marked.getOrDefault(w, false)) {
                edgeTo.put(w, v);
                dfs(w);
            } else if (onStack.getOrDefault(w, false)) {
                cycle = new Stack<>();
                for (Vertex s = v; !s.equals(w); s = edgeTo.get(s)) {
                    cycle.push(s);
                }
                cycle.push(w);
                cycle.push(v);
                cycles.add(new ArrayList<>(cycle));
            }
        }
        onStack.put(v, false);
    }

    public void shortCycle(boolean allCycles) {
        Graph r = g.reverse();
        int length = g.getVertices().size() + 1;
        for (Vertex v : g.getVertices()) {
            bfs(r, v, null);
            for (Vertex w : g.getOutEdges().getOrDefault(v, empty)) {
                if (marked.getOrDefault(w, false) && (distTo.get(w) + 1) < length) {
                    cycle = new Stack<>();
                    Vertex x;
                    cycle.push(v);
                    for (x = w; distTo.get(x) != 0; x = edgeTo.get(x)) {
                        cycle.push(x);
                    }
                    cycle.push(x);
                    cycles.add(new ArrayList<>(cycle));
                    if (!allCycles) {
                        length = distTo.get(w) + 1;
                        if (cycle.size() < 5) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public void bfs(Graph g, Vertex start, Vertex goal) {
        edgeTo = new HashMap<>();
        marked = new HashMap<>();
        distTo = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            distTo.put(v, Integer.MAX_VALUE);
        }
        LinkedList<Vertex> q = new LinkedList<>();
        marked.put(start, true);
        distTo.put(start, 0);
        q.add(start);
        while (!q.isEmpty()) {
            Vertex v = q.poll();
            for (Vertex w : g.getOutEdges().getOrDefault(v, empty)) {
                if (w.equals(goal)) {
                    //System.out.println(start + " " + goal);
                    cycle = new Stack<>();
                    cycle.push(v);
                    //return;
                }
                if (!marked.getOrDefault(w, false)) {
                    edgeTo.put(w, v);
                    distTo.put(w, distTo.get(v) + 1);
                    marked.put(w, true);
                    q.add(w);
                }
            }
        }
    }

    public boolean hasCycle() {
        return cycle != null;
    }

    public ArrayList<Vertex> cycle() {
        if (cycle == null) return new ArrayList<>();
        return new ArrayList<>(cycle);
    }

    public ArrayList<ArrayList<Vertex>> getCycles() {
        ArrayList<ArrayList<Vertex>> cycles = new ArrayList<>(this.cycles);
        cycles.sort((c1, c2) -> c1.size() - c2.size());
        return cycles;
    }
    public ArrayList<ArrayList<Vertex>> getIndependentCycles(ArrayList<ArrayList<Vertex>> cycleList) {
        ArrayList<ArrayList<Vertex>> cycles;
        if (cycleList == null){
            cycles = new ArrayList<>(getCycles());
        } else cycles = new ArrayList<>(cycleList);
        ArrayList<ArrayList<Vertex>> noIntersections = new ArrayList<>();
        while (!cycles.isEmpty()) {
            ArrayList<Vertex> c = cycles.remove(0);
            noIntersections.add(c);
            cycles.removeIf(x -> x.stream().anyMatch(c::contains));
        }
        //System.out.println("#cycles with no intersection: " + noIntersections.size());
        return noIntersections;
    }

    public void getCycleCount() {
        for (ArrayList<Vertex> s :
                getCycles()) {
            for (Vertex v :
                    s) {
                v.setCycleCount(v.getCycleCount() + 1);
            }
        }
    }
}
