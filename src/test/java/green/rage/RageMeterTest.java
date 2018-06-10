package green.rage;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class RageMeterTest {
    @Test
    public void getRageState_passive_upper() {
        RageMeter testObj = new RageMeter(29);

        assertThat(testObj.getRageState(), is(RageState.PASSIVE));
    }

    @Test
    public void getRageState_average_lower() {
        RageMeter testObj = new RageMeter(30);

        assertThat(testObj.getRageState(), is(RageState.AVERAGE));
    }

    @Test
    public void getRageState_average_upper() {
        RageMeter testObj = new RageMeter(59);

        assertThat(testObj.getRageState(), is(RageState.AVERAGE));
    }

    @Test
    public void getRageState_aggressive_lower() {
        RageMeter testObj = new RageMeter(60);

        assertThat(testObj.getRageState(), is(RageState.AGGRESSIVE));
    }

    @Test
    public void getRageState_aggressive_upper() {
        RageMeter testObj = new RageMeter(89);

        assertThat(testObj.getRageState(), is(RageState.AGGRESSIVE));
    }

    @Test
    public void getRageState_hulksmash_lower() {
        RageMeter testObj = new RageMeter(90);

        assertThat(testObj.getRageState(), is(RageState.HULKSMASH));
    }

    @Test
    public void increaseRage_overBoundary() {
        RageMeter testObj = new RageMeter(89);

        assertThat(testObj.getRageState(), is(RageState.AGGRESSIVE));

        testObj.increaseRage(1);

        assertThat(testObj.getRageState(), is(RageState.HULKSMASH));
    }

    @Test
    public void decreaseRage_underBoundary() {
        RageMeter testObj = new RageMeter(90);

        assertThat(testObj.getRageState(), is(RageState.HULKSMASH));

        testObj.decreaseRage(1);

        assertThat(testObj.getRageState(), is(RageState.AGGRESSIVE));
    }

    @Test
    public void increaseRage_cantGoOver100() {
        RageMeter testObj = new RageMeter(100);

        assertThat(testObj.getRageState(), is(RageState.HULKSMASH));

        testObj.increaseRage(100);

        testObj.decreaseRage(11);

        assertThat(testObj.getRageState(), is(RageState.AGGRESSIVE));
    }

    @Test
    public void decreaseRage_cantGoBelow0() {
        RageMeter testObj = new RageMeter(0);

        assertThat(testObj.getRageState(), is(RageState.PASSIVE));

        testObj.decreaseRage(100);

        testObj.increaseRage(30);

        assertThat(testObj.getRageState(), is(RageState.AVERAGE));
    }
}