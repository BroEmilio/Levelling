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
			if(it.previous().isBackSight)
				lastBackSight=it.next();
		}
		return lastBackSight;
	}
	
	Sight nextBackSight(int index) {
		Sight nextBackSight=null;
		List<Sight> data = model.getLevellingData();
		ListIterator<Sight> it = data.listIterator(index);
		while(nextBackSight == null && it.hasNext()) {
			if(it.next().isBackSight)
				nextBackSight=it.previous();
		}
		return nextBackSight;
	}
	
	public void calcDifferences(Sight odczyt, int index) {								// OBLICZENIE RÓ¯NICY MIÊDZY DWOMA PO£O¯ENIAMI
		Sight lastWstecz=lastBackSight(index);
		if(odczyt.isSightIntermediate && odczyt.getIntermediateSight1()!=null && odczyt.getIntermediateSight2()!=null) {
			if(lastWstecz.getBackOrForeSight1()!=null && lastWstecz.getBackOrForeSight2()!=null) {
				int firstSuperiority = lastWstecz.getBackOrForeSight1() - odczyt.getIntermediateSight1();
				int secondSuperiority = lastWstecz.getBackOrForeSight2() - odczyt.getIntermediateSight2();
				odczyt.setDifference(secondSuperiority - firstSuperiority);
			}
		}
		if(! odczyt.isSightIntermediate && odczyt.getBackOrForeSight1()!=null && odczyt.getBackOrForeSight2()!=null) {
			if(lastWstecz.getBackOrForeSight1()!=null && lastWstecz.getBackOrForeSight2()!=null) {
				int firstSuperiority = lastWstecz.getBackOrForeSight1() - odczyt.getBackOrForeSight1();
				int secondSuperiority = lastWstecz.getBackOrForeSight2() - odczyt.getBackOrForeSight2();
				odczyt.setDifference(secondSuperiority - firstSuperiority);
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
