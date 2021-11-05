import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Graph {

    private HashMap<String, HashSet<String>> inEdges = new HashMap<>();
    private HashMap<String, HashSet<String>> outEdges = new HashMap<>();

    public Graph(){}

    public Graph(Graph graph) {
        for (String v : graph.getInEdges().keySet()) {
            this.inEdges.put(v, new HashSet<>());
            this.inEdges.get(v).addAll(graph.getInEdges().get(v));
        }
        for (String v : graph.getOutEdges().keySet()) {
            this.outEdges.put(v, new HashSet<>());
            this.outEdges.get(v).addAll(graph.getOutEdges().get(v));
        }
    }

    public HashMap<String, HashSet<String>> getInEdges() {
        return inEdges;
    }

    public void setInEdges(HashMap<String, HashSet<String>> inEdges) {
        this.inEdges = inEdges;
    }

    public HashMap<String, HashSet<String>> getOutEdges() {
        return outEdges;
    }

    public void setOutEdges(HashMap<String, HashSet<String>> outEdges) {
        this.outEdges = outEdges;
    }

    public int getInDegree(String v) {
        return inEdges.getOrDefault(v, new HashSet<>()).size();
    }

    public int getOutDegree(String v) {
        return outEdges.getOrDefault(v, new HashSet<>()).size();
    }

    public Set<String> getVertices() {
        return Stream.of(inEdges.keySet(), outEdges.keySet()).flatMap(Set::stream).collect(toSet());
    }

    @Override
    public String toString() {
        String graph = "Graph{\n";
        for (String key : outEdges.keySet()) {
            for (String neighbor : outEdges.get(key)) {
                graph += key.toString() + " --> " + neighbor.toString() + "\n";
            }
        }
        return graph + '}';
    }

    public Graph removeVertex(String v) {
        Graph g = new Graph(this);
        g.inEdges.remove(v);
        for (HashSet<String> in : g.inEdges.values()) {
            in.remove(v);
        }
        g.outEdges.remove(v);
        for (HashSet<String> out : g.outEdges.values()) {
            out.remove(v);
        }
        return g;
    }
}