package SubSystems;

import Utilities.Ports;
import Utilities.Util;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class DriveTrain{
	private static DriveTrain instance = null;
	public DriveBase left;
	public DriveBase right;
	private GEAR currentGear = GEAR.LOW;
	private boolean shifting = false;
	public enum SIDE{
		LEFT,RIGHT
	}
	public enum GEAR{
		HIGH,LOW,NUETRAL,PTO
	}
	public DriveTrain(){
		left = new DriveBase(Ports.LEFT_DT_1,Ports.LEFT_DT_2);
		right = new DriveBase(Ports.RIGHT_DT_1,Ports.RIGHT_DT_2);
	}
	
	public static DriveTrain getInstance()
    {
        if( instance == null )
            instance = new DriveTrain();
        return instance;
    }	
	public static class DriveBase{
		private CANTalon _1;
	    private CANTalon _2;
	    
	    public DriveBase(int Port1,int Port2){
	    	_1 = new CANTalon(Port1);
	    	//_1.setVoltageRampRate(12);
	    	_2 = new CANTalon(Port2);
	    	//_2.setVoltageRampRate(12);
	    }
	    public double currentDraw(){
	    	return _2.getOutputCurrent();
	    }
	    public void enableBrakeMode(){
	    	_1.enableBrakeMode(true);
	    	_2.enableBrakeMode(true);
	    }
	    public void disableBrakeMode(){
	    	_1.enableBrakeMode(false);
	    	_2.enableBrakeMode(false);
	    }
	}
	public GEAR currentGear(){
		return currentGear;
	}
	public void applyPower(double power, DriveTrain.SIDE side){
    	switch(side){
		case LEFT:
			left._1.set(power);
			left._2.set(power);
			break;
		case RIGHT:
			right._1.set(power);
			right._2.set(power);
			break;
		default:
			break;
    	}
    }
	public void directArcadeDrive(double x, double y)
    {
        x = Util.limit(x, -1.0, 1.0);
        y = Util.limit(y, -1.0, 1.0);
        double left = y + x;
        double right = y - x;
        left = Util.limit(left, -1.0, 1.0);
        right = Util.limit(right, -1.0, 1.0);
        directDrive(left, right);
    }
	public void directDrive(double left, double right)
    {
        applyPower(left,DriveTrain.SIDE.LEFT);
        applyPower(-right,DriveTrain.SIDE.RIGHT);
    }
	public void update(){
		SmartDashboard.putNumber("DT_LEFT_CUR", left.currentDraw());
		SmartDashboard.putNumber("DT_RIGHT_CUR", right.currentDraw());
	}
	public boolean inLowGear() { return currentGear == GEAR.LOW;}
	private double old_wheel = 0.0;
    private double neg_inertia_accumulator = 0.0;
	public void cheesyDrive(double wheel, double throttle, boolean quickturn)
    {
        double left_pwm,right_pwm,overPower;
        double sensitivity = 1.1;
        double angular_power;
        double linear_power;
        double wheelNonLinearity;
        if(Math.abs(throttle) < 0.2){
        	throttle = 0.0;
        }
        double neg_inertia = wheel - old_wheel;
        old_wheel = wheel;

        if (!inLowGear()) {
                wheelNonLinearity = 0.995; // used to be csvReader->TURN_NONL 0.9
                // Apply a sin function that's scaled to make it feel bette
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        } else {
                wheelNonLinearity = 0.5; // used to be csvReader->TURN_NONL higher is less sensitive 0.8
                // Apply a sin function that's scaled to make it feel bette
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
                wheel = Math.sin(Math.PI / 2.0 * wheelNonLinearity * wheel) / Math.sin(Math.PI / 2.0 * wheelNonLinearity);
        }

        double neg_inertia_scalar;
        if (!inLowGear()) {
                neg_inertia_scalar = 1.25; // used to be csvReader->NEG_INT 11
                sensitivity = 1.06; // used to be csvReader->SENSE_HIGH  1.15
                if (Math.abs(throttle) > 0.15) { // used to be csvReader->SENSE_ .15
                        sensitivity = .9 - (.9 - sensitivity) / Math.abs(throttle);
                }
        } else {
                if (wheel * neg_inertia > 0) {
                        neg_inertia_scalar = 1; // used to be csvReader->NE 5
                } else {
                        if (Math.abs(wheel) > 0.40) {
                                neg_inertia_scalar = 1;// used to be csvRe 10
                        } else {
                                neg_inertia_scalar = 1; // used to be csvRe 3
                        }
                }
                sensitivity = 1.30; // used to be csvReader->SENSE_LOW lower is less sensitive 1.07

                if (Math.abs(throttle) > 0.1) { // used to be csvReader->SENSE_ .1
                        sensitivity = .9 - (.9 - sensitivity) / Math.abs(throttle);
                }
        }
        double neg_inertia_power = neg_inertia * neg_inertia_scalar;
        if (Math.abs(throttle) >= 0.25 || quickturn) neg_inertia_accumulator += neg_inertia_power; //.25
        
        wheel = wheel + neg_inertia_accumulator;
        if (neg_inertia_accumulator > 1)
                neg_inertia_accumulator -= 0.25;
        else if (neg_inertia_accumulator < -1)
                neg_inertia_accumulator += 0.25;
        else
                neg_inertia_accumulator = 0;

        linear_power = throttle;

        if ((!Util.inRange(throttle, -0.2,0.2) || !(Util.inRange(wheel, -0.4, 0.4))) && quickturn) { //.2
                overPower = 1.0;
                if (currentGear == GEAR.HIGH) {
                        sensitivity = 1.0; // default 1.0
                } else {
                        sensitivity = 1.0;
                }
                angular_power = wheel * sensitivity;
        } else {
                overPower = 0.0;
                angular_power = Math.abs(throttle) * wheel * sensitivity;
        }
//      System.out.println("NA " + neg_inertia_accumulator + " AP " + angular_power + " wheel " + wheel + " throttle" + throttle + " NAP " + neg_inertia_power);
        right_pwm = left_pwm = linear_power;
        left_pwm += angular_power;
        right_pwm -= angular_power;

        if (left_pwm > 1.0) {
                right_pwm -= overPower*(left_pwm - 1.0);
                left_pwm = 1.0;
        } else if (right_pwm > 1.0) {
                left_pwm -= overPower*(right_pwm - 1.0);
                right_pwm = 1.0;
        } else if (left_pwm < -1.0) {
                right_pwm += overPower*(-1.0 - left_pwm);
                left_pwm = -1.0;
        } else if (right_pwm < -1.0) {
                left_pwm += overPower*(-1.0 - right_pwm);
                right_pwm = -1.0;
        }
        directDrive(left_pwm,right_pwm);
    }
	
	
	double powerToReduce = 0.0;
    int lastDirection    = 0;
    public void driveHoldHeading(double headingToHold, double currentHeading,double maxSpeed){
        if(currentHeading < headingToHold){
            if(lastDirection != 1)
                powerToReduce = 0;
            if((Math.abs(maxSpeed) - powerToReduce) > 0)
                powerToReduce = powerToReduce + 0.05;
            SmartDashboard.putString("driveHolding", "turn right");
            lastDirection = 1;
            directArcadeDrive(maxSpeed  , maxSpeed - powerToReduce);
        }else if(currentHeading > headingToHold){
            if(lastDirection != -1)
                powerToReduce = 0;
            if((Math.abs(maxSpeed) - powerToReduce) > 0)
                powerToReduce = powerToReduce + 0.05;
            directArcadeDrive(maxSpeed - powerToReduce, maxSpeed );
            lastDirection = -1;
            SmartDashboard.putString("driveHolding", "turn left");
        }else{
            powerToReduce = 0.0;
            SmartDashboard.putString("driveHolding", "straight");
            lastDirection = 0;
            directArcadeDrive(maxSpeed, maxSpeed);
        }
        SmartDashboard.putNumber("POWER_TO_REDUCE", powerToReduce);
        SmartDashboard.putNumber("POWER_REDUCTION",maxSpeed-powerToReduce);
    }
}