package constructives;

import grafo.optilib.metaheuristics.Constructive;
import structure.Candidate;
import structure.TSSInstance;
import structure.TSSSolution;

import java.util.*;

public class TSSGRASP_Heuristic implements Constructive<TSSInstance, TSSSolution> {

    private final Random rnd;
    private final double base_alpha;

    public TSSGRASP_Heuristic(double alpha) {
        this.rnd = new Random(80);
        this.base_alpha = alpha;
    }

    @Override
    public TSSSolution constructSolution(TSSInstance instance) {
        TSSSolution solution = new TSSSolution(instance);
        int numElems = instance.getCol();
        List<Candidate> CL = new ArrayList<>(numElems);
        int[] numAdjList = instance.getNumAdjList();
        int node;

        double alpha;
        if (base_alpha == -1) {
            alpha = rnd.nextDouble(1);
        } else {
            alpha = this.base_alpha;
        }

        do {
            node = rnd.nextInt(numElems);
        } while (!solution.isFeasible(node));
        solution.addValue(node);

        for (int i = 0; i < numElems; i++) {
            if (solution.isFeasible(i))
                CL.add(new Candidate(i, numAdjList[i]));
        }

        int vals = 1;
        while ((vals - 1) != CL.size()) {
            Collections.sort(CL);
            int tope = 0;
            float maxValue = CL.get(0).getNumAdjs();
            float minValue = CL.get(CL.size()-vals).getNumAdjs();

            double threshold = maxValue - alpha * (maxValue - minValue);
            // Si alpha == 0 (me quedo con maxValue), es voraz
            // Si alpha == 1, es totalmente aleatorio
            // Si mejoro, bajo alpha

            for (Candidate candidate : CL) {
                if (candidate.getNumAdjs() >= threshold) {
                    tope += 1;
                }
            }
            int pos = rnd.nextInt(tope);
            int x = CL.get(pos).getNode();
            solution.addValue(x);

            CL.get(pos).setNumAdjs(-1);
            vals += 1;

            for (Candidate candidate : CL) {
                if (candidate.getNumAdjs() == -1)
                    break;
                if (!solution.isFeasible(candidate.getNode())) {
                    candidate.setNumAdjs(-1);

                    vals += 1;
                }
            }
        }
        return solution;
    }

}
