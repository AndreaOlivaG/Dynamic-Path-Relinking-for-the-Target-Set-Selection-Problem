package constructives;

import grafo.optilib.results.Result;
import structure.TSSInstance;
import structure.TSSSolution;
import gurobi.*;

public class ProcedureGurobi {
    private final double timeLimit;

    public ProcedureGurobi(double timeLimit){
        this.timeLimit = timeLimit;
    }

    public Result execute(TSSInstance instance) {
        int n = instance.getRow();
        System.out.print(instance.getName() + "\t");
        Result r = new Result(instance.getName());

        try {
            // En ese fichero se mete lo que pase durante la ejecución
            GRBEnv env = new GRBEnv("log_edp.txt");

            // Tiempo máximo que dejamos ejecutar, en segundos
            env.set(GRB.DoubleParam.TimeLimit, this.timeLimit);

            // Este valor a 0 para que no aparezca información por consola
            env.set(GRB.IntParam.LogToConsole, 0);

            // Starts writing nodes to disk when reaching XXX M
            // Para volcar a disco, sirve para instancias muy grandes
            // env.set(GRB.DoubleParam.NodefileStart, 0.05);

            // Reduce the number of threads to reduce memory usage
            // Para limitar la ejecución a 1 hilo
            // env.set(GRB.IntParam.Threads, 1);

            // Presolve 0 off 1 conservative 2 aggresive
            // Si pones a 0, Gurobi no hace cálculos previos a la ejecución
            // env.set(GRB.IntParam.Presolve, 0);

            GRBModel model = new GRBModel(env);

            // VARIABLES
            GRBVar[][] x = new GRBVar[n][n];
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    x[i][j] = model.addVar(0.0, 1.0, 1.0, GRB.BINARY, "x["+i+"]["+j+"]");
                    // Primero: cota inferior
                    // Segundo: cota superior
                    // Tercero: objetivo ideal
                    // Cuarto: tipo de variable
                    // Quinto: nombre único para la variable
                }
            }

            // OBJECTIVE FUNCTION
            GRBLinExpr objFunc = new GRBLinExpr();
            for (int i = 0; i < n; i++) {
                objFunc.addTerm(instance.getReward()[i], x[i][n-1]);
            }
            model.setObjective(objFunc, GRB.MAXIMIZE);

            // CONSTRAINTS
            // 1
            for (int t = 1; t < n; t++) {
                for (int i = 0; i < n; i++) {
                    model.addConstr(x[i][t-1], GRB.LESS_EQUAL, x[i][t], "r1 -> -> t = " + t + ", i = " + i);
                }
            }

            // 2
            for (int t = 1; t < n; t++) {
                for (int i = 0; i < n; i++) {
                    GRBLinExpr r2 = new GRBLinExpr();
                    for (int j = 0; j < n; j++) {
                        if (i != j) {
                            r2.addTerm(instance.getPsi()[j][i], x[j][t-1]);
                        }
                    }
                    r2.addTerm(1, x[i][0]);
                    model.addConstr(r2, GRB.GREATER_EQUAL, x[i][t], "r2 -> t = " + t + ", i = " + i);
                }
            }

            // 3
            double eps = Math.pow(10, -6);
            for (int t = 1; t < n; t++) {
                for (int i = 0; i < n; i++) {
                    GRBLinExpr r3 = new GRBLinExpr();
                    for (int j = 0; j < n; j++) {
                        if (i != j) {
                            GRBLinExpr r3_aux = new GRBLinExpr();
                            r3_aux.addTerm(1, x[i][t]);
                            r3_aux.addTerm(-1, x[j][t-1]);
                            r3.multAdd(instance.getPsi()[j][i], r3_aux);
                        }
                    }
                    model.addConstr(r3, GRB.GREATER_EQUAL, -1 + eps, "r3 -> t = " + t + ", i = " + i);
                }
            }

            // 6
            GRBLinExpr r6 = new GRBLinExpr();
            for (int i = 0; i < n; i++) {
                r6.addTerm(instance.getEffort()[i], x[i][0]);
            }
            model.addConstr(r6, GRB.LESS_EQUAL, instance.getK_max(), "r6");

            model.update();
            long totalTime = System.currentTimeMillis();
            model.optimize();

            int status = model.get(GRB.IntAttr.Status);
            System.out.print(status+"\t");
            totalTime = System.currentTimeMillis() - totalTime;
            float secs = totalTime / 1000f;
            System.out.print(secs+"\t");
            r.add("Time (s)", secs);

            if (status != GRB.INFEASIBLE) {
                int of = (int)(model.get(GRB.DoubleAttr.ObjVal));
                System.out.println(of);
                r.add("OF", (float) of);
            } else {
                System.out.println(-1);
                r.add("OF", -1);
            }
            r.add("Status", status);

            TSSSolution solution = new TSSSolution(instance);
            if (status != GRB.INFEASIBLE) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (Double.compare(Math.rint(x[i][0].get(GRB.DoubleAttr.X)), 1) == 0)
                            solution.addValue(i);
                    }
                }
            }

            model.dispose();
            env.dispose();

        } catch (GRBException e) {
            e.printStackTrace();
        }

        return r;
    }
}
