package levelling;
import java.util.List;
import javax.swing.JOptionPane;
import levellingTable.NiwelacjaTableModel;


public class ControlData {
	NiwelacjaTableModel model;
	public static int calcType = 0; //  0-TRYB KLASYCZNY,      1-TRYB KREOWANIA  
	public static double lengthLeveling;
	public static int wprzodCount = 0;
	
	public ControlData(NiwelacjaTableModel model) {
		this.model = model;
	}
	
	public boolean controlData() {
		boolean correct = false;
		List<Sight> data = model.getData();
		Sight odczyt;
		wprzodCount = 0;
		int countLock = 0;
		int wsteczCount=0;
		int nullOdczytCount=0;
		int nullRzednaCount=0;
		
		for(int i=0; i<data.size(); i++) {
			odczyt = data.get(i);
			
				if(! odczyt.isSightIntermediate && (odczyt.getBackOrForeSight1()!=null || odczyt.getElevation()!=null)) { 			// ZLICZENIE DO OKREŒLENIA TRYBU OBLICZEÑ
					if(odczyt.isBackSight)
						wsteczCount++;
					else wprzodCount++;
				}
				
				if(odczyt.getBackOrForeSight1()==null && odczyt.getIntermediateSight1()==null)
					nullOdczytCount++;
				if(odczyt.getElevation()==null)
					nullRzednaCount++;
			
			if(odczyt.isSightLock) {
				countLock++;
				if(odczyt.getElevation() == null) {						//SPRAWDZENIE RZÊDNYCH W PUNKCIE POCZ¥TKOWYM I KOÑCOWYM
					JOptionPane.showMessageDialog(null,
					        "Punkt pocz¹tkowy i koñcowy niwelacji musi mieæ okreœlon¹ rzêdn¹. \n"
					        + "Odblokuj brakuj¹c¹ rzêdn¹ klawiszem F5, uzupe³nij jej wartoœæ i ponownie wciœniej F5 aby oznaczyæ j¹ jako punkt pocz¹tkowy/koñcowy niwelacji.",
					        "Brak rzêdnej w punkcie pocz¹tkowym lub koñcowym niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					correct = false;
				}
				if(i>0 && odczyt.isBackSight) {							//KOÑCOWY PUNKT NIWELACJI JEST ODCZYTEM WSTECZ
					JOptionPane.showMessageDialog(null,
					        "Odczyt wstecz nie mo¿e byæ koñcowym punktem niwelacji.",
					        "B³êdny punkt koñcowy niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
		} 																				 // KONIEC PIERWSZEGO FORa
		
		lengthLeveling = ((double)wsteczCount) * 100;
		
		if(wsteczCount != wprzodCount) {					// SPRAWDZENIE CZY LICZBA ODCZYTÓW WSTECZ I WPRZÓD JEST IDENTYCZNA
			JOptionPane.showMessageDialog(null,
			        "Liczba odczytów wstecz ("+wsteczCount+") jest "+(wsteczCount>wprzodCount ? "wiêksza" : "mniejsza")+" ni¿ liczba odczytów wprzód ("+wprzodCount+")." ,
			        "Nierówna liczba odczytów wstecz i wprzód",
			        JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		if(nullOdczytCount<=nullRzednaCount)			// USTALENIE TRYBU OBLICZEÑ
			calcType=0;
		else calcType=1;
		
		
		if(countLock != 2) {												//SPRAWDZENIE CZY OZNACZONO PUNKT POCZ¥TKOWY I KOÑCOWY
			JOptionPane.showMessageDialog(null,
			        "Nie oznaczono punktu pocz¹tkowego lub koñcowego niwelacji. \n"
			        + "Punkt pocz¹tkowy i koñcowy niwelacji podœwietlony jest na niebiesko. Naciœnij F5 aby go oznaczyæ.",
			        "Nie oznaczono punktu pocz¹tkowego lub koñcowego niwelacji",
			        JOptionPane.INFORMATION_MESSAGE);
			correct = false;
		}
		
		
		
		for(int i=0; i<data.size(); i++) {						//DRUGA KONTROLA
			odczyt = data.get(i);
			
			if(odczyt.isBackSight() && i>0 && calcType==1) {						//SPRAWDZENIE CZY RZÊDNA WSTECZ JEST TAKA SAMA JAK WCZEŒNIEJSZA RZÊDNA WPRZÓD
				boolean notFound = true;
				int j = i-1;
				Sight previousOdczyt = null;
				while(j >0 && notFound) {
					previousOdczyt = data.get(j);
					j--;
					if(! previousOdczyt.isSightIntermediate && previousOdczyt.getElevation() != null)
						notFound = false;
				}
				try {
				if(! (odczyt.getElevation().equals( previousOdczyt.getElevation()))) {
					JOptionPane.showMessageDialog(null,
					        "Rzêdna wstecz - "+odczyt.getElevation()+" (wiersz "+(i+1)+") powinna byæ taka sama jak rzêdna wprzód - "+previousOdczyt.getElevation()+" (wiersz " +(j+2)+").",
					        "Ró¿ne rzêdne w tym samym miejscu niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
							odczyt.setEditable(true);
					return false;
				}
				} catch(NullPointerException e) {
					JOptionPane.showMessageDialog(null,
					        "Wpisz rzêdn¹ punktu.",
					        "Brak rzêdnej.",
					        JOptionPane.INFORMATION_MESSAGE);
							odczyt.setEditable(true);
					return false;
				}
			}
			
			if(calcType==1 && odczyt.getElevation()==null) {		//BRAK RZÊDNEJ W TRYBIE KREOWANIA
				JOptionPane.showMessageDialog(null,
				        "Brak rzêdnej w wierszu "+(i+1)+"." ,
				        "Brak rzêdnej",
				        JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			if(calcType==0 && odczyt.getBackOrForeSight1()==null && odczyt.getIntermediateSight1()==null) { // BRAK ODCZYTU W TRYBIE KLASYCZNYM
				JOptionPane.showMessageDialog(null,
				        "Brak odczytu w wierszu "+(i+1)+"." ,
				        "Brak odczytu",
				        JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			if(calcType==0 && odczyt.isSightIntermediate && (odczyt.getIntermediateSight1()==null || odczyt.getBackOrForeSight1() != null)) {
				JOptionPane.showMessageDialog(null,
				        "Niew³aœciwie wype³niony punkt  poœredni wierszu "+(i+1)+"." ,
				        "Niew³aœciwy punkt poœredni",
				        JOptionPane.INFORMATION_MESSAGE);
			return false;
			}
		}
		correct = true;
		return correct;
	}
	
	public int getCalcType() {
		return calcType;
	}
	
	public double getLengthLeveling() {
		return lengthLeveling;
	}
	
}
