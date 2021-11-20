package management;

import GUI.Window;


public class Engine extends CriterionTreeMap{
    public void run() {
        CriterionTreeMap newMap = readFromFile("example.json");
        new Window(newMap);
    }
}
