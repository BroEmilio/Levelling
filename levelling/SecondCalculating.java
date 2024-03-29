package levelling;

import java.util.*;
import levellingTable.*;

public class SecondCalculating {
	LevellingTableModel model;
	Calculating mainCalculating;
	CommonMethods commonMethods;
	Random random = new Random();
	
	public SecondCalculating(LevellingTableModel model, LevellingMetaData levellingMetaData) {
		mainCalculating = new Calculating(model, levellingMetaData);
		commonMethods = new CommonMethods(model);
		this.model = model;
	}
	
	public void setSecondValues() {
		for(int i=0; i<model.getLevellingData().size(); i++)
			calculateSecondValues(i);
		mainCalculating.classicCalculating();
	}
	
	public void calculateSecondValues(int index){
		int shift;
		List<Sight> data = model.getLevellingData();
		Sight odczyt=data.get(index);
		if(odczyt.isBackSight()) {
			int wsteczOdczyt = odczyt.getBackOrForeSight1();
			int maxOdczyt =  wsteczOdczyt;
			int minOdczyt =  wsteczOdczyt;
			int deltaMax = 0;
			int deltaMin = 0;
			ListIterator<Sight> it = data.listIterator(index+1);
			while(it.hasNext() && it.next().isBackSight()==false) {
				Sight nextOdczyt = it.previous();
				it.next();
				int value;
				if(nextOdczyt.isIntermediate())
					value = nextOdczyt.getIntermediateSight1();
				else value = nextOdczyt.getBackOrForeSight1();
				
				if(value>maxOdczyt) 
					maxOdczyt = value;
				if(value<minOdczyt) 
					minOdczyt = value;
			} // end of while
			
			deltaMax = 5000 - maxOdczyt;
			deltaMin = minOdczyt;
			if(deltaMax>150 && deltaMin>150) {
				shift = randomBetween(30, 150)  * ( random.nextBoolean() ? 1 : -1 );
			} else {
				if(deltaMax>=deltaMin) {
					if(deltaMax>150)
						shift = randomBetween(30, 150);
					else if(deltaMax>30)
								shift = randomBetween(30, deltaMax);
							else shift = randomBetween(0,deltaMax);
				} else 
					if(deltaMin>150)
						shift = randomBetween(30, 150) * -1;
					else if(deltaMin>30)
								shift = randomBetween(30, deltaMin) * -1;
							else shift = randomBetween(0,deltaMin) * -1;
			}
			int secondWstecz =odczyt.getBackOrForeSight1()+shift+(randomBetween(0, 1) * (random.nextBoolean() ? 1:-1));
			odczyt.setBackOrForeSight2(secondWstecz);
			 it = data.listIterator(index+1);
			while(it.hasNext() && ! it.next().isBackSight()) {
				Sight nextOdczyt = it.previous();
				it.next();
				if(nextOdczyt.isIntermediate()) {
					int secondValue = nextOdczyt.getIntermediateSight1() + shift+commonMethods.randomShift();
					nextOdczyt.setIntermediateSight2(secondValue);
				} else {
					int secondValue = nextOdczyt.getBackOrForeSight1() + shift+commonMethods.randomShift();
					nextOdczyt.setBackOrForeSight2(secondValue);
				}
			}
		} else {  // obliczenie roznic dla odczytow wprzod i posrednich
			commonMethods.calculateAndSetDifferenceBetweenFirstAndSecondSurvey(odczyt, index);
		}
	} 
	
	public int randomBetween(int min, int max) {
		int randomInt;
		if (min > max) {
			throw new IllegalArgumentException("max must be greater than min");
		}
		randomInt = random.nextInt((max - min) + 1) + min;
		
		return randomInt;
	}
	
	public void complementSecondCalc() {
		mainCalculating.complementSecondValues();
		List<Sight> data = model.getLevellingData();
		for(int i=0; i<data.size(); i++) {
			Sight odczyt = data.get(i);
			if(odczyt.isBackSight()) {
				if(odczyt.getBackOrForeSight2()==null)
					calculateSecondValues(i);
			} else {
				if(odczyt.isIntermediate()) {
					if(odczyt.getIntermediateSight2()==null)
						calculateSecondValues(i);
				} else {
					if(odczyt.getBackOrForeSight2()==null)
						calculateSecondValues(i);
				}
			}
		} //end of for
		mainCalculating.classicCalculating();
	}
	
}
