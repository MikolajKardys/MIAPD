import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AppleMatrix {
    private final Map<String, Apple> apples = new HashMap<>();
    private final List<String> criteriaNames = new LinkedList<>();

    public void addCriteria(String criteria){
        criteriaNames.add(criteria);

        for (Apple apple : apples.values()){
            apple.addCriteria(criteria);
        }
    }

    public Apple getApple(String name){
        return apples.get(name);
    }

    public void addApple(String name){
        if (apples.containsKey(name)){
            System.out.println("This apple already exists in the database!");
            return;
        }

        Apple newApple = new Apple(name, apples.size());
        for (String criteria : criteriaNames){
            newApple.addCriteria(criteria);
        }

        apples.put(name, newApple);
    }

    public Double [][] getMatrix (String criteria){
        if (!criteriaNames.contains(criteria)){
            System.out.println("Invalid criteria name!");
            return null;
        }

        int appleNum = apples.size();

        Double [][] matrix = new Double[appleNum][appleNum];

        for (int i = 0; i < appleNum; i++){
            matrix[i][i] = 1.0;
        }

        for (Apple apple : apples.values()) {
            for (Apple otherApple : apples.values()) {
                if (apple.getCriteriaVal(criteria, otherApple) != null){
                    matrix[otherApple.index][apple.index] = otherApple.getCriteriaVal(criteria, apple);
                    matrix[apple.index][otherApple.index] = apple.getCriteriaVal(criteria, otherApple);
                }
            }
        }

        return matrix;
    }

}
