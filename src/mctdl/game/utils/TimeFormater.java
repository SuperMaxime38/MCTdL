package mctdl.game.utils;

public class TimeFormater {
	
public static String getFormatedTime(int left) {
		
		String format = "";
		
		int dividend = left, divisor = 60;
        int quotient = dividend / divisor;
        int remainder = dividend % divisor;
        
        String hour, min, secs;
        
        secs = String.valueOf(remainder);
        if(remainder < 10) { //SI IL RESTE MOINS DE 10 SECONDS ALORS CA MET UN ZERO DEVANT
        	secs = "0" + secs;
        }
        
        if(quotient >= 1) { //SI LE TEMPS RESTANT EST > QUE 1 MINUTE
        	dividend = quotient;
        	quotient = dividend / divisor;
            remainder = dividend % divisor;
            
            min = String.valueOf(remainder);
            
            if(remainder < 10) { //SI IL RESTE MOINS DE 10 MIN ALORS CA MET UN ZERO DEVANT
            	min = "0" + min;
            }
            
            if(quotient >= 1) {
            	hour = String.valueOf(quotient);
            	format = hour + "h" + min + "m" + secs + "s";
            } else {
            	format = min + "m" + secs + "s";
            }
        } else { //SI LE TEMPS RESTANT EST < QUE 1 MINUTE
        	format = remainder + "s";
        }
        
		return format;
	}
}
