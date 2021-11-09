package GUI;

import management.WindowObserver;

import javax.swing.*;
import java.io.File;
import java.util.LinkedList;
import java.util.Objects;

public class Window extends JFrame {
    private final JButton addAppleButton, removeAppleButton, loadApplesButton, PCButton;
    private final JButton mainPanelButton;
    private final JPanel mainPanel, PCPanel;
    private final LinkedList<JPanel> PCPanels;
    private final JTextField newAppleField;
    private final LinkedList<String> appleNames = new LinkedList<>();

    private WindowObserver windowObserver;

    public Window() {
        newAppleField = new JTextField();
        newAppleField.setBounds(250, 25, 500, 50);

        addAppleButton = new JButton();
        addAppleButton.setBounds(250, 100, 240, 50);
        addAppleButton.setText("Add apple");
        addAppleButton.addActionListener(e -> {
            if(!Objects.equals(newAppleField.getText(), "")) {
                String appleName = newAppleField.getText();
                if(windowObserver.addApple(appleName)) {
                    appleNames.add(appleName);
                    reloadList();
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
                    reloadList();
                }
                newAppleField.setText("");
            }
        });

        loadApplesButton = new JButton();
        loadApplesButton.setBounds(250, 200, 500, 50);
        loadApplesButton.setText("Load apples from file");
        loadApplesButton.addActionListener(e -> {
            JFileChooser fileToOpen = new JFileChooser();

            fileToOpen.setCurrentDirectory(new File("."));

            int response = fileToOpen.showOpenDialog(null);
            /*
            if(response == JFileChooser.APPROVE_OPTION) {

            }
            */
        });


        mainPanel = new JPanel();
        PCPanel = new JPanel();
        PCPanels = new LinkedList<>();


        mainPanelButton = new JButton();
        mainPanelButton.setBounds(20, 400, 180, 50);
        mainPanelButton.setText("Main panel");
        mainPanelButton.addActionListener( e-> {
            mainPanel.setVisible(true);
            PCPanel.setVisible(false);
        });

        PCButton = new JButton();
        PCButton.setBounds(800, 400, 180, 50);
        PCButton.setText("Pairwise comparison");
        PCButton.addActionListener( e-> {
            mainPanel.setVisible(false);
            PCPanel.setVisible(true);

            PCPanel.removeAll();

            PCPanel.add(mainPanelButton);

            int k = 0;
            for(int i = 0; i < appleNames.size(); i++) {
                for(int j = i+1; j < appleNames.size(); j++) {
                    JLabel label = new JLabel();
                    label.setText(appleNames.get(i) + appleNames.get(j));
                    label.setBounds(250, 50 + k*75, 100, 50);
                    PCPanel.add(label);

                    k++;
                }
            }
        });


        PCPanel.setBounds(0,0, 1000, 500);
        PCPanel.setLayout(null);
        PCPanel.setVisible(false);


        mainPanel.setBounds(0,0, 1000, 500);
        mainPanel.setLayout(null);
        mainPanel.add(addAppleButton);
        mainPanel.add(removeAppleButton);
        mainPanel.add(loadApplesButton);
        mainPanel.add(newAppleField);
        mainPanel.add(PCButton);


        this.setTitle("Apple rank");
        this.setSize(1000, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.add(mainPanel);
        this.add(PCPanel);
        this.setVisible(true);
    }

    private void reloadList() {

    }

    public void setWindowObserver(WindowObserver windowObserver) {
        this.windowObserver = windowObserver;
    }

    public void removeWindowObserver() {
        this.windowObserver = null;
    }
}
