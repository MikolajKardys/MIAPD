package management;

import java.util.HashMap;
import java.util.Map;

public class Apple {
    private final Map<String, Map<Apple, Double>> values = new HashMap<>();

    private final String name;

    public Apple (String name, int index){
        this.name = name;
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

    public void removeCriteria(String criteria){
        values.remove(criteria);
    }

    public void changeCriteriaVal (String criteria, Apple other, Double value) {
        if (!values.containsKey(criteria)){
            System.out.println("Invalid criteria name!");
            return;
        }

        if (value != null) {
            values.get(criteria).put(other, value);
            other.values.get(criteria).put(this, 1 / value);
        }
        else if (getCriteriaVal(criteria, other) != null){
            System.out.println("Usuwando");
            values.get(criteria).remove(other);
            other.values.get(criteria).remove(this);
        }
    }

    public Double getCriteriaVal (String criteria, Apple other){
        if (!values.containsKey(criteria)){
            System.out.println("Invalid criteria name!");
            return null;
        }

        return values.get(criteria).get(other);
    }
}
