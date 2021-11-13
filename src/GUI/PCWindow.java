package GUI;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Objects;

public class PCWindow extends JFrame {
    private final JPanel mainPanel;

    public PCWindow(LinkedList<String> appleNames, int comparisonAppleIdx, double [][] C) {
        JLabel titleLabel = new JLabel("Pairwise comparison with apple " + appleNames.get(comparisonAppleIdx));
        titleLabel.setBounds(50, 0, 200, 100);

        LinkedList<JTextField> textFields = new LinkedList<>();
        LinkedList<JLabel> nameLabels = new LinkedList<>();

        mainPanel = new JPanel();

        for(int i=0; i<appleNames.size(); i++) {
            if(i != comparisonAppleIdx) {
                JTextField textField = new JTextField();
                if(C[comparisonAppleIdx][i] != 0)
                    textField.setText(String.valueOf(Math.round(100000./C[comparisonAppleIdx][i])/100000.));

                textField.setBounds(50, 100 + 55*i, 75, 30);
                textFields.add(textField);
                mainPanel.add(textField);

                JLabel nameLabel = new JLabel(appleNames.get(i));
                nameLabel.setBounds(150, 100 + 55*i, 100, 30);
                nameLabels.add(nameLabel);
                mainPanel.add(nameLabel);
            }
        }

        JButton setComparisonsButton = new JButton("Set PC");
        setComparisonsButton.setBounds(150, 650, 100, 30);
        setComparisonsButton.addActionListener( e -> {
            for(int i=0; i<textFields.size(); i++) {
                if(!Objects.equals(textFields.get(i).getText(), "")) {

                    if(i>=comparisonAppleIdx) {
                        C[comparisonAppleIdx][i+1] = 1./toDouble(textFields.get(i).getText());
                        C[i+1][comparisonAppleIdx] = toDouble(textFields.get(i).getText());
                    }
                    else {
                        C[comparisonAppleIdx][i] = 1./toDouble(textFields.get(i).getText());
                        C[i][comparisonAppleIdx] = toDouble(textFields.get(i).getText());
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

        mainPanel.add(setComparisonsButton);

        mainPanel.setBounds(0,0, 300, 750);
        mainPanel.setLayout(null);
        mainPanel.add(titleLabel);

        this.setTitle(appleNames.get(comparisonAppleIdx));
        this.setSize(300, 750);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.add(mainPanel);
        this.setVisible(true);
    }

    private double toDouble(String text) {
        return Double.parseDouble(text);
    }
}
