import java.util.LinkedList;
import java.util.Random;
import java.util.TreeSet;

import Montecarlo.Point;
import Montecarlo.Task;

public class PointGenerator {

    private WorkerController worker;
    private LinkedList<Point> approvedPoints;
    private Thread generatorThread;
    private boolean taskIsAlive;

    public PointGenerator(WorkerController worker) {
        this.worker = worker;
    }

    // We need the points a chronological order, hence the linked list.
    // However, iterating the linked list everytime a point is generated is not time efficient.
    // The solution is to use a TreeSet to decide efficiently if the points is unique or not.
    // This faster, but not memory efficient.
    public void startGeneration(Task t) {
        approvedPoints = new LinkedList<Point>();
        Random random = new Random(t.seed + t.seedOffset);
        TreeSet<Point> approver = new TreeSet<Point>(new PointComparator(Math.pow(10, t.epsilonExponent)));

        this.taskIsAlive = true;
        generatorThread = new Thread(() -> generate(t.numberOfPointsToGenerate, random, approver));
        generatorThread.start();
    }

    private void generate(int target, Random random, TreeSet<Point> approver) {
        for (int i = 0; i < target && this.taskIsAlive;) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            boolean isInside = (x*x) + (y*y) <= 1;
            Point p = new Point(x, y, isInside);
            
            boolean pointIsValid = approver.add(p);
            if (pointIsValid) {
                this.approvedPoints.add(p);
                i++;
            }
        }

        // Don't report if task was killed.
        if(!this.taskIsAlive) {
            worker.reportPoints(approvedPoints);
            approvedPoints.clear();
        }
    }

    /**
     * Kills the current generator thread and returns the points that are already approved.
     * Safe to kil even when task is not alive.
     */
    public LinkedList<Point> intentionalKillGen() {
        if (this.taskIsAlive)
            this.taskIsAlive = false;
        return this.approvedPoints;
    }
}
