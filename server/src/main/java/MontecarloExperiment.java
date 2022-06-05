import java.util.LinkedList;

import Montecarlo.Point;

public class MontecarloExperiment {

    private MasterController master;
    private PointStore pointStore;
    private int totalPointsInside;
    private int totalPointsOutside;
    private int targetPoints;

    public void initExperiment(int targetPoints, int epsilonExp) {
        this.targetPoints = targetPoints;
        this.pointStore = new PointStore(this);
        this.pointStore.initStore(targetPoints, epsilonExp);
    }

    public void processNewPoints(LinkedList<Point> points) {
        pointStore.enqueuToProcess(points);
    }

    public void updateState(int totalPointsInside, int totalPointsOutside, int totalPoints) {
        this.totalPointsInside = totalPointsInside;
        this.totalPointsOutside = totalPointsOutside;

        if (this.targetPoints == totalPoints)
            master.notifyTargetReached();

        master.updateState(totalPointsInside, totalPointsOutside, totalPoints, getPiEstimation());
    }

    private double getPiEstimation() {
        return 4 * (totalPointsInside / (totalPointsInside + totalPointsOutside));
    }
}
