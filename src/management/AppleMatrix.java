package management;

import arithmetics.Solver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AppleMatrix implements WindowObserver{
    private final Map<String, Apple> apples = new HashMap<>();
    private final List<String> criteriaNames = new LinkedList<>();

    public void addCriteria(String criteria){
        criteriaNames.add(criteria);

        for (Apple apple : apples.values()){
            apple.addCriteria(criteria);
        }
    }

    public double [][] getMatrix (String criteria){
        if (!criteriaNames.contains(criteria)){
            System.out.println("Invalid criteria name!");
            return null;
        }

        int appleNum = apples.size();

        double [][] matrix = new double[appleNum][appleNum];

        for (int i = 0; i < appleNum; i++){
            matrix[i][i] = 1.0;
        }

        for (int i = 0; i < appleNum; i++) {
            for (int j = 0; j < appleNum; j++) {
                Apple appleI = apples.get(getIthAppleName(i));
                Apple appleJ = apples.get(getIthAppleName(j));

                if (appleI.getCriteriaVal(criteria, appleJ) != null){
                    matrix[i][j] = appleJ.getCriteriaVal(criteria, appleI);
                    matrix[j][i] = appleI.getCriteriaVal(criteria, appleJ);
                }
            }
        }

        return matrix;
    }

    @Override
    public boolean addApple(String name){
        if (apples.containsKey(name) || apples.size() > 10){
            return false;
        }

        Apple newApple = new Apple(name, apples.size());
        for (String criteria : criteriaNames){
            newApple.addCriteria(criteria);
        }

        apples.put(name, newApple);

        return true;
    }

    @Override
    public boolean removeApple(String appleName) {
        if (!apples.containsKey(appleName)){
            return false;
        }

        Apple removeApple = apples.get(appleName);

        for (String criteria : criteriaNames){
            for (Apple apple : apples.values()){
                apple.changeCriteriaVal(criteria, removeApple, null);
            }
        }

        apples.remove(appleName);

        return true;
    }

    @Override
    public int applesNumber() {
        return apples.size();
    }

    @Override
    public String getIthAppleName(int i) {
        return apples.values().toArray()[i].toString();
    }

    @Override
    public void setCAt(int i, int j, double value) {
        String CRITERIA = criteriaNames.get(0); //TODO : To be changed with multiple criteria

        Apple appleI = apples.get(getIthAppleName(i));
        Apple appleJ = apples.get(getIthAppleName(j));

        appleI.changeCriteriaVal(CRITERIA, appleJ, value);
    }

    @Override
    public double getCAt(int i, int j) {
        String CRITERIA = criteriaNames.get(0); //TODO : To be changed with multiple criteria

        Apple appleI = apples.get(getIthAppleName(i));
        Apple appleJ = apples.get(getIthAppleName(j));

        return appleI.getCriteriaVal(CRITERIA, appleJ) != null ? appleI.getCriteriaVal(CRITERIA, appleJ) : 0;
    }

    @Override
    public boolean isPCTableCorrect() {
        String CRITERIA = criteriaNames.get(0); //TODO : To be changed with multiple criteria

        double [][] matrix = getMatrix(CRITERIA);

        for (int i = 0; i < applesNumber(); i++){
            for (int j = 0; j < applesNumber(); j++){
                if (matrix[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    @Override
    public double[] getRanking() {
        String CRITERIA = criteriaNames.get(0); //TODO : To be changed with multiple criteria

        return Solver.solveForEVM(getMatrix(CRITERIA));
    }
}
