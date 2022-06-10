import Montecarlo.*;

import com.zeroc.Ice.*;
import java.util.List;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;

public final class MasterController implements Master {
    private long POINTS_PER_TASK = (long) Math.pow(10, 7);

    private MontecarloExperiment experiment;
    private GUIController guiController;
    // Experiment vars
    private long startTime;
    private int targetPointsExponent;
    private int epsilonExp;
    private long seed;

    // Management vars
    private List<WorkerPrx> workers;
    private long seedOffset;
    private boolean isTaskAvailable;

    // Automated experiments vars
    private boolean isAutomatedExperiment;
    private LinkedList<FileManager.Experiment> experiments;
    private FileManager.Experiment currentExperiment;
    private int expCounter;
    private int repetitionsPerExperiment;

    public MasterController(GUIController guiController) {
        this.workers = new ArrayList<WorkerPrx>();
        this.guiController = guiController;
    }

    @Override
    public void subscribe(WorkerPrx worker, Current current) {
        this.workers.add(worker);
        worker.ice_oneway().update(isTaskAvailable);
        System.out.println("New worker. Total now: " + workers.size());
    }

    @Override
    public void unsubscribe(WorkerPrx worker, Current current) {
        this.workers.remove(worker);
        System.out.println("A Worker left. Total now " + workers.size());
    }

    private void updateAll(boolean isTaskAvailable) {
        new Thread(() -> {
            for (WorkerPrx worker : workers) {
                worker.ice_oneway().update(isTaskAvailable);
            }
            System.out.println("Ended updateAll with val: " + isTaskAvailable);
        }).start();
    }

    @Override
    public Task getTask(Current current) {
        if (!this.isTaskAvailable)
            return null;
        Task t = new Task(POINTS_PER_TASK, epsilonExp, seed, seedOffset);
        seedOffset++;
        System.out.println("New task dispatched. offset: " + seedOffset);
        return t;
    }

    @Override
    public void reportPartialResult(long inside, long outside, Current current) {
        if (!this.isTaskAvailable)
            System.out.println("Ignoring partial result report.");
        else
            new Thread(() -> experiment.processNewPoints(inside, outside)).start();
    }

    public void initCalculation(int targetPointsExponent, long seed) {
        this._initCalculation(targetPointsExponent, epsilonExp, seed);
    }

    public void initCalculation(FileManager.Experiment exp) {
        this._initCalculation(exp.targetPointsExponent, exp.epsilonExp, exp.seed);
    }

    private void _initCalculation(int targetPointsExponent, int epsilonExp, long seed) {
        this.startTime = System.currentTimeMillis();
        this.targetPointsExponent = targetPointsExponent;
        this.epsilonExp = 0;
        this.seed = seed;
        this.seedOffset = 0;
        this.experiment = new MontecarloExperiment(this);
        this.experiment.initExperiment(BigInteger.TEN.pow(targetPointsExponent), epsilonExp);
        this.isTaskAvailable = true;
        updateAll(isTaskAvailable);
    }

    public void updateState(long insidePoints, long outsidePoints, BigInteger processedPoints,
            BigInteger remaining, double pi) {
        System.out.println(
                "New update. Inside=" + insidePoints +
                        " outside=" + outsidePoints + " total=" + processedPoints +
                        " remaining=" + remaining +
                        " pi=" + pi);
    }

    public void notifyTargetReached(long insidePoints, long outsidePoints, BigInteger processedPoints, double pi) {
        long millisElapsed = (System.currentTimeMillis() - startTime);
        System.out.println("Target reached. Results were:");
        System.out.println(
                "inside=" + insidePoints + " outside=" + outsidePoints + " total=" + processedPoints + " pi=" + pi
                        + " seconds:" + millisElapsed);

        FileManager.writeOnReport(targetPointsExponent, millisElapsed, workers.size(), pi);

        if (this.isAutomatedExperiment) {
            System.out.println("Taking 3000 millis to catch up...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("Error while sleep(3000)");
                e.printStackTrace();
            }
            automaticNext();
        } else {
            guiController.update(pi, processedPoints, millisElapsed);
        }
    }

    public void notifyEnoughPoints() {
        this.isTaskAvailable = false;
        updateAll(isTaskAvailable);
        System.out.println("Enough points received. Waiting for results...");
    }

    public void setupAutomaticExperiment(LinkedList<FileManager.Experiment> experiments, int repetitionsPerExperiment) {
        this.isAutomatedExperiment = true;
        this.experiments = experiments;
        this.expCounter = 0;
        this.repetitionsPerExperiment = repetitionsPerExperiment;
    }

    public void automaticStart() {
        this.currentExperiment = experiments.poll();
        automaticNext();
    }

    public void automaticNext() {
        System.out.println("Starting experiment 10^" + this.currentExperiment.targetPointsExponent);
        if (this.expCounter < this.repetitionsPerExperiment) {
            this.expCounter++;
            initCalculation(this.currentExperiment);
        } else {
            this.expCounter = 0;
            this.currentExperiment = experiments.poll();

            if (this.currentExperiment != null) {

                automaticNext();
            } else
                System.out.println("All experiments done. Please check resources/experiment-results.csv");
        }
    }
}