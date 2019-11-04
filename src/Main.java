import java.io.File;
import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {

        File trainingFile = new File("optdigits_train.txt");
        File testingFile = new File("optdigits_test.txt");

        NeuralNet instance = new NeuralNet(trainingFile, testingFile);
        instance.start();
    }
}
