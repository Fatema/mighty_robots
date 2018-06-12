package green;

import green.rage.RageMeter;
import green.rage.RageState;
import robocode.*;
import robocode.Robot;
import static robocode.util.Utils.normalRelativeAngleDegrees;


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

    private RageMeter rageMeter = new RageMeter(100); // start at aggressive rage
    private boolean peek; // Don't turn if there's a robot there
    private double moveDefaultAmount; // How much to move relative to battle field
    private double moveSmallAmount; // How much to move when bobbing and weaving!
    
    static final double MAX_VELOCITY = 8;
    static final double WALL_MARGIN = 25;
    long fireTime = 0;
    
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

        // Initialize moveDefaultAmount to the maximum possible for this battlefield.
        moveDefaultAmount = Math.max(getBattleFieldWidth(), getBattleFieldHeight());
        // Set bob and weave travel distance
        moveSmallAmount = 100;
        // Initialize peek to false
        peek = false;
        
    	/** S Cox this doesn't help the coming out raging mode
        // turnLeft to face a wall.
  		// getHeading() % 90 means the remainder of
  		// getHeading() divided by 90.
  		turnLeft(getHeading() % 90);
  		ahead(moveDefaultAmount);
  		// Turn the gun to turn right 90 degrees.
  		peek = true;
  		turnGunRight(90);
  		turnRight(90);
		*/
        

        while (true) {
            switch (rageMeter.getRageState()) {
                case HULKSMASH:
                	nudgeNKillMovement();
                    break;
                case AGGRESSIVE:
                	nudgeNKillMovement();
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
		ahead(moveDefaultAmount);
		// Don't look now
		peek = false;
		// Turn to the next wall
		turnRight(90);
		turnGunRight(120);

    }
    
    private void nudgeNKillMovement() {
        
        // Shuffle
        ahead(moveSmallAmount * 2);
        
        // Turn about
        turnRight(45);
        turnGunRight(135);
        
     	// Take another look for good measure and shoot
        peek = false;
		scan();
        
    }
    
    void doGun(ScannedRobotEvent e) {
        
    	// Calculate exact location of the enemy robot
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

		
		if (e.getDistance() > 400 || getEnergy() < 15) {
			fire(1);
		} else if (e.getDistance() > 50 && bearingFromGun <= 5) {
			fire(2);
		} else if (getGunHeat() == 0 && bearingFromGun <= 3) {
			fire(3);
		} else {
			// Take another look for good measure and shoot if better odds!
			peek = false;
			fire(1);
			scan();
		}

    }
    
    
    // normalizes a bearing to between +180 and -180
    private double normalizeBearing(double angle) {
    	while (angle >  180) angle -= 360;
    	while (angle < -180) angle += 360;
    	return angle;
    }

    /**
     * onHitRobot:  Move away a bit.
     */
    public void onHitRobot(HitRobotEvent e) {
        
    	// Calculate exact location of the enemy robot
    	double absoluteBearing = getHeading() + e.getBearing();
    	double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());

    	
    	// Initiate the preferred gun barrel rotation relative to the enemy
    	if (absoluteBearing < 0) {
        	turnGunLeft(bearingFromGun);
        }else {
        	turnGunRight(bearingFromGun);
        }
    	
    	// If the enemy is in front of us, set back up a bit.
        if (absoluteBearing > -90 && absoluteBearing < 90) {
            back(moveSmallAmount);
        } // else the enemy is at the back of us, so set ahead a bit.
        else {
            ahead(moveSmallAmount);
            scan();
        }
        
    }

    /**
     * onBulletHit: increase rage
     */
    @Override
    public void onBulletHit(BulletHitEvent event) {
        rageMeter.increaseRage(20);
        // If you hit look again at fire if still in range, during the next tick
        // (though in accordance with common shooting methodology)
        scan();
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
        
    	// Take steps here to move into passive mode only where health < 30 [mission critical!]
    	long longValue = Math.round(getEnergy());
        int roboHealth = (int) longValue;
        if (roboHealth < 30) {
        	rageMeter.setRage(roboHealth);
        } else {
        	rageMeter.decreaseRage(10);
        	
        }
        // Shuffle escape arbitarily
    	if (getVelocity() == 0) {
    		turnRight(90);
    		moveSmallAmount *= -1;
    		ahead(moveSmallAmount);
    	}
    }

    /**
     * onScannedRobot:  Fire!
     */
    public void onScannedRobot(ScannedRobotEvent e) {
    	
    	RageState rageState = rageMeter.getRageState();
    	// Initialise to default firepower
        double firePower = 2;
        // Prepare to scan again after fire is shot
        peek = true;
        
	        //System.out.println("Current rage state: " + rageState);
	
	        switch (rageState) {
	            case PASSIVE:
	            	
	            	doGun(e);
	                break;
	            case AVERAGE:
	            	
	            	doGun(e);
	                break;
	            case AGGRESSIVE:
	            	stop();
	            	// Do_gun : just find and shoot
	            	doGun(e);
	            	resume();
	                break;
	            case HULKSMASH:
	                
					stop();
	            	if (getGunHeat() == 0) {
 
				        // calculate firepower based on distance
				        firePower = Math.min(500 / e.getDistance(), 3);
				        // calculate speed of bullet
				        double bulletSpeed = 20 - firePower * 3;
				        // distance = rate * time, solved for time
				        long time = (long)(e.getDistance() / bulletSpeed);
				        fire(firePower);
				        //Square off against enemy correctly
				        turnRight(normalizeBearing(e.getBearing() + 90));
					} else {
						// Do_gun : just find and shoot
						doGun(e);
					}
					
					resume();
	            	//Small movement triggers scan which in turn may result  in immediate further shot
					ahead(moveSmallAmount / 2);
					
					break;
        }
        // Note that scan is called automatically when the robot is moving.
        // By calling it manually here, we make sure we generate another scan event if there's a robot on the next
        // wall, so that we do not start moving up it until it's gone.
        if (peek) {
            scan();
        }
    }
}