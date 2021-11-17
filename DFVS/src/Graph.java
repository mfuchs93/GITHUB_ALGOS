import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Graph {

    private HashMap<Vertex, HashSet<Vertex>> inEdges = new HashMap<>();
    private HashMap<Vertex, HashSet<Vertex>> outEdges = new HashMap<>();

    public Graph() {
    }

    public Graph(Graph graph) {
        for (Vertex v : graph.getInEdges().keySet()) {
            this.inEdges.put(v, new HashSet<>());
            this.inEdges.get(v).addAll(graph.getInEdges().get(v));
        }
        for (Vertex v : graph.getOutEdges().keySet()) {
            this.outEdges.put(v, new HashSet<>());
            this.outEdges.get(v).addAll(graph.getOutEdges().get(v));
        }
    }

    public HashMap<Vertex, HashSet<Vertex>> getInEdges() {
        return inEdges;
    }

    public void setInEdges(HashMap<Vertex, HashSet<Vertex>> inEdges) {
        this.inEdges = inEdges;
    }

    public HashMap<Vertex, HashSet<Vertex>> getOutEdges() {
        return outEdges;
    }

    public void setOutEdges(HashMap<Vertex, HashSet<Vertex>> outEdges) {
        this.outEdges = outEdges;
    }

    public int getInDegree(Vertex v) {
        return inEdges.getOrDefault(v, new HashSet<>()).size();
    }

    public int getOutDegree(Vertex v) {
        return outEdges.getOrDefault(v, new HashSet<>()).size();
    }

    public Set<Vertex> getVertices() {
        return Stream.of(inEdges.keySet(), outEdges.keySet()).flatMap(Set::stream).collect(toSet());
    }

    @Override
    public String toString() {
        String graph = "Graph{\n";
        for (Vertex key : outEdges.keySet()) {
            for (Vertex neighbor : outEdges.get(key)) {
                graph += key.toString() + " --> " + neighbor.toString() + "\n";
            }
        }
        return graph + '}';
    }

    public Graph removeVertex(Vertex v) {
        Graph g = new Graph(this);
        g.inEdges.remove(v);
        for (HashSet<Vertex> in : g.inEdges.values()) {
            in.remove(v);
        }
        g.outEdges.remove(v);
        for (HashSet<Vertex> out : g.outEdges.values()) {
            out.remove(v);
        }
        v.setForbidden(true);
        return g;
    }
}