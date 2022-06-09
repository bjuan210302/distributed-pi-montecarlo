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
        if (taskAvailable) {
            System.out.println("About to work()");
            work();
        }
        else if (generator.isWorking()) {
            this.generator.intentionalKillTask();
            System.out.println("Generation stopped.");
        }
    }

    public void reportPoints(LinkedList<Point> points) {
        master.ice_oneway().ice_compress(true).reportPartialResult(points);
    }

    private void work() {
        System.out.println("Getting task...");
        Task t = this.master.getTask();
        System.out.println("Task fetched ssuccesfully.");
        this.generator.startGeneration(t);
    }

    public void notifyNoWork() {
        work();
    }
    public void onShutDown() {
        LinkedList<Point> saved = this.generator.intentionalKillTask();
        System.out.println("Generation killed by shutdown. Reporting " + saved.size() + " saved points." );
        master.ice_oneway().ice_compress(true).reportPartialResult(saved);
    }
}
