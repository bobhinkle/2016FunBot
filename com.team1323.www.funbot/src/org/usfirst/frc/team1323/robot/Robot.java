
package org.usfirst.frc.team1323.robot;


import ControlSystem.FSM;
import ControlSystem.RoboSystem;
import IO.TeleController;
import SubSystems.Turret;
import SubSystems.Vision;
import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.Timer;

public class Robot extends SampleRobot {
    RoboSystem robot;
    private TeleController controllers;
   
    private FSM fsm;
   
    
    public Robot() {
        robot = RoboSystem.getInstance();        
    }
    public static enum AUTO{
    	CDF, LOWBAR,PC,ROUGH_ROCK_RAMPS,MOAT,ROCK_WALL
    }
    public static enum AUTO_TARGET_SELECT{
    	LEFT,RIGHT,BIGGEST
    }
    public void robotInit() {
        fsm = FSM.getInstance();
        fsm.start();
        
        controllers = TeleController.getInstance();
    }

    public void autonomous() {
    	
    }

    /**
     * Runs the motors with arcade steering.
     */
    public void operatorControl() {
    	robot.Init();
    	
    	robot.vision.setAutonomousTracking(false);
    	robot.turret.setState(Turret.State.HOLDING);
    	robot.vision.setBias(Vision.BIAS.BIGGEST);
        while (isOperatorControl() && isEnabled()) {
        	try{	
        		controllers.update();  
        	}catch(Exception e){
        		
        	}
            Timer.delay(0.01);		// wait for a motor update time
        }
    }
    public void disabledInit(){
  //  	robot.vision.gripProcess.destroy();
    	
    }
    public void disabledPeriodic(){
    	try{	
    		controllers.update();  
    		if(controllers.codriver.aButton.isPressed() && !controllers.codriver.aButton.isHeld()){
    			robot.nav.gyroCalibrate();
    		}
    	}catch(Exception e){
    		
    	}
        Timer.delay(0.01);	
    }
    /**
     * Runs during test mode
     */
    public void test() {
    }
}