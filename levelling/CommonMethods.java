package levelling;

import java.math.*;
import java.text.*;
import java.util.*;
import levellingTable.*;

public class CommonMethods {
	LevellingTableModel model;
	
	public CommonMethods(LevellingTableModel model) {
		this.model = model;
	}
	
	public double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_EVEN);
	    return bd.doubleValue();
	}
	
	public int roundToInt(double value) {
		 DecimalFormat df = new DecimalFormat("#0");
		 df.setRoundingMode(RoundingMode.HALF_EVEN);
		 return new Integer(df.format(value));
	}
	
	Sight lastBackSight(int index) {
		Sight lastBackSight=null;
		List<Sight> data = model.getLevellingData();
		ListIterator<Sight> it = data.listIterator(index);
		while(lastBackSight == null && it.hasPrevious()) {
			if(it.previous().isBackSight())
				lastBackSight=it.next();
		}
		return lastBackSight;
	}
	
	Sight nextBackSight(int index) {
		Sight nextBackSight=null;
		List<Sight> data = model.getLevellingData();
		ListIterator<Sight> it = data.listIterator(index);
		while(nextBackSight == null && it.hasNext()) {
			if(it.next().isBackSight())
				nextBackSight=it.previous();
		}
		return nextBackSight;
	}
	
	public void calculateAndSetDifferenceBetweenFirstAndSecondSurvey(Sight sight, int index) {	// calculate difference between first and second survey for chosen sight
		Sight lastBackSight=lastBackSight(index);
		if(sight.isIntermediate() && sight.getIntermediateSight1()!=null && sight.getIntermediateSight2()!=null) {
			if(lastBackSight.getBackOrForeSight1()!=null && lastBackSight.getBackOrForeSight2()!=null) {
				int firstSurvey = lastBackSight.getBackOrForeSight1() - sight.getIntermediateSight1();
				int secondSurvey = lastBackSight.getBackOrForeSight2() - sight.getIntermediateSight2();
				sight.setDifference(secondSurvey - firstSurvey);
			}
		}
		if(! sight.isIntermediate() && sight.getBackOrForeSight1()!=null && sight.getBackOrForeSight2()!=null) {
			if(lastBackSight.getBackOrForeSight1()!=null && lastBackSight.getBackOrForeSight2()!=null) {
				int firstSurvey = lastBackSight.getBackOrForeSight1() - sight.getBackOrForeSight1();
				int secondSurvey = lastBackSight.getBackOrForeSight2() - sight.getBackOrForeSight2();
				sight.setDifference(secondSurvey - firstSurvey);
			}
		}
	}
	
	public Integer[] scatterDisparity(double disparityAsDouble,int foreSightsCount) {	// calculate array for scatter disparity among all sights
		Integer[] scatterArray = new Integer[foreSightsCount];
		double averageDisparity = disparityAsDouble / foreSightsCount;
		double currentDisparity = averageDisparity;
		scatterArray[0]=roundToInt(averageDisparity);
		int currentInt = scatterArray[0];
		for(int i=1; i<scatterArray.length; i++) {
			currentDisparity = currentDisparity + averageDisparity;
			scatterArray[i] = roundToInt(currentDisparity)-currentInt;
			currentInt = currentInt + scatterArray[i];
		}
		return scatterArray;
	}
		
		
	
	public void updateSightsSequence(LevellingTableModel model) {		// update sequence of backsights and foresights in levelling data
		List<Sight> data = model.getLevellingData();
		Boolean isBackSight = true;
		for(int i=0; i<data.size(); i++) {
			Sight sight = data.get(i);
			if((sight.getBackOrForeSight1() != null || sight.getElevation() != null) && ! sight.isIntermediate()) {
				sight.setAsBackSight(isBackSight);
				if(isBackSight) {
					sight.setEditable(false);
					if(i==0)
						sight.setEditable(true);
				} else sight.setEditable(true);
			isBackSight = ! isBackSight;
			}
			if(sight.getIntermediateSight1() != null)
				sight.setIntermediate(true);
		}
	}
}