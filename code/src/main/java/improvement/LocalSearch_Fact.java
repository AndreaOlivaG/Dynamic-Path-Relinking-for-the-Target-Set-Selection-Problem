package improvement;
import grafo.optilib.metaheuristics.Improvement;
import structure.NodeInfluence;
import structure.TSSSolution;

import java.util.*;

public class LocalSearch_Fact implements Improvement<TSSSolution> {

    private int numElems;
    private int[][] psi;
    private final HashSet<Integer> check = new HashSet<>();
    private TSSSolution auxSol;

    HashSet<Integer> toAdd = new HashSet<>();
    Queue<Integer> modifiedNodes = new ArrayDeque<>();

    public void improve(TSSSolution solution) {
        this.numElems = solution.getInstance().getCol();
        this.psi = solution.getInstance().getPsiInt();

        float actualReward = solution.BFS();

        this.auxSol = new TSSSolution(solution);
        TSSSolution preDel = new TSSSolution(solution);
        ArrayList<Integer> v = new ArrayList<>(solution.getInstance().getRow());

        HashSet<Integer> hashValues = new HashSet<>();
        hashValues.add(auxSol.getSolutionSet().hashCode());
        float newReward;

        List<NodeInfluence> notSolNodes = new ArrayList<>();
        for (int i = 0; i < numElems; i++) {
            if (!this.auxSol.getSolutionSet().contains(i))
                notSolNodes.add(new NodeInfluence(i, this.auxSol.getInstance().getEffort()[i]));
        }
        int iters = 0;
        boolean improve = true;
        while (improve) {
            improve = false;
            for (int del : solution.getSolutionSet()) {
                preDel.copy(this.auxSol);
                this.auxSol.removeValue(del);
                int val = this.auxSol.getSolutionSet().hashCode();
                if (hashValues.contains(val)) {
                    this.auxSol.addValue(del);
                    continue;
                }
                hashValues.add(val);
                iters++;
                if (iters == 1000) break;
                removeInfluenced(del);
                notSolNodes.sort((o1, o2) -> (int) -(o2.getEffort() - o1.getEffort()));
                int tope = binarySearch(notSolNodes,0,notSolNodes.size()-1,(int) auxSol.getInstance().getK_max() - (int) auxSol.getActualEffort());
                v.clear();
                for (int i = 0; i < tope; i++)
                    v.add(i + 1);
                for (int i = 0; i < tope * 1.0; i++) {
                    if (improve) break;
                    int rnd = getNum(v);
                    NodeInfluence node2 = notSolNodes.get(rnd);
                    int add = node2.getNode();
                    if (del != add && this.auxSol.isFeasible(add) && !auxSol.getVisitedStatus()[add]) {
                        this.auxSol.addValue(add);
                        //Hashcode
                        val = this.auxSol.getSolutionSet().hashCode();
                        if (hashValues.contains(val)) {
                            this.auxSol.removeValue(add);
                            continue;
                        }
                        hashValues.add(val);
                        //Hashcode
                        this.auxSol.setVisited(add, true);
                        if (sumInfluences(add))
                            addInfluenced();

                        newReward = this.auxSol.getActualReward();
                        if (Double.compare(newReward, actualReward) > 0) {
                            improve = true;
                            actualReward = newReward;
                            NodeInfluence node1 = new NodeInfluence(del, auxSol.getInstance().getEffort()[del]);
                            notSolNodes.add(node1);
                            notSolNodes.remove(node2);
                            solution.copy(this.auxSol);
                        } else {
                            this.auxSol.copy(preDel);
                        }
                    }
                }
                if (improve) {
                    break;
                }
                this.auxSol.copy(preDel);
            }
        }
    }

    // Function to return the next random number
    int getNum(ArrayList<Integer> v) {
        int n = v.size();
        int index = (int)(Math.random() * n);
        int num = v.get(index);
        v.set(index, v.get(n - 1));
        v.remove(n - 1);
        return num;
    }

    int binarySearch(List<NodeInfluence> arr, int l, int r, int x) {
        int mid = arr.size()-1;
        if (r >= l) {
            mid = l + (r - l) / 2;
            if (arr.get(mid).getEffort() == x)
                return mid;
            if (arr.get(mid).getEffort() > x)
                return binarySearch(arr, l, mid - 1, x);
            return binarySearch(arr, mid + 1, r, x);
        }
        return mid;
    }

    private boolean sumInfluences(int node) {
        boolean modified = false;
        int pre, post, i;
        for (i = 0; i < this.numElems; i++) {
            post = this.psi[node][i];
            if (post != 0) {
                pre = this.auxSol.getInfluenceValue(i);
                this.auxSol.setInfluenceValue(i, pre + post);
                if (pre < 100 && (pre + post) >= 100)
                    modified = true;
            }
        }
        return modified;
    }

    private void removeInfluenced(int deleted) {
        int directInf, newInf, node;
        modifiedNodes.clear();
        modifiedNodes.add(deleted);
        this.auxSol.setVisited(deleted, false);
        check.clear();

        boolean changed = false;
        while (!modifiedNodes.isEmpty()) {
            int size = modifiedNodes.size();
            for (int j = 0; j < size; j++) {
                node = modifiedNodes.poll();
                for (int i = 0; i < numElems; i++) {
                    directInf = this.psi[node][i];
                    if (directInf != 0) {
                        newInf = this.auxSol.getInfluenceValue(i) - directInf;
                        this.auxSol.setInfluenceValue(i, newInf);
                        changed = true;
                        if (!this.auxSol.getSolutionSet().contains(i) && !check.contains(i)
                                && this.auxSol.getVisitedStatus()[i]) {
                            modifiedNodes.add(i);
                            check.add(i);
                        }
                    }
                }
            }
        }
        if (changed) {
            for (int i : check) {
                if (!this.auxSol.getSolutionSet().contains(i) && this.auxSol.getVisitedStatus()[i]) {
                    this.auxSol.setVisited(i, false);
                }
            }
        }
        addInfluenced();
    }

    private void addInfluenced() {
        check.clear();
        check.addAll(this.auxSol.getNoVisited());
        boolean activated = true;
        while (activated) {
            toAdd.clear();
            activated = false;
            for (int node : check) {
                if (auxSol.getInfluenceValue(node) >= 100) {
                    toAdd.add(node);
                    this.auxSol.setVisited(node, true);
                    sumInfluences(node);
                    activated = true;
                }
            }
            for (int node : toAdd) {
                check.remove((Integer) node);
            }
        }
    }

}
