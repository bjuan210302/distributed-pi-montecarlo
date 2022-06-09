import java.util.ArrayList;
import java.util.List;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
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
                MasterController masterController = new MasterController();
                ObjectAdapter adapter = communicator.createObjectAdapter("Master");
                adapter.add(masterController, Util.stringToIdentity("subject"));
                adapter.activate();
                System.out.println("Servidor listo. Empezar exp");
                GUIController guiController = new GUIController();
                guiController.setMasterController(masterController);
                guiController.run();
                communicator.waitForShutdown();
            }
        }

        System.exit(status);
    }
}
