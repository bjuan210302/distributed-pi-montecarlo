import java.math.BigInteger;
// import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
// import java.util.TreeSet;

import Montecarlo.Point;

public class PointStore {

    private class Result {
        LinkedList<Point> points;
        long inside, outside;

        public Result(LinkedList<Point> points, long inside, long outside) {
            this.points = points;
            this.inside = inside;
            this.outside = outside;
        }
    }

    private MontecarloExperiment experiment;
    private Queue<Result> toCheckQueue;
    // private TreeSet<Point> inside;
    // private TreeSet<Point> outside;
    // private LinkedList<Point> allPoints;
    private long insideCounter;
    private long outsideCounter;
    private Thread processerThread;
    private boolean isChecking;
    private boolean enoughPoints;
    private BigInteger targetPoints;
    private BigInteger totalPointCounter;
    // private int epsilonExp;

    public PointStore(MontecarloExperiment experiment) {
        this.experiment = experiment;
        this.toCheckQueue = new LinkedList<Result>();
        this.isChecking = false;
    }

    public void initStore(BigInteger targetPoints, int epsilonExp) {
        // this.allPoints = new LinkedList<Point>();
        this.totalPointCounter = BigInteger.ZERO;
        // Comparator<Point> c = new PointComparator(Math.pow(10, this.epsilonExp));
        // this.inside = new TreeSet<Point>(c);
        // this.outside = new TreeSet<Point>(c);
        this.targetPoints = targetPoints;
        this.enoughPoints = false;
        // this.epsilonExp = epsilonExp;
    }

    public void enqueuToProcess(LinkedList<Point> points, long inside, long outside) {
        if (enoughPoints)
            return;

        BigInteger totalAfterResult = totalPointCounter.add(BigInteger.valueOf(inside + outside));
        enoughPoints = targetPoints.compareTo(totalAfterResult) <= 0;
        System.out.println("Total after result is " + totalAfterResult.toString());
        System.out.println("Enough points is " + enoughPoints);
        if (enoughPoints)
            experiment.notifyEnoughPoints();

        this.toCheckQueue.add(new Result(points, inside, outside));

        if (!this.isChecking) {
            this.processerThread = new Thread(() -> check());
            this.processerThread.start();
        }
    }

    private void check() {
        this.isChecking = true;
        Result currentResult = toCheckQueue.poll();

        while (currentResult != null) {
            BigInteger totalAfterResult = totalPointCounter
                    .add(BigInteger.valueOf(currentResult.inside + currentResult.outside));
            boolean needAllPoints = targetPoints.compareTo(totalAfterResult) >= 0;

            if (needAllPoints) {
                totalPointCounter = totalAfterResult;
                insideCounter += currentResult.inside;
                outsideCounter += currentResult.outside;
            } else {
                System.out.println("Inside else");
                System.out.println("totalAfterResult was " + totalAfterResult.toString() + "(" + currentResult.inside + "+" + currentResult.outside);
                for (Point p : currentResult.points) {
                    totalPointCounter = totalPointCounter.add(BigInteger.ONE);
                    if (p.isInside) {
                        this.insideCounter++;
                    } else {
                        this.outsideCounter++;
                    }

                    if (this.totalPointCounter.equals(this.targetPoints)) {
                        toCheckQueue.clear();
                        break;
                    }
                }
            }
            this.experiment.updateState(this.insideCounter, this.outsideCounter, this.totalPointCounter);
            currentResult = toCheckQueue.poll();
        }

        this.isChecking = false;
    }

}
