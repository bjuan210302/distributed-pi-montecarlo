import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class GUI extends JFrame {

    private JPanel content;
    private JLabel lPoints, lSeed;
    private JTextField tfPoints, tfSeed;
    private JButton compute;
    private JScrollPane scrollPane;
    private JTable results;
    private DefaultTableModel table;
    private JLabel lExperiments;
    private JComboBox<String> experiments;
    private JButton doExperiment;

    public GUI() {
        initComponents();
    }

    public void initComponents() {

        setTitle("PiMonteCarlo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 640, 480);
        setResizable(false);

        content = new JPanel();
        content.setBorder(new EmptyBorder(10,10,10,10));
        content.setLayout(null);

        lPoints = new JLabel("Points Exponent:");
        lPoints.setBounds(70, 30, 100, 15);
        content.add(lPoints);

        tfPoints = new JTextField();
        tfPoints.setBounds(70, 50, 100, 20);
        content.add(tfPoints);
        tfPoints.setColumns(20);

        lSeed = new JLabel("Seed:");
        lSeed.setBounds(330, 30, 100, 15);
        content.add(lSeed);

        tfSeed = new JTextField();
        tfSeed.setBounds(330, 50, 100, 20);
        content.add(tfSeed);
        tfSeed.setColumns(20);

        compute = new JButton("Compute");
        compute.setBounds(460, 40, 95, 30);
        content.add(compute);

        JSeparator s = new JSeparator();
        s.setBounds(100, 85, 425, 10);
        content.add(s);

        results = new JTable();
        String[] columns = {"Pi","UsedPoints","Time (m:s:ms)"};
        table =  new DefaultTableModel(columns, 0);
        results.setModel(table);
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        results.setDefaultRenderer(Object.class, centerRenderer);
        //results.setEnabled(false);
        results.setDefaultEditor(Object.class, null);
        scrollPane = new JScrollPane(results);
        scrollPane.setBounds(70, 100, 300, 300);
        content.add(scrollPane);


        lExperiments = new JLabel(String.format("<html><body style=\"text-align: center;  text-justify: inter-word;\">%s</body></html>","Predetermined Experiment"));
        lExperiments.setBounds(430, 155, 130, 40);
        content.add(lExperiments);

        setContentPane(content);
    }

    public JTextField getPoints() {
        return tfPoints;
    }

    public JTextField getSeed() {
        return tfSeed;
    }

    public JButton getCompute() {
        return compute;
    }

    public DefaultTableModel getTable() {
        return table;
    }

    public JComboBox<String> getExperiments() {
        return experiments;
    }

    public JButton getDoExperiment() {
        return doExperiment;
    }
}