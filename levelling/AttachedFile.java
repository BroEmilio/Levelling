package levelling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import levellingTable.LevellingTableModel;

public class AttachedFile {
	
	File file;
	boolean isFileAttached;
	Map<String, Double> elevationsMap;
	LevellingTableModel model;
	JFileChooser chooser = new JFileChooser();
	List<String> missedElevations = new ArrayList<>();
	
	public AttachedFile(LevellingTableModel model) {
		elevationsMap = new HashMap<>();
		this.model = model;
	}
	
	public boolean chooseAttachedFile() {
		chooser.setDialogTitle("Wybierz plik z rzêdnymi (<NR> <X> <Y> <H>)");
    	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	chooser.setAcceptAllFileFilterUsed(false);
    	chooser.showOpenDialog(null);
    	file=chooser.getSelectedFile();
    	
    	if(loadElevationsFromFile()) {
    		for (Map.Entry<String, Double> entry : elevationsMap.entrySet())
    		     System.out.println(entry.getKey()+" "+entry.getValue());
    		isFileAttached = true;
    		if(file != null)
        		MainFrame.labelFileName.setText(file.getAbsolutePath());
    		model.setElevationsMap(elevationsMap, this);
    	} else {
    		isFileAttached = false;
    		elevationsMap.clear();
    		model.setElevationsMap(elevationsMap, this);
    		MainFrame.labelFileName.setText("brak");
    	}
    	
    	return true;
	}
	
	public boolean loadElevationsFromFile() {
		if(file!=null) {
			elevationsMap.clear();
			missedElevations.clear();
			BufferedReader reader=null;
			String pointName=null;
			Double elevation = null;
			String rowDescription=null;
			try {
				reader = new BufferedReader(new FileReader(file));
				String currentLine;
				int rowNumber = 0;
					while ((currentLine = reader.readLine()) != null) {
						try {
							rowNumber++;
							rowDescription = "B³¹d w wierszu "+rowNumber+": "+currentLine;
							String[] subString = currentLine.split("\\s+");
							if(subString.length>1)
								pointName = subString[0];
							else break;
							
							elevation = null;
							System.out.println(rowNumber+": "+currentLine);
							if(subString.length>3) {
								if(subString[3] != null) 
									elevation=Double.parseDouble(subString[3]);
								elevationsMap.put(pointName, elevation);
							} else {
								missedElevations.add(pointName);
							}
						} catch (NumberFormatException e) {
							missedElevations.add(pointName);
							elevation = null;
						}
					}
			}catch (FileNotFoundException e) {
				displayErrorMessege(rowDescription, e);
				e.printStackTrace();
				return false;
				
			} catch (IOException e) {
				displayErrorMessege(rowDescription, e);
				e.printStackTrace();
				return false;
				
			} catch (Exception e) {
				displayErrorMessege(rowDescription, e);
				e.printStackTrace();
				return false;
			}
			finally {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
							return false;
						}
			}
			
			if(missedElevations.size()>0) {
				JOptionPane.showMessageDialog(null,
						"Nie zaimportowano rzêdnych dla punktów: \n"+prepareListToDisplay(missedElevations),
				        "B³ad w za³¹czonym pliku",
				        JOptionPane.INFORMATION_MESSAGE);
			}
			if(checkIsElevationsCorrect())
				return true;
			else {
				JOptionPane.showMessageDialog(null,
						"Niepoprawny format danych w pliku.\n"+
						"W ka¿dym wierszu dane powinny byæ odzielone spacj¹ i zaczynaæ siê od pocz¹tku linii:\n<NR> <X> <Y> <H>",
				        "B³ad w za³¹czonym pliku",
				        JOptionPane.ERROR_MESSAGE);
				return false;
			}
		} else return false; // don't choosed file
	}
	
	void displayErrorMessege(String rowDescription, Exception e) {
		JOptionPane.showMessageDialog(null,
				rowDescription+"\n"+e.getMessage(),
		        "B³ad w za³¹czonym pliku",
		        JOptionPane.ERROR_MESSAGE);
	}
	
	boolean checkIsElevationsCorrect() {
		for (Map.Entry<String, Double> entry : elevationsMap.entrySet()) {
			Double elevation = entry.getValue();
			if(elevation != null) {
				if(elevation>11000.0 || elevation<-110000.0)
					return false;
			}
		}
		return true;
	}
	
	public void unlinkAttachedFile() {
		elevationsMap.clear();
		file = null;
		isFileAttached=false;
		MainFrame.labelFileName.setText("brak");
	}
	
	String prepareListToDisplay(List<String> missedElevations) {
		String preparedString="";
		for(int i=0; i<missedElevations.size(); i++) {
			preparedString += missedElevations.get(i) + ", ";
			if(i>0 && i%10==0)
				preparedString += "\n";
		}
		return preparedString;
	}

	public boolean isFileAttached() {
		return isFileAttached;
	}

	public void setFileAttached(boolean isFileAttached) {
		this.isFileAttached = isFileAttached;
	}

}
