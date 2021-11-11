import java.util.*;

public class DFVS {

    public static HashSet<Vertex> branch(Graph g, int k) {
        if(Thread.interrupted()){
            System.out.println("Timeout");
            System.exit(1);
        }
        if(k < 0) return null;
        HashSet<Vertex> s = new HashSet<>();
        ArrayList<Vertex> cycle = new Cycle(g).cycle();
        if(cycle.isEmpty()) {
            return s;
        }
        for(int i = 0; i < cycle.size(); i++) {
            s = branch(g.removeVertex(cycle.get(i)), k - 1);
            if(s != null) {
                s.add(cycle.get(i));
                return s;
            }
        }
        return null;
    }

    public static HashSet<Vertex> solve(Graph g) {
        int k = 0;
        HashSet<Vertex> s = null;
        while(s == null) {
            s = branch(g, k);
            k = k + 1;
        }
        return s;
    }
}
