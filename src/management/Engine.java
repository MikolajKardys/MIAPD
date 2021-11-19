package management;

import GUI.Window;
import arithmetics.Solver;

import java.util.Arrays;

public class Engine extends CriterionTreeMap{
    public void run() {
        CriterionTreeMap newMap = readFromFile("example.json");

        new Window(newMap);
    }
}
