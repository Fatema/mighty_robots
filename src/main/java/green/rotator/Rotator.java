package green.rotator;

public class Rotator {
    private final double maxValue;
    private double currentValue;

    public Rotator(int maxValue) {
        this.maxValue = maxValue;
        this.currentValue = maxValue;
    }

    public void decrease(double amount) {
        this.currentValue -= amount;
    }

    public boolean shouldRotate() {
        if (currentValue <= 0) {
            currentValue = maxValue;
            return true;
        }

        return false;
    }
}
