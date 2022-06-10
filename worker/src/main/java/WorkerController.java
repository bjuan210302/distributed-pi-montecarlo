import com.zeroc.Ice.Current;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;

import Montecarlo.MasterPrx;
import Montecarlo.Task;
import Montecarlo.Worker;
import Montecarlo.WorkerPrx;

public final class WorkerController implements Worker {

    private MasterPrx master;
    WorkerPrx selfPrx;
    private PointGenerator generator;

    public WorkerController(ObjectAdapter iceAdapter, MasterPrx master) {
        iceAdapter.add(this, Util.stringToIdentity("worker"));
        iceAdapter.activate();

        selfPrx = WorkerPrx
                .uncheckedCast(iceAdapter.createProxy(com.zeroc.Ice.Util.stringToIdentity("worker")));

        this.master = master;
        this.generator = new PointGenerator(this);
        master.subscribe(selfPrx);
    }

    @Override
    public void update(boolean taskAvailable, Current current) {
        System.out.println("Update received from master, isTaskAvailable is " + taskAvailable);
        if (taskAvailable) {
            System.out.println("About to work()");
            work();
        } else if (generator.isWorking()) {
            this.generator.intentionalKillTask();
            System.out.println("Generation stopped.");
        }
    }

    public void reportPoints(long insideCounter, long outsideCounter) {
        master.ice_compress(true).ice_oneway().reportPartialResult(insideCounter, outsideCounter);
    }

    private void work() {
        System.out.println("Getting task...");
        Task t = this.master.getTask();
        if (t == null) {
            System.out.println("Task was null.");
        } else {
            System.out.println("Task fetched ssuccesfully.");
            this.generator.startGeneration(t);
        }
    }

    public void notifyNoWork() {
        work();
    }

    public void onShutDown() {
        this.generator.intentionalKillTask();
        System.out.println("Killed by shutdown.");
        System.out.println("Unsubscribing from Master...");
        master.ice_oneway().unsubscribe(selfPrx);
    }
}
