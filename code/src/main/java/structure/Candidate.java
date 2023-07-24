package structure;

public class Candidate implements Comparable<Candidate> {

    private int node;
    private int numAdjs;

    public Candidate(int node, int numAdjs) {
        this.node = node;
        this.numAdjs = numAdjs;
    }

    public int getNode() {
        return node;
    }

    public int getNumAdjs() {
        return numAdjs;
    }

    public void setNumAdjs(int numAdjs) {
        this.numAdjs = numAdjs;
    }

    @Override
    public int compareTo(Candidate c) {
        return - (this.numAdjs - c.numAdjs);
    }


}

