import java.math.BigInteger;
// import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;
// import java.util.TreeSet;

public class PointStore {

    private class Result {
        long inside, outside;

        public Result(long inside, long outside) {
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

    public void enqueuToProcess(long inside, long outside) {
        if (enoughPoints)
            return;

        BigInteger totalAfterResult = totalPointCounter.add(BigInteger.valueOf(inside + outside));
        enoughPoints = targetPoints.compareTo(totalAfterResult) <= 0;
        if (enoughPoints)
            experiment.notifyEnoughPoints();

        this.toCheckQueue.add(new Result(inside, outside));

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

            totalPointCounter = totalAfterResult;
            insideCounter += currentResult.inside;
            outsideCounter += currentResult.outside;

            this.experiment.updateState(this.insideCounter, this.outsideCounter, this.totalPointCounter);
            currentResult = toCheckQueue.poll();
        }

        this.isChecking = false;
    }

}
