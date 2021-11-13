package management;

public interface WindowObserver {
    //try to add apple name and return true if apple was added and false otherwise
    //any two apples can have the same name
    //there can only be 10 apples (for next ones return false)
    boolean addApple(String appleName);

    //try to remove apple name and return true if apple was removed and false otherwise
    boolean removeApple(String appleName);

    //return number of apples
    int applesNumber();

    //return ith apple name (apple at ith position is corresponding to ith row and column in C table)
    String getIthAppleName(int i);

    //set C[i][j] = value (value is always greater than 0)
    void setCAt(int i, int j, double value);

    //get C[i][j] (if this cell wasn't set return 0.0)
    double getCAt(int i, int j);

    //return true if every cell in C table is set (every cell > 0)
    boolean isPCTableCorrect();

    //return table (with size of apples number) which is ranking w of PC table
    double [] getRanking();
}
