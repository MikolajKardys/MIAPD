package GUI;

import management.WindowObserver;

import javax.swing.*;

public class RankingWindow extends JFrame {
    private final JPanel mainPanel;

    public RankingWindow(WindowObserver windowObserver, double [] w) {
        int wSize = w.length;

        mainPanel = new JPanel();

        for(int i=0; i<wSize; i++) {
            JLabel label = new JLabel();
            label.setBounds(100, 50 + 55*i, 200, 30);
            label.setText(i + ". " + windowObserver.getIthAppleName(i) + ": " + w[i]);
            mainPanel.add(label);
        }

        mainPanel.setBounds(0,0, 400, 750);
        mainPanel.setLayout(null);

        this.setTitle("Apple ranking");
        this.setSize(400, 750);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.add(mainPanel);
        this.setVisible(true);
    }
}
