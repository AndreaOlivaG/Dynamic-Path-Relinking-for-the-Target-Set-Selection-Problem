package algorithms;

import grafo.optilib.metaheuristics.Algorithm;
import grafo.optilib.metaheuristics.Constructive;
import grafo.optilib.metaheuristics.Improvement;
import grafo.optilib.results.Result;
import grafo.optilib.structure.Solution;
import org.apache.commons.collections4.iterators.ReverseListIterator;
import structure.TSSInstance;
import structure.TSSSolution;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class DPRAlg implements Algorithm<TSSInstance> {

    Constructive<TSSInstance, TSSSolution> constructive;
    Improvement<TSSSolution> improvement;
    int numSi;
    int numSg;
    Random rnd;

    public DPRAlg(Constructive<TSSInstance, TSSSolution> constructive, Improvement<TSSSolution> improvement,
                  int numSi, int initialPopulation) {
        this.constructive = constructive;
        this.improvement = improvement;
        this.numSi = numSi;
        this.numSg = initialPopulation;
        this.rnd = new Random(80);
    }

    @Override
    public Result execute(TSSInstance tssInstance) {
        final long startTime = System.nanoTime();
        TSSSolution pathSol = new TSSSolution(tssInstance);
        TSSSolution finalBest = new TSSSolution(tssInstance);
        List<Integer> aux1 = new ArrayList<>();
        List<Integer> aux2 = new ArrayList<>();
        int  size;
        int iterations = 0;

        System.out.println("INSTANCE: " + tssInstance.getName());

        List<TSSSolution> solutions_list = generateInitialSol(tssInstance);

        //Las ordeno por valor de la función objetivo (beneficio)
        Comparator<TSSSolution> comparator = (s1, s2) -> (int) -(s1.getActualReward() - s2.getActualReward());
        solutions_list.sort(comparator);
        List<TSSSolution> path_solutions = new LinkedList<>();

        //En cada iteración, combino cada solución inicial con una nueva solución guía generada, la cual ya está mejorada
        while (iterations != this.numSg) {
            TSSSolution sg = this.constructive.constructSolution(tssInstance);
            improvement.improve(sg);
            path_solutions.add(sg);

            for (TSSSolution si : solutions_list) {
                pathSol.copy(si); //Es una copia para no modificar la lista de soluciones
                aux1.clear();
                aux2.clear();
                aux1.addAll(pathSol.getSolutionSet());
                aux2.addAll(sg.getSolutionSet());
                aux1.removeAll(sg.getSolutionSet()); //Los que hay que quitar
                aux2.removeAll(pathSol.getSolutionSet()); //Los que hay que añadir

                size = aux2.size();
                TSSSolution bestOfPath = createPath(pathSol, aux1, aux2, size);
                improvement.improve(bestOfPath);
                path_solutions.add(bestOfPath);
            }

            for (TSSSolution sol : path_solutions) {
                insert(sol, solutions_list);
            }
            iterations += 1;
            path_solutions.clear();
        }

        finalBest.copy(solutions_list.get(0));

        long timeToSolution = TimeUnit.MILLISECONDS.convert(System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
        double seconds = timeToSolution / 1000.0;
        float val = finalBest.getActualReward();

        //System.out.println("Conjunto: " + finalBest.getSolutionSet());
        System.out.println("OF: " + val);
        System.out.println("Time (s): " + seconds);
        System.out.println("---------------------------------");

        Result r = new Result(tssInstance.getName());
        r.add("# Cols", tssInstance.getCol());
        r.add("# Rows", tssInstance.getRow());
        r.add("K Max", finalBest.getInstance().getK_max());
        r.add("Effort", finalBest.getActualEffort());
        r.add("# Nodes", finalBest.getSolutionSet().size());
        r.add("Reward", val);
        r.add("Time (s)", seconds);
        return r;
    }

    private List<TSSSolution> generateInitialSol(TSSInstance tssInstance) {
        TSSSolution newSol;
        Set<TSSSolution> solutions = new HashSet<>(); //Al ser un set, me aseguro de que no entran soluciones repetidds
        int it = 0;
        while (solutions.size() < this.numSi && it <= this.numSi*2) {
            newSol = this.constructive.constructSolution(tssInstance);
            improvement.improve(newSol);
            solutions.add(newSol);
            it++;
        }
        return new ArrayList<>(solutions);
    }

    private TSSSolution createPath(TSSSolution pathSol, List<Integer> aux1, List<Integer> aux2, int size) {
        float reward;
        int added;
        added = 0;
        TSSSolution bestOfPath = new TSSSolution(pathSol);
        float bestReward = bestOfPath.getActualReward();
        while (added != size) {
            if (!aux1.isEmpty()) { //Si el inicial no está vacío, elimino nodo aleatorio de Si
                int toDel = aux1.get(this.rnd.nextInt(aux1.size()));
                pathSol.removeValue(toDel);
                pathSol.removeInfluenced(toDel);
                aux1.remove((Integer) toDel);
            }
            if (!aux2.isEmpty()) {
                if (aux1.isEmpty() && aux2.size() == 1) {
                    break;
                }
                int toAdd = aux2.get(this.rnd.nextInt(aux2.size()));
                if (pathSol.addValue(toAdd)) {
                    aux2.remove((Integer) toAdd);
                    pathSol.setVisited(toAdd, true);
                    if (pathSol.sumInfluences(toAdd)) {
                        pathSol.addInfluenced();
                    }
                    reward = pathSol.getActualReward();
                    if (reward > bestReward) {
                        bestReward = reward;
                        bestOfPath.copy(pathSol);
                    }
                    added += 1;
                }
            }
        }
        return bestOfPath;
    }


    private static void insert(TSSSolution solution, List<TSSSolution> solutions_list) {
        //Inserto la solución en orden según su FO y elimino la más parecida para no perder diversidad
        ListIterator<TSSSolution> it = solutions_list.listIterator();
        int tope = 0;
        double reward = solution.getActualReward();
        while (it.hasNext()) {
            if (it.next().getActualReward() < reward) {
                it.previous(); //Retrocedo y añado para mantener el orden
                it.add(solution);
                removeNumSimNodes(tope, solutions_list, solution);
                break;
            }
            tope += 1;
        }
    }


    private static void removeNumSimNodes(int tope, List<TSSSolution> solutions_list, TSSSolution solution) {
        //Inserto la solución en orden según su FO y elimino la más parecida para no perder diversidad
        ReverseListIterator<TSSSolution> rev_it = new ReverseListIterator<>(solutions_list.subList(tope+1, solutions_list.size()));
        Set<Integer> bestSolSet = solution.getSolutionSet();
        int minSimNodes = Integer.MAX_VALUE;
        int pos = -1;

        List<Integer> aux1 = new ArrayList<>();
        List<Integer> aux2 = new ArrayList<>();
        while(rev_it.hasNext()) {

            aux1.clear();
            aux1.addAll(rev_it.next().getSolutionSet()); //Tiene los nodos de una de las soluciones peores a las que se quiere añadir

            aux2.clear();
            aux2.addAll(bestSolSet);

            aux2.removeAll(aux1);
            aux1.removeAll(bestSolSet);

            int numNodes = aux1.size() + aux2.size();
            if (numNodes < minSimNodes) {
                minSimNodes = numNodes;
                pos = rev_it.previousIndex();
            }
        }
        solutions_list.remove(tope+pos+1);
    }

    @Override
    public Solution getBestSolution() {
        return null;
    }
}