package GUI;

import CriterionTree.CriterionTreeMap;
import management.WindowObserver;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Window extends JFrame {
    private WindowObserver windowObserver;

    private final JPanel mainPanel = new JPanel();

    private final List<JLabel> appleNameLabels = new ArrayList<>();
    private final List<JButton> criterionListButtons = new ArrayList<>();

    public Window() {
        addAlternativeLabels();

        addCriterionButtons(windowObserver.getTreeMapRoot());

        /*
        JButton getRankingButton = new JButton("Get ranking");
        getRankingButton.setBounds(800, 650, 150, 50);
        getRankingButton.addActionListener(e -> {
            if(windowObserver.isPCTableCorrect())
                new RankingWindow(windowObserver, windowObserver.getRanking());
        });
        */

        mainPanel.setBounds(0,0, 1000, 750);
        mainPanel.setLayout(null);

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

    private void addCriterionButtons(TreeNode node) {
        String criterionName = node.toString();

        int criterionNumber = criterionListButtons.size();

        JButton criterionButton = new JButton(criterionName);
        criterionButton.setBounds(50 + 225*(criterionNumber%4), 175 + 75*(criterionNumber/4), 200, 50);
        criterionButton.setFont(new Font("Serif", Font.PLAIN, 16));
        criterionButton.addActionListener( e -> {

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
}
