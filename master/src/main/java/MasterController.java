import Montecarlo.*;

import com.zeroc.Ice.*;
import java.util.List;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;

public final class MasterController implements Master
{
    private long POINTS_PER_TASK = (long) Math.pow(10, 7);

    private MontecarloExperiment experiment;
    
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

    public MasterController() {
        this.workers = new ArrayList<WorkerPrx>();
    }
    @Override
    public void subscribe(WorkerPrx worker, Current current) {
        this.workers.add(worker);
        worker.ice_oneway().update(isTaskAvailable);
        System.out.println("Ended subscribe.");
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
        Task t = new Task(POINTS_PER_TASK, epsilonExp, seed, seedOffset);
        seedOffset++;
        System.out.println("New task dispatched. offset: " + seedOffset);
        return t;
    }

    @Override
    public void reportPartialResult(LinkedList<Point> points, Current current) {
        experiment.processNewPoints(points);
    }

    public void initCalculation(int targetPointsExponent, int epsilonExp, long seed) {
        this._initCalculation(targetPointsExponent, epsilonExp, seed);
    }

    public void initCalculation(FileManager.Experiment exp) {
        this._initCalculation(exp.targetPointsExponent, exp.epsilonExp, exp.seed);
    }

    private void _initCalculation(int targetPointsExponent, int epsilonExp, long seed) {
        this.startTime = System.currentTimeMillis();
        this.targetPointsExponent = targetPointsExponent;
        this.epsilonExp = epsilonExp;
        this.seed = seed;
        this.experiment = new MontecarloExperiment(this);
        this.experiment.initExperiment(BigInteger.TEN.pow(targetPointsExponent), epsilonExp);
        this.isTaskAvailable = true;
        updateAll(isTaskAvailable);
    }

    public void updateState(long insidePoints, long outsidePoints, BigInteger processedPoints, double pi) {
        System.out.println("New update. Inside=" + insidePoints + " outside=" + outsidePoints + " total=" + processedPoints + " pi=" + pi);
    }

    public void notifyTargetReached(long insidePoints, long outsidePoints, BigInteger processedPoints, double pi) {
        this.isTaskAvailable = false;
        updateAll(isTaskAvailable);
        System.out.println("Target reached. Results were:");
        System.out.println("inside=" + insidePoints + " outside=" + outsidePoints + " total=" + processedPoints + " pi=" + pi);

        long secondsElapsed = (System.currentTimeMillis() - startTime)/1000;
        FileManager.writeOnReport(targetPointsExponent, secondsElapsed, workers.size(), pi);

        if (this.isAutomatedExperiment)
            automaticNext();
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
        if (this.expCounter < this.repetitionsPerExperiment) {
            this.expCounter++;
            initCalculation(this.currentExperiment);
        } else {
            this.expCounter = 0;
            this.currentExperiment = experiments.poll();

            if (this.currentExperiment != null)
                automaticNext();
        }
    }
}
