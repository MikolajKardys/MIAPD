package management;

import GUI.Window;

public class Engine implements WindowObserver {
    public void run() {
        Window window = new Window();
        window.setWindowObserver(this);
    }

    @Override
    public boolean addApple(String appleName) {
        return false;
    }

    @Override
    public boolean removeApple(String appleName) {
        return false;
    }

    @Override
    public int applesNumber() {
        return 0;
    }

    @Override
    public String getIthAppleName(int i) {
        return null;
    }

    @Override
    public void setCAt(int i, int j, double value) {

    }

    @Override
    public double getCAt(int i, int j) {
        return 0;
    }

    @Override
    public boolean isPCTableCorrect() {
        return false;
    }

    @Override
    public double[] getRanking() {
        return new double[0];
    }
}
