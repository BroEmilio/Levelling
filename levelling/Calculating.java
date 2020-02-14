package levelling;

import java.math.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import levellingTable.*;

public class Calculating {
	LevellingTableModel model;
	LevellingMetaData levellingMetaData;
	CommonMethods commonMethods;
	
	
	public Calculating(LevellingTableModel model, LevellingMetaData levellingMetaData) {
		this.model = model;
		this.levellingMetaData = levellingMetaData;
		commonMethods = new CommonMethods(model);
	}

	public void calcLeveling (int calculationMode, boolean leaveCurrentValues) {
		if(leaveCurrentValues)
			complementCalc();
		else {
			if(calculationMode==LevellingMetaData.CLASSIC_MODE)
				classicCalculating();
			else creationCalculating();
		}
	}
	
	//-------------------------------------------------- CLASSIC CALCULATING ---------------------------------------------------------------------------------------------------
	
	public void classicCalculating() {		// calculate leveling in classic mode
		List<Integer> firstDeltaHeightList = getHeightDifferencesList(1);
		List<Integer> secondDeltaHeightList = getHeightDifferencesList(2);
		double levellingDisparity = calculateLevellingDisparity(firstDeltaHeightList, secondDeltaHeightList);
		Integer[] scatterArray = commonMethods.scatterDisparity(levellingDisparity, levellingMetaData.getForeSightsCount());
		int foreSightIndex = 0;
		List<Sight> data = model.getLevellingData();
		Sight sight=null;
		for(int i=0; i<data.size(); i++) {
			sight = data.get(i);
			sight.setDifference(null);
			if(! sight.isBackSight()) {		// calculate elevations for fore sights and intermediate sights
				Sight lastBackSight = commonMethods.lastBackSight(i);
				if(! sight.isLock()) {	
					double calculatedElevation;
					if(! sight.isIntermediate()) {		// fore sights
						calculatedElevation = lastBackSight.getElevation() + ((double)lastBackSight.getBackOrForeSight1()/1000) - ((double)sight.getBackOrForeSight1()/1000) - ((double)scatterArray[foreSightIndex]/1000);
						foreSightIndex++;
					}
					else {								// intermediate sights
						calculatedElevation = lastBackSight.getElevation() + ((double)lastBackSight.getBackOrForeSight1()/1000) - ((double)sight.getIntermediateSight1()/1000);
					}
					commonMethods.calculateAndSetDifferenceBetweenFirstAndSecondSurvey(sight, i);
					if(sight.getDifference() != null) {		// add half value of difference between first and second surveys
						BigDecimal halfDifference = BigDecimal.valueOf(((double)sight.getDifference()/1000)/2).setScale(4,RoundingMode.HALF_EVEN);
						halfDifference = halfDifference.setScale(3, RoundingMode.HALF_EVEN);
						calculatedElevation = calculatedElevation + halfDifference.doubleValue();
					}
					sight.setElevation(commonMethods.round(calculatedElevation,3));
					Sight nextBackSight = commonMethods.nextBackSight(i);
					if(nextBackSight != null && ! sight.isIntermediate())
						nextBackSight.setElevation(commonMethods.round(calculatedElevation,3)); // set the same elevation for next back sight as last calculated fore sight
					
				} else { // set difference for last sight(lock) and display ending window
					commonMethods.calculateAndSetDifferenceBetweenFirstAndSecondSurvey(sight, i);
					double maxDisparity = 20 * Math.sqrt((levellingMetaData.getLengthLeveling()/1000));
					showEndingWindow(commonMethods.round(levellingDisparity,2), commonMethods.round(maxDisparity,2));
				}
			}
		}
	}
	
