import Montecarlo.*;

import com.zeroc.Ice.*;
import java.util.List;
import java.util.ArrayList;

public final class MasterController implements Master
{
    private List<WorkerPrx> workers;
    private int remainingPoints;
    private int epsilonExponent;
    private int pointsPerTask;
    private boolean isTaskAvailable;

    public MasterController() {
        this.workers = new ArrayList<WorkerPrx>();
    }
    @Override
    public void subscribe(WorkerPrx worker, Current current) {
        this.workers.add(worker);
    }

    @Override
    public Task getTask(Current current) {
        //Naive implementation
        return new Task(pointsPerTask, epsilonExponent);

        //TODO: Consider pointsPerTask > remainingPoints
    }

    @Override
    public void reportPartialResult(Point[] outside, Point[] inside, Current current) {
        // TODO Auto-generated method stub
        
    }

    public void initCalculation(int numberOfPoints, int epsilonExponent) {
        this.remainingPoints = numberOfPoints;
        this.epsilonExponent = epsilonExponent;
        setTaskAvailableAndNotify(true);
    }

    public void setPointsLeft(int n) {
        if (n == 0)
            setTaskAvailableAndNotify(false);
        else
            this.remainingPoints = n;
    }

    private void setTaskAvailableAndNotify(boolean isTaskAvailable) {
        this.isTaskAvailable = isTaskAvailable;

        //TODO: Should be done in object called WorkerNotifier
        new Thread(() -> {
            for (WorkerPrx worker : workers) {
                worker.update(isTaskAvailable);
            }
        }).start();
    }

}
