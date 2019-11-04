import java.text.DecimalFormat;
import java.util.Random;

/**
 * Created by James on 6/28/2017.
 */
public class Edge {

    private Node parent;
    private Node child;
    private double weight;

    public Edge(Node parent, Node child) {

        // Since the edge is first being created we assign a random weight to it
        Random random = new Random();
        DecimalFormat df = new DecimalFormat("#.000");

        this.parent = parent;
        this.child = child;
        this.weight = Double.parseDouble(df.format(random.nextDouble() / 100)); // weight should not be 0
        if (weight == 0.0) {
            weight = 0.001;
        }
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getChild() {
        return child;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void addWeight(double weight) {
        this.weight += weight;
    }
}
