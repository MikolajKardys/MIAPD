package GUI;

import management.WindowObserver;

import javax.swing.*;
import java.util.LinkedList;
import java.util.Objects;

public class Window extends JFrame {
    private final JButton addAppleButton, removeAppleButton, loadApplesButton, getRankingButton;
    private final JButton [] alternativesButtons;

    private final JPanel mainPanel;

    private final JTextField newAppleField;
    private final LinkedList<String> appleNames = new LinkedList<>();

    private final double [][] C = new double[10][10];

    private WindowObserver windowObserver;

    public Window() {
        for(int i=0; i<10; i++)
            C[i][i] = 1;

        alternativesButtons = new JButton[10];
        for(int i=0; i<10; i++) {
            alternativesButtons[i] = new JButton();
            if(i%2==0)
                alternativesButtons[i].setBounds(250, 300 + i/2 * 75, 240, 50);
            else
                alternativesButtons[i].setBounds(510, 300 + (i-1)/2 * 75, 240, 50);
            alternativesButtons[i].setVisible(false);
        }


        newAppleField = new JTextField();
        newAppleField.setBounds(250, 25, 500, 50);

        addAppleButton = new JButton();
        addAppleButton.setBounds(250, 100, 240, 50);
        addAppleButton.setText("Add apple");
        addAppleButton.addActionListener(e -> {
            if(!Objects.equals(newAppleField.getText(), "")) {
                if(appleNames.size() < 10) {
                    String appleName = newAppleField.getText();
                    if (windowObserver.addApple(appleName)) {
                        appleNames.add(appleName);

                        reloadList();
                    }
                }
                newAppleField.setText("");
            }
        });

        removeAppleButton = new JButton();
        removeAppleButton.setBounds(510, 100, 240, 50);
        removeAppleButton.setText("Remove apple");
        removeAppleButton.addActionListener(e -> {
            if(!Objects.equals(newAppleField.getText(), "")) {
                String appleName = newAppleField.getText();
                if(windowObserver.removeApple(appleName)) {
                    appleNames.remove(appleName);
                    System.out.println(appleNames.size());
                    reloadList();
                }
                newAppleField.setText("");
            }
        });

        loadApplesButton = new JButton();
        loadApplesButton.setBounds(250, 180, 500, 50);
        loadApplesButton.setText("Load apples from file");
        loadApplesButton.addActionListener(e -> {
            /*
            JFileChooser fileToOpen = new JFileChooser();

            fileToOpen.setCurrentDirectory(new File("."));

            int response = fileToOpen.showOpenDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {

            }
            */
        });

        getRankingButton = new JButton();
        getRankingButton.setBounds(800, 650, 150, 50);
        getRankingButton.setText("Get ranking");
        getRankingButton.addActionListener( e -> {
            boolean tableCFilled = true;
            for(int i=0; i<appleNames.size(); i++) {
                for(int j=0; j<appleNames.size(); j++) {
                    if(C[i][j] == 0) {
                        tableCFilled = false;
                        break;
                    }
                }
            }

            if(tableCFilled)
                new RankingWindow(windowObserver.getRanking(C));
        });

        mainPanel = new JPanel();
        mainPanel.setBounds(0,0, 1000, 750);
        mainPanel.setLayout(null);
        mainPanel.add(addAppleButton);
        mainPanel.add(removeAppleButton);
        mainPanel.add(loadApplesButton);
        mainPanel.add(newAppleField);
        mainPanel.add(getRankingButton);

        for(JButton alternativeButton : alternativesButtons) {
            mainPanel.add(alternativeButton);
        }

        this.setTitle("Apple rank");
        this.setSize(1000, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.add(mainPanel);
        this.setVisible(true);
    }

    private void reloadList() {
        for(int i=0; i<10; i++) {
            alternativesButtons[i].setVisible(false);
        }

        for(int i=0; i<appleNames.size(); i++) {
            alternativesButtons[i].setText(appleNames.get(i));
            alternativesButtons[i].setVisible(true);
            int finalI = i;

            if (alternativesButtons[i].getActionListeners().length == 0)
                alternativesButtons[i].addActionListener(e -> {
                    new PCWindow(appleNames, finalI, C);
            });
        }
    }

    public void setWindowObserver(WindowObserver windowObserver) {
        this.windowObserver = windowObserver;
    }

    public void removeWindowObserver() {
        this.windowObserver = null;
    }
}
