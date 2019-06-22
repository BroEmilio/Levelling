package levelling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import levellingTable.LevellingTableModel;

public class LoadLevellingFile {
	LevellingTableModel model;
	JFileChooser chooser = new JFileChooser();
	//FileFilter filter = new FileNameExtensionFilter("Niwelacja C-Geo (.niw)", "niw");
	FileFilter filter = new FileFilter() {
		@Override
		public String getDescription() {
		  return "Niwelacja C-Geo (.niw)";
		}
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
	            return true;
	        } else {
	            return f.getName().toLowerCase().endsWith(".niw");
	        }
		}
	};
	File file;

	public LoadLevellingFile(LevellingTableModel model) {
		this.model = model;
		chooser.setFileFilter(filter);
		chooser.setCurrentDirectory(SaveLevellingFile.projectsPath);
    	chooser.setDialogTitle("Wczytaj niwelacj� z pliku");
    	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	chooser.setAcceptAllFileFilterUsed(false);
    	chooser.showOpenDialog(null);
    	file=chooser.getSelectedFile();
    	if(chooser.getSelectedFile()!=null)
    		SaveLevellingFile.projectsPath = chooser.getSelectedFile();
	}
	
	public boolean loadFromNiwFile() {
		if(file!=null) {
		BufferedReader reader=null;
		List<Sight> data = model.getLevellingData();
		data.clear();
		Sight odczyt=null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String currentLine;
				while ((currentLine = reader.readLine()) != null) {
					Pattern p = Pattern.compile("\\[\\d+\\]");
					Matcher m = p.matcher(currentLine);
					 if(m.find()) {
						 if(odczyt != null)
							 data.add(odczyt);
						 odczyt = new Sight();
					 }
					 if(currentLine.contains("=")) {
						 String[] elements = currentLine.split("=");
						 for(int i=0; i<elements.length;i++)
						
						 if(isInteger(elements[0])) {
							 int key = new Integer(elements[0]);
							 switch(key) {
							 case 1 : {
								 if(elements.length>1)
									 odczyt.setPointNumber(elements[1]);
								 break;
							 }
							 case 3 : break; // nie uwzgl�dnia d�ugo�ci pomierzonej celowej
							 case 4 : odczyt.setBackOrForeSight1(returnIntegerFromString(elements[1]));break;
							 case 5 : odczyt.setBackOrForeSight2(new Integer(elements[1]));break;
							 case 6 : odczyt.setIntermediateSight1(new Integer(elements[1]));break;
							 case 7 : odczyt.setElevation(new Double(elements[1]));break;
							 case 8 : odczyt.setIntermediateSight2(new Integer(elements[1]));break;
							 default : System.err.println("B�ad w metodzie loadFromNiwFile(), nierozpoznany key:"+key);return false;
							 }
						 }
					 }
				  }
				if(odczyt!=null)
					data.add(odczyt);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}  finally {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
							return false;
						}
			}
		if(data.size()>0) {
			Calculating.updateBackAndForeSightSequence(model);
			Calculating.updateBackAndForeSightSequence(model);
			data.get(0).setLock(true);
			getLastForesight(data.size()).setLock(true);
			return true;
			}
		} else return true; // nie wybrano pliku
		return false;
	}
	
	boolean isInteger(String string) {
		try { 
	        Integer.parseInt(string); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    } catch(NullPointerException e) {
	       return false;
	    }
		return true;
	}
	
	Sight getLastForesight(int index) {
		List<Sight> data = model.getLevellingData();
		ListIterator<Sight> it = data.listIterator(index);
		while(it.hasPrevious()) {
			Sight sight = it.previous();
			if(sight.isBackSight==false &&  sight.isSightIntermediate==false) {
				return sight;
			}
		}
		return null;
	}
	
	Integer returnIntegerFromString(String string) {
		Integer integer=null;
		try {
			integer=Integer.parseInt(string);
		} catch(NumberFormatException ex) {
			try {
				Double doubleValue=Double.parseDouble(string);
				integer=Calculating.roundToInt(doubleValue);
			} catch(Exception ex2) {
				System.err.println(ex2);
			}
		}
		return integer;
	}

}
