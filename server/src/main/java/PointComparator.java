import java.util.Comparator;

import Montecarlo.Point;

public class PointComparator implements Comparator<Point> {

    private double epsilon;
    private boolean considerEpsilon;

    public PointComparator(double epsilon) {
        this.epsilon = epsilon;
        this.considerEpsilon = epsilon < 1;
    }

    /**
     * Points are compared first with the X component and then with the Y component.
     */
    @Override
    public int compare(Point o1, Point o2) {
        int xDiff = compareComponent(o1.x, o2.x);
        if (xDiff != 0 ) return xDiff;

        int yDiff = compareComponent(o1.y, o2.y);
        if (yDiff != 0 ) return xDiff;

        // Points are equal in X and Y.
        return 0;
    }

    private int compareComponent(double x1, double x2) {
        double xDiff = x1 - x2;
        if (considerEpsilon && xDiff < this.epsilon) return 0;

        if (xDiff < 0) return -1;
        else return 1;
    }
}
