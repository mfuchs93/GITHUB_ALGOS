import java.util.*;

public class DFVS {

    public static HashSet<Vertex> branch(Graph g, int k) {
        if (Thread.interrupted()) {
            System.out.println("Timeout");
            System.exit(1);
        }
        if (k < 0) return null;
        HashSet<Vertex> s = new HashSet<>();
        HashSet<Graph> subGraphs = new Tarjan(g).SCC();
        if (subGraphs.isEmpty()) return s;
        if (subGraphs.size() > k) return null;
        int counter = 1; // wieviele wurden gelöscht
        int restK = k; // wieviele können wir in den restlichen SubGraphen noch löschen
        HashSet<Vertex> solution = new HashSet<>();
        for (Graph subGraph :
                subGraphs) {
            ArrayList<Vertex> cycle = new Cycle(subGraph).cycle();
            if (cycle.isEmpty()) {  //kann eigentlich hier nicht vorkommen, da subGraph mindestens einen Kreis hat!
                return s;
            }
            while (restK - counter >= 0) {
                for (Vertex vertex : cycle) {
                    s = branch(subGraph.removeVertex(vertex), counter - 1);
                    if (s != null) {
                        s.add(vertex);
                        break;
                    }
                }
                if (s != null) {
                    restK -= counter;
                    counter = 1;
                    break;
                } else {
                    counter++;
                }
            }
            if (s == null) {
                return null;
            } else {
                solution.addAll(s);
                s = null;
            }
        }
        return solution;
    }

    public static HashSet<Vertex> solve(Graph g) {
        int k = 1; //können wir hier mit k = 1 starten? wenn wir in branch(mit tarjan keine SCC mit size größer 1 finden,
        // geben wir eh nichts aus. müssen ja auch nichts löschen
        HashSet<Vertex> s = null;
        while (s == null) {
            s = branch(g, k);
            k = k + 1;
        }
//        for (Vertex v :
//                s) {
//            g = g.removeVertex(v);
//        }
//        HashSet<Graph> subGraphs = new Tarjan(g).SCC();
        return s;
    }
}
