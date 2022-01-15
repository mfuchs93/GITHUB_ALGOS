import gurobi.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class ILPOrder {


    public static void main(String[] args) {
        try {
            long startTime = System.currentTimeMillis();
            InputStream in = new FileInputStream(args[0]);
            Graph g = Main.readGraphFromFile(in);

            // Create empty environment, set options, and start
            GRBEnv env = new GRBEnv(true);
            env.set(GRB.IntParam.OutputFlag, 0);
            env.start();
            // Create empty model
            GRBModel model = new GRBModel(env);
            GRBLinExpr expr = new GRBLinExpr();
            for (Vertex v :
                    g.getVertices()) {
                GRBVar x = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + v.getName());
                GRBVar u = model.addVar(1.0, g.getVertices().size(), 0.0, GRB.INTEGER, "u" + v.getName());
                expr.addTerm(1.0, x);
            }
            model.setObjective(expr, GRB.MINIMIZE);
            model.update();
            int i = 0;
            for (Vertex w:
                    g.getOutEdges().keySet()) {
                GRBVar uw = model.getVarByName("u" + w.getName());
                GRBVar xw = model.getVarByName("x" + w.getName());
                for (Vertex v :
                        g.getOutEdges().get(w)) {
                    GRBVar uv = model.getVarByName("u" + v.getName());
                    GRBLinExpr orderexpr = new GRBLinExpr();
                    orderexpr.addTerm(1.0, uv);
                    orderexpr.addTerm(-1.0, uw);
                    orderexpr.addTerm(g.getVertices().size(), xw);
                    model.addConstr(orderexpr,GRB.GREATER_EQUAL, 1.0, "edge" + i++);
                }
            }
            ArrayList<ArrayList<Vertex>> cycles = new Cycle(g, SearchType.SHORT_CYCLE, true).getCycles();
            int j=0;
            for (ArrayList<Vertex> cycle :
                    cycles) {
                expr = new GRBLinExpr();
                for (Vertex v :
                        cycle) {
                    expr.addTerm(1.0, model.getVarByName("x" + v.getName()));
                }
                model.addConstr(expr,GRB.GREATER_EQUAL,1.0, "c" + j++);
            }
            model.update();
            model.optimize();

            System.out.println("#time: " + (System.currentTimeMillis() - startTime));

            for (GRBVar var:model.getVars()){
                if (var.get(GRB.DoubleAttr.X) >= 0.5 && var.get(GRB.StringAttr.VarName).startsWith("x")) System.out.println(var.get(GRB.StringAttr.VarName).substring(1));
            }


        } catch (GRBException | IOException e) {
            e.printStackTrace();
        }
    }
}
