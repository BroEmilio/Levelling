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
			
				if(! odczyt.isSightIntermediate && (odczyt.getBackOrForeSight1()!=null || odczyt.getElevation()!=null)) { 			// ZLICZENIE DO OKRE�LENIA TRYBU OBLICZE�
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
				if(odczyt.getElevation() == null) {						//SPRAWDZENIE RZ�DNYCH W PUNKCIE POCZ�TKOWYM I KO�COWYM
					JOptionPane.showMessageDialog(null,
					        "Punkt pocz�tkowy i ko�cowy niwelacji musi mie� okre�lon� rz�dn�. \n"
					        + "Odblokuj brakuj�c� rz�dn� klawiszem F5, uzupe�nij jej warto�� i ponownie wci�niej F5 aby oznaczy� j� jako punkt pocz�tkowy/ko�cowy niwelacji.",
					        "Brak rz�dnej w punkcie pocz�tkowym lub ko�cowym niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					correct = false;
				}
				if(i>0 && odczyt.isBackSight) {							//KO�COWY PUNKT NIWELACJI JEST ODCZYTEM WSTECZ
					JOptionPane.showMessageDialog(null,
					        "Odczyt wstecz nie mo�e by� ko�cowym punktem niwelacji.",
					        "B��dny punkt ko�cowy niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
		} 																				 // KONIEC PIERWSZEGO FORa
		
		lengthLeveling = ((double)wsteczCount) * 100;
		
		if(wsteczCount != wprzodCount) {					// SPRAWDZENIE CZY LICZBA ODCZYT�W WSTECZ I WPRZ�D JEST IDENTYCZNA
			JOptionPane.showMessageDialog(null,
			        "Liczba odczyt�w wstecz ("+wsteczCount+") jest "+(wsteczCount>wprzodCount ? "wi�ksza" : "mniejsza")+" ni� liczba odczyt�w wprz�d ("+wprzodCount+")." ,
			        "Nier�wna liczba odczyt�w wstecz i wprz�d",
			        JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		if(nullOdczytCount<=nullRzednaCount)			// USTALENIE TRYBU OBLICZE�
			calcType=0;
		else calcType=1;
		
		
		if(countLock != 2) {												//SPRAWDZENIE CZY OZNACZONO PUNKT POCZ�TKOWY I KO�COWY
			JOptionPane.showMessageDialog(null,
			        "Nie oznaczono punktu pocz�tkowego lub ko�cowego niwelacji. \n"
			        + "Punkt pocz�tkowy i ko�cowy niwelacji pod�wietlony jest na niebiesko. Naci�nij F5 aby go oznaczy�.",
			        "Nie oznaczono punktu pocz�tkowego lub ko�cowego niwelacji",
			        JOptionPane.INFORMATION_MESSAGE);
			correct = false;
		}
		
		
		
		for(int i=0; i<data.size(); i++) {						//DRUGA KONTROLA
			odczyt = data.get(i);
			
			if(odczyt.isBackSight() && i>0 && calcType==1) {						//SPRAWDZENIE CZY RZ�DNA WSTECZ JEST TAKA SAMA JAK WCZE�NIEJSZA RZ�DNA WPRZ�D
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
					        "Rz�dna wstecz - "+odczyt.getElevation()+" (wiersz "+(i+1)+") powinna by� taka sama jak rz�dna wprz�d - "+previousOdczyt.getElevation()+" (wiersz " +(j+2)+").",
					        "R�ne rz�dne w tym samym miejscu niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
							odczyt.setEditable(true);
					return false;
				}
				} catch(NullPointerException e) {
					JOptionPane.showMessageDialog(null,
					        "Wpisz rz�dn� punktu.",
					        "Brak rz�dnej.",
					        JOptionPane.INFORMATION_MESSAGE);
							odczyt.setEditable(true);
					return false;
				}
			}
			
			if(calcType==1 && odczyt.getElevation()==null) {		//BRAK RZ�DNEJ W TRYBIE KREOWANIA
				JOptionPane.showMessageDialog(null,
				        "Brak rz�dnej w wierszu "+(i+1)+"." ,
				        "Brak rz�dnej",
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
				        "Niew�a�ciwie wype�niony punkt  po�redni wierszu "+(i+1)+"." ,
				        "Niew�a�ciwy punkt po�redni",
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
