import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

public class GUIController implements Runnable {

    private GUI gui;
    private MasterController masterController;

    public void setMasterController(MasterController masterController) {
        this.masterController = masterController;
    }

    @Override
    public void run() {
        try {
            gui = new GUI();
            gui.setLocationRelativeTo(null);
            gui.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        events();
    }

    public void update(double pi, BigInteger processedPoints, long millisElapsed) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisElapsed);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisElapsed) - TimeUnit.MINUTES.toSeconds(minutes);
        long millis = millisElapsed - TimeUnit.SECONDS.toMillis(seconds);
        String millisString = String.format("%02d : %02d : %04d", minutes, seconds, millis);
        String[] result = { "" + pi, processedPoints.toString(), millisString };

        gui.getTable().addRow(result);
    }

    public void events() {
        gui.getCompute().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gui.getPoints().getText().isEmpty() && !gui.getSeed().getText().isEmpty()) {
                    int targetPointsExponent = Integer.parseInt(gui.getPoints().getText());
                    int seed = Integer.parseInt(gui.getSeed().getText());

                    masterController.initCalculation(targetPointsExponent, seed);
                }
            }

        });
    }

}
