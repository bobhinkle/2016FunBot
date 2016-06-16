package ControlSystem;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
public class FSM {
	
	public enum State{
    	DEFAULT, INIT, LOW_BAR, 
    	INTAKE, INTAKE_READY,STOW, ELEVATOR_WAITING,ELEVATOR_LOWER, STOW_READY,
    	SHOOTER_CLOSE, SHOOTER_FAR, SHOOTER_WAITING,SHOOTER_READY,PTO,AUTO_SHOT,BATTER_SHOT,
    	SHOOTER_BALL_SUCK,SHOOTER_WAIT_FOR_BALL_SUCK,CDF_CROSS
    	
    }
	private RoboSystem robot;
	private static FSM instance = null;
	private volatile State currentState = State.INIT;
    private volatile State goalState = State.DEFAULT;
    private volatile State prevState = State.DEFAULT;
    private static final int K_READING_RATE = 100;
    // synchronized access object
    private final Timer mTimer = new Timer();
	public static FSM getInstance()
    {
        if( instance == null )
            instance = new FSM();
        return instance;
    }
	public void start() {
        synchronized (mTimer) {
            mTimer.schedule(new InitTask(), 0);
        }
    }
	private class InitTask extends TimerTask {
        @Override
        public void run() {
            while (true) {
                try {
                	SmartDashboard.putString("FSM", "STARTED");
                    robot = RoboSystem.getInstance();
                    break;
                } catch (Exception e) {
                    System.out.println("FSM failed to initialize: " + e.getMessage());
                    synchronized (mTimer) {
                        mTimer.schedule(new InitTask(), 500);
                    }
                }
            }
            synchronized (mTimer) {
                mTimer.schedule(new UpdateTask(), 0, (int) (1000.0 / K_READING_RATE));
            }
        }
    }
    public FSM() {
    	
    }
    
    public void setGoalState(State goal) {
        if(currentState == goal){
            currentState = State.DEFAULT;
            goalState = goal;
        }else{
        	prevState = currentState;
            goalState = goal;
        }
    }
    
    public State getCurrentState() {
        return currentState;
    }
    private void stateComplete(State state){
        currentState = state;
    }
    public State getPreviousState(){
    	return prevState;
    }
    public void nextState(){
    	switch(getPreviousState()){
    	
		
		default:
			setGoalState(FSM.State.INIT);
			break;
		}
    }
    private class UpdateTask extends TimerTask {
	    public void run(){ 
	        switch(goalState){
	            case INIT:
	                SmartDashboard.putString("FSM_STATE", "INIT");
	                stateComplete(State.INIT);
	                break;       
	            case ELEVATOR_WAITING:
	            	if(robot.turret.safeToLower()){
	            		robot.elevator.down();
	            		stateComplete(FSM.State.ELEVATOR_WAITING);
	            		setGoalState(State.STOW_READY);
	            	}
	            	SmartDashboard.putString("FSM_STATE", "STOW WAITING");
	            	break;
	            
	            case DEFAULT:
	            	SmartDashboard.putString("FSM_STATE", "WAITING");
	            	break;
			default:
				break;
	        }
	        
	       
	        try{
		        robot.turret.update();
			}catch(Exception e){
				        	
			 }
	        try{
		        robot.elevator.update();
			}catch(Exception e){
				        	
			}		    
	    }
    }
}