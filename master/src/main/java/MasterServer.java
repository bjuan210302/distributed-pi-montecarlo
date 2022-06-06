import java.io.IOException;
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
                MasterController masterController = new MasterController();
                ObjectAdapter adapter = communicator.createObjectAdapter("Master");
                adapter.add(masterController, Util.stringToIdentity("subject"));
                adapter.activate();
                FileManager.readFile();
                System.out.println("Servidor listo. Empezar exp");
                masterController.initCalculation(FileManager.getExperiment(8));
                communicator.waitForShutdown();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(status);
    }
}
