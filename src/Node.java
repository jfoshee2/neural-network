import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by James on 6/28/2017.
 */
public class Node {

    private double before;
    private double data;
    private double delta;
    private NodeType type;
    private ArrayList<Edge> parentEdges;
    private ArrayList<Edge> childEdges;

    public Node(double data, NodeType type) {
        this.data = data;
        this.type = type;
        parentEdges = new ArrayList<>();
        childEdges = new ArrayList<>();
    }

    public Node(NodeType type) {
        this.type = type;
        parentEdges = new ArrayList<>();
        childEdges = new ArrayList<>();
    }

    public double getBefore() {
        return before;
    }

    public void setBefore(double before) {
        this.before = before;
    }

    public void setData(double data) {
        this.data = data;
    }

    public double getData() {
        return data;
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public boolean isInput() {
        return type == NodeType.INPUT;
    }

    public boolean isHidden() {
        return type == NodeType.HIDDEN;
    }

    public boolean isOutput() {
        return type == NodeType.OUTPUT;
    }

    public NodeType getType() {
        return type;
    }

    public boolean isBias() {
        return type == NodeType.BIAS;
    }

    public void addChildEdge(Edge edge) {
        childEdges.add(edge);
    }

    public ArrayList<Edge> getChildEdges() {
        return childEdges;
    }

    public void addParentEdge(Edge edge) {
        parentEdges.add(edge);
    }

    public ArrayList<Edge> getParentEdges() {
        return parentEdges;
    }
}
