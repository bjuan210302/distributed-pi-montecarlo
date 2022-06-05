import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.TreeSet;

import Montecarlo.Point;

public class PointStore {

    private class Result {
        LinkedList<Point> points;

        Result(LinkedList<Point> points) {
            this.points = points;
        }
    }

    private MontecarloExperiment experiment;
    private Queue<Result> toCheckQueue;
    private TreeSet<Point> inside;
    private TreeSet<Point> outside;
    private LinkedList<Point> allPoints;
    private int insideCounter;
    private int outsideCounter;
    private int repeatedCounter;
    private Thread processerThread;
    private boolean isChecking;
    private int targetPoints;
    private int addedPoints;
    private int epsilonExp;

    public PointStore(MontecarloExperiment experiment) {
        this.experiment = experiment;
        this.toCheckQueue = new LinkedList<Result>();
        this.isChecking = false;
    }

    public void initStore(int targetPoints, int epsilonExp) {
        this.addedPoints = 0;
        Comparator<Point> c = new PointComparator(Math.pow(10, this.epsilonExp));
        this.inside = new TreeSet<Point>(c);
        this.outside = new TreeSet<Point>(c);
        this.targetPoints = targetPoints;
        this.epsilonExp = epsilonExp;
    }

    public void enqueuToProcess(LinkedList<Point> points) {
        if (this.addedPoints == this.targetPoints)
            return;

        this.toCheckQueue.add(new Result(points));

        if (!this.isChecking) {
            this.processerThread = new Thread(() -> check());
            this.processerThread.start();
        }
    }

    private void check() {
        this.isChecking = true;
        Result currentResult = toCheckQueue.poll();

        while (currentResult != null) {

            for (Point p : currentResult.points) {
                this.allPoints.add(p);
                this.addedPoints++;

                boolean pointIsNew = false;
                if (p.isInside) {
                    this.insideCounter++;
                    pointIsNew = this.inside.add(p);
                } else {
                    this.outsideCounter++;
                    pointIsNew = this.outside.add(p);
                }

                if (!pointIsNew)
                    this.repeatedCounter++;

                if (this.addedPoints == this.targetPoints) {
                    toCheckQueue.clear();
                    break;
                }
            }

            this.experiment.updateState(this.insideCounter, this.outsideCounter, this.addedPoints, this.repeatedCounter);
            currentResult = toCheckQueue.poll();
        }

        this.isChecking = false;
    }

}
