import java.util.LinkedList;
import java.util.Random;

import Montecarlo.Point;
import Montecarlo.Task;

public class PointGenerator {

    private WorkerController worker;
    private LinkedList<Point> generatedPoints;
    private Thread generatorThread;
    private boolean taskIsAlive;

    public PointGenerator(WorkerController worker) {
        this.worker = worker;
    }
    public void startGeneration(Task t) {
        generatedPoints = new LinkedList<Point>();
        Random random = new Random(t.seed + t.seedOffset);

        System.out.println("Starting generation seed is " + t.seed + ", offset is " + t.seedOffset);
        this.taskIsAlive = true;
        generatorThread = new Thread(() -> generate(t.target, random));
        generatorThread.start();
    }

    private void generate(long target, Random random) {
        System.out.println("Generation thread started. Generating...");
        long half = target / 2;
        System.out.println("Half of points to generate is: " + half);
        for (long i = 0; i < target && this.taskIsAlive; i++) {
            double x = random.nextDouble();
            double y = random.nextDouble();
            boolean isInside = (x*x) + (y*y) <= 1;
            this.generatedPoints.add(new Point(x, y, isInside));

            if (i > 0 && i % 1000000 == 0){
                System.out.println("Reached " + i + " points. Reporting points and reseting list");
                worker.reportPoints(generatedPoints);
                generatedPoints.clear();
            }
        }
        System.out.println("Done generating.");

        // Don't report if task was killed.
        if(this.taskIsAlive) {
            System.out.println("Reporting points to Worker");
            worker.reportPoints(generatedPoints);
        }
        generatedPoints.clear();
        worker.notifyNoWork();
    }

    /**
     * Kills the current generator thread and returns the points that are already approved.
     * Safe to kil even when task is not alive.
     */
    public LinkedList<Point> intentionalKillTask() {
        if (this.taskIsAlive)
            this.taskIsAlive = false;
        return this.generatedPoints;
    }
}
