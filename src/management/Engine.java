package management;

import GUI.Window;

public class Engine extends AppleMatrix implements WindowObserver {
    public void run() {
        addCriteria("Jakość");

        Window window = new Window();
        window.setWindowObserver(this);
    }
}
