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

        while (currentResult != null && this.addedPoints < this.targetPoints) {

            for (Point p : currentResult.points) {
                boolean pointWasAdded = tryToAdd(p);

                if (pointWasAdded) {
                    this.addedPoints++;
                    if (this.addedPoints == this.targetPoints)
                        toCheckQueue.clear();
                }
            }

            this.experiment.updateState(inside.size(), outside.size(), addedPoints);
            currentResult = toCheckQueue.poll();
        }
    }

    private boolean tryToAdd(Point p) {
        if (p.isInside)
            return this.inside.add(p);
        else
            return this.outside.add(p);
    }
}
