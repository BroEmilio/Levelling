package levelling;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.swing.JOptionPane;

import levellingTable.CellRendererBlad;
import levellingTable.NiwelacjaTableModel;


public class Calculating {
	NiwelacjaTableModel model;
	private static final DecimalFormat formatterOnePlace = new DecimalFormat("#0.0");
	
	public Calculating(NiwelacjaTableModel model) {
		this.model = model;
	}

	public void calcLeveling (int calcType, boolean leaveCurrentValues) {
		if(leaveCurrentValues)
			complementCalc();
		else {
			if(calcType==0)
				classicCalc();
			else creationCalc();
		}
	}
	
	// ---------------------------------------------------------------- COMMON METHODS ----------------------------------------------------------------------------------
	
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_EVEN);
	    return bd.doubleValue();
	}
	
	public static int roundToInt(double value) {
		 DecimalFormat df = new DecimalFormat("#0");
		 df.setRoundingMode(RoundingMode.HALF_EVEN);
		 return new Integer(df.format(value));
		 
	}
	
	Sight lastWstecz(int index) {													// ODSZUKANIE OSTATNIEGO ODCZYTU WSTECZ
		Sight lastWstecz=null;
		List<Sight> data = model.getData();
		ListIterator<Sight> it = data.listIterator(index);
		while(lastWstecz == null && it.hasPrevious()) {
			if(it.previous().isBackSight)
				lastWstecz=it.next();
		}
		return lastWstecz;
	}
	
	Sight nextWstecz(int index) {												// ODSZUKANIE NASTÊPNEGO ODCZYTU WSTECZ
		Sight nextWstecz=null;
		List<Sight> data = model.getData();
		ListIterator<Sight> it = data.listIterator(index);
		while(nextWstecz == null && it.hasNext()) {
			if(it.next().isBackSight)
				nextWstecz=it.previous();
		}
		return nextWstecz;
	}
	
	
	public static void updateWsteczWprzod(NiwelacjaTableModel model) {		// AKTUALIZACJA MODELU TABELI
		List<Sight> data = model.getData();
		Boolean isWstecz = true;
		for(int i=0; i<data.size(); i++) {
			Sight odczyt = data.get(i);
			if((odczyt.getBackOrForeSight1() != null || odczyt.getElevation() != null) && ! odczyt.isSightIntermediate) {
				odczyt.setAsBackSight(isWstecz);
				if(isWstecz) {
					odczyt.setEditable(false);
					if(i==0)
						odczyt.setEditable(true);
				} else odczyt.setEditable(true);
			isWstecz = ! isWstecz;
			}
			if(odczyt.getIntermediateSight1() != null)
				odczyt.setIntermediate(true);
		}
	}
	
	public void calcDifferences(Sight odczyt, int index) {								// OBLICZENIE RÓ¯NICY MIÊDZY DWOMA PO£O¯ENIAMI
		Sight lastWstecz=lastWstecz(index);
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
	
	public Integer[] scatterDisparity(double disparityAsDouble,int wprzodCount) {			// OBLICZENIE TABLICY ROZPROSZENIA ODCHY£KI NIWELACJI
		Integer[] scatterArray = new Integer[wprzodCount];
		double averageDisparity = disparityAsDouble / wprzodCount;
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
	
	public List<Integer> getDeltaHighList(int firstOrSecond) {								// WYGENEROWANIE LISTY PRZEWY¯SZEÑ DLA PIERWSZYCH LUB DRUGICH ODCZYTÓW
		List<Sight> data = model.getData();
		List<Integer> deltaHighList = new ArrayList<Integer>();
		switch(firstOrSecond) {
		case 1 : {
				for(int i=0; i<data.size(); i++) {
					Sight odczyt = data.get(i);
					if(!odczyt.isBackSight && ! odczyt.isSightIntermediate) {
						Sight lastWstecz = lastWstecz(i);
						int delta = lastWstecz.getBackOrForeSight1() - odczyt.getBackOrForeSight1();
						deltaHighList.add(delta);
					}
				}
				break;
			}
		case 2 : {
			for(int i=0; i<data.size(); i++) {
				Sight odczyt = data.get(i);
				if(!odczyt.isBackSight && ! odczyt.isSightIntermediate) {
					Sight lastWstecz = lastWstecz(i);
					if(lastWstecz.getBackOrForeSight2() != null && odczyt.getBackOrForeSight2() != null) {
						int delta = lastWstecz.getBackOrForeSight2() - odczyt.getBackOrForeSight2();
						deltaHighList.add(delta);
					} else deltaHighList.add(null);
				}
			}
			break;
		}
		default : deltaHighList = null;
		}
		
		return deltaHighList;
	}
	
	double calcLevelingDisparity(List<Integer> firstDeltaHighList, List<Integer> secondDeltaHighList) {			// OBLICZENIE ODCHY£KI NIWELACJI
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
		for(Sight odczyt:model.getData()) {
			if(odczyt.isSightLock) {
				if(odczyt.isBackSight)
					startPoint = odczyt.getElevation();
				else endPoint = odczyt.getElevation();
			}
		}
		double theoreticalSuperiority = (double)(endPoint-startPoint)*1000;
		return ((double)(firstSum+secondSum)/2) - theoreticalSuperiority;
	}
	
	public void showEndingWindow(double disparity, double maxDisparity) {								// WYŒWIETLENIE OKNA PODSUMOWANIA NIWELACJI
		if(Math.abs(disparity)<=maxDisparity) {
			JOptionPane.showMessageDialog(null,
					"Uzyskana odchy³ka mieœci siê w wartoœci dopuszczalnej.\n"
			        +"D³ugoœæ niwelacji: "+formatterOnePlace.format(ControlData.lengthLeveling)+" m\n"
			        + "Dopuszczalna odchy³ka: "+formatterOnePlace.format(maxDisparity)+" mm\n\n"
			        +"<html><u>Odchy³ka uzyskana: "+formatterOnePlace.format(disparity)+" mm\n",
			        "Odchy³ka ok",
			        JOptionPane.INFORMATION_MESSAGE);
			CellRendererBlad.isOverRange=false;
		}
		else {
			JOptionPane.showMessageDialog(null,
					"Odchy³ka przekracza wartoœæ dopuszczaln¹.\n"
			        +"D³ugoœæ niwelacji: "+formatterOnePlace.format(ControlData.lengthLeveling)+" m\n"
			        + "Dopuszczalna odchy³ka: "+formatterOnePlace.format(maxDisparity)+" mm\n\n"
			        +"<html><u>Odchy³ka uzyskana: "+formatterOnePlace.format(disparity)+" mm\n",
			        "Przekroczona odchy³ka",
			        JOptionPane.ERROR_MESSAGE);
			CellRendererBlad.isOverRange = true;
		}
		
		MainFrame.secondCalcButton.setEnabled(true);
		scatterDisparity(disparity, ControlData.wprzodCount);
	}
	
	//-------------------------------------------------------------------- CLASSIC CALCULATING ---------------------------------------------------------------------------------------------------
	
	public void classicCalc() {																																						// OBLICZENIA W TRYBIE KLASYCZNYM
		List<Integer> firstDeltaHigh = getDeltaHighList(1);
		List<Integer> secondDeltaHigh = getDeltaHighList(2);
		double disparity = calcLevelingDisparity(firstDeltaHigh, secondDeltaHigh);
		Integer[] scatterArray = scatterDisparity(disparity, ControlData.wprzodCount);
		int wprzodIndex = 0;
		List<Sight> data = model.getData();
		Sight odczyt=null;
		for(int i=0; i<data.size(); i++) {
			odczyt = data.get(i);
			odczyt.setDifference(null);
			if(! odczyt.isBackSight) {																	//odczyty wprzód i poœrednie
				Sight lastWstecz = lastWstecz(i);
				if(! odczyt.isSightLock) {	
					double wprzodRzedna;
					if(! odczyt.isSightIntermediate) {														// odczyty wprzod
						wprzodRzedna = lastWstecz.getElevation() + ((double)lastWstecz.getBackOrForeSight1()/1000) - ((double)odczyt.getBackOrForeSight1()/1000) - ((double)scatterArray[wprzodIndex]/1000);
						wprzodIndex++;
					}
					else {																						// odczyty poœrednie
						wprzodRzedna = lastWstecz.getElevation() + ((double)lastWstecz.getBackOrForeSight1()/1000) - ((double)odczyt.getIntermediateSight1()/1000);
					}
					calcDifferences(odczyt, i);
					if(odczyt.getDifference() != null) {												// dodanie b³edu pomiêdzy dwoma po³o¿eniami
						BigDecimal halfBlad = BigDecimal.valueOf(((double)odczyt.getDifference()/1000)/2).setScale(4,RoundingMode.HALF_EVEN);
						halfBlad = halfBlad.setScale(3, RoundingMode.HALF_EVEN);
						wprzodRzedna = wprzodRzedna + halfBlad.doubleValue();
						if(! odczyt.isSightIntermediate && (firstDeltaHigh.get(wprzodIndex-1)==0 || secondDeltaHigh.get(wprzodIndex-1)==0)) {
							wprzodRzedna = wprzodRzedna + halfBlad.doubleValue();
						}
					}
					odczyt.setElevation(round(wprzodRzedna,3));
					Sight nextWstecz = nextWstecz(i);
					if(nextWstecz != null && ! odczyt.isSightIntermediate)
						nextWstecz.setElevation(round(wprzodRzedna,3));									// przepisuje rzêdn¹ do nastêpnego wstecz
					
				} else {																					//ostatni wprzód i max odchy³ka
					calcDifferences(odczyt, i);
					double maxDisparity = 20 * Math.sqrt((ControlData.lengthLeveling/1000));
					showEndingWindow(round(disparity,2), round(maxDisparity,2));
				}
			}
		}		// koniec for-a
	}
	
	//------------------------------------------------------------------------- CREATION CALCULATING ----------------------------------------------------------------------------
	
	public int randomOdczyt(double maxSuperiority) {									// LOSOWANIE WARTOŒCI ODCZYTU WSTECZ  WED£UG PRZEWY¯SZENIA
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
	
	public int randomWsteczForPosredni(int index,  double minRzedna, double maxRzedna) {			// LOSOWANIE WARTOŒCI ODCZYTU WSTECZ WED£UG PUNKTÓW POŒREDNICH
		Random random = new Random();
		int randomOdczyt =-1;
		Sight lastWstecz= lastWstecz(index+1);
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
	
	public int randomDisparity(double maxDisparity) {																// LOSOWANIE WARTOŒCI ODCHY£KI NIWELACJI
		Random random = new Random();
		int randomDisparity= 0;
		double averageDisparity = round((maxDisparity*0.40), 3);
		int max = (int)(averageDisparity);
		
		randomDisparity = (random.nextInt(max)+1) * ( random.nextBoolean() ? 1 : -1 );
		
		return randomDisparity;
	}
	
	public void creationCalc() {																									// OBLICZENIA W TRYBIE KREOWANIA
		double maxDisparity = 20 * Math.sqrt((ControlData.lengthLeveling/1000));
		int disparity = randomDisparity(maxDisparity);
		Integer[] scatterArray = scatterDisparity((double)disparity, ControlData.wprzodCount);
		int wprzodIndex = 0;
		List<Sight> data = model.getData();
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
					double maxSuperiority = round((maxRzedna - minRzedna), 3);
					if(maxSuperiority >= 5) {
						JOptionPane.showMessageDialog(null,
						        "Za du¿a ró¿nica wysokoœci("+round((maxRzedna-minRzedna),3)+"m) pomiêdzy rzêdn¹ "+minRzedna+"m(wiersz "+(minIndex+1)+") a rzêdn¹ "+maxRzedna+"m(wiersz "+(maxIndex+1)+").\n"+
						        "Maksmymalna ró¿nica mo¿e wynosiæ do 5.000 m ( mo¿e dodaj jakieœ przejœcie ).",
						        "Za du¿e przewy¿szenie",
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
			
			if(odczyt.isBackSight==false) {		// odczyty wprzód i poœrednie
				Sight lastWstecz = lastWstecz(i);
				
				double odczytDouble =  ((double)lastWstecz.getBackOrForeSight1()/1000)+lastWstecz.getElevation()-odczyt.getElevation();
				odczytDouble = round(odczytDouble, 3);
				int odczytInt = (int)(odczytDouble*1000);
				if(odczyt.isSightIntermediate) {			// poœrednie
					odczyt.setIntermediateSight1(odczytInt);
				} else {									// wprzod
						if(wprzodIndex<scatterArray.length)
							odczytInt -= scatterArray[wprzodIndex];
						odczyt.setBackOrForeSight1(odczytInt);
						wprzodIndex++;
				}
				
			}
		} // koniec for-a
	showEndingWindow(round(disparity,1), round(maxDisparity,1));
	}
	
	//--------------------------------------------------------------------- COMPLEMENT CALCULATING -------------------------------------------------------------------------
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
	
	public void complementCalc() {
		List<Sight> data = model.getData();
		Sight odczyt=null;
		for(int i=0; i<data.size(); i++) {
			odczyt=data.get(i);
			
			if(odczyt.isBackSight && odczyt.getBackOrForeSight1()==null && odczyt.getElevation()!=null) {	// ustalenie odczytu wstecz na podstawie œredniej z poœrednich i wprzód
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
							int wsteczOdczyt = (int)round((nextOdczyt.getElevation()*1000), 3) + nextOdczyt.getIntermediateSight1() - (int)round((odczyt.getElevation()*1000), 3);
							sum += wsteczOdczyt;
						}
					} else {
						if(nextOdczyt.getBackOrForeSight1()!=null && nextOdczyt.getElevation()!=null) {
							count++;
							int wsteczOdczyt = (int)round((nextOdczyt.getElevation()*1000), 3) + nextOdczyt.getBackOrForeSight1() - (int)round((odczyt.getElevation()*1000), 3);
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
				Sight lastWstecz = lastWstecz(i);
				int value = lastWstecz.getBackOrForeSight1() +  (int)round((lastWstecz.getElevation()*1000), 3) - (int)round((odczyt.getElevation()*1000), 3);
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
		List<Sight> data = model.getData();
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
}
