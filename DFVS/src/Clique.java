import java.util.*;

public class Clique {
    private Graph g;
    //size of cliques to find
    private int k;
    //mapping id to Vertex object
    private Vertex[] vertices;

    public ArrayList<HashSet<Vertex>> getCliques() {
        return cliques;
    }

    ArrayList<HashSet<Vertex>> cliques;

    private int MAX;

    // Stores the vertices
    private int[] store;
    private int n;

    // Graph
    private int[][] graph;

    // Degree of the vertices
    private int[] d;

    public Clique(Graph g, int k) {
        this.g = g;
        this.k = k;
        this.MAX = g.getId_counter() + 1;
        this.store = new int[MAX];
        this.graph = new int[MAX][MAX];
        this.d = new int[MAX];
        this.n = 0;
        this.vertices = new Vertex[MAX];
        this.cliques = new ArrayList<>();
        convertGraph();
        for (int i :
                d) {
            if (i > 0) this.n++;
        }
        //findCliques(0, 1, k, false);
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
    private void findCliques(int i, int l, int s, boolean findOne) {
        // Check if any vertices from i+1 can be inserted
        for (int j = i + 1; j <= n - (s - l); j++)

            // If the degree of the graph is sufficient
            if (d[j] >= s - 1) {

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

    public HashSet<Vertex> triangleRule() {
        findCliques(0, 1, this.k, false);
        boolean[] shouldBeDeleted = new boolean[this.MAX];
        HashSet<Vertex> verticesToDelete = new HashSet<>();
        for (HashSet<Vertex> clique :
                cliques) {
            Iterator<Vertex> iter = clique.iterator();
            Vertex u = iter.next();
            Vertex v = iter.next();
            Vertex w = iter.next();
            Vertex x = null;
            int outX = 0;
            int inX = 0;
            if (this.k == 4) {
                x = iter.next();
                outX = this.g.getOutDegree(x);
                inX = this.g.getInDegree(x);
            }

            //return list of vertices to delete
            int outU = this.g.getOutDegree(u);
            int outV = this.g.getOutDegree(v);
            int outW = this.g.getOutDegree(w);
            int inU = this.g.getInDegree(u);
            int inV = this.g.getInDegree(v);
            int inW = this.g.getInDegree(w);

            if (outU == this.k - 1 || inU == this.k - 1) {
                shouldBeDeleted[v.getId()] = true;
                shouldBeDeleted[w.getId()] = true;
                if (this.k == 4) shouldBeDeleted[x.getId()] = true;
            } else if (outV == this.k - 1 || inV == this.k - 1) {
                shouldBeDeleted[u.getId()] = true;
                shouldBeDeleted[w.getId()] = true;
                if (this.k == 4) shouldBeDeleted[x.getId()] = true;
            } else if (outW == this.k - 1 || inW == this.k - 1) {
                shouldBeDeleted[u.getId()] = true;
                shouldBeDeleted[v.getId()] = true;
                if (this.k == 4) shouldBeDeleted[x.getId()] = true;
            } else if (outX == this.k - 1 || inX == this.k - 1) {
                shouldBeDeleted[u.getId()] = true;
                shouldBeDeleted[v.getId()] = true;
                shouldBeDeleted[w.getId()] = true;
            }
            if (shouldBeDeleted[u.getId()]) verticesToDelete.add(u);
            if (shouldBeDeleted[v.getId()]) verticesToDelete.add(v);
            if (shouldBeDeleted[w.getId()] && verticesToDelete.size() < k - 1) verticesToDelete.add(w);
            if (x != null && shouldBeDeleted[x.getId()] && verticesToDelete.size() < k - 1) verticesToDelete.add(x);
        }
        return verticesToDelete;
    }
}
