package IO;import ControlSystem.FSM;
import ControlSystem.RoboSystem;
import SubSystems.DriveTrain.GEAR;
import SubSystems.Turret;
import Utilities.Util;


public class TeleController
{
    public static final double STICK_DEAD_BAND = 0.2;
    public  Controller codriver;
    public Controller wheel;
    private Controller driver;
    private FSM fsm;
    private RoboSystem robot;
    private static TeleController instance = null;
    public TeleController(){
        driver = new Controller(2);
        driver.start();
        codriver  = new Controller(0);
        codriver.start();
        wheel = new Controller(1);
        wheel.start();
        robot = RoboSystem.getInstance();
        fsm = FSM.getInstance();
        System.out.println("CONTROLS STARTED");
    }
    public static TeleController getInstance(){
        if(instance == null){
            instance = new TeleController();
        }
        return instance;
    }        
    public void coDriver(){
        if(codriver.aButton.isPressed() || codriver.aButton.isHeld()){
        	
        }else if(codriver.aButton.isReleased()){
        	
        }        
        //////////////////////////////////////////
        if(codriver.bButton.isPressed()){
        	fsm.setGoalState(FSM.State.STOW);
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " B  PRESSED");
        }
        ////////////////////////////////////////
        if(codriver.xButton.isPressed()){
        	
        }
        if(codriver.xButton.buttonHoldTime() > 250){
        	
        }
        ///////////////////////////////////////
        if(codriver.yButton.isPressed()){
        	
        }
        //////////////////////////////////////////////////////////////////// 
        if(codriver.leftBumper.isPressed()){ 
        	
        }
        //////////////////////////////////
        if(codriver.rightBumper.isPressed()) {
        	
        }
        ///////////////////////////////////////////////////////
        if(codriver.leftTrigger.isPressed()){
        	
        }
        
        //////////////////////////////////////////////////////
        if(codriver.backButton.isPressed() || codriver.backButton.isHeld()){  // stop all      
        	
        }
        ////////////////////////////////////////////////////////
        if(codriver.startButton.isPressed()){
        	
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Controller.RIGHT_STICK_Y) > 0.75) {
        	robot.elevator.up();       
        }else if(codriver.getButtonAxis(Controller.RIGHT_STICK_Y) < -0.75){
        	robot.turret.set(0.0);
        	fsm.setGoalState(FSM.State.ELEVATOR_WAITING);
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " RSY DOWN");
        }else{
        	
        }
        ////////////////////////////////////////////////////////        
        if (codriver.getButtonAxis(Controller.RIGHT_STICK_X) > 0.25) {

        	robot.turret.manualMove(-Util.turretSmoother(codriver.getButtonAxis(Controller.RIGHT_STICK_X))*13);
        }else if(codriver.getButtonAxis(Controller.RIGHT_STICK_X) < -0.25){
        	robot.turret.manualMove(Util.turretSmoother(codriver.getButtonAxis(Controller.RIGHT_STICK_X))*13);
        }else{
        	
        }
        ///////////////////////////////////////////////
        
        if (codriver.getButtonAxis(Controller.LEFT_STICK_Y) > 0.3 || codriver.getButtonAxis(Controller.LEFT_STICK_Y) < -0.3) {
        	
        robot.dt.directArcadeDrive(codriver.getButtonAxis(Controller.LEFT_STICK_X), -codriver.getButtonAxis(Controller.LEFT_STICK_Y));	 
        }else{
        	robot.dt.cheesyDrive(codriver.getButtonAxis(Controller.LEFT_STICK_X), -codriver.getButtonAxis(Controller.LEFT_STICK_Y), codriver.leftTrigger.isPressed() || codriver.leftBumper.isHeld());
        }
		///////////////////////////////////////////////
		if (codriver.getButtonAxis(Controller.LEFT_STICK_X) > 0.3) {
//			robot.turret.manualMove(-Util.turretSmoother(codriver.getButtonAxis(Controller.LEFT_STICK_X))*13);
		}else if( codriver.getButtonAxis(Controller.LEFT_STICK_X) < -0.3){
//			robot.turret.manualMove(Util.turretSmoother(codriver.getButtonAxis(Controller.LEFT_STICK_X))*13);
		}else{
		
		}
        ///////////////////////////////////////////////
        if(codriver.leftCenterClick.isPressed() || codriver.leftCenterClick.isHeld()){
        	
        }     
        ///////////////////////////////////////////////
        if(codriver.rightCenterClick.isPressed() || codriver.rightCenterClick.isHeld()) {
        	robot.turret.set(0.0);
        	robot.turret.setState(Turret.State.OFF);        	
        }
        if(codriver.getPOV() == 0){
//        	robot.logFile.writeToLog(System.currentTimeMillis() + " GP UP PRESSED");
        	robot.turret.set(0.0);
        	robot.turret.setState(Turret.State.OFF);   
        }
        if(codriver.getPOV() == 90){
        	robot.turret.setState(Turret.State.OFF);  
        	robot.turret.set(-45);
        }
        if(codriver.getPOV() == 270){
        	robot.turret.setState(Turret.State.OFF);  
        	robot.turret.set(45);
        }
        if(codriver.getPOV() == 180){
        	
        }
    }
    
    public void driver() {
    	if (driver.aButton.isPressed()){
//    		robot.logFile.writeToLog(System.currentTimeMillis() + " DRIVER (1) PRESSED");
//    		robot.dt.setGear(GEAR.HIGH);
    	}
    	if(driver.bButton.isPressed()){
//    		robot.logFile.writeToLog(System.currentTimeMillis() + " DRIVER (2) PRESSED");
//    		robot.dt.setGear(GEAR.LOW); 
    		}
        
//        robot.dt.cheesyDrive(wheel.getX(), -driver.getY(), wheel.leftBumper.isPressed() || wheel.leftBumper.isHeld());
    }
    public void update(){
    	coDriver();
    	driver();
    }
    
}
