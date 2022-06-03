import com.zeroc.Ice.*;

import Montecarlo.*;

import java.util.*;

public class WorkerInitializer {
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
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                communicator.destroy();
                System.out.println("Shutting down");
            }));
        }

        System.exit(status);
    }

    private static int run(Communicator communicator) {
        MasterPrx master = MasterPrx.checkedCast(communicator.propertyToProxy("Subject.Proxy")).ice_twoway()
                .ice_timeout(-1).ice_secure(false);
        if (master == null) {
            System.err.println("No Master to subscribe to");
            return 1;
        }

        ObjectAdapter adapter = communicator.createObjectAdapter("MasterWorker.Worker");
        WorkerController workerController = new WorkerController(adapter, master);
        communicator.waitForShutdown();

        return 0;
    }
}
