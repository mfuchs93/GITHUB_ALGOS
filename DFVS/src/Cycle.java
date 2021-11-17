import java.util.*;

public class Cycle {
    private HashMap<Vertex, Boolean> marked;
    private HashMap<Vertex, Vertex> edgeTo;
    private HashMap<Vertex, Boolean> onStack;
    private Stack<Vertex> cycle;
    Graph g;
    HashSet<Vertex> empty = new HashSet<>();

    public Cycle(Graph g) {
        this.g = g;
        onStack = new HashMap<>();
        edgeTo = new HashMap<>();
        marked = new HashMap<>();
        for (Vertex v : g.getVertices()) {
            if (!marked.getOrDefault(v, false)) {
                dfs(v);
            }
        }
    }

    public Cycle(Graph g, Vertex v) {
        this.g = g;
        onStack = new HashMap<>();
        edgeTo = new HashMap<>();
        marked = new HashMap<>();
        dfs(v);
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

    public boolean hasCycle() {
        return cycle != null;
    }

    public ArrayList<Vertex> cycle() {
        ArrayList<Vertex> stackToList = new ArrayList<>();
        if(cycle == null)
            return stackToList;
        stackToList.addAll(cycle);
        Collections.reverse(stackToList);
        return stackToList;
    }
}
