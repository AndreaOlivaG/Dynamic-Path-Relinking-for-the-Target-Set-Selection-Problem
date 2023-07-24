package structure;
import java.util.*;

import grafo.optilib.structure.Solution;

public class TSSSolution implements Solution {
    private TSSInstance instance;
    private float actualEffort;
    private float actualReward;
    private float k_max;
    private int numElems;
    private Set<Integer> solutionSet;
    private HashSet<Integer> aux = new HashSet<>();
    private Integer[] influenceValues;

    private boolean[] visitedStatus;
    private HashSet<Integer> visited;
    private HashSet<Integer> noVisited;

    private int[][] psi;
    private final HashSet<Integer> check = new HashSet<>();
    HashSet<Integer> toAdd = new HashSet<>();
    Queue<Integer> modifiedNodes = new ArrayDeque<>();

    public TSSSolution(TSSInstance instance) {
        this.instance = instance;
        this.actualEffort = 0;
        this.actualReward = 0;
        this.k_max = this.instance.getK_max();
        this.numElems = instance.getCol();
        this.solutionSet = new HashSet<>();
        this.influenceValues = new Integer[numElems];
        this.visitedStatus = new boolean[numElems];
        this.visited = new HashSet<>();
        this.noVisited = new HashSet<>();
        this.psi = instance.getPsiInt();
    }

    public TSSSolution(TSSSolution solution) {
        copy(solution);
    }

    public void copy(TSSSolution solution) {
        this.instance = solution.getInstance();
        this.solutionSet = new HashSet<>(solution.getSolutionSet());
        this.actualEffort = solution.getActualEffort();
        this.actualReward = solution.getActualReward();
        this.numElems = solution.getInstance().getCol();
        this.k_max = this.instance.getK_max();
        this.influenceValues = Arrays.copyOf(solution.getInfluenceValues(), numElems);
        this.visitedStatus = Arrays.copyOf(solution.getVisitedStatus(), numElems);
        this.visited = new HashSet<>(solution.getVisited());
        this.noVisited = new HashSet<>(solution.getNoVisited());
        this.psi = solution.getInstance().getPsiInt();
    }

    public boolean isFeasible() {
        return (this.actualEffort <= this.k_max);
    }

    public boolean isFeasible(int node) {
        return (!this.solutionSet.contains(node) &&
                (this.actualEffort + this.instance.getEffort()[node]) <= this.k_max);
    }

    public float BFS() {
        this.visited.clear();
        this.noVisited.clear();

        if (!isFeasible())
            System.out.println("Está entrando a BFS sin ser factible");

        this.actualReward = 0;

        for (int i = 0; i < this.numElems; i++) {
            if (this.solutionSet.contains(i)) {
                this.visitedStatus[i] = true;
                this.visited.add(i);
                this.actualReward += this.instance.getReward()[i];
            } else {
                this.visitedStatus[i] = false;
                this.noVisited.add(i);
            }
        }

        int sum;
        do {
            aux.clear();
            for (int i = 0; i < this.numElems; i++) {
                sum = 0;
                if (!this.visitedStatus[i]) {
                    for (int j = 0; j < this.numElems; j++) {
                        if (this.visitedStatus[j]) {
                            sum += this.instance.getPsiInt()[j][i];
                        }
                    }
                    this.setInfluenceValue(i, sum);
                    if (sum >= 100) {
                        aux.add(i);
                    }
                } else {
                    for (int node : this.visited) {
                        sum += this.instance.getPsiInt()[node][i];
                    }
                    this.setInfluenceValue(i, sum);
                }
            }
            for (Integer elem : aux) {
                this.actualReward += this.instance.getReward()[elem];
                this.visitedStatus[elem] = true;
                this.visited.add(elem);
                this.noVisited.remove((Integer) elem);
            }
        } while (!aux.isEmpty());
        return this.actualReward;
    }

    public boolean addValue(int node) { //Solo para nodos solución
        if (isFeasible(node)) {
            this.solutionSet.add(node);
            this.actualEffort += this.instance.getEffort()[node];
            return true;
        }
        return false;
    }

    public void removeValue(int node) { //Solo para nodos solución
        this.solutionSet.remove(node);
        this.actualEffort -= this.instance.getEffort()[node];
    }

    public void restoreInstance() {
        this.solutionSet.clear();
        this.actualEffort = 0;
    }

    public void setVisited(int node, boolean mode) {
        if (this.visitedStatus[node] != mode) {
            this.visitedStatus[node] = mode;
            if (!mode) {
                this.noVisited.add(node);
                this.visited.remove((Integer) node);
                this.actualReward -= this.instance.getReward()[node];
            } else {
                this.visited.add(node);
                this.noVisited.remove((Integer) node);
                this.actualReward += this.instance.getReward()[node];
            }
        }
    }
    public Set<Integer> getSolutionSet() {
        return solutionSet;
    }

    public TSSInstance getInstance() {
        return instance;
    }

    public float getActualEffort() {
        return actualEffort;
    }

    public float getActualReward() {
        return actualReward;
    }

    public boolean[] getVisitedStatus() {
        return visitedStatus;
    }

    public HashSet<Integer> getNoVisited() {
        return noVisited;
    }

    public HashSet<Integer> getVisited() {
        return visited;
    }

    public int getInfluenceValue(int node) {
        return influenceValues[node];
    }

    public void setInfluenceValue(int node, int sum) {
        this.influenceValues[node] = sum;
    }

    public Integer[] getInfluenceValues() {
        return influenceValues;
    }

    public boolean sumInfluences(int node) {
        boolean modified = false;
        int pre, post, i;
        for (i = 0; i < this.numElems; i++) {
            post = this.psi[node][i];
            if (post != 0) {
                pre = this.getInfluenceValue(i);
                this.setInfluenceValue(i, pre + post);
                if (pre < 100 && (pre + post) >= 100)
                    modified = true;
            }
        }
        return modified;
    }

    public void addInfluenced() { //Este método es para los influenciados, influencia de manera recursiva
        check.clear();
        check.addAll(getNoVisited());
        boolean activated = true;
        while (activated) {
            toAdd.clear();
            activated = false;
            for (int node : check) {
                if (getInfluenceValue(node) >= 100) {
                    toAdd.add(node);
                    activated = true;
                }
            }
            for (Integer node: toAdd) {
                check.remove((Integer) node);
                setVisited(node, true);
                sumInfluences(node);
            }
        }
    }


    public void removeInfluenced(int deleted) {
        int directInf, newInf, node;
        modifiedNodes.clear();
        modifiedNodes.add(deleted);
        setVisited(deleted, false);
        check.clear();

        boolean changed = false;
        while (!modifiedNodes.isEmpty()) {
            int size = modifiedNodes.size();
            for (int j = 0; j < size; j++) {
                node = modifiedNodes.poll();
                for (int i = 0; i < numElems; i++) {
                    directInf = this.psi[node][i];
                    if (directInf != 0) {
                        newInf = getInfluenceValue(i) - directInf;
                        setInfluenceValue(i, newInf);
                        changed = true;
                        if (!getSolutionSet().contains(i) && !check.contains(i) && getVisitedStatus()[i]) {
                            modifiedNodes.add(i);
                            check.add(i);
                        }
                    }
                }
            }
        }

        if (changed) {
            for (int i : check) {
                if (!getSolutionSet().contains(i) && getVisitedStatus()[i]) {
                    setVisited(i, false);
                }
            }
        }
        addInfluenced();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TSSSolution solution = (TSSSolution) o;
        return Objects.equals(solutionSet, solution.solutionSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(solutionSet);
    }

}
