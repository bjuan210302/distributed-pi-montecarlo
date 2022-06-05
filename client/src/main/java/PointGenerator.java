import java.util.LinkedList;
import java.util.Random;

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
    public void startGeneration(Task t) {
        approvedPoints = new LinkedList<Point>();
        Random random = new Random(t.seed + t.seedOffset);

        this.taskIsAlive = true;
        generatorThread = new Thread(() -> generate(t.numberOfPointsToGenerate, random));
        generatorThread.start();
    }

    private void generate(int target, Random random) {
        for (int i = 0; i < target && this.taskIsAlive; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            boolean isInside = (x*x) + (y*y) <= 1;
            Point p = new Point(x, y, isInside);
            
            this.approvedPoints.add(p);
        }

        // Don't report if task was killed.
        if(!this.taskIsAlive) {
            worker.reportPoints(approvedPoints);
        }
        approvedPoints.clear();
    }

    /**
     * Kills the current generator thread and returns the points that are already approved.
     * Safe to kil even when task is not alive.
     */
    public LinkedList<Point> intentionalKillTask() {
        if (this.taskIsAlive)
            this.taskIsAlive = false;
        return this.approvedPoints;
    }
}
