import com.zeroc.Ice.*;

import Montecarlo.*;

import java.util.*;

public class WorkerServer {
    public static void main(String[] args) {
        int status = 0;
        List<String> extraArgs = new ArrayList<>();
        try (Communicator communicator = Util.initialize(args, "config.client", extraArgs)) {
            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                status = 1;
            } else {
                status = run(communicator);
            }
        }

        System.exit(status);
    }

    private static int run(Communicator communicator) {
        MasterPrx master = MasterPrx.checkedCast(communicator.propertyToProxy("Master.Direct")).ice_twoway()
                .ice_timeout(-1).ice_secure(false);
        if (master == null) {
            System.err.println("No Master to subscribe to");
            return 1;
        }

        ObjectAdapter adapter = communicator.createObjectAdapter("MasterWorker.Worker");
        WorkerController workerController = new WorkerController(adapter, master);
        System.out.println("Worker is ready. Waiting for isTaskAvailable");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down");
            workerController.onShutDown();
            communicator.destroy();
        }));
        communicator.waitForShutdown();

        return 0;
    }
}
