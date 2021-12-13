package GUI;

import arithmetics.PrioritizationMethod;
import management.CriterionTreeMap;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ExpertWindow extends JFrame {
    private final Map<String, CriterionTreeMap> expertMap = new HashMap<>();

    private final JPanel expertPane;

    public void updateExperts(){
        expertPane.removeAll();

        if (expertMap.size() == 0){
            JLabel emptyLabel = new JLabel();
            emptyLabel.setText("No experts yet...");
            emptyLabel.setFont(new Font("Sherif", Font.ITALIC, emptyLabel.getFont().getSize()));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            emptyLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
            expertPane.add(emptyLabel);
        }
        else {
            for (String expertName : expertMap.keySet()){
                expertPane.add(Box.createRigidArea(new Dimension(0, 10)));

                JButton expertButton = new JButton(expertName);
                expertButton.addActionListener(e -> {
                    this.setEnabled(false);
                    new Window(this, expertMap.get(expertName));
                });
                expertButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                expertButton.setMaximumSize(new Dimension(225, 30));

                expertPane.add(expertButton);
            }
        }

        this.revalidate();
        this.repaint();
    }

    private void calculateRanking(PrioritizationMethod method) throws Exception{
        if (expertMap.isEmpty()){
            throw new Exception("You must provide data from at least one expert");
        }
        CriterionTreeMap firstMap = expertMap.get(expertMap.keySet().toArray(new String[0])[0]);

        int appleNum = firstMap.applesNumber();

        Map<String, Double> resultsMap = new HashMap<>();
        for (int i = 0; i < appleNum; i++){
            resultsMap.put(firstMap.getIthAppleName(i), 0.0);
        }

        Set<String> appleNames = resultsMap.keySet();

        for (String expertName : expertMap.keySet()){
            CriterionTreeMap currExpert = expertMap.get(expertName);

            List<String> currAppleNames = new ArrayList<>();
            for (int i = 0; i < appleNum; i++)
                currAppleNames.add(currExpert.getIthAppleName(i));

            if (currAppleNames.size() != appleNum){
                throw new Exception("Error: Different number of choices for expert \"" + expertName + "\"");
            }
            for (String appleName : appleNames){
                if (!currAppleNames.contains(appleName)){
                    throw new Exception("Error: Different names of choices for expert \"" + expertName + "\"");
                }
            }

            try{
                if (currExpert.arePCTablesCorrect()) {
                    double [] currResult = currExpert.getRanking(method);

                    for (int i = 0; i < appleNum; i++){
                        double oldValue = resultsMap.get(currExpert.getIthAppleName(i));
                        resultsMap.put(currExpert.getIthAppleName(i), oldValue + currResult[i]);
                    }
                }
            }
            catch (IllegalArgumentException ex){
                throw new Exception("Error while calculating values for \"" + expertName + "\": " + ex.getMessage());
            }
        }

        for (String appleName : resultsMap.keySet()){
            double oldValue = resultsMap.get(appleName);
            resultsMap.put(appleName, oldValue / expertMap.size());
        }

        new RankingWindow(this, resultsMap);
    }

    public ExpertWindow(){
        JPanel mainPanel = new JPanel();
        mainPanel.setBounds(0, 0, 550, 750);
        mainPanel.setLayout(null);

        JLabel title = new JLabel("Choose which experts to take into account");
        title.setBounds(0, 70, 400, 30);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(title);


        expertPane = new JPanel();
        expertPane.setLayout(new BoxLayout(expertPane, BoxLayout.PAGE_AXIS));

        JLabel emptyLabel = new JLabel();
        emptyLabel.setText("No experts yet...");
        emptyLabel.setFont(new Font("Sherif", Font.ITALIC, title.getFont().getSize()));
        emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emptyLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        expertPane.add(emptyLabel);

        JScrollPane scrollPane = new JScrollPane(expertPane);
        scrollPane.setBounds(40, 100, 315, 250);
        scrollPane.setAlignmentX(Container.CENTER_ALIGNMENT);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        mainPanel.add(scrollPane);


        JButton loadFileButton = new JButton("Add an expert...");
        loadFileButton.setBounds(50, 375, 300, 25);
        loadFileButton.addActionListener(e -> {
            try{
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
                fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("*.json", "json"));
                int result = fileChooser.showOpenDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();

                    CriterionTreeMap newMap = CriterionTreeMap.readFromFile(selectedFile.getAbsolutePath());

                    expertMap.put(selectedFile.getName().replaceAll(".json", ""), newMap);
                }
            }
            catch (Exception ex){
                JOptionPane.showMessageDialog(this, "Unable to read this file properly. " +
                                "Please make sure to select a file with valid rankings",
                        "Unable to read file", JOptionPane.ERROR_MESSAGE);
            }

            updateExperts();
        });
        mainPanel.add(loadFileButton);

        JButton calculateButton = new JButton("Calculate Combined Ranking");
        calculateButton.setBounds(25, 450, 350, 50);
        calculateButton.addActionListener(e -> {
            PrioritizationMethod [] choices = { PrioritizationMethod.EVM, PrioritizationMethod.GMM };
            PrioritizationMethod method = (PrioritizationMethod) JOptionPane.showInputDialog(this,
                    "Select a method to calculate the ranking...",
                    "Choose a method", JOptionPane.QUESTION_MESSAGE, null,
                    choices, choices[0]);

            if (method != null)
                try {
                    calculateRanking(method);
                }
                catch (Exception ex){
                    JOptionPane.showMessageDialog(this, ex.getMessage(),
                            "Calculation Error", JOptionPane.ERROR_MESSAGE);
                }
        });
        mainPanel.add(calculateButton);



        this.add(mainPanel);

        this.setTitle("Choose your experts");
        this.setSize(420, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLayout(null);
        this.setVisible(true);
    }


}
