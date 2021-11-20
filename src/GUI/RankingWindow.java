package GUI;

import management.WindowObserver;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.LinkedList;

public class RankingWindow extends JFrame {
    public static class Pair<L,R> {
        private final L l;
        private final R r;
        public Pair(L l, R r){
            this.l = l;
            this.r = r;
        }
    }

    public RankingWindow(WindowObserver windowObserver, double [] w) {
        int wSize = w.length;

        JPanel mainPanel = new JPanel();

        LinkedList<Pair<Double, String>> ranking = new LinkedList<>();

        for(int i=0; i<wSize; i++)
            ranking.add(new Pair<>(w[i], windowObserver.getIthAppleName(i)));

        ranking.sort((o1, o2) -> {
            if(o1.l > o2.l)
                return -1;
            else if(o1.l < o2.l)
                return 1;
            return 0;
        });

        for(int i=0; i<ranking.size(); i++) {
            JLabel label = new JLabel(i+1 + ".   " + ranking.get(i).r + ":   " + Math.round(1000*ranking.get(i).l)/1000., SwingConstants.CENTER);
            label.setBounds(25, 50 + 55*i, 325, 30);
            label.setFont(new Font("Serif", Font.PLAIN, 24));
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
