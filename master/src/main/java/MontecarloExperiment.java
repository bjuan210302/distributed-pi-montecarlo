import java.math.BigInteger;
import java.util.LinkedList;

import Montecarlo.Point;

public class MontecarloExperiment {

    private MasterController master;
    private PointStore pointStore;
    private long totalPointsInside;
    private long totalPointsOutside;
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

    public void updateState(long totalPointsInside, long totalPointsOutside, BigInteger totalPoints, long repeatedPoints) {
        this.totalPointsInside = totalPointsInside;
        this.totalPointsOutside = totalPointsOutside;

        if (this.targetPoints == totalPoints)
            master.notifyTargetReached(totalPointsInside, totalPointsOutside, totalPoints, getPiEstimation());
        else
            master.updateState(totalPointsInside, totalPointsOutside, totalPoints, getPiEstimation());
    }

    private double getPiEstimation() {
        return 4 * ((double)totalPointsInside / (double)(totalPointsInside + totalPointsOutside));
    }
}
