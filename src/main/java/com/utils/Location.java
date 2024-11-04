package main.java.com.utils;

public class Location {
    
    private double xPos;
    private double yPos;
    private int servingWlanId;

    /*
	 * Default Constructor: Creates an empty Location
	 */
	public Location() {
	}

    public Location(double xPos, double yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        // this.servingWlanId = servingWlanId;
    }

    @Override
	public boolean equals(Object other){
		boolean result = false;
	    if (other == null) return false;
	    if (!(other instanceof Location))return false;
	    if (other == this) return true;
	    
	    Location otherLocation = (Location)other;
	    if(this.xPos == otherLocation.xPos && this.yPos == otherLocation.yPos)
	    	result = true;

	    return result;
	}

	public int getServingWlanId(){
		return servingWlanId;
	}
	
	public double getXPos(){
		return xPos;
	}
	
	public double getYPos(){
		return yPos;
	}
}
