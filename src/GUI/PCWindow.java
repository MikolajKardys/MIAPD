package GUI;

import management.WindowObserver;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Objects;

public class PCWindow extends JFrame {

    private final LinkedList<JTextField> textFields = new LinkedList<>();

    public PCWindow(WindowObserver windowObserver, int comparisonAppleIdx) {
        JLabel titleLabel = new JLabel("Pairwise comparison with apple " +windowObserver.getIthAppleName(comparisonAppleIdx));
        titleLabel.setBounds(50, 0, 200, 100);

        JPanel mainPanel = new JPanel();

        for(int i=0; i<windowObserver.applesNumber(); i++) {
            if(i != comparisonAppleIdx) {
                JTextField textField = new JTextField();
                if(windowObserver.getCAt(comparisonAppleIdx, i) != 0)
                    textField.setText(String.valueOf(Math.round(100000./windowObserver.getCAt(comparisonAppleIdx, i))/100000.));

                textField.setBounds(50, 100 + 55*i, 75, 30);
                textFields.add(textField);
                mainPanel.add(textField);

                JLabel nameLabel = new JLabel(windowObserver.getIthAppleName(i));
                nameLabel.setBounds(150, 100 + 55*i, 100, 30);
                mainPanel.add(nameLabel);
            }
        }

        JButton setComparisonsButton = new JButton("Set PC");
        setComparisonsButton.setBounds(150, 650, 100, 30);

        setComparisonsButton.addActionListener( e -> {
            for(int i=0; i<textFields.size(); i++) {
                if(isValidDouble(i)) {

                    if(i>=comparisonAppleIdx) {
                        windowObserver.setCAt(comparisonAppleIdx, i+1, 1./toDouble(textFields.get(i).getText()));
                        windowObserver.setCAt(i+1, comparisonAppleIdx, toDouble(textFields.get(i).getText()));
                    }
                    else {
                        windowObserver.setCAt(comparisonAppleIdx, i, 1./toDouble(textFields.get(i).getText()));
                        windowObserver.setCAt(i, comparisonAppleIdx, toDouble(textFields.get(i).getText()));
                    }
                }
            }

            /*
            for(int i=0; i<10; i++) {
                for(int j=0; j<10; j++) {
                    System.out.print(C[i][j] + " ");
                }
                System.out.println("");
            }
             */

            this.dispose();
        });

        mainPanel.setBounds(0,0, 300, 750);
        mainPanel.setLayout(null);
        mainPanel.add(titleLabel);
        mainPanel.add(setComparisonsButton);

        this.setTitle(windowObserver.getIthAppleName(comparisonAppleIdx));
        this.setSize(300, 750);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setVisible(true);
        this.add(mainPanel);
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
