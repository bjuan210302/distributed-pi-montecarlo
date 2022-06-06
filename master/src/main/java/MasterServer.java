import java.util.*;
import com.zeroc.Ice.*;
public class MasterServer {
    public static void main(String[] args) {
        int status = 0;
        List<String> extraArgs = new ArrayList<String>();

        try (Communicator communicator = Util.initialize(args, "config.server", extraArgs)) {

            Runtime.getRuntime().addShutdownHook(new Thread(() -> communicator.destroy()));

            if (!extraArgs.isEmpty()) {
                System.err.println("too many arguments");
                status = 1;
            } else {
                ObjectAdapter adapter = communicator.createObjectAdapter("Master");
                MasterController masterController = new MasterController();
                adapter.add(masterController, Util.stringToIdentity("subject"));
                adapter.activate();
                System.out.println("Servidor listo.");
                Controller guiController = new Controller();
                guiController.setMasterController(masterController);
                communicator.waitForShutdown();
            }
        }

        System.exit(status);
    }
}
