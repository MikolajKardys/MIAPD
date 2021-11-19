package GUI;

import management.WindowObserver;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Objects;

public class PCWindow extends JFrame {

    private final LinkedList<JTextField> textFields = new LinkedList<>();

    public PCWindow(WindowObserver windowObserver, int comparisonIdx, int criterionName) {
        JPanel mainPanel = new JPanel();
/*
        for(int i=0; i<windowObserver.applesNumber(); i++) {
            if(i != comparisonIdx) {
                JTextField textField = new JTextField();
                if(windowObserver.getCAt(comparisonIdx, i) != 0)
                    textField.setText(String.valueOf(Math.round(100000./windowObserver.getCAt(comparisonIdx, i))/100000.));

                textField.setBounds(220, 25 + 50*i, 60, 40);
                textFields.add(textField);
                mainPanel.add(textField);

                JLabel nameLabel = new JLabel(windowObserver.getIthAppleName(i) + " : " +
                        windowObserver.getIthAppleName(comparisonIdx), SwingConstants.CENTER);
                nameLabel.setBounds(10, 25 + 50*i, 200, 40);
                nameLabel.setFont(new Font("Serif", Font.PLAIN, 16));
                mainPanel.add(nameLabel);
            }
        }

        JButton setComparisonsButton = new JButton("Set PC");
        setComparisonsButton.setBounds(10, 540, 270, 20);

        setComparisonsButton.addActionListener( e -> {
            for(int i=0; i<textFields.size(); i++) {
                if(isValidDouble(i)) {

                    if(i>=comparisonIdx) {
                        windowObserver.setCAt(comparisonIdx, i+1, 1./toDouble(textFields.get(i).getText()));
                        windowObserver.setCAt(i+1, comparisonIdx, toDouble(textFields.get(i).getText()));
                    }
                    else {
                        windowObserver.setCAt(comparisonIdx, i, 1./toDouble(textFields.get(i).getText()));
                        windowObserver.setCAt(i, comparisonIdx, toDouble(textFields.get(i).getText()));
                    }
                }
            }

            this.dispose();
        });

        mainPanel.setBounds(0,0, 300, 600);
        mainPanel.setLayout(null);
        mainPanel.add(setComparisonsButton);

        this.setTitle(windowObserver.getIthAppleName(comparisonIdx));
        this.setSize(300, 600);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setVisible(true);
        this.add(mainPanel);
        */
    }

    private boolean isValidDouble(int textFieldIndex) {
        if(Objects.equals(textFields.get(textFieldIndex).getText(), "")) {
            return false;
        }
        else {
            String text = textFields.get(textFieldIndex).getText();

            try {
                return Double.parseDouble(text) > 0;
            } catch(NumberFormatException e) {
                textFields.get(textFieldIndex).setText("");
                return false;
            }
        }
    }

    private double toDouble(String text) {
        return Double.parseDouble(text);
    }
}
