package GUI;

import arithmetics.PrioritizationMethod;
import management.CriterionTreeNode;

import java.util.Map;

public interface WindowObserver {
    //return number of apples
    int applesNumber();

    //return ith apple name (apple at ith position is corresponding to ith row and column in C table)
    String getIthAppleName(int i);

    //set criterionName C[i][j] = value (value is always greater than 0)
    void setCAt(int i, int j, String criterionName, double value);

    //get criterionName C[i][j] (if this cell wasn't set return 0.0)
    double getCAt(int i, int j, String criterionName);

    //return true if every cell in C table is set (every cell > 0)
    boolean arePCTablesCorrect();

    //return table (with size of apples number) which is ranking w of PC table
    double [] getRanking(PrioritizationMethod method);

    CriterionTreeNode getRoot();

    Map<CriterionTreeNode, int[]> getNodeOrder();

    Map<String, Double> getIncIndex(CriterionTreeNode node);

    void writeToFile(String fileName) throws IllegalArgumentException;

    CriterionTreeNode getCriterion(String criterionName);

    void addCriteria(String criterionName, String parent);

    void removeCriteria(String criterionName);

    void renameCriteria(String oldName, String newName);

    void addApple(String appleName);

    void removeApple(String appleName);

    void renameApple(String oldName, String newName);
}
