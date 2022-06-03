import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Montecarlo.Point;

public class PointGenerator {
    
    private WorkerController worker;
    private Random random;

    public PointGenerator() {
        this.random = new Random();
    }

    public void startGeneration(int n, int epsilonExponent) {
        List<Point> inside = new ArrayList<Point>();
        List<Point> outside = new ArrayList<Point>();

        // TODO: Generate, check for duplicates, check if inside or outside.
    }
}
