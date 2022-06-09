import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        update();
        events();
    }

    public void update() {
        String[] result = {};

        gui.getTable().addRow(result);
    }

    public void events() {
        gui.getCompute().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(!gui.getPoints().getText().isEmpty() && !gui.getSeed().getText().isEmpty() && !gui.getEpsilon().getText().isEmpty()) {
                    int targetPointsExponent = Integer.parseInt(gui.getPoints().getText());
                    int epsilonExp = Integer.parseInt(gui.getEpsilon().getText());
                    int seed = Integer.parseInt(gui.getSeed().getText());

                    masterController.initCalculation(targetPointsExponent, epsilonExp, seed);
                }
            }
            
        });

        gui.getDoExperiment().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!gui.getExperiments().getSelectedItem().toString().equals("-Select-")) {
                    int targetPointsExponent = Integer.parseInt(gui.getExperiments().getSelectedItem().toString().split("e")[1]);
                    
                    masterController.initCalculation(targetPointsExponent, 0, 42);
                }
            }
        });
    }
    
}
