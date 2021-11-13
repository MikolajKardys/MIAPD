package management;

public interface WindowObserver {
    //return true if apple was added and false otherwise
    boolean addApple(String appleName);

    //return true if apple was added and false otherwise
    boolean removeApple(String appleName);

    //return table (with size of apples number) which is ranking w
    double [] getRanking(double [][] C);
}
