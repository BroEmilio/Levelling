package levelling;

public class Sight {
	private String pointNumber;
	private Integer backOrForeSight1,backOrForeSight2, intermediateSight1, intermediateSight2, difference;
	private Double elevation;
	private Boolean isSightLock;
	private Boolean isSightIntermediate;
	private Boolean isSightEditable;
	private Boolean isBackSight;

	public Sight(){
		 isSightLock = false;
		 isSightIntermediate =false;
		 isSightEditable= true;
		 isBackSight = false;
	 }
	
	public Boolean isLock() {
		return isSightLock;
	}
	public void setLock(Boolean booleanForLock) {
		this.isSightLock = booleanForLock;
		this.isSightEditable = true;
	}
	
	public String getPointNumber() {
	return pointNumber;
	}
	public void setPointNumber(String pointNumber) {
		this.pointNumber = pointNumber;
	}
	
	public Integer getBackOrForeSight1() {
		return backOrForeSight1;
	}
	public void setBackOrForeSight1(Integer value) {
		this.backOrForeSight1 = value;
	}
	
	public Integer getBackOrForeSight2() {
		return backOrForeSight2;
	}
	public void setBackOrForeSight2(Integer value) {
		this.backOrForeSight2 = value;
	}
	
	public Integer getIntermediateSight1() {
		return intermediateSight1;
	}
	public void setIntermediateSight1(Integer value) {
		this.intermediateSight1 = value;
	}
	
	public Integer getIntermediateSight2() {
		return intermediateSight2;
	}
	public void setIntermediateSight2(Integer value) {
		this.intermediateSight2 = value;
	}
	
	public Integer getDifference() {
		return difference;
	}
	public void setDifference(Integer value) {
		this.difference = value;
	}
	
	public Double getElevation() {
		return elevation;
	}
	public void setElevation(Double elevation) {
		this.elevation = elevation;
	}
	
	 public Boolean isIntermediate() {
			return isSightIntermediate;
		}
	 public void setIntermediate(Boolean isIntermediate) {
		 	this.isSightIntermediate = isIntermediate;
		 	if(isIntermediate) {
		 		this.isSightEditable=true;
		 		this.isBackSight=false;
		 	}
		}
	 
		public Boolean isEditable() {
			return isSightEditable;
		}
		
		public void setEditable(Boolean isEditable) {
			this.isSightEditable = isEditable;
		}
		
		
		public Boolean isBackSight() {
			return this.isBackSight;
		}
		
		public void setAsBackSight(Boolean isBackSight) {
			if(isBackSight) {
				this.isSightIntermediate=false;
				this.isSightEditable=false;
			} else {
				this.isSightEditable=true;
			}
			this.isBackSight = isBackSight;
		} 
		
		public String toString() {
		String sightDescription;
		sightDescription = "Backsight or Foresight 1:"+ backOrForeSight1+"; Elevation:"+elevation+
				"; isBacksight:"+isBackSight+"; isIntermediate:"+isSightIntermediate+
				"; isEditable:"+isSightEditable+"; isLock:"+isSightLock;
		
		return sightDescription;
		}
	
}
