import Montecarlo.*;

import com.zeroc.Ice.*;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;

public final class MasterController implements Master
{
    private MontecarloExperiment experiment;
    private List<WorkerPrx> workers;
    private int epsilonExp;
    private int pointsPerTask;
    private long seed;
    private long seedOffset;
    private boolean isTaskAvailable;
    private int remainingPoints;

    public MasterController() {
        this.workers = new ArrayList<WorkerPrx>();
    }
    @Override
    public void subscribe(WorkerPrx worker, Current current) {
        this.workers.add(worker);
        worker.update(isTaskAvailable);
    }

    @Override
    public Task getTask(Current current) {
        //TODO: Consider pointsPerTask > remainingPoints
        Task t = new Task(pointsPerTask, epsilonExp, seed, seedOffset);
        seedOffset++;
        return t;
    }

    @Override
    public void reportPartialResult(LinkedList<Point> points, Current current) {
        experiment.processNewPoints(points);
    }

    public void initCalculation(int targetPoints, int epsilonExp) {
        this.experiment = new MontecarloExperiment();
        this.experiment.initExperiment(targetPoints, epsilonExp);
        this.remainingPoints = targetPoints;
        this.epsilonExp = epsilonExp;
        updateAll(true);
    }

    public void updateState(int insidePoints, int outsidePoints, int processedPoints, double pi) {
        // TODO: This is sent to the GUI
    }

    public void notifyTargetReached() {
        this.isTaskAvailable = false;
        updateAll(false);
    }

    private void updateAll(boolean isTaskAvailable) {
        //TODO: Should be done in object called WorkerNotifier
        new Thread(() -> {
            for (WorkerPrx worker : workers) {
                worker.update(isTaskAvailable);
            }
        }).start();
    }
}
