import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;

public class Tarjan {
    private final Graph g;
    private int index;
    private final ArrayList<Vertex> unvisitedVertices;
    private final Stack<Vertex> verticesInProgress;
    private final HashSet<Graph> subGraphs = new HashSet<>();
    private int k;

    public Tarjan(Graph g) {
        this.g = g;
        this.index = 0;
        this.unvisitedVertices = new ArrayList<>(g.getVertices());
        this.verticesInProgress = new Stack<>();
        while (!unvisitedVertices.isEmpty()) {
            tarjan(unvisitedVertices.get(0));
        }
        //System.out.println("--------------------------------------------------------------------------------");
    }

    public void tarjan(Vertex v) {
        v.setDfsIndex(index);
        v.setDfsLowLink(index++);
        verticesInProgress.push(v);
        unvisitedVertices.remove(v);
        for (Vertex w : g.getOutEdges().getOrDefault(v, new HashSet<>())) {
            if (unvisitedVertices.contains(w)) {
                tarjan(w);
                v.setDfsLowLink(min(v.getDfsLowLink(), w.getDfsLowLink()));
            } else if (verticesInProgress.contains(w)) {
                v.setDfsLowLink(min(v.getDfsLowLink(), w.getDfsIndex()));
            }
        }
        if (v.getDfsLowLink() == v.getDfsIndex()) {
            //System.out.println("SZK");
            HashSet<Vertex> vertices = new HashSet<>();
            while (verticesInProgress.contains(v)) {
                Vertex z = verticesInProgress.pop();
                //System.out.println(z);
                vertices.add(z);
            }
            if (vertices.size() == 1) {
                Vertex x = vertices.iterator().next();
                if (g.getOutEdges().getOrDefault(x, new HashSet<>()).contains(x) && !x.isForbidden()) {
                    buildSCC(vertices);
                }
            } else if (vertices.size() > 1) {
                buildSCC(vertices);
                // System.out.println("SZK: " + vertices);
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
        return subGraphs;
    }
}
