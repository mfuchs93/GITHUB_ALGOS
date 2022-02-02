import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

public class Graph implements Comparable<Graph> {
    HashSet<Vertex> empty = new HashSet<>();

    private HashMap<Vertex, HashSet<Vertex>> inEdges = new HashMap<>();
    private HashMap<Vertex, HashSet<Vertex>> outEdges = new HashMap<>();

    private int id_counter;

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
        this.id_counter = graph.id_counter;
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
        StringBuilder graph = new StringBuilder("Graph{\n");
        for (Vertex key : outEdges.keySet()) {
            for (Vertex neighbor : outEdges.get(key)) {
                graph.append(key.toString()).append(" --> ").append(neighbor.toString()).append("\n");
            }
        }
        return graph.toString() + '}';
    }

    public Graph removeVertex(Vertex v, boolean makeCopy, boolean setForbidden) {
        Graph g;
        if (makeCopy) {
            g = new Graph(this);
        } else {
            g = this;
        }
        g.inEdges.remove(v);
        for (HashSet<Vertex> in : g.inEdges.values()) {
            in.remove(v);
        }
        g.outEdges.remove(v);
        for (HashSet<Vertex> out : g.outEdges.values()) {
            out.remove(v);
        }
        if (setForbidden) {
            v.setForbidden(true);
        }
        return g;
    }

    public Graph reverse() {
        Graph g = new Graph(this);
        HashMap<Vertex, HashSet<Vertex>> tmp = g.getOutEdges();
        g.setOutEdges(g.getInEdges());
        g.setInEdges(tmp);
        return g;
    }

    public int getDegree(Vertex v){
        return this.getOutDegree(v) + this.getInDegree(v);
    }

    @Override
    public int compareTo(Graph graph) {
        return this.getVertices().size() - graph.getVertices().size();
//        if (this.getVertices().size() <= graph.getVertices().size()) {
//            return -1;
//        } else {
//            return 1;
//        }
    }

    public void removeEdge(Vertex source, Vertex target) {
        this.getOutEdges().get(source).remove(target);
        this.getInEdges().get(target).remove(source);
    }
    public void splitVertex(Vertex v) {
        //new v+ and v- vertices
        Vertex v_plus = new Vertex(this.id_counter++, v.getName());
        v_plus.setPolarity('+');
        v_plus.setParent(v);
        Vertex v_minus = new Vertex(this.id_counter++, v.getName());
        v_minus.setPolarity('-');
        v_minus.setParent(v);
        //save in and out edges of v
        this.outEdges.put(v_plus, this.outEdges.getOrDefault(v, empty));
        for (Vertex w : this.outEdges.get(v_plus)) {
            this.inEdges.get(w).add(v_plus);
        }
        this.inEdges.put(v_minus, this.inEdges.getOrDefault(v, empty));
        for (Vertex w : this.inEdges.get(v_minus)) {
            this.outEdges.get(w).add(v_minus);
        }
        //remove v
        this.removeVertex(v, false, false);

        this.outEdges.put(v_minus, new HashSet<>(Arrays.asList(v_plus)));
        this.inEdges.put(v_plus, new HashSet<>(Arrays.asList(v_minus)));

    }

    public int getId_counter() {
        return id_counter;
    }

    public void setId_counter(int id_counter) {
        this.id_counter = id_counter;
    }
}