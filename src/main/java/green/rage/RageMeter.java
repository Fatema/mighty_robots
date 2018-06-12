package green.rage;

public class RageMeter {
    private int rageValue;

    public RageMeter(int rageValue) {
        this.rageValue = rageValue;
    }

    public RageState getRageState() {
        if (rageValue < 30) {
            return RageState.PASSIVE;
        }

        if (rageValue < 60) {
            return RageState.AVERAGE;
        }

        if (rageValue < 90) {
            return RageState.AGGRESSIVE;
        }

        return RageState.HULKSMASH;
    }

    public void increaseRage(int value) {
        rageValue += value;

        if (rageValue > 100) {
            rageValue = 100;
        }
    }
    public void setRage(int value) {
        rageValue = value;

        if (rageValue > 100) {
            rageValue = 100;
        }
    }

    public void decreaseRage(int value) {
        rageValue -= value;

        if (rageValue < 0) {
            rageValue = 0;
        }
    }
}
