import gurobi.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;

public class ILPLazy {

    public static void main(String[] args) {
        try {
            InputStream in = new FileInputStream(args[0]);
            Graph g = Main.readGraphFromFile(in);
            HashSet<Vertex> solution = ReductionRules.chainingRule(g);
            solution.forEach(v -> System.out.println(v.getName()));

            // Create empty environment, set options, and start
            GRBEnv env = new GRBEnv(true);
            //env.set(GRB.IntParam.Threads, 1);
            env.set(GRB.IntParam.OutputFlag, 0);
            env.start();
            // Create empty model
            GRBModel model = new GRBModel(env);
            model.set(GRB.IntParam.LazyConstraints, 1);
            GRBLinExpr expr = new GRBLinExpr();
            for (Vertex v :
                    g.getVertices()) {
                GRBVar x = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, v.getName());
                expr.addTerm(1.0, x);
            }
            model.setObjective(expr, GRB.MINIMIZE);
            model.update();
            ArrayList<ArrayList<Vertex>> cycles = new Cycle(g, SearchType.SHORT_CYCLE, true, true).getCycles();
            int i = 0;
            for (ArrayList<Vertex> cycle :
                    cycles) {
                expr = new GRBLinExpr();
                for (Vertex v :
                        cycle) {
                    expr.addTerm(1.0, model.getVarByName(v.getName()));
                }
                model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c" + i++);
            }
            // Open log file
            model.update();
            MyCallback cb = new MyCallback(model.getVars(), g);
            model.setCallback(cb);
            model.update();
            model.optimize();
            for (GRBVar var : model.getVars()) {
                if (var.get(GRB.DoubleAttr.X) >= 0.5) System.out.println(var.get(GRB.StringAttr.VarName));
            }
        } catch (GRBException | IOException e) {
            e.printStackTrace();
        }
    }
}
