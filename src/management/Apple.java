package management;

import java.util.HashMap;
import java.util.Map;

public class Apple {
    private final Map<String, Map<Apple, Double>> values = new HashMap<>();

    private final String name;

    public final int index;

    public Apple (String name, int index){
        this.name = name;
        this.index = index;
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public int hashCode(){
        return name.hashCode();
    }

    public void addCriteria(String criteria){
        values.put(criteria, new HashMap<>());
    }

    public void changeCriteriaVal (String criteria, Apple other, Double value) {
        if (!values.containsKey(criteria)){
            System.out.println("Invalid criteria name!");
        }

        values.get(criteria).put(other, value);

        other.values.get(criteria).put(this, 1 / value);
    }

    public Double getCriteriaVal (String criteria, Apple other){
        if (!values.containsKey(criteria)){
            System.out.println("Invalid criteria name!");
            return null;
        }

        return values.get(criteria).get(other);
    }
}
