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
				if(sight.getElevation() == null) {						//SPRAWDZENIE RZ�DNYCH W PUNKCIE POCZ�TKOWYM I KO�COWYM
					JOptionPane.showMessageDialog(null,
					        "Punkt pocz�tkowy i ko�cowy niwelacji musi mie� okre�lon� rz�dn�. \n"
					        + "Odblokuj brakuj�c� rz�dn� klawiszem F5, uzupe�nij jej warto�� i ponownie wci�niej F5 aby oznaczy� j� jako punkt pocz�tkowy/ko�cowy niwelacji.",
					        "Brak rz�dnej w punkcie pocz�tkowym lub ko�cowym niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					isCorrect = false;
				}
				if(i>0 && sight.isBackSight) {							//KO�COWY PUNKT NIWELACJI JEST ODCZYTEM WSTECZ
					JOptionPane.showMessageDialog(null,
					        "Odczyt wstecz nie mo�e by� ko�cowym punktem niwelacji.",
					        "B��dny punkt ko�cowy niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
		} 																				 // KONIEC PIERWSZEGO FORa
		
		lengthLeveling = ((double)backSightsCount) * 100;
		
		if(backSightsCount != foreSightsCount) {					// SPRAWDZENIE CZY LICZBA ODCZYT�W WSTECZ I WPRZ�D JEST IDENTYCZNA
			JOptionPane.showMessageDialog(null,
			        "Liczba odczyt�w wstecz ("+backSightsCount+") jest "+(backSightsCount>foreSightsCount ? "wi�ksza" : "mniejsza")+" ni� liczba odczyt�w wprz�d ("+foreSightsCount+")." ,
			        "Nier�wna liczba odczyt�w wstecz i wprz�d",
			        JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		if(emptyFirstSightsCount<=emptyElevationCount)			// USTALENIE TRYBU OBLICZE�
			calculatingMode=CLASSIC_MODE;
		else calculatingMode=CREATION_MODE;
		
		
		if(lockSightsCount != 2) {												//SPRAWDZENIE CZY OZNACZONO PUNKT POCZ�TKOWY I KO�COWY
			JOptionPane.showMessageDialog(null,
			        "Nie oznaczono punktu pocz�tkowego lub ko�cowego niwelacji. \n"
			        + "Punkt pocz�tkowy i ko�cowy niwelacji pod�wietlony jest na niebiesko. Naci�nij F5 aby go oznaczy�.",
			        "Nie oznaczono punktu pocz�tkowego lub ko�cowego niwelacji",
			        JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		
		
		for(int i=0; i<data.size(); i++) {						//DRUGA KONTROLA
			sight = data.get(i);
			
			if(sight.isBackSight() && i>0 && calculatingMode==CREATION_MODE) {						//SPRAWDZENIE CZY RZ�DNA WSTECZ JEST TAKA SAMA JAK WCZE�NIEJSZA RZ�DNA WPRZ�D
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
					        "Rz�dna wstecz - "+sight.getElevation()+" (wiersz "+(i+1)+") powinna by� taka sama jak rz�dna wprz�d - "+previousOdczyt.getElevation()+" (wiersz " +(j+2)+").",
					        "R�ne rz�dne w tym samym miejscu niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
							sight.setEditable(true);
					return false;
				}
				} catch(NullPointerException e) {
					JOptionPane.showMessageDialog(null,
					        "Wpisz rz�dn� punktu.",
					        "Brak rz�dnej.",
					        JOptionPane.INFORMATION_MESSAGE);
							sight.setEditable(true);
					return false;
				}
			}
			
			if(calculatingMode==CREATION_MODE && sight.getElevation()==null) {		//BRAK RZ�DNEJ W TRYBIE KREOWANIA
				JOptionPane.showMessageDialog(null,
				        "Brak rz�dnej w wierszu "+(i+1)+"." ,
				        "Brak rz�dnej",
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
				        "Niew�a�ciwie wype�niony punkt  po�redni wierszu "+(i+1)+"." ,
				        "Niew�a�ciwy punkt po�redni",
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
