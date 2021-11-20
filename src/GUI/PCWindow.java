package GUI;

import management.CriterionTreeNode;
import management.WindowObserver;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class PCWindow extends JFrame {

    private final List<JTextField> textFields = new ArrayList<>();

    public PCWindow(WindowObserver windowObserver, CriterionTreeNode node) {
        JPanel mainPanel = new JPanel();

        int labelNumber;
        if(node.isLeaf()) {
            labelNumber = windowObserver.applesNumber();
            for(int i=0; i<windowObserver.applesNumber(); i++) {
                JLabel label1 = new JLabel(windowObserver.getIthAppleName(i), SwingConstants.CENTER);
                label1.setBounds(125 + i*(600/windowObserver.applesNumber()), 25, (600/windowObserver.applesNumber())-25, 25);
                mainPanel.add(label1);

                JLabel label2 = new JLabel(windowObserver.getIthAppleName(i), SwingConstants.CENTER);
                label2.setBounds(25, 100 + i*(600/windowObserver.applesNumber()), 75, (600/windowObserver.applesNumber())-25);
                mainPanel.add(label2);
            }
        } else {
            labelNumber = node.getChildCount();
            for(int i=0; i<node.getChildCount(); i++) {
                JLabel label1 = new JLabel(node.getChildAt(i).toString(), SwingConstants.CENTER);
                label1.setBounds(125 + i*(600/node.getChildCount()), 25, (600/node.getChildCount())-25, 25);
                mainPanel.add(label1);

                JLabel label2 = new JLabel(node.getChildAt(i).toString(), SwingConstants.CENTER);
                label2.setBounds(25, 100 + i*(600/node.getChildCount()), 75, (600/node.getChildCount())-25);
                mainPanel.add(label2);
            }
        }

        for(int i=0; i<labelNumber; i++) {
            for(int j=0; j<labelNumber; j++) {
                JTextField textField;
                if(i<j)
                    textField = new JTextField(String.valueOf(Math.round(1000*windowObserver.getCAt(i,j,node.toString()))/1000.0));
                else
                    textField = new JTextField(String.valueOf(Math.round(1000*(1.0/windowObserver.getCAt(j,i,node.toString())))/1000.0));
                textField.setBounds(125 + j*(600/labelNumber), 100 + i*(600/labelNumber), (600/labelNumber)-25, (600/labelNumber)-25);
                textFields.add(textField);
                if(i>=j)
                    textField.setEditable(false);
                mainPanel.add(textField);
            }
        }

        JButton setComparisonsButton = new JButton("Set PC");
        setComparisonsButton.setBounds(25, 25, 75, 50);
        setComparisonsButton.addActionListener( e -> {
            int i, j;
            for(int k=0; k<textFields.size(); k++) {
                i = k/labelNumber;
                j = k%labelNumber;

                if(i < j) {
                    if(isValidDouble(k)) {
                        windowObserver.setCAt(i, j, node.toString(), toDouble(textFields.get(k).getText()));
                        windowObserver.setCAt(j, i, node.toString(), 1.0/toDouble(textFields.get(k).getText()));
                    } else
                        textFields.get(k).setText("");
                }
                else {
                    textFields.get(k).setText(String.valueOf(Math.round(1000*windowObserver.getCAt(i,j,node.toString()))/1000.0));
                }
            }
        });
        mainPanel.add(setComparisonsButton);

        mainPanel.setBounds(0,0, 750, 750);
        mainPanel.setLayout(null);

        this.setTitle(node.toString());
        this.setSize(750, 750);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setVisible(true);
        this.add(mainPanel);

    }

    private boolean isValidDouble(int textFieldIndex) {
        if(Objects.equals(textFields.get(textFieldIndex).getText(), "")) {
            return false;
        } else {
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