	public List<Integer> getHeightDifferencesList(int firstOrSecondSight) {	// Generate list of height differences for first or second sights
		List<Sight> data = model.getLevellingData();
		List<Integer> deltaHighList = new ArrayList<Integer>();
		for(int i=0; i<data.size(); i++) {
			Sight sight = data.get(i);
			if(! sight.isBackSight() && ! sight.isIntermediate()) {
				Sight lastBackSight = commonMethods.lastBackSight(i);
				switch(firstOrSecondSight) {
					case 1 : {
						int delta = lastBackSight.getBackOrForeSight1() - sight.getBackOrForeSight1();
						deltaHighList.add(delta);
						break;
					}
					case 2 : {
						if(lastBackSight.getBackOrForeSight2() != null && sight.getBackOrForeSight2() != null) {
							int delta = lastBackSight.getBackOrForeSight2() - sight.getBackOrForeSight2();
							deltaHighList.add(delta);
						} else deltaHighList.add(null);	
						break;
					}
					default : deltaHighList = null;
				}
			}
		}
		return deltaHighList;
	}
	
	double calculateLevellingDisparity(List<Integer> firstDeltaHeightList, List<Integer> secondDeltaHighList) {
		int firstSum = 0;
		int secondSum = 0;
		for(int i=0; i<firstDeltaHeightList.size(); i++) {
			Integer temp = firstDeltaHeightList.get(i);
			if(temp==0 && secondDeltaHighList.get(i) != null)
				temp = secondDeltaHighList.get(i);
			
			firstSum+=temp;
		}
		for(int i=0; i<secondDeltaHighList.size(); i++) {
			Integer temp = secondDeltaHighList.get(i);
			if(temp != null && temp==0)
				temp = firstDeltaHeightList.get(i);
			if(temp != null)
			secondSum+=temp;
			else secondSum+=firstDeltaHeightList.get(i);
		}
		double startPointElevation = 0;
		double endPointElevation = 0;
		for(Sight sight:model.getLevellingData()) {
			if(sight.isLock()) {
				if(sight.isBackSight())
					startPointElevation = sight.getElevation();
				else endPointElevation = sight.getElevation();
			}
		}
		double theoreticalDisparity = (double)(endPointElevation-startPointElevation)*1000;
		return ((double)(firstSum+secondSum)/2) - theoreticalDisparity;
	}
	
	//-------------------------------------------------- CREATION CALCULATING ----------------------------------------------------------------------------
	
