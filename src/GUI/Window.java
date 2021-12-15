package GUI;

import arithmetics.PrioritizationMethod;
import management.CriterionTreeNode;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Window extends JFrame {
    private class MyJPanel extends JPanel{
        public MyJPanel(){
            super();
        }

        private void paintRec(Graphics2D g2, CriterionTreeNode node){
            int [] index_1 = {criterionButtonsMap.get(node).getX(), criterionButtonsMap.get(node).getY()};
            int width = criterionButtonsMap.get(node).getWidth();
            int height = criterionButtonsMap.get(node).getHeight();

            for (int i = 0; i < node.getChildCount(); i++){
                CriterionTreeNode child = node.getChildAt(i);
                int [] index_2 = {criterionButtonsMap.get(child).getX(), criterionButtonsMap.get(child).getY()};
                int childWidth = criterionButtonsMap.get(child).getWidth();
                int childHeight = criterionButtonsMap.get(child).getHeight();

                g2.drawLine(index_1[0] + width / 2, index_1[1] + height / 2,
                        index_2[0] + childWidth / 2, index_2[1] + childHeight / 2);
                paintRec(g2, node.getChildAt(i));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            paintRec((Graphics2D) g, windowObserver.getRoot());
        }
    }

    private final WindowObserver windowObserver;

    private final JPanel buttonPanel = new MyJPanel();
    private final JPanel labelPanel = new JPanel();

    private final List<String> appleNameLabels = new ArrayList<>();
    private final Map<CriterionTreeNode, JButton> criterionButtonsMap = new HashMap<>();

    public Window(JFrame owner, WindowObserver windowObserver) {
        JPanel mainPanel = new JPanel();

        this.addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e){
                owner.setEnabled(true);
            }
        });

        this.windowObserver = windowObserver;

        JLabel titleLabels = new JLabel("Choice names:");
        titleLabels.setBounds(0, 10, 1000, 30);
        titleLabels.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabels.setFont(new Font("Serif", Font.PLAIN, 20));
        mainPanel.add(titleLabels);
        labelPanel.setLayout(new GridBagLayout());
        JScrollPane labelScrollPane = new JScrollPane(labelPanel);
        labelScrollPane.setBounds(50, 50, 900, 100);
        addAlternativeLabels();


        JLabel titleButtons = new JLabel("Criteria tree:");
        titleButtons.setBounds(0, 180, 1000, 30);
        titleButtons.setHorizontalAlignment(SwingConstants.CENTER);
        titleButtons.setFont(new Font("Serif", Font.PLAIN, 20));
        mainPanel.add(titleButtons);
        buttonPanel.setLayout(new GridBagLayout());
        JScrollPane buttonScrollPane = new JScrollPane(buttonPanel);
        buttonScrollPane.setBounds(50, 220, 900, 375);
        addCriterionButtons(windowObserver, windowObserver.getRoot());


        JButton getRankingButton = new JButton("Get ranking");
        getRankingButton.setBounds(600, 620, 250, 50);
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
        saveFileButton.setBounds(150, 620, 250, 50);
        saveFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("*.json", "json"));
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                String fileName = fileChooser.getSelectedFile().getAbsolutePath();

                if (!fileName.endsWith(".json"))
                    fileName += ".json";

                windowObserver.writeToFile(fileName);
            }
        });

        mainPanel.setBounds(0,0, 1000, 750);
        mainPanel.setLayout(null);
        mainPanel.add(getRankingButton);
        mainPanel.add(saveFileButton);

        mainPanel.add(labelScrollPane);
        mainPanel.add(buttonScrollPane);

        this.setTitle("Apple rank");
        this.setSize(1000, 750);
        this.setResizable(false);
        this.setLayout(null);
        this.add(mainPanel);
        this.setVisible(true);
    }

    private void addAlternativeLabels() {
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1,5,2,5);
        c.anchor = GridBagConstraints.FIRST_LINE_START;
        for(int i=0; i<windowObserver.applesNumber(); i++) {
            c.gridx = i % 4;
            c.gridy = i / 4;

            String appleName = windowObserver.getIthAppleName(i);
            JLabel appleLabel = new JLabel(appleName, SwingConstants.CENTER);
            appleLabel.setPreferredSize(new Dimension(200, 40));
            appleLabel.setFont(new Font("Serif", Font.PLAIN, 16));
            appleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
            appleLabel.setVerticalAlignment(SwingConstants.CENTER);

            JPopupMenu menu = new JPopupMenu();
            JMenuItem remove = new JMenuItem("Remove choice");
            remove.addActionListener(e -> {
                try {
                    windowObserver.removeApple(appleName);

                    labelPanel.removeAll();

                    addAlternativeLabels();

                    this.repaint();
                    this.revalidate();

                } catch (IllegalArgumentException ex){
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Can't delete this choice", JOptionPane.ERROR_MESSAGE);
                }
            });
            menu.add(remove);
            JMenuItem rename = new JMenuItem("Rename this choice to...");
            rename.addActionListener(e -> {
                String newName = JOptionPane.showInputDialog(this, "Rename this choice to...",
                        "Rename choice", JOptionPane.QUESTION_MESSAGE);
                if(newName != null && !newName.isEmpty()){
                    try {
                        windowObserver.renameApple(appleName, newName);

                        labelPanel.removeAll();

                        addAlternativeLabels();

                        this.repaint();
                        this.revalidate();

                    } catch (IllegalArgumentException ex){
                        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                                "Can't rename this choice", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            menu.add(rename);

            appleLabel.setComponentPopupMenu(menu);

            appleNameLabels.add(appleName);

            labelPanel.add(appleLabel, c);
        }
        c.gridx = windowObserver.applesNumber() % 4;
        c.gridy = windowObserver.applesNumber() / 4;
        c.insets = new Insets(3,10,4,10);

        JButton addLabel = new JButton("Add new choice...");
        addLabel.setPreferredSize(new Dimension(190, 36));
        addLabel.setFont(new Font("Serif", Font.PLAIN, 16));
        addLabel.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Create a name for the new choice...",
                    "New choice", JOptionPane.QUESTION_MESSAGE);
            if(newName != null && !newName.isEmpty()){
                if (appleNameLabels.contains(newName))
                    JOptionPane.showMessageDialog(this, "A choice with this name " +
                                    "already exists.",
                            "Unable to create a new choice", JOptionPane.ERROR_MESSAGE);
                else{
                    windowObserver.addApple(newName);

                    labelPanel.removeAll();

                    addAlternativeLabels();

                    this.repaint();
                    this.revalidate();
                }
            }
        });
        labelPanel.add(addLabel, c);
    }

    private void addCriterionButtons(WindowObserver windowObserver, CriterionTreeNode node) {
        Map<CriterionTreeNode, int[]> orderMap = windowObserver.getNodeOrder();

        String criterionName = node.toString();

        int [] index = orderMap.get(node);
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = index[1];
        c.gridy = index[0];
        c.insets = new Insets(15,15,15,15);
        c.anchor = GridBagConstraints.FIRST_LINE_START;

        JButton criterionButton = new JButton(criterionName);
        criterionButton.setPreferredSize(new Dimension(200, 50));

        criterionButton.setFont(new Font("Serif", Font.PLAIN, 16));
        criterionButton.addActionListener( e -> new PCWindow(this, windowObserver, node));
        criterionButtonsMap.put(node, criterionButton);
        buttonPanel.add(criterionButton, c);

        JPopupMenu menu = new JPopupMenu();
        JMenuItem addNew = new JMenuItem("Add new sub criteria");
        addNew.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Create a name for the new criteria...",
                    "New criteria", JOptionPane.QUESTION_MESSAGE);
            if(newName != null && !newName.isEmpty()){
                if (windowObserver.getCriterion(newName) != null)
                    JOptionPane.showMessageDialog(this, "Criterion with this name " +
                                    "already exists.",
                            "Unable to create a new criterion", JOptionPane.ERROR_MESSAGE);
                else{
                    windowObserver.addCriteria(newName, criterionName);

                    for (JButton button : criterionButtonsMap.values())
                        buttonPanel.remove(button);

                    addCriterionButtons(windowObserver, windowObserver.getRoot());

                    this.repaint();
                    this.revalidate();
                }
            }
        });
        menu.add(addNew);
        JMenuItem remove = new JMenuItem("Remove criteria");
        remove.addActionListener(e -> {
            try {
                windowObserver.removeCriteria(criterionName);

                for (JButton button : criterionButtonsMap.values())
                    buttonPanel.remove(button);

                addCriterionButtons(windowObserver, windowObserver.getRoot());

                this.repaint();
                this.revalidate();

            } catch (IllegalArgumentException ex){
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Can't delete this criteria", JOptionPane.ERROR_MESSAGE);
            }
        });
        menu.add(remove);
        JMenuItem rename = new JMenuItem("Rename this criteria to...");
        rename.addActionListener(e -> {
            String newName = JOptionPane.showInputDialog(this, "Rename this criteria to...",
                    "Rename criteria", JOptionPane.QUESTION_MESSAGE);
            if(newName != null && !newName.isEmpty()){
                try {
                    windowObserver.renameCriteria(criterionName, newName);

                    for (JButton button : criterionButtonsMap.values())
                        buttonPanel.remove(button);

                    addCriterionButtons(windowObserver, windowObserver.getRoot());

                    this.repaint();
                    this.revalidate();

                } catch (IllegalArgumentException ex){
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                            "Can't rename this criteria", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        menu.add(rename);
        criterionButton.setComponentPopupMenu(menu);

        if(node.getChildCount() > 0)
            for(int i=0; i<node.getChildCount(); i++)
                addCriterionButtons(windowObserver, node.getChildAt(i));
    }

    /*
    public void setWindowObserver(WindowObserver windowObserver) {
        this.windowObserver = windowObserver;
    }

    public void removeWindowObserver() {
        this.windowObserver = null;
    }*/

}
