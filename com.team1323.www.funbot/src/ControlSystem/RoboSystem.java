
package ControlSystem;

import IO.Logger;
import IO.TeleController;
import SubSystems.DistanceController;
import SubSystems.DriveTrain;
import SubSystems.Elevator;
import SubSystems.Navigation;
import SubSystems.TurnController;
import SubSystems.Turret;
import SubSystems.Vision;
import edu.wpi.first.wpilibj.Timer;


public class RoboSystem{
	public DriveTrain dt;	
	public Turret turret;
	public Elevator elevator;
	
	public Navigation nav;
	public Vision vision;
	private  distanceThread distTh;
    public DistanceController dist;
    private static RoboSystem instance = null;
    public Logger logFile;
    public turnThread turnTh;
    public TurnController turn; 
    public boolean turnRunning = false;
    private TeleController controller;
    public static RoboSystem getInstance()
    {
        if( instance == null )
            instance = new RoboSystem();
        return instance;
    }
    
    public RoboSystem(){
    	dt = DriveTrain.getInstance();
    	turret = Turret.getInstance();
    	elevator = Elevator.getInstance();
    	nav = Navigation.getInstance();
    	vision = Vision.getInstance();
    	logFile = Logger.getInstance();
    }
    public void Init(){
    	turret.set(turret.getAngle());
    }
    
    public double driveDistanceHoldingHeading(double distance, double heading,double maxSpeed,double timeout,double tol, boolean holdSpeed,double breakTime){                
        dist.resetDistance();
        double startDistance = 0;
        double distanceChange = distance + startDistance;
        System.out.println("DD: " + startDistance + " " + distanceChange + " " + distance);
        dist.reset();
        dist.setGoal(distanceChange, maxSpeed,heading,timeout,tol,holdSpeed);
        distTh.run();
        return nav.getDistance() - startDistance;
    }
    private class distanceThread extends Thread{
        private boolean keeprunning = false;
        public void run(){
            if(!keeprunning){
                while(!dist.onTarget()){
                    dist.run();
                    Timer.delay(0.01);
                }
            }else{
               
            }
            System.out.println("done");
            dt.directDrive(0, 0);                       
        }
    }
    public void turnToHeading(double heading, double timeout,boolean hold){
        nav.resetRobotPosition(0, 0, 0,false);
        if(turn == null){
            turn = TurnController.getInstance();
        }
        turn.reset();
        turn.setGoal(heading,timeout,hold);
        turnTh = new turnThread();
        turnTh.start();
        System.out.println("TurnHold");
    }
    public class turnThread extends Thread{
        private boolean keepRunning = true;
        public void run(){
            try {
            	turnRunning = true;
                while(!turn.onTarget() && keepRunning){
                    turn.run();
                    Timer.delay(0.01);
                }
                System.out.println("done");
                dt.directDrive(0, 0);
                turnRunning = false;
            }catch(Exception e){
                System.out.println("crash" + e.toString());
            }            
        }
        public void kill(){
        	keepRunning = false;
        	turnRunning = false;
        }
    }
    
   
}
