import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

public class FileManager {

    static class Experiment {
        int targetPointsExponent;
        int epsilonExp;
        long seed;

        public Experiment(int targetPointsExponent, int epsilonExp, long seed) {
            this.targetPointsExponent = targetPointsExponent;
            this.epsilonExp = epsilonExp;
            this.seed = seed;
        }
    }

    public static LinkedList<Experiment> experiments;

    public static void readFile() throws IOException {
        experiments = new LinkedList<Experiment>();
        BufferedReader br = new BufferedReader(new FileReader("./master/src/main/resources/default-experiments.csv"));
        String line = "";

        while ((line = br.readLine()) != null) {

            String[] experimentString = line.split(";");
            int targetPointsExponent = Integer.parseInt(experimentString[0]);
            int epsilonExp = Integer.parseInt(experimentString[1]);
            long seed = Long.parseLong(experimentString[2]);

            Experiment experiment = new Experiment(targetPointsExponent, epsilonExp, seed);
            experiments.add(experiment);
        }
        System.out.println("Reading done, number of experiments: " + experiments.size());
        br.close();
    }

    public static void writeOnReport(int exponent, long seconds, int numberOfWorkers, double calculatedPi) {
        try {
            FileWriter fw = new FileWriter("./master/src/main/resources/experiment-results.csv", true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(exponent + ";" + seconds + ";" + numberOfWorkers + ";" + calculatedPi);
            bw.newLine();
            bw.close();
        } catch (Exception e) {
            System.err.print(e.getStackTrace());
        }
    }
}
