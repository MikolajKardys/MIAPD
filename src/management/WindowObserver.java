package management;

public interface WindowObserver {
    //return true if apple was added and false otherwise
    boolean addApple(String appleName);

    //return true if apple was added and false otherwise
    boolean removeApple(String appleName);
}
