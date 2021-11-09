package management;

import GUI.Window;

public class Engine implements WindowObserver {
    public void run() {
        Window window = new Window();
        window.setWindowObserver(this);
    }

    @Override
    public boolean addApple(String appleName) {
        return true;
    }

    @Override
    public boolean removeApple(String appleName) {
        return false;
    }
}
