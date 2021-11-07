import java.util.Arrays;

public class Main {
    public static void main (String [] args){
        AppleMatrix matrix = new AppleMatrix();

        matrix.addApple("Ligol");
        matrix.addApple("Gala");

        matrix.addCriteria("Cena");

        matrix.getApple("Ligol").changeCriteriaVal("Cena", matrix.getApple("Gala"), 0.25);

        matrix.addApple("Fiji");

        matrix.getApple("Fiji").changeCriteriaVal("Cena", matrix.getApple("Gala"), 0.3);
        matrix.getApple("Fiji").changeCriteriaVal("Cena", matrix.getApple("Ligol"), 0.5);

        System.out.println(Arrays.deepToString(matrix.getMatrix("Cena")));
    }
}
