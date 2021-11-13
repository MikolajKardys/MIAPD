package GUI;

import javax.swing.*;

public class RankingWindow extends JFrame {
    private final JPanel mainPanel;

    public RankingWindow(double [] w) {
        mainPanel = new JPanel();
        mainPanel.setBounds(0,0, 400, 500);
        mainPanel.setLayout(null);

        this.setTitle("Apple ranking");
        this.setSize(400, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.add(mainPanel);
        this.setVisible(true);
    }
}
