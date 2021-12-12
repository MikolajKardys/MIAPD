package GUI;

import management.CriterionTreeMap;
import management.CriterionTreeNode;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Window extends JFrame {
    private WindowObserver windowObserver;

    private final JPanel mainPanel = new JPanel();

    private final List<JLabel> appleNameLabels = new ArrayList<>();
    private final List<JButton> criterionListButtons = new ArrayList<>();

    private final Map<CriterionTreeNode, int[]> orderMap;

    public Window(WindowObserver windowObserver) {
        this.windowObserver = windowObserver;

        orderMap = windowObserver.getNodeOrder();

        addAlternativeLabels();
        addCriterionButtons(windowObserver.getRoot());

        JButton getRankingButton = new JButton("Get ranking");
        getRankingButton.setBounds(100, 600, 800, 25);
        getRankingButton.addActionListener(e -> {
            if(true) {
                new RankingWindow(this, windowObserver, windowObserver.getRanking());
            }

            /*if(windowObserver.isPCTablesCorrect())
                new RankingWindow(windowObserver, windowObserver.getRanking());*/
        });

        JButton loadFileButton = new JButton("Load file");
        loadFileButton.setBounds(100, 650, 375, 25);
        loadFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                CriterionTreeMap newMap = CriterionTreeMap.readFromFile(selectedFile.getAbsolutePath());
                new Window(newMap);
                this.dispose();
            }
        });

        JButton saveFileButton = new JButton("Save file");
        saveFileButton.setBounds(525, 650, 375, 25);
        saveFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                System.out.println("Save as file: " + fileToSave.getAbsolutePath());

                windowObserver.writeToFile(fileToSave.getAbsolutePath());
            }
        });

        mainPanel.setBounds(0,0, 1000, 750);
        mainPanel.setLayout(null);
        mainPanel.add(getRankingButton);
        mainPanel.add(loadFileButton);
        mainPanel.add(saveFileButton);

        this.setTitle("Apple rank");
        this.setSize(1000, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.add(mainPanel);
        this.setVisible(true);
    }

    private void addAlternativeLabels() {
        for(int i=0; i<windowObserver.applesNumber(); i++) {
            JLabel appleLabel = new JLabel(windowObserver.getIthAppleName(i), SwingConstants.CENTER);
            appleLabel.setBounds(50 + 225*(i%4), 25 + 50*(i/4), 200, 25);
            appleLabel.setFont(new Font("Serif", Font.PLAIN, 16));
            appleNameLabels.add(appleLabel);
            mainPanel.add(appleLabel);
        }
    }

    private void addCriterionButtons(CriterionTreeNode node) {
        String criterionName = node.toString();

        JButton criterionButton = new JButton(criterionName);

        int [] index = orderMap.get(node);
        criterionButton.setBounds(50 + 225*index[1], 175 + 75*index[0], 200, 50);

        criterionButton.setFont(new Font("Serif", Font.PLAIN, 16));
        criterionButton.addActionListener( e -> {
            new PCWindow(this, windowObserver, node);
        });
        criterionListButtons.add(criterionButton);
        mainPanel.add(criterionButton);

        if(node.getChildCount() > 0)
            for(int i=0; i<node.getChildCount(); i++)
                addCriterionButtons(node.getChildAt(i));
    }

    public void setWindowObserver(WindowObserver windowObserver) {
        this.windowObserver = windowObserver;
    }

    public void removeWindowObserver() {
        this.windowObserver = null;
    }


    private void paintRec(Graphics2D g2, CriterionTreeNode node){
        int [] index_1 = orderMap.get(node);
        for (int i = 0; i < node.getChildCount(); i++){
            int [] index_2 = orderMap.get(node.getChildAt(i));
            Line2D lin = new Line2D.Double(
                    150 + 225*index_1[1], 225 + 75*index_1[0],
                    150 + 225*index_2[1], 225 + 75*index_2[0]
            );
            g2.draw(lin);
            paintRec(g2, node.getChildAt(i));
        }
    }

    public void paint(Graphics g) {
        super.paint(g);
        paintRec((Graphics2D) g, windowObserver.getRoot());
    }
}
