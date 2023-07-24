package structure;

public class NodeInfluence {

    private final int node;
    private float effort;

    public NodeInfluence(int node, float effort) {
        this.node = node;
        this.effort = effort;
    }

    public int getNode() {
        return node;
    }

    public float getEffort() {
        return effort;
    }

    public void setEffort(float effort) {
        this.effort = effort;
    }

}
