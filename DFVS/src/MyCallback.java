import gurobi.*;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MyCallback extends GRBCallback {
    private double lastiter;
    private double lastnode;
    private GRBVar[] vars;
    Graph g;

    public MyCallback(GRBVar[] xvars, Graph xg) {
        lastiter = lastnode = -GRB.INFINITY;
        vars = xvars;
        g = xg;
    }

    @Override
    protected void callback() {
        try {
            if (where == GRB.Callback.MIPSOL) {
                // General MIP callback
                ArrayList<GRBVar> solVertices = new ArrayList<>();
                double[] sol = getSolution(vars);
                for (int i=0; i < vars.length; i++) {
                    if (sol[i] > 0.0){
                        solVertices.add(vars[i]);
                    }
                }
                Graph h = new Graph(g);
                ArrayList<String> solNames = new ArrayList<>();
                for (GRBVar xvar :
                        solVertices) {
                    solNames.add(xvar.get(GRB.StringAttr.VarName));
                }
                g.getVertices().stream().filter(vertex -> solNames.contains(vertex.getName())).forEach(v -> h.removeVertex(v, false, false));
                ArrayList<Vertex> cycle = new Cycle(h, SearchType.SHORT_CYCLE, false).cycle();
                if (!cycle.isEmpty()) {
                    GRBLinExpr expr = new GRBLinExpr();
                    for (Vertex v :
                            cycle) {
                        String vName = v.getName();
                        GRBVar var = null;
                        for (GRBVar xvar :
                                vars) {
                            if (xvar.get(GRB.StringAttr.VarName).equals(vName)) var = xvar;
                        }
                        expr.addTerm(1.0, var);
                    }
                    addLazy(expr, GRB.GREATER_EQUAL, 1.0);
                }
            }
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }
}
