package GUI;

import arithmetics.PrioritizationMethod;
import management.CriterionTreeNode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Window extends JFrame {
    private class MyJPanel extends JPanel{
        public MyJPanel(){
            super();
        }

        private void paintRec(Graphics2D g2, CriterionTreeNode node){
            int [] index_1 = orderMap.get(node);
            for (int i = 0; i < node.getChildCount(); i++){
                int [] index_2 = orderMap.get(node.getChildAt(i));
                Line2D lin = new Line2D.Double(
                        150 + 225*index_1[1], 200 + 75*index_1[0],
                        150 + 225*index_2[1], 200 + 75*index_2[0]
                );
                g2.draw(lin);
                paintRec(g2, node.getChildAt(i));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            paintRec((Graphics2D) g, windowObserver.getRoot());
        }
    }

    private WindowObserver windowObserver;

    private final JPanel mainPanel = new MyJPanel();

    private final List<JLabel> appleNameLabels = new ArrayList<>();
    private final List<JButton> criterionListButtons = new ArrayList<>();

    private final Map<CriterionTreeNode, int[]> orderMap;

    public Window(JFrame owner, WindowObserver windowObserver) {
        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                owner.setEnabled(true);
            }
        });

        this.windowObserver = windowObserver;

        orderMap = windowObserver.getNodeOrder();

        addAlternativeLabels();
        addCriterionButtons(windowObserver.getRoot());

        JButton getRankingButton = new JButton("Get ranking");
        getRankingButton.setBounds(600, 600, 250, 50);
        getRankingButton.addActionListener(e -> {
            PrioritizationMethod [] choices = { PrioritizationMethod.EVM, PrioritizationMethod.GMM };
            PrioritizationMethod method = (PrioritizationMethod) JOptionPane.showInputDialog(this,
                    "Select a method to calculate the ranking...",
                    "Choose a method", JOptionPane.QUESTION_MESSAGE, null,
                    choices, choices[0]);


            if (method != null)
                try{
                    if (windowObserver.arePCTablesCorrect())
                        new RankingWindow(this, windowObserver, windowObserver.getRanking(method));
                }
                catch (IllegalArgumentException ex){
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
        });

        JButton saveFileButton = new JButton("Save file as...");
        saveFileButton.setBounds(150, 600, 250, 50);
        saveFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                windowObserver.writeToFile(fileToSave.getAbsolutePath());
            }
        });

        mainPanel.setBounds(0,0, 1000, 750);
        mainPanel.setLayout(null);
        mainPanel.add(getRankingButton);
        mainPanel.add(saveFileButton);

        this.setTitle("Apple rank");
        this.setSize(1000, 750);
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
        criterionButton.addActionListener( e -> new PCWindow(this, windowObserver, node));
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

}
