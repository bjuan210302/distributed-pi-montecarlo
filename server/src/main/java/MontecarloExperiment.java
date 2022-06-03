import java.util.ArrayList;
import java.util.List;

import Montecarlo.Point;

public class MontecarloExperiment {
    
    private MasterController master;
    private List<Point> approvedPointsInside;
    private List<Point> approvedPointsOutside;
    private int remainingPoints;
    private double currentPi;

    public MontecarloExperiment() {
        this.approvedPointsInside = new ArrayList<Point>();
        this.approvedPointsOutside = new ArrayList<Point>();
    }
    
    public void initNumberOfPoints(int n) {
        this.remainingPoints = n;
    }

    public double getCurrentPi() {
        return this.currentPi;
    }

    public List<Point> getApprovedPointsInside(){
        return this.approvedPointsInside;
    }

    public List<Point> getApprovedPointsOutside(){
        return this.approvedPointsOutside;
    }

    public void proccessApprovedPoints(List<Point> inside, List<Point> outside) {
        if (inside.size() + outside.size() > remainingPoints) {
            // master set points left zero
        }
        this.approvedPointsInside.addAll(inside);
        this.approvedPointsOutside.addAll(outside);
        this.remainingPoints -= inside.size() + outside.size();
        // master set points left
        // calculate new pi based on approved points arrays sizes
    }
}
