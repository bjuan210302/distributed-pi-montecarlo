import java.math.BigInteger;
import java.util.LinkedList;

import Montecarlo.Point;

public class MontecarloExperiment {

    private MasterController master;
    private PointStore pointStore;
    private BigInteger targetPoints;

    public MontecarloExperiment(MasterController master) {
        this.master = master;
    }

    public void initExperiment(BigInteger targetPoints, int epsilonExp) {
        this.targetPoints = targetPoints;
        this.pointStore = new PointStore(this);
        this.pointStore.initStore(targetPoints, epsilonExp);
    }

    public void processNewPoints(LinkedList<Point> points) {
        pointStore.enqueuToProcess(points);
    }

    public void updateState(long totalPointsInside, long totalPointsOutside, BigInteger totalPoints,
            long repeatedPoints) {

        if (this.targetPoints.equals(totalPoints)) {
            master.notifyTargetReached(totalPointsInside, totalPointsOutside, totalPoints,
                    getPiEstimation(totalPointsInside, totalPointsOutside));
        } else
            master.updateState(totalPointsInside, totalPointsOutside, totalPoints, targetPoints.subtract(totalPoints),
                    getPiEstimation(totalPointsInside, totalPointsOutside));
    }

    private double getPiEstimation(long totalPointsInside, long totalPointsOutside) {
        return 4 * ((double) totalPointsInside / (double) (totalPointsInside + totalPointsOutside));
    }
}
