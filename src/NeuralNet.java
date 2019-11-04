import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by James on 6/28/2017.
 */
public class NeuralNet {

    private ArrayList<Node> inputLayer;                 // Contains all nodes in input layer
    private ArrayList<ArrayList<Node>> hiddenLayers;    // Contains all hidden layers
    private ArrayList<Node> outputLayer;                // Contains all nodes in output layer
    private ArrayList<ArrayList<Node>> allLayers;       // Contains all layers within neural net

    private File trainingFile;
    private File testingFile;

    private Scanner input;

    public NeuralNet(File trainingFile, File testingFile) {
        this.trainingFile = trainingFile;
        this.testingFile = testingFile;
        inputLayer = new ArrayList<>();
        hiddenLayers = new ArrayList<>();
        outputLayer = new ArrayList<>();
        allLayers = new ArrayList<>();
    }

    public void start() throws FileNotFoundException {
        initializeGraph();
        backPropLearning();
//        for (int i = 0; i < 10000; i++) {
//            backPropLearning();
//            //test();
//        }
        test();
    }

    private void initializeGraph() throws FileNotFoundException {

        input = new Scanner(trainingFile);

        // Generate Input Layer
        for (int i = 0; i < 64; i++) {
            inputLayer.add(new Node(NodeType.INPUT));
        }

        inputLayer.add(new Node(1.0, NodeType.BIAS)); // Add bias node

        allLayers.add(inputLayer);

        //ArrayList<Node> hiddenLayer = new ArrayList<>();
        boolean firstTime; // firstTime adding hiddenNodes to layer

        /* Add three hidden layers */

        ArrayList<Node> prevLayer = inputLayer;
        ArrayList<Node> newHiddenLayer = new ArrayList<>();
        for (int i = 1; i <= 2; i *= 2) {
            firstTime = true;

            for (Node hiddenNode : prevLayer) {

                Node newHiddenNode;
                Edge edge;
                for (int j = 0; j < 32 / i; j++) {
                    if (firstTime) {
                        newHiddenNode = new Node(NodeType.HIDDEN);
                        edge = new Edge(hiddenNode, newHiddenNode);
                        hiddenNode.addChildEdge(edge);
                        newHiddenNode.addParentEdge(edge);
                        newHiddenLayer.add(newHiddenNode);
                        //firstTime = false;
                    } else {
                        newHiddenNode = newHiddenLayer.get(j);
                        edge = new Edge(hiddenNode, newHiddenNode);
                        hiddenNode.addChildEdge(edge);
                        newHiddenNode.addParentEdge(edge);
                    }
                }
                firstTime = false; // Hack way of doing it
            }

            newHiddenLayer.add(new Node(1.0, NodeType.BIAS)); // Add bias node

            allLayers.add(newHiddenLayer);
            hiddenLayers.add(newHiddenLayer);
            prevLayer = allLayers.get(allLayers.size() - 1);
            newHiddenLayer = new ArrayList<>();

        }

        ArrayList<Node> lastHiddenLayer = hiddenLayers.get(hiddenLayers.size() - 1);
        firstTime = true;

        for (Node node : lastHiddenLayer) {
            Node outputNode;
            Edge edge;

            // Create 10 output nodes for 10 digits
            for (int i = 0; i < 10; i++) {
                if (firstTime) {
                    outputNode = new Node(NodeType.OUTPUT);
                    edge = new Edge(node, outputNode);
                    node.addChildEdge(edge);
                    outputNode.addParentEdge(edge);
                    outputLayer.add(outputNode);
                } else {
                    outputNode = outputLayer.get(i);
                    edge = new Edge(node, outputNode);
                    node.addChildEdge(edge);
                    outputNode.addParentEdge(edge);
                }
            }

            firstTime = false;

        }

        allLayers.add(outputLayer);

    }

