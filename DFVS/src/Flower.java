import java.util.*;
import java.util.stream.Collectors;

public class Flower {
    private Graph g;
    private Graph maxFlowGraph;
    private int[][] maxFlowMatrix;
    private int vertexCount;
    private HashSet<Vertex> empty = new HashSet<>();

    public Flower(Graph g) {
        this.g = g;
        this.maxFlowGraph = new Graph(g);
        convertToFlowGraph();
        HashSet<Vertex> vertices = (HashSet<Vertex>) this.maxFlowGraph.getVertices();
        for (int i = 0; i < vertexCount - 1; i += 2) {
            int flow = fordFulkerson(this.maxFlowMatrix, i, i + 1);
            int finalI = i;
            Vertex v = vertices.stream().filter(x -> x.getId() == finalI).findFirst().get();
            v.getParent().setMaxPetal(flow);
        }
        this.resetPetals();
    }

    public void petalOneRule(Graph g) {
        int removed = 0;
        HashSet<Vertex> s = new HashSet<>();
        for (Vertex v : g.getVertices()) {
            if (v.isForbidden()) break;
            if (v.getPetal() == 1 && !g.getInEdges().get(v).contains(v)) {
                for (Vertex u :
                        g.getInEdges().getOrDefault(v, empty)) {
                    g.getOutEdges().getOrDefault(v, empty).forEach(x -> {
                        g.getOutEdges().get(u).add(x);
                        g.getInEdges().get(x).add(u);
                    });
                }
                g.removeVertex(v, false, false);
                removed++;
            }
        }
        System.out.println("#removed by petalOne: "+ removed);
    }

    public ArrayList<Vertex> petalRule(int k) {
        ArrayList<Vertex> verticesToRemove = new ArrayList<>();
        Vertex removeVertex = findRemoveVertex(k, verticesToRemove);
        while (removeVertex != null) {
            this.g.getVertices().forEach(x -> x.setPetal(x.getPetal() - 1));
            verticesToRemove.add(removeVertex);
            removeVertex = findRemoveVertex(k, verticesToRemove);
        }
        return verticesToRemove;
    }

    private Vertex findRemoveVertex(int k, ArrayList<Vertex> verticesToRemove) {
        Vertex removeVertex = null;
        for (Vertex v :
                this.g.getVertices()) {
            if (v.getPetal() > k) {
                if (!verticesToRemove.contains(v) && (removeVertex == null || removeVertex.getPetal() > v.getPetal()))
                    removeVertex = v;
            }
        }
        return removeVertex;
    }

    public void resetPetals() {
        for (Vertex v : this.g.getVertices()) {
            v.setPetal(v.getMaxPetal());
        }
    }


    public void convertToFlowGraph() {
        HashSet<Vertex> vertices = (HashSet<Vertex>) this.maxFlowGraph.getVertices()
                .stream()
                .filter(v -> v.getPolarity() == '.')
                .collect(Collectors.toSet());
        this.maxFlowGraph.setId_counter(0);
        for (Vertex v : vertices) {
            this.maxFlowGraph.splitVertex(v);
        }
        this.vertexCount = this.maxFlowGraph.getVertices().size();
        convertToMatrix();
    }

    public void convertToMatrix() {
        this.maxFlowMatrix = new int[this.vertexCount][this.vertexCount];
        for (Map.Entry<Vertex, HashSet<Vertex>> set :
                this.maxFlowGraph.getOutEdges().entrySet()) {
            int i = set.getKey().getId();
            for (Vertex w :
                    set.getValue()) {
                int j = w.getId();
                this.maxFlowMatrix[i][j] = 1;
            }
        }
    }

    //https://www.geeksforgeeks.org/ford-fulkerson-algorithm-for-maximum-flow-problem/
    private boolean bfs(int[][] rGraph, int s, int t, int[] parent) {
        // Create a visited array and mark all vertices as
        // not visited
        boolean[] visited = new boolean[this.vertexCount];
        for (int i = 0; i < this.vertexCount; ++i)
            visited[i] = false;

        // Create a queue, enqueue source vertex and mark
        // source vertex as visited
        LinkedList<Integer> queue
                = new LinkedList<>();
        queue.add(s);
        visited[s] = true;
        parent[s] = -1;

        // Standard BFS Loop
        while (queue.size() != 0) {
            int u = queue.poll();

            for (int v = 0; v < this.vertexCount; v++) {
                if (!visited[v]
                        && rGraph[u][v] > 0) {
                    // If we find a connection to the sink
                    // node, then there is no point in BFS
                    // anymore We just have to set its parent
                    // and can return true
                    if (v == t) {
                        parent[v] = u;
                        return true;
                    }
                    queue.add(v);
                    parent[v] = u;
                    visited[v] = true;
                }
            }
        }

        // We didn't reach sink in BFS starting from source,
        // so return false
        return false;
    }

    //https://www.geeksforgeeks.org/ford-fulkerson-algorithm-for-maximum-flow-problem/
    // Returns tne maximum flow from s to t in the given
    // graph
    int fordFulkerson(int[][] graph, int s, int t) {
        int u, v;

        // Create a residual graph and fill the residual
        // graph with given capacities in the original graph
        // as residual capacities in residual graph

        // Residual graph where rGraph[i][j] indicates
        // residual capacity of edge from i to j (if there
        // is an edge. If rGraph[i][j] is 0, then there is
        // not)
        int[][] rGraph = new int[this.vertexCount][this.vertexCount];

        for (u = 0; u < this.vertexCount; u++)
            for (v = 0; v < this.vertexCount; v++)
                rGraph[u][v] = graph[u][v];

        // This array is filled by BFS and to store path
        int[] parent = new int[this.vertexCount];

        int max_flow = 0; // There is no flow initially

        // Augment the flow while there is path from source
        // to sink
        while (bfs(rGraph, s, t, parent)) {
            // Find minimum residual capacity of the edhes
            // along the path filled by BFS. Or we can say
            // find the maximum flow through the path found.
            int path_flow = Integer.MAX_VALUE;
            for (v = t; v != s; v = parent[v]) {
                u = parent[v];
                path_flow
                        = Math.min(path_flow, rGraph[u][v]);
            }

            // update residual capacities of the edges and
            // reverse edges along the path
            for (v = t; v != s; v = parent[v]) {
                u = parent[v];
                rGraph[u][v] -= path_flow;
                rGraph[v][u] += path_flow;
            }

            // Add path flow to overall flow
            max_flow += path_flow;
        }

        // Return the overall flow
        return max_flow;
    }

    public Graph getG() {
        return g;
    }

    public void setG(Graph g) {
        this.g = g;
    }

    public Graph getMaxFlowGraph() {
        return maxFlowGraph;
    }

    public void setMaxFlowGraph(Graph maxFlowGraph) {
        this.maxFlowGraph = maxFlowGraph;
    }

    public int[][] getMaxFlowMatrix() {
        return maxFlowMatrix;
    }

    public void setMaxFlowMatrix(int[][] maxFlowMatrix) {
        this.maxFlowMatrix = maxFlowMatrix;
    }
}