	public void creationCalculating() {																									// OBLICZENIA W TRYBIE KREOWANIA
		double maxClosingErrorOfLevelling = 20 * Math.sqrt((levellingMetaData.getLengthLeveling()/1000));
		int closingErrorOfLevelling = randomClosingErrorValue(maxClosingErrorOfLevelling);
		Integer[] scatterArray = commonMethods.scatterDisparity((double)closingErrorOfLevelling, levellingMetaData.getForeSightsCount());
		int foreSightIndex = 0;
		List<Sight> data = model.getLevellingData();
		Sight sight=null;
		for(int i=0; i<data.size(); i++) {
			sight=data.get(i);
			
			if(sight.isBackSight()) {											// estimating value for BACKSIGHTS
					double backSightElevation = sight.getElevation();
					double maxElevation = backSightElevation;
					double minElevation = backSightElevation;
					int minIndex = i;
					int maxIndex = i;
					int count = i;
					boolean hasIntermediateSights = false;
					ListIterator<Sight> it = data.listIterator(i+1);
					while(it.hasNext() && it.next().isBackSight()==false) { //check for min and max elevation and them index
						count++;
						Sight nextSight = it.previous();
						if(nextSight.isIntermediate()) {
							hasIntermediateSights = true;
						}
						if(nextSight.getElevation() < minElevation) {
							minElevation = nextSight.getElevation();
							minIndex = count;
						}
						if(nextSight.getElevation() > maxElevation) {
							maxElevation = nextSight.getElevation();
							maxIndex = count;
						}
						
						it.next();
					}
					double maxSuperiority = commonMethods.round((maxElevation - minElevation), 3);
					if(maxSuperiority >= 5) {
						JOptionPane.showMessageDialog(null,
						        "Za du¿a ró¿nica wysokoœci("+commonMethods.round((maxElevation-minElevation),3)+"m)"
						        + " pomiêdzy rzêdn¹ "+commonMethods.round(minElevation,3)+"m(wiersz "+(minIndex+1)+") "
						        + "a rzêdn¹ "+commonMethods.round(maxElevation,3)+"m(wiersz "+(maxIndex+1)+").\n"+
						        "Maksmymalna ró¿nica mo¿e wynosiæ do 5.000 m ( mo¿e dodaj jakieœ przejœcie ).",
						        "Za du¿e przewy¿szenie",
						        JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					int estimatedValue;
					if(hasIntermediateSights) {
						estimatedValue = randomBacksightValueBasingOnIntermediets(i, minElevation, maxElevation);
					} else 	{
						double elevationsDisparity = backSightElevation - data.get(i+1).getElevation();
						estimatedValue = randomBacksightValueBasingOnElevationsDisparity(elevationsDisparity);
					}
					sight.setBackOrForeSight1(new Integer(estimatedValue));
			}
			
			if(sight.isBackSight()==false) {		// 	calculating values for FORESIGHTS and INTERMEDIATE SIGHTS
				Sight lastBackSight = commonMethods.lastBackSight(i);
				
				double sightValueAsDouble =  ((double)lastBackSight.getBackOrForeSight1()/1000)+lastBackSight.getElevation()-sight.getElevation();
				sightValueAsDouble = commonMethods.round(sightValueAsDouble, 3);
				int sightValueAsInteger = (int)(sightValueAsDouble*1000);
				if(sight.isIntermediate()) {			// set for intermediate sights
					sight.setIntermediateSight1(sightValueAsInteger);
				} else {								// set for foresights
						if(foreSightIndex<scatterArray.length)
							sightValueAsInteger -= scatterArray[foreSightIndex];
						sight.setBackOrForeSight1(sightValueAsInteger);
						foreSightIndex++;
				}
				
			}
		}
	showEndingWindow(commonMethods.round(closingErrorOfLevelling,1), commonMethods.round(maxClosingErrorOfLevelling,1));
	}
	
	public int randomClosingErrorValue(double maxClosingError) {	// random closing error of levelling (disparity between sum of backsights and sum of foresights)
		Random random = new Random();
		int randomClosingError= 0;
		double maxErrorAsDouble = commonMethods.round((maxClosingError*0.40), 3);
		int maxErrorAsInteger = (int)(maxErrorAsDouble);
		
		randomClosingError = (random.nextInt(maxErrorAsInteger)+1) * ( random.nextBoolean() ? 1 : -1 );
		
		return randomClosingError;
	}
	
	public int randomBacksightValueBasingOnIntermediets(int index,  double minElevation, double maxElevation) {
		Random random = new Random();
		int randomSight =-1;
		Sight lastBackSight= commonMethods.lastBackSight(index+1);
		int min = (int)(Math.abs((maxElevation - lastBackSight.getElevation())*1000)); // minimum value of sight for inserted elevations
		int max = 4999 - (int)(Math.abs((lastBackSight.getElevation()-minElevation)*1000)); // // maximum value of sight for inserted elevations
			if((max-min)>1500) {	// ensuring the most natural random value
				if(min>=0 && min<=1000) {
					min = min+800;
					max = min+700;
				}
				if(min>1000 && min <=2000) {
					max = min + 1000;
				}
				if(min>2000 && min <=3000) {
					max = min + 500;
				}
			}
		
		randomSight = random.nextInt((max-min)+1) + min;
		return randomSight;
	}
	
	public int randomBacksightValueBasingOnElevationsDisparity(double elevationsDisparity) {
		Random random = new Random();
		int randomValue = -1;
		int disparity = (int)(elevationsDisparity*1000);
		
		if(disparity>=5000 || disparity<=-5000)	// disparity out of range
			return -1;
		
		if(disparity>=-1000 && disparity<=1000) {
			int rand = random.nextInt(400);
			if(disparity>0) {
				randomValue=1500 - rand;
			} else randomValue = 1500 + rand;
		}
		
		if(disparity>1000 && disparity<3000) {
			int min = 1501-(int)(Math.abs(disparity)/2);
			randomValue = random.nextInt(((1501-min)+1))+min;
		}
		
		if(-3000<disparity && disparity<-1000) {
			int max = Math.abs(disparity)+(int)(Math.abs(disparity)/2);
			randomValue = random.nextInt((max-Math.abs(disparity))+1)+Math.abs(disparity);
		}
		
		if(3000<=disparity && disparity<5000) {
			int max = 4990 - disparity;
			randomValue =  random.nextInt((max-10)+1)+10;
		}
		
		if(-5000<disparity && disparity<=-3000) {
			int min = Math.abs(disparity);
			randomValue = random.nextInt((4997-min)+1)+min;
		}
		return randomValue;
	}
	
	//----------------------------------------------- COMPLEMENT CALCULATING -------------------------------------------------------------------------
	
	public void complementCalc() {
		List<Sight> data = model.getLevellingData();
		Sight sight=null;
		for(int i=0; i<data.size(); i++) {
			sight=data.get(i);
			
			if(sight.isBackSight() && sight.getBackOrForeSight1()==null && sight.getElevation()!=null) {	// set value of backsight basing on average of intermediate sights and foresight
				ListIterator<Sight> it = data.listIterator(i+1);
				int count = 0;
				int sum = 0;
				double minElevation = sight.getElevation();
				double maxElevation = sight.getElevation();
				while(it.hasNext() && ! it.next().isBackSight()) {
					Sight nextSight = it.previous();
					if(nextSight.getElevation()<minElevation)
						minElevation = nextSight.getElevation();
					if(nextSight.getElevation()>maxElevation)
						maxElevation = nextSight.getElevation();
					if(nextSight.isIntermediate() ) {
						if(nextSight.getIntermediateSight1()!=null && nextSight.getElevation()!=null) {
							count++;
							int backSightValue = (int)commonMethods.round((nextSight.getElevation()*1000), 3) + nextSight.getIntermediateSight1() - (int)commonMethods.round((sight.getElevation()*1000), 3);
							sum += backSightValue;
						}
					} else {
						if(nextSight.getBackOrForeSight1()!=null && nextSight.getElevation()!=null) {
							count++;
							int backSightValue = (int)commonMethods.round((nextSight.getElevation()*1000), 3) + nextSight.getBackOrForeSight1() - (int)commonMethods.round((sight.getElevation()*1000), 3);
							sum += backSightValue;
						}
					}
					it.next();
				}
				if(count>0) {
					int backSightFromAverage = (int)sum/count;
					sight.setBackOrForeSight1(backSightFromAverage);
				} else {
					int backSightRandoming = randomBacksightValueBasingOnIntermediets(i, minElevation, maxElevation);
					sight.setBackOrForeSight1(backSightRandoming);
				}
			}
			
			if(!sight.isBackSight() && sight.getElevation()!=null && 
			  ((sight.isIntermediate() && sight.getIntermediateSight1()==null) || (!sight.isIntermediate() && sight.getBackOrForeSight1()==null))) {
				Sight lastBackSight = commonMethods.lastBackSight(i);
				int value = lastBackSight.getBackOrForeSight1() +  (int)commonMethods.round((lastBackSight.getElevation()*1000), 3) - (int)commonMethods.round((sight.getElevation()*1000), 3);
				if(sight.isIntermediate()) 
					sight.setIntermediateSight1(value);
				 else
					 sight.setBackOrForeSight1(value);
			}
		} // end of for
		complementSecondValues();
		classicCalculating();
	}
	
	public void complementSecondValues() {
		Random random = new Random();
		List<Sight> data = model.getLevellingData();
		Sight sight=null;
		int shift = 0;
		for(int i=0; i<data.size(); i++) {
			sight=data.get(i);
			if(sight.isBackSight()) {
				int count = 0;
				int sum = 0;
				if(sight.getBackOrForeSight1() != null && sight.getBackOrForeSight2() != null) {
					int shiftFromBackSights = sight.getBackOrForeSight1() - sight.getBackOrForeSight2();
					count++;
					sum+=shiftFromBackSights;
				}
				ListIterator<Sight> it = data.listIterator(i+1);
				while(it.hasNext() && ! it.next().isBackSight()) {
					Sight nextSight = it.previous();
					if(nextSight.isIntermediate()) {	// intermediates Sights
						if(nextSight.getIntermediateSight1()!=null && nextSight.getIntermediateSight2()!=null) {
							int shiftFromIntermediateSights = nextSight.getIntermediateSight1() - nextSight.getIntermediateSight2();
							count++;
							sum+=shiftFromIntermediateSights;
						}
					} else {	 // foreSight
						if(nextSight.getBackOrForeSight1()!=null && nextSight.getBackOrForeSight2()!=null) {
							int shiftFromForeSights = nextSight.getBackOrForeSight1() - nextSight.getBackOrForeSight2();
							count++;
							sum+=shiftFromForeSights;
						}
					}
					it.next();
				}	// end of while
				if(count>0) {
					int averageShift = (int)sum/count;
					shift = averageShift;
					if(sight.getBackOrForeSight2()==null) {
						sight.setBackOrForeSight2(sight.getBackOrForeSight1()-averageShift+((random.nextBoolean() ? 0:1) * (random.nextBoolean() ? 1:-1)));
					}
				} else shift = 0;
			}
			if(!sight.isBackSight() && shift!=0) {
				if(sight.isIntermediate() && sight.getIntermediateSight2()==null)
					sight.setIntermediateSight2(sight.getIntermediateSight1()-shift+commonMethods.randomShift());
				if(!sight.isIntermediate() && sight.getBackOrForeSight2()==null)
					sight.setBackOrForeSight2(sight.getBackOrForeSight1()-shift+commonMethods.randomShift());
			}
			
		}	// end of for
	}
	
	//------------------------------------------------ SHOW ENDING WINDOW-------------------------------------------------------
	
	public void showEndingWindow(double disparity, double maxDisparity) {
		final DecimalFormat formatterOnePlace = new DecimalFormat("#0.0");
		if(Math.abs(disparity)<=maxDisparity) {
			JOptionPane.showMessageDialog(null,
					"Uzyskana odchy³ka mieœci siê w wartoœci dopuszczalnej.\n"
			        +"D³ugoœæ niwelacji: "+formatterOnePlace.format(levellingMetaData.getLengthLeveling())+" m\n"
			        + "Dopuszczalna odchy³ka: "+formatterOnePlace.format(maxDisparity)+" mm\n\n"
			        +"<html><u>Odchy³ka uzyskana: "+formatterOnePlace.format(disparity)+" mm\n",
			        "Odchy³ka ok",
			        JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(null,
					"Odchy³ka przekracza wartoœæ dopuszczaln¹.\n"
			        +"D³ugoœæ niwelacji: "+formatterOnePlace.format(levellingMetaData.getLengthLeveling())+" m\n"
			        + "Dopuszczalna odchy³ka: "+formatterOnePlace.format(maxDisparity)+" mm\n\n"
			        +"<html><u>Odchy³ka uzyskana: "+formatterOnePlace.format(disparity)+" mm\n",
			        "Przekroczona odchy³ka",
			        JOptionPane.ERROR_MESSAGE);
		}
		
		MainFrame.secondCalcButton.setEnabled(true);	// activate button for calculating second values
	}
}
