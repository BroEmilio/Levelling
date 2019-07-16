package levelling;

public class LevellingMetaData {
	public static final int CLASSIC_MODE = 0;  // calculating mode for inserted values of back and fore sights
	public static final int CREATION_MODE = 1; // calculate values for back and fore sights leaning on inserted elevations
	
	int calculatingMode;   
	double lengthLeveling;
	int foreSightsCount;
	
	public LevellingMetaData() {
		calculatingMode = CLASSIC_MODE;
		foreSightsCount = 0;
	}
	
	public int getCalculatingMode() {
		return calculatingMode;
	}

	public void setCalculatingMode(int calculatingMode) {
		this.calculatingMode = calculatingMode;
	}

	public double getLengthLeveling() {
		return lengthLeveling;
	}

	public void setLengthLeveling(double lengthLeveling) {
		this.lengthLeveling = lengthLeveling;
	}

	public int getForeSightsCount() {
		return foreSightsCount;
	}
	
	public void setForeSightsCount(int foreSightsCount) {
		this.foreSightsCount = foreSightsCount;
	}


	
}