    private void backPropLearning() {
        double[] y = new double[10];

        /* Network should have already been initialized with random weights */

        // for each example in the training file
        while (input.hasNext()) {
            /* Propagate the inputs forward to compute the outputs */

            String line = input.nextLine();
            String[] parts = line.split(",");
            int answer = Integer.parseInt(parts[parts.length - 1]);
            y[answer] = 1.0;

            for (int i = 0; i < inputLayer.size() - 1; i++) {
                inputLayer.get(i).setData(Double.parseDouble(parts[i]) / 16);
            }

            // Start at first hidden layer aka second layer in neural net
            for (int i = 1; i < allLayers.size(); i++) {
                for (Node j : allLayers.get(i)) {
                    if (!j.isBias()) { // Makes sure that the bias node data always stays at 1.0
                        j.setBefore(sum(j));
                        if (!j.isOutput()) {
                            j.setData(sigmoid(sum(j)));
                        }
                    }
                }
            }

            softMax();

            /* Propagate deltas backward from output layer to input layer */

            /* Compute deltas for output layer */
            for (Node j : outputLayer) {
                j.setDelta(sigmoidPrime(j.getBefore()) * (y[outputLayer.indexOf(j)] - j.getData()));
            }

            /* From the last hidden layer compute the deltas of the other nodes */
            for (int i = allLayers.size() - 2; i > 0; i--) {
                for (Node node : allLayers.get(i)) {
                    if (!node.isBias()) {
                        node.setDelta(sigmoidPrime(node.getBefore()) * sum2(node));
                    }

                }
            }

            /* Update all weights in network */
            for (ArrayList<Node> layer : allLayers) {
                for (Node node : layer) {
                    for (Edge edge : node.getChildEdges()) {
                        edge.addWeight(1.8 * edge.getParent().getData() * edge.getChild().getDelta());
                    }
                }
            }

            y = new double[10]; // Reset y back to all 0.0
            //delta.clear(); // Reset HashMap
        }


    }

    private void test() throws FileNotFoundException {
        //double[] y = new double[10];
        int genCount = 0;
        int correctCount = 0;

        input = new Scanner(testingFile);

        while (input.hasNext()) {

            /* Propagate the inputs forward to compute the outputs */

            String line = input.nextLine();
            String[] parts = line.split(",");
            int answer = Integer.parseInt(parts[parts.length - 1]);
            //y[answer] = 1.0;

            for (int i = 0; i < inputLayer.size() - 1; i++) {
                inputLayer.get(i).setData(Double.parseDouble(parts[i]) / 16);
            }

            for (int i = 1; i < allLayers.size(); i++) {
                for (Node j : allLayers.get(i)) {
                    if (!j.isBias()) {
                        j.setData(sigmoid(sum(j)));
                    }
                }
            }

            if (answer == maxOutputIndex()) {
                System.out.println(answer + "\t\t\t" + maxOutputIndex());
                ++correctCount;
            }

            ++genCount;
        }

        System.out.println("Guessed " + correctCount + " out of " + genCount + " correctly");
    }

    private void softMax() {
        double max = Double.NEGATIVE_INFINITY;

        for (Node node : outputLayer) {
            if (node.getData() > max) {
                max = node.getBefore();
            }
        }

        double sum = 0.0;
        for (Node node : outputLayer) {
            double out = Math.exp(node.getBefore() - max);
            node.setData(out);
            sum += out;
        }

        for (Node node : outputLayer) {
            node.setData(node.getData() / sum);
        }
    }

    private int maxOutputIndex() {

        Node maxNode = outputLayer.get(0);
        for (Node node : outputLayer) {
            if (node.getData() > maxNode.getData()) {
                maxNode = node;
            }
        }

        return outputLayer.indexOf(maxNode);
    }

    private static double sum(Node node) {
        double result = 0.0;

        if (!node.isInput()) {
            for (Edge parentEdge : node.getParentEdges()) {
                result += parentEdge.getWeight() * parentEdge.getParent().getData();
            }
        }

        return result;
    }

    private static double sum2(Node node) {
        double result = 0.0;

        if (!node.isOutput()) {
            for (Edge childEdge : node.getChildEdges()) {
                //result += childEdge.getWeight() * delta.get(childEdge.getChild());
                result += childEdge.getWeight() * childEdge.getChild().getDelta();
            }
        }

        return result;
    }

    private static double sigmoid(double x) {
        return (1 / (1 + Math.pow(Math.E, -x)));
    }

    private static double sigmoidPrime(double x) {
        return (sigmoid(x) * (1 - sigmoid(x)));
    }


}
