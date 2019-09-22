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
				classicCalc();
			else creationCalc();
		}
	}
	
	//-------------------------------------------------- CLASSIC CALCULATING ---------------------------------------------------------------------------------------------------
	
	public void classicCalc() {																																						// OBLICZENIA W TRYBIE KLASYCZNYM
		List<Integer> firstDeltaHigh = getDeltaHeightList(1);
		List<Integer> secondDeltaHigh = getDeltaHeightList(2);
		double disparity = calcLevelingDisparity(firstDeltaHigh, secondDeltaHigh);
		Integer[] scatterArray = commonMethods.scatterDisparity(disparity, levellingMetaData.getForeSightsCount());
		int wprzodIndex = 0;
		List<Sight> data = model.getLevellingData();
		Sight odczyt=null;
		for(int i=0; i<data.size(); i++) {
			odczyt = data.get(i);
			odczyt.setDifference(null);
			if(! odczyt.isBackSight) {																	//odczyty wprz�d i po�rednie
				Sight lastWstecz = commonMethods.lastBackSight(i);
				if(! odczyt.isSightLock) {	
					double wprzodRzedna;
					if(! odczyt.isSightIntermediate) {														// odczyty wprzod
						wprzodRzedna = lastWstecz.getElevation() + ((double)lastWstecz.getBackOrForeSight1()/1000) - ((double)odczyt.getBackOrForeSight1()/1000) - ((double)scatterArray[wprzodIndex]/1000);
						wprzodIndex++;
					}
					else {																						// odczyty po�rednie
						wprzodRzedna = lastWstecz.getElevation() + ((double)lastWstecz.getBackOrForeSight1()/1000) - ((double)odczyt.getIntermediateSight1()/1000);
					}
					commonMethods.calcDifferences(odczyt, i);
					if(odczyt.getDifference() != null) {												// dodanie b�edu pomi�dzy dwoma po�o�eniami
						BigDecimal halfBlad = BigDecimal.valueOf(((double)odczyt.getDifference()/1000)/2).setScale(4,RoundingMode.HALF_EVEN);
						halfBlad = halfBlad.setScale(3, RoundingMode.HALF_EVEN);
						wprzodRzedna = wprzodRzedna + halfBlad.doubleValue();
						if(! odczyt.isSightIntermediate && (firstDeltaHigh.get(wprzodIndex-1)==0 || secondDeltaHigh.get(wprzodIndex-1)==0)) {
							wprzodRzedna = wprzodRzedna + halfBlad.doubleValue();
						}
					}
					odczyt.setElevation(commonMethods.round(wprzodRzedna,3));
					Sight nextWstecz = commonMethods.nextBackSight(i);
					if(nextWstecz != null && ! odczyt.isSightIntermediate)
						nextWstecz.setElevation(commonMethods.round(wprzodRzedna,3));									// przepisuje rz�dn� do nast�pnego wstecz
					
				} else {																					//ostatni wprz�d i max odchy�ka
					commonMethods.calcDifferences(odczyt, i);
					double maxDisparity = 20 * Math.sqrt((levellingMetaData.getLengthLeveling()/1000));
					showEndingWindow(commonMethods.round(disparity,2), commonMethods.round(maxDisparity,2));
				}
			}
		}		// koniec for-a
	}
	
	public List<Integer> getDeltaHeightList(int firstOrSecondSight) {	// Generate list of height differences for first or second sights
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
	
	double calcLevelingDisparity(List<Integer> firstDeltaHighList, List<Integer> secondDeltaHighList) {			// OBLICZENIE ODCHY�KI NIWELACJI
		int firstSum = 0;
		int secondSum = 0;
		for(int i=0; i<firstDeltaHighList.size(); i++) {
			Integer temp = firstDeltaHighList.get(i);
			if(temp==0 && secondDeltaHighList.get(i) != null)
				temp = secondDeltaHighList.get(i);
			
			firstSum+=temp;
		}
		for(int i=0; i<secondDeltaHighList.size(); i++) {
			Integer temp = secondDeltaHighList.get(i);
			if(temp != null && temp==0)
				temp = firstDeltaHighList.get(i);
			if(temp != null)
			secondSum+=temp;
			else secondSum+=firstDeltaHighList.get(i);
		}
		double startPoint =0;
		double endPoint = 0;
		for(Sight odczyt:model.getLevellingData()) {
			if(odczyt.isSightLock) {
				if(odczyt.isBackSight)
					startPoint = odczyt.getElevation();
				else endPoint = odczyt.getElevation();
			}
		}
		double theoreticalSuperiority = (double)(endPoint-startPoint)*1000;
		return ((double)(firstSum+secondSum)/2) - theoreticalSuperiority;
	}
	
	//-------------------------------------------------- CREATION CALCULATING ----------------------------------------------------------------------------
	
	public void creationCalc() {																									// OBLICZENIA W TRYBIE KREOWANIA
		double maxDisparity = 20 * Math.sqrt((levellingMetaData.getLengthLeveling()/1000));
		int disparity = randomDisparity(maxDisparity);
		Integer[] scatterArray = commonMethods.scatterDisparity((double)disparity, levellingMetaData.getForeSightsCount());
		int wprzodIndex = 0;
		List<Sight> data = model.getLevellingData();
		Sight odczyt=null;
		for(int i=0; i<data.size(); i++) {
			odczyt=data.get(i);
			
			if(odczyt.isBackSight) {																		// odczyty wstecz
					double wsteczRzedna = odczyt.getElevation();
					double maxRzedna = wsteczRzedna;
					double minRzedna = wsteczRzedna;
					int minIndex = i;
					int maxIndex = i;
					int count = i;
					boolean hasPosrednie = false;
					ListIterator<Sight> it = data.listIterator(i+1);
					while(it.hasNext() && it.next().isBackSight==false) {
						count++;
						Sight nextOdczyt = it.previous();
						if(nextOdczyt.isSightIntermediate) {
							hasPosrednie = true;
						}
						if(nextOdczyt.getElevation() < minRzedna) {
							minRzedna = nextOdczyt.getElevation();
							minIndex = count;
						}
						if(nextOdczyt.getElevation() > maxRzedna) {
							maxRzedna = nextOdczyt.getElevation();
							maxIndex = count;
						}
						
						it.next();
					}
					double maxSuperiority = commonMethods.round((maxRzedna - minRzedna), 3);
					if(maxSuperiority >= 5) {
						JOptionPane.showMessageDialog(null,
						        "Za du�a r�nica wysoko�ci("+commonMethods.round((maxRzedna-minRzedna),3)+"m) pomi�dzy rz�dn� "+minRzedna+"m(wiersz "+(minIndex+1)+") a rz�dn� "+maxRzedna+"m(wiersz "+(maxIndex+1)+").\n"+
						        "Maksmymalna r�nica mo�e wynosi� do 5.000 m ( mo�e dodaj jakie� przej�cie ).",
						        "Za du�e przewy�szenie",
						        JOptionPane.INFORMATION_MESSAGE);
						break;
					}
					
					int readed;
					if(hasPosrednie) {
						readed = randomWsteczForPosredni(i, minRzedna, maxRzedna);
					} else 	{
						double superiority = wsteczRzedna - data.get(i+1).getElevation();
						readed = randomOdczyt(superiority);
					}
					odczyt.setBackOrForeSight1(new Integer(readed));
			}
			
			if(odczyt.isBackSight==false) {		// odczyty wprz�d i po�rednie
				Sight lastWstecz = commonMethods.lastBackSight(i);
				
				double odczytDouble =  ((double)lastWstecz.getBackOrForeSight1()/1000)+lastWstecz.getElevation()-odczyt.getElevation();
				odczytDouble = commonMethods.round(odczytDouble, 3);
				int odczytInt = (int)(odczytDouble*1000);
				if(odczyt.isSightIntermediate) {			// po�rednie
					odczyt.setIntermediateSight1(odczytInt);
				} else {									// wprzod
						if(wprzodIndex<scatterArray.length)
							odczytInt -= scatterArray[wprzodIndex];
						odczyt.setBackOrForeSight1(odczytInt);
						wprzodIndex++;
				}
				
			}
		} // koniec for-a
	showEndingWindow(commonMethods.round(disparity,1), commonMethods.round(maxDisparity,1));
	}
	
	public int randomDisparity(double maxDisparity) {																// LOSOWANIE WARTO�CI ODCHY�KI NIWELACJI
		Random random = new Random();
		int randomDisparity= 0;
		double averageDisparity = commonMethods.round((maxDisparity*0.40), 3);
		int max = (int)(averageDisparity);
		
		randomDisparity = (random.nextInt(max)+1) * ( random.nextBoolean() ? 1 : -1 );
		
		return randomDisparity;
	}
	
	public int randomWsteczForPosredni(int index,  double minRzedna, double maxRzedna) {			// LOSOWANIE WARTO�CI ODCZYTU WSTECZ WED�UG PUNKT�W PO�REDNICH
		Random random = new Random();
		int randomOdczyt =-1;
		Sight lastWstecz= commonMethods.lastBackSight(index+1);
		int min = (int)(Math.abs((maxRzedna - lastWstecz.getElevation())*1000));
		int max = 4999 - (int)(Math.abs((lastWstecz.getElevation()-minRzedna)*1000));
			if((max-min)>1500) {													// CHECK 
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
		
		randomOdczyt = random.nextInt((max-min)+1) + min;
		return randomOdczyt;
	}
	
	public int randomOdczyt(double maxSuperiority) {									// LOSOWANIE WARTO�CI ODCZYTU WSTECZ  WED�UG PRZEWY�SZENIA
		Random random = new Random();
		int randomOdczyt= -1;
		int intSuperiority = (int)(maxSuperiority*1000);
		
		if(intSuperiority>=5000 || intSuperiority<=-5000)
			return randomOdczyt;
		
		if(intSuperiority>=-1000 && intSuperiority<=1000) {
			int rand = random.nextInt(400);
			if(intSuperiority>0) {
				randomOdczyt=1500 - rand;
			} else randomOdczyt = 1500 + rand;
		}
		
		if(intSuperiority>1000 && intSuperiority<3000) {
			int min = 1501-(int)(Math.abs(intSuperiority)/2);
			randomOdczyt = random.nextInt(((1501-min)+1))+min;
		}
		
		if(-3000<intSuperiority && intSuperiority<-1000) {
			int max = Math.abs(intSuperiority)+(int)(Math.abs(intSuperiority)/2);
			randomOdczyt = random.nextInt((max-Math.abs(intSuperiority))+1)+Math.abs(intSuperiority);
		}
		
		if(3000<=intSuperiority && intSuperiority<5000) {
			int max = 4990 - intSuperiority;
			randomOdczyt =  random.nextInt((max-10)+1)+10;
		}
		
		if(-5000<intSuperiority && intSuperiority<=-3000) {
			int min = Math.abs(intSuperiority);
			randomOdczyt = random.nextInt((4997-min)+1)+min;
		}
		return randomOdczyt;
	}
	
	//----------------------------------------------- COMPLEMENT CALCULATING -------------------------------------------------------------------------
	
	public void complementCalc() {
		List<Sight> data = model.getLevellingData();
		Sight odczyt=null;
		for(int i=0; i<data.size(); i++) {
			odczyt=data.get(i);
			
			if(odczyt.isBackSight && odczyt.getBackOrForeSight1()==null && odczyt.getElevation()!=null) {	// ustalenie odczytu wstecz na podstawie �redniej z po�rednich i wprz�d
				ListIterator<Sight> it = data.listIterator(i+1);
				int count = 0;
				int sum = 0;
				double minRzedna = odczyt.getElevation();
				double maxRzedna = odczyt.getElevation();
				while(it.hasNext() && it.next().isBackSight==false) {
					Sight nextOdczyt = it.previous();
					if(nextOdczyt.getElevation()<minRzedna)
						minRzedna = nextOdczyt.getElevation();
					if(nextOdczyt.getElevation()>maxRzedna)
						maxRzedna = nextOdczyt.getElevation();
					if(nextOdczyt.isSightIntermediate ) {
						if(nextOdczyt.getIntermediateSight1()!=null && nextOdczyt.getElevation()!=null) {
							count++;
							int wsteczOdczyt = (int)commonMethods.round((nextOdczyt.getElevation()*1000), 3) + nextOdczyt.getIntermediateSight1() - (int)commonMethods.round((odczyt.getElevation()*1000), 3);
							sum += wsteczOdczyt;
						}
					} else {
						if(nextOdczyt.getBackOrForeSight1()!=null && nextOdczyt.getElevation()!=null) {
							count++;
							int wsteczOdczyt = (int)commonMethods.round((nextOdczyt.getElevation()*1000), 3) + nextOdczyt.getBackOrForeSight1() - (int)commonMethods.round((odczyt.getElevation()*1000), 3);
							sum += wsteczOdczyt;
						}
					}
					it.next();
				}
				if(count>0) {
					int wsteczAverage = (int)sum/count;
					odczyt.setBackOrForeSight1(wsteczAverage);
				} else {
					int wsteczRandom = randomWsteczForPosredni(i, minRzedna, maxRzedna);
					odczyt.setBackOrForeSight1(wsteczRandom);
				}
			}
			
			if(!odczyt.isBackSight && odczyt.getElevation()!=null && ((odczyt.isSightIntermediate && odczyt.getIntermediateSight1()==null) || (!odczyt.isSightIntermediate && odczyt.getBackOrForeSight1()==null))) {
				Sight lastWstecz = commonMethods.lastBackSight(i);
				int value = lastWstecz.getBackOrForeSight1() +  (int)commonMethods.round((lastWstecz.getElevation()*1000), 3) - (int)commonMethods.round((odczyt.getElevation()*1000), 3);
				if(odczyt.isSightIntermediate) 
					odczyt.setIntermediateSight1(value);
				 else
					 odczyt.setBackOrForeSight1(value);
			}
		} // end of for
		complementSecondValues();
		classicCalc();
	}
	
	public void complementSecondValues() {
		Random random = new Random();
		List<Sight> data = model.getLevellingData();
		Sight odczyt=null;
		int shift = 0;
		for(int i=0; i<data.size(); i++) {
			odczyt=data.get(i);
			if(odczyt.isBackSight) {
				int count = 0;
				int sum = 0;
				if(odczyt.getBackOrForeSight1() != null && odczyt.getBackOrForeSight2() != null) {
					int shiftWs = odczyt.getBackOrForeSight1() - odczyt.getBackOrForeSight2();
					count++;
					sum+=shiftWs;
				}
				ListIterator<Sight> it = data.listIterator(i+1);
				while(it.hasNext() && it.next().isBackSight==false) {
					Sight nextOdczyt = it.previous();
					if(nextOdczyt.isSightIntermediate) {
						if(nextOdczyt.getIntermediateSight1()!=null && nextOdczyt.getIntermediateSight2()!=null) {
							int shiftP = nextOdczyt.getIntermediateSight1() - nextOdczyt.getIntermediateSight2();
							count++;
							sum+=shiftP;
						}
					} else {
						if(nextOdczyt.getBackOrForeSight1()!=null && nextOdczyt.getBackOrForeSight2()!=null) {
							int shiftWp = nextOdczyt.getBackOrForeSight1() - nextOdczyt.getBackOrForeSight2();
							count++;
							sum+=shiftWp;
						}
					}
					it.next();
				}	// end of while
				if(count>0) {
					int averageShift = (int)sum/count;
					shift = averageShift;
					if(odczyt.getBackOrForeSight2()==null) {
						odczyt.setBackOrForeSight2(odczyt.getBackOrForeSight1()-averageShift+((random.nextBoolean() ? 0:1) * (random.nextBoolean() ? 1:-1)));
					}
				} else shift= 0;
			}
			if(!odczyt.isBackSight && shift!=0) {
				if(odczyt.isSightIntermediate && odczyt.getIntermediateSight2()==null)
					odczyt.setIntermediateSight2(odczyt.getIntermediateSight1()-shift+randomShift());
				if(!odczyt.isSightIntermediate && odczyt.getBackOrForeSight2()==null)
					odczyt.setBackOrForeSight2(odczyt.getBackOrForeSight1()-shift+randomShift());
			}
			
		}//end of for
	}
	
	public int randomShift() {
		Random random = new Random();
		int randomShift = 0;
		int temp;
		temp=random.nextInt(100);
		if(temp<=20)
			randomShift=0;
		if(temp>20 && temp<=70)
			randomShift=1;
		if(temp>70 && temp<=100)
			randomShift=2;
		
		randomShift = randomShift  * ( random.nextBoolean() ? 1 : -1 );
		return randomShift;
	}
	
	//------------------------------------------------ SHOW ENDING WINDOW-------------------------------------------------------
	
	public void showEndingWindow(double disparity, double maxDisparity) {								// WY�WIETLENIE OKNA PODSUMOWANIA NIWELACJI
		final DecimalFormat formatterOnePlace = new DecimalFormat("#0.0");
		if(Math.abs(disparity)<=maxDisparity) {
			JOptionPane.showMessageDialog(null,
					"Uzyskana odchy�ka mie�ci si� w warto�ci dopuszczalnej.\n"
			        +"D�ugo�� niwelacji: "+formatterOnePlace.format(levellingMetaData.getLengthLeveling())+" m\n"
			        + "Dopuszczalna odchy�ka: "+formatterOnePlace.format(maxDisparity)+" mm\n\n"
			        +"<html><u>Odchy�ka uzyskana: "+formatterOnePlace.format(disparity)+" mm\n",
			        "Odchy�ka ok",
			        JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			JOptionPane.showMessageDialog(null,
					"Odchy�ka przekracza warto�� dopuszczaln�.\n"
			        +"D�ugo�� niwelacji: "+formatterOnePlace.format(levellingMetaData.getLengthLeveling())+" m\n"
			        + "Dopuszczalna odchy�ka: "+formatterOnePlace.format(maxDisparity)+" mm\n\n"
			        +"<html><u>Odchy�ka uzyskana: "+formatterOnePlace.format(disparity)+" mm\n",
			        "Przekroczona odchy�ka",
			        JOptionPane.ERROR_MESSAGE);
		}
		
		MainFrame.secondCalcButton.setEnabled(true);
		commonMethods.scatterDisparity(disparity, levellingMetaData.getForeSightsCount());
	}
}
