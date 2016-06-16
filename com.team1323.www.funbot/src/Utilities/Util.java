package Utilities;

import java.util.List;


public class Util {

    /**
     * carful with negative
     * 
     * @param val 
     * @param min
     * @param max
     * @return 
     */
    public static double speedLimit(double speed, double position, double min, double max) {
        if((position < min && speed < 0) || (position > max && speed > 0))
            return 0.0;
        else
            return speed;
    } 
    public static double getDifferenceInAngleDegrees(double from, double to)
    {
        return boundAngleNeg180to180Degrees(to-from);
    }
    public static double boundAngleNeg180to180Degrees(double angle)
    {
        // Naive algorithm
        while(angle >= 180.0)
        {
            angle -= 360.0;
        }
        while(angle < -180.0)
        {
            angle += 360.0;
        }
        return angle;
    }
    public static double pidPower(double power,double minReverse, double maxReverse, double minForward, double maxForward){
        if(maxReverse > minReverse){
            maxReverse = minReverse;
        }
        if(maxForward < minForward){
            maxForward = minForward;
        }
        if(power < 0){
            if(power > minReverse){
                return minReverse;
            }else if(power < maxReverse){
                return maxReverse;
            }else{
                return power;
            }
        }else{
            if(power < minForward){
                return minForward;
            }else if(power > maxForward){
                return maxForward;
            }else{
                return power;
            }
        }
    }

    /**
     * 
     * @param val
     * @param min
     * @param max
     * @return 
     */
    public static double limit(double val, double min, double max) {
        if (min > max) return 0.0;
        if (val > max) return max;
        if (val < min) return min;
        return val;
    } 
//    public static double limit(double v, double limit) {
//        return (Math.abs(v) < limit) ? v : limit * (v < 0 ? -1 : 1);
//      }
    /**
     * 
     * @param val
     * @param abs
     * @return 
     */
    public static double limit(double val, double abs){
        if (val > abs) 
            return abs;
        else if (val < -abs) 
            return -abs;
        else
            return val;
    }  
    
    public static double limit(double val){
        if (val > 1) 
            return 1;
        else if (val < -1) 
            return -1;
        else
            return val;
    }  
    
    public static double buffer(double goalValue, double storedValue, int strength) { 
        return ((storedValue * strength) + (goalValue * (100 - strength))) / 100;
    }

    public static double deadBand(double val, double deadband){
        if (val < deadband && val > -deadband) 
            return 0.0;
        else 
            return val;
    }
    public static double deadBandBump(double val, double deadband){
        if (val < deadband && val > 0){ 
            return deadband;
        }else if(val > -deadband && val < 0){
            return -deadband;
        }else{
            return val;
        }
    }
    public static boolean onTarget(double target, double current, double error){
        return ((Math.abs(current) < (Math.abs(target)+ Math.abs(error))) && (Math.abs(current) > (Math.abs(target)- Math.abs(error))));
    }
    public static boolean inRange(double val, double maxAbsError) {
        return (Math.abs(val) < maxAbsError);
    }

    public static boolean inRange(double val, double minError, double maxError) {
        return (val > minError && val < maxError);
    }
    
    public static double aTan(double opp, double adj) {
        return Math.toDegrees(Math.atan2(opp, adj)); 
    }
    
    public static double aSin(double opp, double hyp) {
        return Math.toDegrees(Math.asin(opp / hyp)); 
    }
   
    public static double boundAngle0to360Degrees(double angle)
    {
        // Naive algorithm
        while(angle >= 360.0)
        {
            angle -= 360.0;
        }
        while(angle < 0.0)
        {
            angle += 360.0;
        }
        return angle;
    }
    public static double scaledInput(double input, double deadband){
    	return 0;
    }
    public static double scale(double x, double from_min, double from_max, double to_min, double to_max)
    {
        if(x < from_min)
            x = from_min;
        else if(x > from_max)
            x = from_max;
        return ((x-from_min)*(to_max-to_min)/(from_max-from_min)) + to_min;
    }
    public static double normalize(double current, double test){
    	if(current > test)
    		return current;
    	return test;
    }
    
    public static double degreesToRadians(double angle_in_degrees){
    	return (angle_in_degrees * Math.PI)/180.0; 
    }
    public static double arcLength(double radius, double angleInDegrees){
    	return (radius*angleInDegrees*Math.PI/180.0);
    }
    public static double trueAngletoDriveAngle(double angle){ //Angel in degrees clockwise
    	return angle - 360.0;
    }
    public static double boundAngle0to90Degrees(double angle){
    	angle = boundAngle0to360Degrees(angle);
    	if(0 >= angle && angle <= 90.0)
    		return angle;
    	if(angle > 90 && angle <= 180)
    		return angle - 90.0;
    	if(angle > 180 && angle <= 270)
    		return angle - 180.0;
    	return angle - 270.0;
    }
    public static String joinStrings(String delim, List<?> strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); ++i) {
            sb.append(strings.get(i).toString());
            if (i < strings.size() - 1) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    
    public static double xyToAngle(double x, double y){
    	if(x == 0 && y == 1)
    		return 0.0;
    	if(x == 1 && y == 0)
    		return 90.0;
    	if(x == 0 && y == -1)
    		return 180.0;
    	if(x == -1 && y == 0)
    		return 270.0;
    	else{
    		return Math.atan2(y, x)*180.0/Math.PI;
    	}
    }
    public static double controlSmoother(double input){
       double val = Math.abs(input);
       double p1 =     0.0369;
       double p2 =     0.1511;
       double p3 =     0.263;
       double p4 =     0.3577;
       double result = (p1*Math.pow(val, 3)) + (p2*Math.pow(val, 2))  + (p3*val) + p4;
       if(input > 0.2)
    	   return result;
       if(input < -0.2)
    	   return -result;
       else
    	   return 0;
    }
    public static double turretSmoother(double input){
    	double val = Math.abs(input);
    	double p1 = 54.63;
    	double p2 = -214.7;
    	double p3 = 321.8;
    	double p4 = -231.6;
    	double p5 = 83.36;
    	double p6 = -13.65;
    	double p7 = 1.169;
    	double p8 = -0.000534;
    	double result = (p1*Math.pow(val, 7)) + (p2*Math.pow(val, 6))  +(p3*Math.pow(val, 5)) + (p4*Math.pow(val, 4))  +(p5*Math.pow(val, 3)) + (p6*Math.pow(val, 2))  + (p7*val) + p8;
    	return result;
    }
    double t,k1,k2,k3,m;
    public void setTime(double acceleration, double distance){
    	t = Math.sqrt((2*Math.PI*distance/acceleration));
    }
    public static double radsToDegrees(double rads){
    	return rads * (180.0 /Math.PI);
    }
    public static double turnControlSmoother(double input){
    	double val = Math.abs(input);
        double p1 =     3.036;
        double p2 =     -3.829;
        double p3 =     1.945;
        double p4 =     -0.1536;
        double result = (p1*Math.pow(val, 3)) + (p2*Math.pow(val, 2))  + (p3*val) + p4;
        if(input > 0.2)
     	   return result;
        if(input < -0.2)
     	   return -result;
        else
     	   return 0;
    	
    }
}
