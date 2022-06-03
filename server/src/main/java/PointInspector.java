import java.util.AbstractQueue;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import Montecarlo.Point;

public class PointInspector {
    
    private class Result {
        Result(Point[] inside, Point[] outside) {
            this.inside = inside;
            this.outside = outside;
        }
        Point[] inside;
        Point[] outside;
    }

    private MontecarloExperiment experiment;
    private Queue<Result> toCheck;
    private boolean isChecking;

    public PointInspector(MontecarloExperiment experiment) {
        this.toCheck = new LinkedList<Result>();
        this.experiment = experiment;
    }

    public void enqueuToCheckDuplicates(Point[] inside, Point[] outside) {
        this.toCheck.add(new Result(inside, outside));

        if (!this.isChecking) check();
    }

    private void check() {
        // TODO: Start checking for duplicates in new thread
        // How to check efficiently? Sort the lists?
    }

    private void sendToExperiment(Point[] approvedInside, Point[] approvedOutside) {
        this.experiment.proccessApprovedPoints(Arrays.asList(approvedInside), Arrays.asList(approvedOutside));
    }
}
