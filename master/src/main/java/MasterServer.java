import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
                System.out.println("Servidor listo.");
                new Thread(() -> {
                    menuLoop(masterController);
                }).start();
                communicator.waitForShutdown();
            }
        }

        System.exit(status);
    }

    public static void menuLoop(MasterController masterController) {
        try {
            FileManager.readFile();
        } catch (IOException e1) {
            System.out.println("ERROR ON READING PRE CONFIGURED EXPERIMENTS");
            e1.printStackTrace();
        }
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String input = null;
        do {
            try {
                System.out.println("option >>");
                input = in.readLine();

                if (input.equals("gui")) {
                   // Start GUI
                }

                if (input.equals("auto")) {
                    masterController.setupAutomaticExperiment(FileManager.experiments, 10);
                    masterController.automaticStart();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (!input.equals("x"));
    }
}
