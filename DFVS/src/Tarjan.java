import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class Tarjan {
    private final Graph g;
    private int index;
    private final ArrayList<Vertex> unvisitedVertices;
    private final Stack<Vertex> verticesInProgress;
    private final HashSet<Graph> subGraphs = new HashSet<>();

    public Tarjan(Graph g) {
        this.g = g;
        this.index = 0;
        this.unvisitedVertices = new ArrayList<Vertex>(g.getVertices());
        this.verticesInProgress = new Stack<>();
        while (!unvisitedVertices.isEmpty()) {
            tarjan(unvisitedVertices.get(0));
        }
    }

    public void tarjan(Vertex v) {
        v.setDfsIndex(index);
        v.setDfsLowLink(index++);
        verticesInProgress.push(v);
        unvisitedVertices.remove(v);
        for (Vertex w : g.getOutEdges().get(v)) {
            if (unvisitedVertices.contains(w)) {
                tarjan(w);
                v.setDfsLowLink(min(v.getDfsLowLink(), w.getDfsLowLink()));
            } else if (verticesInProgress.contains(w)) {
                v.setDfsLowLink(min(v.getDfsLowLink(), w.getDfsIndex()));
            }
        }
        if (v.getDfsLowLink() == v.getDfsIndex()) {
            System.out.println("SZK");
            HashSet<Vertex> vertices = new HashSet<>();
            while (verticesInProgress.contains(v)) {
                Vertex z = verticesInProgress.pop();
                System.out.println(z);
                vertices.add(z);
            }
            if (vertices.size() > 1) {
                buildSCC(vertices);
            }

        }
    }

    private void buildSCC(HashSet<Vertex> vertices) {
        Graph subGraph = new Graph();
        for (Vertex v : g.getInEdges().keySet()) {
            if (vertices.contains(v)) {
                subGraph.getInEdges().put(v, new HashSet<>());
                subGraph.getInEdges().get(v).addAll(g.getInEdges().get(v)
                        .stream()
                        .filter(vertices::contains)
                        .collect(Collectors.toSet()));
            }
        }
        for (Vertex v : g.getOutEdges().keySet()) {
            if (vertices.contains(v)) {
                subGraph.getOutEdges().put(v, new HashSet<>());
                subGraph.getOutEdges().get(v).addAll(g.getOutEdges().get(v)
                        .stream()
                        .filter(vertices::contains)
                        .collect(Collectors.toSet()));
            }
        }
        subGraphs.add(subGraph);
    }

    public HashSet<Graph> SCC() {
        HashSet<Graph> empty = new HashSet<>();
        if(subGraphs == null) {
            return empty;
        }
        return subGraphs;
    }
}
