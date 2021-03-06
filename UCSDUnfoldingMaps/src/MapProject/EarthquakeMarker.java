package MapProject;

import de.fhpotsdam.unfolding.data.PointFeature;
import processing.core.PConstants;
import processing.core.PGraphics;

/** Implements a visual marker for earthquakes on an earthquake map
 * 
 * @author UC San Diego Intermediate Software Development MOOC team
 * @author Veronika Benkeser
 *
 */
public abstract class EarthquakeMarker extends CommonMarker
{
	// This will be set by the subclasses.
	protected boolean isOnLand;

	// The radius of the Earthquake marker
	protected float radius;
	
	// constants for distance
	protected static final float kmPerMile = 1.6f;
	
	/** Greater than or equal to this threshold is a moderate earthquake */
	public static final float THRESHOLD_MODERATE = 5;
	/** Greater than or equal to this threshold is a light earthquake */
	public static final float THRESHOLD_LIGHT = 4;

	/** Greater than or equal to this threshold is an intermediate depth */
	public static final float THRESHOLD_INTERMEDIATE = 70;
	/** Greater than or equal to this threshold is a deep depth */
	public static final float THRESHOLD_DEEP = 300;

	// abstract method implemented in derived classes
	public abstract void drawEarthquake(PGraphics pg, float x, float y);
		
	// constructor
	public EarthquakeMarker (PointFeature feature) 
	{
		super(feature.getLocation());
		// Add a radius property and then set the properties
		java.util.HashMap<String, Object> properties = feature.getProperties();
		float magnitude = Float.parseFloat(properties.get("magnitude").toString());
		properties.put("radius", 2*magnitude );
		setProperties(properties);
		this.radius = 1.75f*getMagnitude();
	}
	
	public void colorMarkerRelatToOthers(PGraphics pg, int colorChange){
		if(colorChange<0){
			colorChange=0;
		}
		
		int redVal = colorChange;
		int blueVal = 255-(int)(0.85*colorChange);
		int greenVal =0;
		
		pg.fill(redVal, greenVal, blueVal);	
	}
	
	// Calls abstract method drawEarthquake and then checks age and draws X if needed. If the user has entered
	//a valid keyboard command, the color of each marker is determined by how the property specified by the user
	//compares to the same property of other earthquakes. Otherwise, the color is determined by the earthquake's depth.
	@Override
	public void drawMarker(PGraphics pg,  float x, float y) {

		// save previous styling
		pg.pushStyle();
		if(this.getCMTyped()){
			colorMarkerRelatToOthers(pg, this.getCMValue());
		} else {
			// determine color of marker based on the depth
			colorDetermine(pg);
		}
		
		// call abstract method implemented in child class to draw marker shape
		drawEarthquake(pg, x, y);
			
		String age = getStringProperty("age");
		if ("Past Hour".equals(age) || "Past Day".equals(age)) {
			
			pg.strokeWeight(2);
			int buffer = 2;
			pg.line(x-(radius+buffer), 
					y-(radius+buffer), 
					x+radius+buffer, 
					y+radius+buffer);
			pg.line(x-(radius+buffer), 
					y+(radius+buffer), 
					x+radius+buffer, 
					y-(radius+buffer));
			
		}
		
		// reset to previous styling
		pg.popStyle();
		
	}
	
	//This method displays the radius, depth, and magnitude of each earthquake that is hovered once the user
	//has entered a valid command via her keyboard.
	public void showDetailedTitle(PGraphics pg, float x, float y){
		
		String radius = "Radius:" + String.valueOf(getRadius());
		String depth = "Depth: "+ String.valueOf(getDepth());
		String magnitude= "Mag: "+String.valueOf(getMagnitude());
		
		pg.clear();
		float maxWidth= Math.max(pg.textWidth(depth), pg.textWidth(radius));
		
		//Adjust x and y coordinates to make sure the label will fit within the map
		if (850-x<maxWidth){
			System.out.println("less");
			if(x-maxWidth>200){
				x = x-maxWidth;
			} else{
				x=210;
			}
		}
		if (800-y<15){
			y = 785;
		} 
		
		pg.pushStyle();
		
		pg.rectMode(PConstants.CORNER);
		pg.stroke(110);
		pg.fill(255,255,255);
		
		
		pg.rect(x, y + 15, maxWidth + 6, 44);
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(radius, x+3, y+18);
		pg.text(depth, x+3, y +30);
		pg.text(magnitude, x+3, y + 42);
		pg.popStyle();
		
	}
	
	//This method displays the title of the earthquake.
	public void showTitle(PGraphics pg, float x, float y)
	{
		String title = getTitle();
		pg.clear();
		
		//Making sure that the label will fit within the window
		if (850-x<pg.textWidth(title)){
			
			if(x-pg.textWidth(title)>200){
				x = x-6-pg.textWidth(title);
			} else{
				x=210;
			}
		}
		
		if (650-y<30){
			y = 614;
		} 
		
		pg.pushStyle();
		
		pg.rectMode(PConstants.CORNER);
		
		pg.stroke(110);
		pg.fill(255,255,255);
		pg.rect(x, y + 15, pg.textWidth(title) +6, 18, 5);
		
		pg.textAlign(PConstants.LEFT, PConstants.TOP);
		pg.fill(0);
		pg.text(title, x + 3 , y +18);
		
		pg.popStyle();
		
	}

	
	/**
	 * Return the "threat circle" radius, or distance up to 
	 * which this earthquake can affect things, for this earthquake.   
	 * DISCLAIMER: this formula is for illustration purposes
	 *  only and is not intended to be used for safety-critical 
	 *  or predictive applications.
	 */
	public double threatCircle() {	
		double miles = 20.0f * Math.pow(1.8, 2*getMagnitude()-5);
		double km = (miles * kmPerMile);
		return km;
	}
	
	// determine color of marker from depth
	// We use: Deep = red, intermediate = blue, shallow = yellow
	private void colorDetermine(PGraphics pg) {
		float depth = getDepth();
		
		if (depth < THRESHOLD_INTERMEDIATE) {
			pg.fill(255, 255, 0);
		}
		else if (depth < THRESHOLD_DEEP) {
			pg.fill(0, 0, 255);
		}
		else {
			pg.fill(255, 0, 0);
		}
		
	}
	
	
	/** toString
	 * Returns an earthquake marker's string representation
	 * @return the string representation of an earthquake marker.
	 */
	public String toString()
	{
		return getTitle();
	}
	
	/*
	 * getters for earthquake properties
	 */
	public float getMagnitude() {
		return Float.parseFloat(getProperty("magnitude").toString());
	}
	
	public float getDepth() {
		return Float.parseFloat(getProperty("depth").toString());	
	}
	
	public String getTitle() {
		return (String) getProperty("title");	
		
	}
	
	public float getRadius() {
		return Float.parseFloat(getProperty("radius").toString());
	}
	
	public boolean isOnLand()
	{
		return isOnLand;
	}
}
