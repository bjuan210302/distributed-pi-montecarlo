import java.math.BigInteger;
// import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
// import java.util.TreeSet;

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
    // private TreeSet<Point> inside;
    // private TreeSet<Point> outside;
    private LinkedList<Point> allPoints;
    private long insideCounter;
    private long outsideCounter;
    private long repeatedCounter;
    private Thread processerThread;
    private boolean isChecking;
    private BigInteger targetPoints;
    private BigInteger totalPointCounter;
    // private int epsilonExp;

    public PointStore(MontecarloExperiment experiment) {
        this.experiment = experiment;
        this.toCheckQueue = new LinkedList<Result>();
        this.isChecking = false;
    }

    public void initStore(BigInteger targetPoints, int epsilonExp) {
        this.allPoints = new LinkedList<Point>();
        this.totalPointCounter = BigInteger.ZERO;
        // Comparator<Point> c = new PointComparator(Math.pow(10, this.epsilonExp));
        // this.inside = new TreeSet<Point>(c);
        // this.outside = new TreeSet<Point>(c);
        this.targetPoints = targetPoints;
        // this.epsilonExp = epsilonExp;
    }

    public void enqueuToProcess(LinkedList<Point> points) {
        if (this.totalPointCounter == this.targetPoints)
            return;

        this.toCheckQueue.add(new Result(points));

        if (!this.isChecking) {
            System.out.println("Store not cheecking. Starting thread...");
            this.processerThread = new Thread(() -> check());
            this.processerThread.start();
        }
    }

    private void check() {
        this.isChecking = true;
        Result currentResult = toCheckQueue.poll();
        System.out.println("Thread successfully started. First job is " + currentResult);

        while (currentResult != null) {

            for (Point p : currentResult.points) {
                this.allPoints.add(p);
                this.totalPointCounter = this.totalPointCounter.add(BigInteger.ONE);

                // boolean pointIsNew = false;
                if (p.isInside) {
                    this.insideCounter++;
                    // pointIsNew = this.inside.add(p);
                } else {
                    this.outsideCounter++;
                    // pointIsNew = this.outside.add(p);
                }

                // if (!pointIsNew)
                //     this.repeatedCounter++;

                if (this.totalPointCounter == this.targetPoints) {
                    toCheckQueue.clear();
                    break;
                }
            }

            System.out.println("From checker thread: Total is: " + this.totalPointCounter);
            this.experiment.updateState(this.insideCounter, this.outsideCounter, this.totalPointCounter, this.repeatedCounter);
            currentResult = toCheckQueue.poll();
        }

        System.out.println("No more results to check");
        this.isChecking = false;
    }

}
