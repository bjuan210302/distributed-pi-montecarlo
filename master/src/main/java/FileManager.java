import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    static class Experiment {
        int targetPointsExponent;
        int epsilonExp;
        long seed;
        public Experiment(int targetPointsExponent, int epsilonExp, long seed){
            this.targetPointsExponent = targetPointsExponent;
            this.epsilonExp = epsilonExp;
            this.seed = seed;
        }
    }

    public static Map<Integer, Experiment> experiments;

    public static void readFile() throws IOException{
        experiments = new HashMap<Integer, Experiment>();
        BufferedReader br = new BufferedReader(new FileReader("./master/src/main/resources/default-experiments.csv"));
        String line = "";

        while ((line = br.readLine()) != null) {

            String[] experimentString = line.split(";");
            int targetPointsExponent = Integer.parseInt(experimentString[0]);
            int epsilonExp = Integer.parseInt(experimentString[1]);
            long seed = Long.parseLong(experimentString[2]);

            Experiment experiment = new Experiment(targetPointsExponent, epsilonExp, seed);
            experiments.put(targetPointsExponent, experiment);
        }
        System.out.println("Reading done, number of experiments: " + experiments.size());
        br.close();
    }

    public static Experiment getExperiment(int targetPointsExponent) {
        return experiments.get(targetPointsExponent);
    }
}
