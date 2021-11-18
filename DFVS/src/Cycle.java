import java.util.*;

public class Cycle {
    private HashMap<Vertex, Boolean> marked;
    private HashMap<Vertex, Vertex> edgeTo;
    private HashMap<Vertex, Boolean> onStack;
    private Stack<Vertex> cycle;
    private final Graph g;
    private final HashSet<Vertex> empty = new HashSet<>();
    private HashMap<Vertex, Integer> distTo;

    public Cycle(Graph g, SearchType type) {
        this.g = g;

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
            case SHORTEST_CYCLE -> shortestCycle();
        }
    }

    public Cycle(Graph g, Vertex v, SearchType type) {
        this.g = g;

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
            }
        }
        onStack.put(v, false);
    }

    public void shortestCycle() {
        Graph r = g.reverse();
        int length = g.getVertices().size() + 1;
        for (Vertex v : g.getVertices()) {
            bfs(r, v, null);
            for (Vertex w : g.getOutEdges().get(v)) {
                if (marked.get(w) && (distTo.get(w) +1) < length) {
                    length = distTo.get(w) + 1;
                    cycle = new Stack<>();
                    Vertex x;
                    for (x = w; distTo.get(x) != 0; x = edgeTo.get(x) ) {
                        cycle.push(x);
                    }
                    cycle.push(x);
                    cycle.push(v);
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
                    cycle = new Stack<>();
                    cycle.push(v);
                    return;
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
        ArrayList<Vertex> stackToList = new ArrayList<>();
        if (cycle == null)
            return stackToList;
        stackToList.addAll(cycle);
        Collections.reverse(stackToList);
        return stackToList;
    }
}
