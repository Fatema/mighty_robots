package green;

import green.rage.RageMeter;
import green.rage.RageState;
import robocode.*;
import robocode.Robot;

import java.awt.*;

/**
 * Hulk - A Green Team adaptation of:
 * <p>
 * Walls - a sample robot by Mathew Nelson, and maintained by Flemming N. Larsen
 * <p>
 * Moves around the outer edge with the gun facing in.
 *
 * @author Mathew A. Nelson (original)
 * @author Flemming N. Larsen (contributor)
 */
public class Hulk extends Robot {

    private RageMeter rageMeter = new RageMeter(70); // start at aggressive rage
    private boolean peek; // Don't turn if there's a robot there
    private double moveAmount; // How much to move

    /**
     * run: Move around the walls
     */
    public void run() {
        // Set colors
        setBodyColor(Color.green);
        setGunColor(Color.black);
        setRadarColor(Color.black);
        setBulletColor(Color.green);
        setScanColor(Color.green);

        // Initialize moveAmount to the maximum possible for this battlefield.
        moveAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
        // Initialize peek to false
        peek = false;

        // turnLeft to face a wall.
        // getHeading() % 90 means the remainder of
        // getHeading() divided by 90.
        turnLeft(getHeading() % 90);
        ahead(moveAmount);
        // Turn the gun to turn right 90 degrees.
        peek = true;
        turnGunRight(90);
        turnRight(90);

        while (true) {
            switch (rageMeter.getRageState()) {
                case HULKSMASH:
                    wallsMovement();
                    break;
                case AGGRESSIVE:
                    wallsMovement();
                    break;
                case AVERAGE:
                    wallsMovement();
                    break;
                case PASSIVE:
                    wallsMovement();
                    break;
                default:
                    wallsMovement();
                    break;
            }
        }
    }

    private void wallsMovement() {
        // Look before we turn when ahead() completes.
        peek = true;
        // Move up the wall
        ahead(moveAmount);
        // Don't look now
        peek = false;
        // Turn to the next wall
        turnRight(90);
    }

    /**
     * onHitRobot:  Move away a bit.
     */
    public void onHitRobot(HitRobotEvent e) {
        // If he's in front of us, set back up a bit.
        if (e.getBearing() > -90 && e.getBearing() < 90) {
            back(100);
        } // else he's in back of us, so set ahead a bit.
        else {
            ahead(100);
        }
    }

    /**
     * onBulletHit: increase rage
     */
    @Override
    public void onBulletHit(BulletHitEvent event) {
        rageMeter.increaseRage(20);
    }

    /**
     * onBulletMiss: decrease rage
     */
    @Override
    public void onBulletMissed(BulletMissedEvent event) {
        rageMeter.decreaseRage(5);
    }

    /**
     * onHitByBullet: decrease rage
     */
    @Override
    public void onHitByBullet(HitByBulletEvent event) {
        rageMeter.decreaseRage(10);
    }

    /**
     * onScannedRobot:  Fire!
     */
    public void onScannedRobot(ScannedRobotEvent e) {
        RageState rageState = rageMeter.getRageState();
        System.out.println("Current rage state: " + rageState);

        if (this.getEnergy() >= 30) {
            switch (rageState) {
                case PASSIVE:
                    fire(1.0);
                    break;
                case AVERAGE:
                    fire(2.0);
                    break;
                case AGGRESSIVE:
                    fire(2.5);
                    break;
                case HULKSMASH:
                    fire(3.0);
            }
            // Note that scan is called automatically when the robot is moving.
            // By calling it manually here, we make sure we generate another scan event if there's a robot on the next
            // wall, so that we do not start moving up it until it's gone.
            if (peek) {
                scan();
            }
        } else {
            System.out.println("Preservation Mode Active, current Energy is below 30");
        }

    }
}
