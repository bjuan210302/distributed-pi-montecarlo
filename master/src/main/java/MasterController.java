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
    private List<WorkerPrx> workers;
    private int epsilonExp;
    private long seed;
    private long seedOffset;
    private boolean isTaskAvailable;

    public MasterController() {
        this.workers = new ArrayList<WorkerPrx>();
    }
    @Override
    public void subscribe(WorkerPrx worker, Current current) {
        this.workers.add(worker);
        worker.ice_oneway().update(isTaskAvailable);
        System.out.println("Ended subscribe.");
    }

    @Override
    public Task getTask(Current current) {
        //TODO: Consider pointsPerTask > remainingPoints
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
        this.experiment = new MontecarloExperiment(this);
        this.epsilonExp = epsilonExp;
        this.experiment.initExperiment(BigInteger.TEN.pow(targetPointsExponent), epsilonExp);
        this.isTaskAvailable = true;
        updateAll(isTaskAvailable);
    }

    public void initCalculation(FileManager.Experiment exp) {
        this.experiment = new MontecarloExperiment(this);
        this.epsilonExp = exp.epsilonExp;
        this.experiment.initExperiment(BigInteger.TEN.pow(exp.targetPointsExponent), epsilonExp);
        this.isTaskAvailable = true;
        updateAll(isTaskAvailable);
    }

    public void updateState(long insidePoints, long outsidePoints, BigInteger processedPoints, double pi) {
        System.out.println("New update. Inside=" + insidePoints + " outside=" + outsidePoints + " total=" + processedPoints + " pi=" + pi);
    }

    public void notifyTargetReached(long insidePoints, long outsidePoints, BigInteger processedPoints, double pi) {
        System.out.println("Target reached. Results were:");
        System.out.println("inside=" + insidePoints + " outside=" + outsidePoints + " total=" + processedPoints + " pi=" + pi);
        this.isTaskAvailable = false;
        updateAll(isTaskAvailable);
    }

    private void updateAll(boolean isTaskAvailable) {
        //TODO: Should be done in object called WorkerNotifier
        new Thread(() -> {
            for (WorkerPrx worker : workers) {
                worker.ice_oneway().update(isTaskAvailable);
            }
            System.out.println("Ended updateAll with val: " + isTaskAvailable);
        }).start();
    }
}
