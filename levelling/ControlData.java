package levelling;

import java.util.List;
import javax.swing.JOptionPane;
import levellingTable.LevellingTableModel;

public class ControlData {
	LevellingTableModel model;
	public static final int CLASSIC_MODE = 0;  // calculating mode for inserted values of back and fore sights
	public static final int CREATION_MODE = 1; // calculate values for back and fore sights leaning on inserted elevations
	public static int calculatingMode = CLASSIC_MODE;   
	public static double lengthLeveling;
	public static int foreSightsCount = 0;
	
	public ControlData(LevellingTableModel model) {
		this.model = model;
	}
	
	public boolean controlData() {		// count and first check of data			
		boolean isCorrect = false;
		List<Sight> data = model.getLevellingData();
		Sight sight;
		foreSightsCount = 0;
		int lockSightsCount = 0;
		int backSightsCount=0;
		int emptyFirstSightsCount=0;
		int emptyElevationCount=0;
		
		for(int i=0; i<data.size(); i++) {
			sight = data.get(i);
			
				if(! sight.isIntermediate() && (sight.getBackOrForeSight1()!=null || sight.getElevation()!=null)) { // count back and fore sights 
					if(sight.isBackSight)
						backSightsCount++;
					else foreSightsCount++;
				}
				
				if(sight.getBackOrForeSight1()==null && sight.getIntermediateSight1()==null) // count empty sights and empty elevations to determine calculating mode 
					emptyFirstSightsCount++;
				if(sight.getElevation()==null)
					emptyElevationCount++;
			
			if(sight.isSightLock) {
				lockSightsCount++;
				if(sight.getElevation() == null) {						//SPRAWDZENIE RZÊDNYCH W PUNKCIE POCZ¥TKOWYM I KOÑCOWYM
					JOptionPane.showMessageDialog(null,
					        "Punkt pocz¹tkowy i koñcowy niwelacji musi mieæ okreœlon¹ rzêdn¹. \n"
					        + "Odblokuj brakuj¹c¹ rzêdn¹ klawiszem F5, uzupe³nij jej wartoœæ i ponownie wciœniej F5 aby oznaczyæ j¹ jako punkt pocz¹tkowy/koñcowy niwelacji.",
					        "Brak rzêdnej w punkcie pocz¹tkowym lub koñcowym niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					isCorrect = false;
				}
				if(i>0 && sight.isBackSight) {							//KOÑCOWY PUNKT NIWELACJI JEST ODCZYTEM WSTECZ
					JOptionPane.showMessageDialog(null,
					        "Odczyt wstecz nie mo¿e byæ koñcowym punktem niwelacji.",
					        "B³êdny punkt koñcowy niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
		} 																				 // KONIEC PIERWSZEGO FORa
		
		lengthLeveling = ((double)backSightsCount) * 100;
		
		if(backSightsCount != foreSightsCount) {					// SPRAWDZENIE CZY LICZBA ODCZYTÓW WSTECZ I WPRZÓD JEST IDENTYCZNA
			JOptionPane.showMessageDialog(null,
			        "Liczba odczytów wstecz ("+backSightsCount+") jest "+(backSightsCount>foreSightsCount ? "wiêksza" : "mniejsza")+" ni¿ liczba odczytów wprzód ("+foreSightsCount+")." ,
			        "Nierówna liczba odczytów wstecz i wprzód",
			        JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		if(emptyFirstSightsCount<=emptyElevationCount)			// USTALENIE TRYBU OBLICZEÑ
			calculatingMode=CLASSIC_MODE;
		else calculatingMode=CREATION_MODE;
		
		
		if(lockSightsCount != 2) {												//SPRAWDZENIE CZY OZNACZONO PUNKT POCZ¥TKOWY I KOÑCOWY
			JOptionPane.showMessageDialog(null,
			        "Nie oznaczono punktu pocz¹tkowego lub koñcowego niwelacji. \n"
			        + "Punkt pocz¹tkowy i koñcowy niwelacji podœwietlony jest na niebiesko. Naciœnij F5 aby go oznaczyæ.",
			        "Nie oznaczono punktu pocz¹tkowego lub koñcowego niwelacji",
			        JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		
		
		for(int i=0; i<data.size(); i++) {						//DRUGA KONTROLA
			sight = data.get(i);
			
			if(sight.isBackSight() && i>0 && calculatingMode==CREATION_MODE) {						//SPRAWDZENIE CZY RZÊDNA WSTECZ JEST TAKA SAMA JAK WCZEŒNIEJSZA RZÊDNA WPRZÓD
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
				if(! (sight.getElevation().equals( previousOdczyt.getElevation()))) {
					JOptionPane.showMessageDialog(null,
					        "Rzêdna wstecz - "+sight.getElevation()+" (wiersz "+(i+1)+") powinna byæ taka sama jak rzêdna wprzód - "+previousOdczyt.getElevation()+" (wiersz " +(j+2)+").",
					        "Ró¿ne rzêdne w tym samym miejscu niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
							sight.setEditable(true);
					return false;
				}
				} catch(NullPointerException e) {
					JOptionPane.showMessageDialog(null,
					        "Wpisz rzêdn¹ punktu.",
					        "Brak rzêdnej.",
					        JOptionPane.INFORMATION_MESSAGE);
							sight.setEditable(true);
					return false;
				}
			}
			
			if(calculatingMode==CREATION_MODE && sight.getElevation()==null) {		//BRAK RZÊDNEJ W TRYBIE KREOWANIA
				JOptionPane.showMessageDialog(null,
				        "Brak rzêdnej w wierszu "+(i+1)+"." ,
				        "Brak rzêdnej",
				        JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			if(calculatingMode==CLASSIC_MODE && sight.getBackOrForeSight1()==null && sight.getIntermediateSight1()==null) { // BRAK ODCZYTU W TRYBIE KLASYCZNYM
				JOptionPane.showMessageDialog(null,
				        "Brak odczytu w wierszu "+(i+1)+"." ,
				        "Brak odczytu",
				        JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			if(calculatingMode==CLASSIC_MODE && sight.isSightIntermediate && (sight.getIntermediateSight1()==null || sight.getBackOrForeSight1() != null)) {
				JOptionPane.showMessageDialog(null,
				        "Niew³aœciwie wype³niony punkt  poœredni wierszu "+(i+1)+"." ,
				        "Niew³aœciwy punkt poœredni",
				        JOptionPane.INFORMATION_MESSAGE);
			return false;
			}
		}
		isCorrect = true;
		return isCorrect;
	}
	
	public int getCalculatingMode() {
		return calculatingMode;
	}
	
	public double getLengthLeveling() {
		return lengthLeveling;
	}
	
}
