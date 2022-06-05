import Montecarlo.*;

import java.util.LinkedList;

import com.zeroc.Ice.Current;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

public final class WorkerController implements Worker {

    private MasterPrx master;
    private PointGenerator generator;

    public WorkerController(ObjectAdapter iceAdapter, MasterPrx master) {
        iceAdapter.add(this, Util.stringToIdentity("worker"));
        iceAdapter.activate();

        WorkerPrx receiver = WorkerPrx
                .uncheckedCast(iceAdapter.createProxy(com.zeroc.Ice.Util.stringToIdentity("worker")));

        this.master = master;
        this.generator = new PointGenerator(this);
        master.subscribe(receiver);
    }

    @Override
    public void update(boolean taskAvailable, Current current) {
        System.out.println("Update received from master, isTaskAvailable is " + taskAvailable);
        if (taskAvailable)
            work();
        else {
            this.generator.intentionalKillTask();
            System.out.println("Generation killed");
        }
    }

    public void reportPoints(LinkedList<Point> points) {
        master.reportPartialResult(points);
        work();
    }

    private void work() {
        Task t = this.master.getTask();
        this.generator.startGeneration(t);
    }

    public void onShutDown() {
        this.generator.intentionalKillTask();
        // Report points
        System.out.println("Generation killed by shutdown");
    }
}
