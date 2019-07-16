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
	
	public boolean controlData() {					
		List<Sight> data = model.getLevellingData();
		Sight sight;
		foreSightsCount = 0;
		int lockSightsCount = 0;
		int backSightsCount=0;
		int emptyFirstSightsCount=0;
		int emptyElevationCount=0;
		
		for(int i=0; i<data.size(); i++) {	// counting sights and check is elevations in benchmarks
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
			
			if(sight.isLock()) {
				lockSightsCount++;
				if(sight.getElevation() == null) {		// check is known elevations in first and last benchmarks
					JOptionPane.showMessageDialog(null,
					        "Punkt pocz�tkowy i ko�cowy niwelacji musi mie� okre�lon� rz�dn�. \n"
					        + "Odblokuj brakuj�c� rz�dn� klawiszem F5, uzupe�nij jej warto�� i "
					        + "ponownie wci�niej F5 aby oznaczy� j� jako punkt pocz�tkowy/ko�cowy niwelacji.",
					        "Brak rz�dnej w punkcie pocz�tkowym lub ko�cowym niwelacji",
					        JOptionPane.INFORMATION_MESSAGE);
					return false;
				}
			}
		} 									// end of counting
		
		lengthLeveling = ((double)backSightsCount) * 100;	
		
		if(backSightsCount != foreSightsCount) {		// check is backSightsCount equal foreSightsCount
			JOptionPane.showMessageDialog(null,
			        "Liczba odczyt�w wstecz ("+backSightsCount+") jest "+(backSightsCount>foreSightsCount ? "wi�ksza" : "mniejsza")+" ni� liczba odczyt�w wprz�d ("+foreSightsCount+")." ,
			        "Nier�wna liczba odczyt�w wstecz i wprz�d",
			        JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		if(emptyFirstSightsCount<=emptyElevationCount)	// determine calculating mode
			calculatingMode=CLASSIC_MODE;
		else calculatingMode=CREATION_MODE;
		
		
		if(lockSightsCount != 2) {			// check is exist first and last benchmark
			JOptionPane.showMessageDialog(null,
			        "Nie oznaczono punktu pocz�tkowego lub ko�cowego niwelacji. \n"
			        + "Punkt pocz�tkowy i ko�cowy niwelacji pod�wietlony jest na niebiesko. Naci�nij F5 aby go oznaczy�.",
			        "Nie oznaczono punktu pocz�tkowego lub ko�cowego niwelacji",
			        JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		
		
		
		for(int i=0; i<data.size(); i++) {		// second control
			sight = data.get(i);
			
			if(sight.isBackSight() && i>0 && calculatingMode==CREATION_MODE) {	//check is elevation in backsight is the same as elevation in last foresight
				boolean notFound = true;
				int previousIndex = i-1;
				Sight previousSight = null;
				while(previousIndex > 0 && notFound) {
					previousSight = data.get(previousIndex);
					previousIndex--;
					if(! previousSight.isIntermediate() && previousSight.getElevation() != null)
						notFound = false;
				}
				try {
				if(! (sight.getElevation().equals( previousSight.getElevation()))) {
					JOptionPane.showMessageDialog(null,
					        "Rz�dna wstecz - "+sight.getElevation()+" (wiersz "+(i+1)+") powinna by� taka sama jak rz�dna wprz�d - "+previousSight.getElevation()+" (wiersz " +(previousIndex+2)+").",
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
			
			if(calculatingMode==CREATION_MODE && sight.getElevation()==null) {		//empty elevation in row (CREATION MODE)
				JOptionPane.showMessageDialog(null,
				        "Brak rz�dnej w wierszu "+(i+1)+"." ,
				        "Brak rz�dnej",
				        JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			if(calculatingMode==CLASSIC_MODE && sight.getBackOrForeSight1()==null && sight.getIntermediateSight1()==null) { //empty first sight in row (CLASSIC MODE)
				JOptionPane.showMessageDialog(null,
				        "Brak odczytu w wierszu "+(i+1)+"." ,
				        "Brak odczytu",
				        JOptionPane.INFORMATION_MESSAGE);
				return false;
			}
			
			if(calculatingMode==CLASSIC_MODE && sight.isIntermediate() && (sight.getIntermediateSight1()==null || sight.getBackOrForeSight1() != null)) {
				JOptionPane.showMessageDialog(null,
				        "Niew�a�ciwie wype�niony punkt  po�redni wierszu "+(i+1)+"." ,
				        "Niew�a�ciwy punkt po�redni",
				        JOptionPane.INFORMATION_MESSAGE);
			return false;
			}
		}
		return true;	
	}
	
	public int getCalculatingMode() {
		return calculatingMode;
	}
	
	public double getLengthLeveling() {
		return lengthLeveling;
	}
	
}
