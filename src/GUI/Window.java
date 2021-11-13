package GUI;

import management.WindowObserver;

import javax.swing.*;
import java.util.Objects;

public class Window extends JFrame {
    private final JButton [] alternativesButtons;
    private WindowObserver windowObserver;

    public Window() {
        alternativesButtons = new JButton[10];
        for(int i=0; i<10; i++) {
            alternativesButtons[i] = new JButton();
            if(i%2==0)
                alternativesButtons[i].setBounds(250, 300 + i/2 * 75, 240, 50);
            else
                alternativesButtons[i].setBounds(510, 300 + (i-1)/2 * 75, 240, 50);
            alternativesButtons[i].setVisible(false);
        }

        JTextField newAppleField = new JTextField();
        newAppleField.setBounds(250, 25, 500, 50);

        JButton addAppleButton = new JButton("Add apple");
        addAppleButton.setBounds(250, 100, 240, 50);
        addAppleButton.addActionListener(e -> {
            if(!Objects.equals(newAppleField.getText(), "")) {
                if (windowObserver.addApple(newAppleField.getText()))
                    reloadList();

                newAppleField.setText("");
            }
        });

        JButton removeAppleButton = new JButton("Remove apple");
        removeAppleButton.setBounds(510, 100, 240, 50);
        removeAppleButton.addActionListener(e -> {
            if(!Objects.equals(newAppleField.getText(), "")) {
                if(windowObserver.removeApple(newAppleField.getText()))
                    reloadList();

                newAppleField.setText("");
            }
        });

        JButton loadApplesButton = new JButton("Load apples from file");
        loadApplesButton.setBounds(250, 180, 500, 50);
        loadApplesButton.addActionListener(e -> {
            /*
            JFileChooser fileToOpen = new JFileChooser();

            fileToOpen.setCurrentDirectory(new File("."));

            int response = fileToOpen.showOpenDialog(null);

            if(response == JFileChooser.APPROVE_OPTION) {

            }
            */
        });

        JButton getRankingButton = new JButton("Get ranking");
        getRankingButton.setBounds(800, 650, 150, 50);
        getRankingButton.addActionListener(e -> {
            if(windowObserver.isPCTableCorrect())
                new RankingWindow(windowObserver, windowObserver.getRanking());
        });

        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(0,0, 1000, 750);
        mainPanel.setLayout(null);
        mainPanel.add(addAppleButton);
        mainPanel.add(removeAppleButton);
        mainPanel.add(loadApplesButton);
        mainPanel.add(newAppleField);
        mainPanel.add(getRankingButton);
        for(JButton alternativeButton : alternativesButtons)
            mainPanel.add(alternativeButton);

        this.setTitle("Apple rank");
        this.setSize(1000, 750);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.add(mainPanel);
        this.setVisible(true);
    }

    private void reloadList() {
        for(int i=0; i<10; i++)
            alternativesButtons[i].setVisible(false);

        for(int i=0; i<windowObserver.applesNumber(); i++) {
            alternativesButtons[i].setText(windowObserver.getIthAppleName(i));
            alternativesButtons[i].setVisible(true);

            if(alternativesButtons[i].getActionListeners().length == 0) {
                int finalI = i;
                alternativesButtons[i].addActionListener(e -> new PCWindow(windowObserver, finalI));
            }
        }
    }

    public void setWindowObserver(WindowObserver windowObserver) {
        this.windowObserver = windowObserver;
    }

    public void removeWindowObserver() {
        this.windowObserver = null;
    }
}
