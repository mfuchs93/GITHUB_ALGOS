import java.util.*;

public class DFVS {

    public static HashSet<String> branch(Graph g, int k) {
        if(Thread.interrupted()){
            System.out.println("Timeout");
            System.exit(1);
        }
        if(k < 0) return null;
        HashSet<String> s = new HashSet<>();
        ArrayList<String> cycle =(ArrayList<String>) new Cycle(g).cycle();
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

    public static HashSet<String> solve(Graph g) {
        int k = 0;
        HashSet<String> s = null;
        while(s == null) {
           // System.out.println("value of k is " + k);
            s = branch(g, k);
            k = k + 1;
        }
        //System.out.println("Solution is " + s);
        return s;
    }
}
