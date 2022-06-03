import Montecarlo.*;
import com.zeroc.Ice.Current;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ObjectPrx;
import com.zeroc.Ice.Util;

public final class WorkerController implements Worker
{

    private MasterPrx master;

    public WorkerController(ObjectAdapter iceAdapter, MasterPrx master) {
        ObjectPrx receiverO = iceAdapter.add(this, Util.stringToIdentity("worker"));
        iceAdapter.activate();

        WorkerPrx receiver = WorkerPrx.uncheckedCast(receiverO);
        master.subscribe(receiver);

        this.master = master;
    }

    @Override
    public void update(boolean taskAvailable, Current current) {
        // TODO if true get task if false stop everything
    }

    public void reportPoints(Point[] inside, Point[] outside) {
        master.reportPartialResult(outside, inside);
    }

}
