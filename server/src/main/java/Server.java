import java.util.*;
import com.zeroc.Ice.*;
import Montecarlo.*;
public class Server {
    public static void main(String[] args) {
        int status = 0;
        List<String> extraArgs = new ArrayList<String>();

        try (Communicator communicator = Util.initialize(args, "config.server", extraArgs)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> communicator.destroy()));

            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                status = 1;
            } else {
                ObjectAdapter adapter = communicator.createObjectAdapter("MasterWorker.Master");
                adapter.add(new MasterController(), Util.stringToIdentity("subject"));
                adapter.activate();
                System.out.println("Servidor listo.");
                communicator.waitForShutdown();
            }
        }

        System.exit(status);
    }
}
