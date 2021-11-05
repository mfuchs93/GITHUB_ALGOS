import java.util.*;

public class Cycle {
    private HashMap<String, Boolean> marked;
    private HashMap<String, String> edgeTo;
    private HashMap<String, Boolean> onStack;
    private Stack<String> cycle;
    Graph g;
    HashSet<String> empty = new HashSet<>();

    public Cycle(Graph g) {
        this.g = g;
        onStack = new HashMap<>();
        edgeTo = new HashMap<>();
        marked = new HashMap<>();
        for (String v : g.getVertices()) {
            if (!marked.getOrDefault(v, false)) {
                dfs(v);
            }
        }
    }

    public void dfs(String v) {
        onStack.put(v, true);
        marked.put(v, true);
        for (String w : g.getOutEdges().getOrDefault(v, empty)) {
            if (hasCycle()) {
                return;
            } else if (!marked.getOrDefault(w, false)) {
                edgeTo.put(w, v);
                dfs(w);
            } else if (onStack.getOrDefault(w, false)) {
                cycle = new Stack<>();
                for (String s = v; !s.equals(w); s = edgeTo.get(s)) {
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

    public ArrayList<String> cycle() {
        ArrayList<String> stackToList = new ArrayList<>();
        if(cycle == null)
            return stackToList;
        stackToList.addAll(cycle);
        Collections.reverse(stackToList);
        return stackToList;
    }
}
