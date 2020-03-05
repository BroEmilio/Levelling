package levelling;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import levellingTable.LevellingTableModel;

public class AttachedFile {
	
	File file;
	boolean isFileAttached;
	Map<String, Double> elevationsMap;
	LevellingTableModel model;
	JFileChooser chooser = new JFileChooser();
	List<String> missedElevations = new ArrayList<>();
	FileFilter filter = new FileFilter() {
		@Override
		public String getDescription() {
		  return "Plik z rzêdnymi (.txt, .csv)";
		}
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
	            return true;
	        } else {
	            return (f.getName().toLowerCase().endsWith(".txt") || f.getName().toLowerCase().endsWith(".csv")
	            		|| ! f.getName().contains("."));
	        }
		}
	};
	
	public AttachedFile(LevellingTableModel model) {
		elevationsMap = new HashMap<>();
		this.model = model;
	}
	
	public boolean chooseAttachedFile() {
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		chooser.setDialogTitle("Wybierz plik z rzêdnymi (<NR> <X> <Y> <H>)");
    	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	chooser.setAcceptAllFileFilterUsed(false);
    	chooser.showOpenDialog(null);
    	file=chooser.getSelectedFile();
    	
    	if(loadElevationsFromFile()) {
    		isFileAttached = true;
    		if(file != null)
        		MainFrame.labelFileName.setText(file.getAbsolutePath());
    			MainFrame.labalFileInstrution.setText("Za³¹czony plik z rzêdnymi (<NR> <X> <Y> <H>) : "+elevationsMap.size()+" pkt");
    		model.setElevationsMap(elevationsMap, this);
    	} else {
    		isFileAttached = false;
    		elevationsMap.clear();
    		model.setElevationsMap(elevationsMap, this);
    		MainFrame.labelFileName.setText("brak");
    		MainFrame.labalFileInstrution.setText("Za³¹czony plik z rzêdnymi (<NR> <X> <Y> <H>) :");
    		return false;
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
			String rowDescription="";
			try {
				reader = new BufferedReader(new FileReader(file));
				String currentLine;
				int rowNumber = 0;
					while ((currentLine = reader.readLine()) != null) {
						try {
							rowNumber++;
							rowDescription = "B³¹d w wierszu "+rowNumber+": "+currentLine;
							String[] subString = currentLine.split("\\s+");
							if(subString.length>0)
								pointName = subString[0];
							else break;
							
							elevation = null;
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
			
			if(checkIsElevationsCorrect()) {
				if(missedElevations.size()>0) {
					JOptionPane.showMessageDialog(null,
							"Nie zaimportowano rzêdnych dla punktów: \n"+prepareListToDisplay(missedElevations),
					        "B³ad w za³¹czonym pliku",
					        JOptionPane.INFORMATION_MESSAGE);
				}
				return true;
			} else {
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
		int errorCounter = 0;
		for (Map.Entry<String, Double> entry : elevationsMap.entrySet()) {
			Double elevation = entry.getValue();
			if(elevation != null) {
				if(elevation>11000.0 || elevation<-110000.0)
					errorCounter++;
			} else errorCounter++;
		}
		
		if(elevationsMap.size()==0)
			return false;
		
		if((errorCounter/elevationsMap.size())>0.85)
			return false;
		else return true;
	}
	
	public void unlinkAttachedFile() {
		elevationsMap.clear();
		file = null;
		isFileAttached=false;
		MainFrame.labelFileName.setText("brak");
		MainFrame.labalFileInstrution.setText("Za³¹czony plik z rzêdnymi (<NR> <X> <Y> <H>) :");
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
