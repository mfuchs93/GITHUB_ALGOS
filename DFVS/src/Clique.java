import java.util.*;
import java.util.stream.Collectors;

public class Clique {
    private final Graph g;
    //size of cliques to find
    private int k;
    //mapping id to Vertex object
    private final Vertex[] vertices;
    ArrayList<HashSet<Vertex>> cliques = new ArrayList<>();
    private final int MAX;
    // Stores the vertices
    private final int[] store;
    private int n;

    public void setCliques(ArrayList<HashSet<Vertex>> cliques) {
        this.cliques = cliques;
    }

    // Graph
    private final int[][] graph;
    // Degree of the vertices
    private final int[] d;

    public Clique(Graph g, int k) {
        this.g = g;
        this.k = k;
        this.MAX = g.getId_counter() + 1;
        this.store = new int[MAX];
        this.graph = new int[MAX][MAX];
        this.d = new int[MAX];
        this.n = 0;
        this.vertices = new Vertex[MAX];
        convertGraph();
        for (int i :
                d) {
            if (i > 0) this.n++;
        }
        if (n > 0){
            if (this.n < 10) this.k = this.n;
            findCliques(0, 1, this.k, false);
            cliques.sort((vertices, t1) -> t1.size() - vertices.size());
        }
    }

    public ArrayList<HashSet<Vertex>> getIndependentCliques() {
        ArrayList<HashSet<Vertex>> cliques = new ArrayList<>();
        for (HashSet<Vertex> set :
                this.getCliques()) {
            cliques.add(new HashSet<>(set));
        }
        ArrayList<HashSet<Vertex>> noIntersections = new ArrayList<>();
        while (!cliques.isEmpty()) {
            HashSet<Vertex> c = cliques.remove(0);
            noIntersections.add(c);
            cliques.removeIf(x -> x.stream().anyMatch(c::contains));
        }
        return noIntersections;
    }

    public void convertGraph() {
        for (Map.Entry<Vertex, HashSet<Vertex>> entries :
                g.getOutEdges().entrySet()) {
            Vertex v = entries.getKey();
            for (Vertex w : entries.getValue()) {
                if (g.getInEdges().getOrDefault(v, new HashSet<>()).contains(w)) {
                    graph[v.getId() + 1][w.getId() + 1] = 1;
                    graph[w.getId() + 1][v.getId() + 1] = 1;
                    d[v.getId() + 1]++;
                    vertices[v.getId() + 1] = v;
                }
            }
        }
    }

    public ArrayList<HashSet<Vertex>> getCliques() {
        return cliques;
    }

    // Function to check if the given set of vertices
    // in store array is a clique or not
    private boolean is_clique(int b) {
        // Run a loop for all the set of edges
        // for the select vertex
        for (int i = 1; i < b; i++) {
            for (int j = i + 1; j < b; j++)

                // If any edge is missing
                if (graph[store[i]][store[j]] == 0)
                    return false;
        }
        return true;
    }

    // Function to print the clique
    private void print(int n) {
        HashSet<Vertex> clique = new HashSet<>(k);
        for (int i = 1; i < n; i++) {
            //System.out.print(store[i] + " ");
            clique.add(vertices[store[i]]);
        }
        //System.out.print("\n");
        this.cliques.add(clique);
    }

    // Function to find all the cliques of size s
    public void findCliques(int i, int l, int s, boolean findOne) {
        // Check if any vertices from i+1 can be inserted
        for (int j = i + 1; j <= n - (s - l); j++)

            // If the degree of the graph is sufficient
            if (d[j] >= l - 1) {

                // Add the vertex to store
                store[l] = j;

                // If the graph is not a clique of size k
                // then it cannot be a clique
                // by adding another edge
                if (is_clique(l + 1)) {
                    if (findOne && this.cliques.size() > 0) return;

                    // If the length of the clique is
                    // still less than the desired size
                    if (l < s) {
                        // Recursion to add vertices
                        findCliques(j, l + 1, s, findOne);
                        if (l > 2) print(l+1);

                        if (findOne && this.cliques.size() > 0) return;
                    }
                    // Size is met
                    else {
                        print(l + 1);
                        if (findOne && this.cliques.size() > 0) return;
                    }
                }
            }
    }

    public void setK(int k) {
        this.k = k;
    }

    public void removeVertex(Vertex v) {
        for (int i = 0; i < this.MAX; i++) {
            this.graph[i][v.getId() + 1] = 0;
            this.graph[v.getId() + 1][i] = 0;
        }
    }

    public int getK() {
        return k;
    }

    public HashSet<Vertex> cliqueRule() {
        HashSet<Vertex> verticesToDelete = new HashSet<>();
        for (HashSet<Vertex> clique :
                cliques) {
            for (Vertex v :
                    clique) {
                if (this.g.getOutDegree(v) == clique.size() - 1 || this.g.getInDegree(v) == clique.size() - 1) {
                    verticesToDelete.addAll(clique.stream().filter(vertex -> vertex != v).collect(Collectors.toSet()));
                    break;
                }
            }
        }
        this.cliques.removeIf(x -> x.stream().anyMatch(verticesToDelete::contains));
        verticesToDelete.forEach(this::removeVertex);
        return verticesToDelete;
    }
}
