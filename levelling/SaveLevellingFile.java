package levelling;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import levellingTable.LevellingTableModel;

public class SaveLevellingFile {
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
	List<Sight> sortedData = new ArrayList<Sight>();
	static File projectsPath = new File(".");
	
	public SaveLevellingFile(LevellingTableModel model) {
		for(Sight odczyt:model.getLevellingData()) {
			sortedData.add(odczyt);
		}
    	chooser.setFileFilter(filter);
    	chooser.setSelectedFile(new File("niwelacja "));
    	chooser.setCurrentDirectory(projectsPath);
    	chooser.setDialogTitle("Zapisz niwelacjê jako");
    	chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	chooser.setAcceptAllFileFilterUsed(false);
    	chooser.showSaveDialog(null);
    	file=chooser.getSelectedFile();
    	if(chooser.getSelectedFile()!=null)
    		projectsPath = chooser.getSelectedFile();
	}

	public void saveToNiwFile() {
		Writer writer = null;
		sortForCgeo();
		Sight odczyt=null;
		if(file != null) {
			String fullPath = file.getPath();
			int dotIndex = fullPath.lastIndexOf(".");
			if(dotIndex != -1) {
				String extension = fullPath.substring(dotIndex, fullPath.length());
				if(extension==".niw")
					file = new File(file.toString());
				else new File(file.toString() + ".niw"); 
			} else {
			file = new File(file.toString() + ".niw"); 
			}
			try {
			    writer = new BufferedWriter(new FileWriter(file));
			    writer.write("[Niwelacja]\r\n" + 
			    		"Ver=2\r\n" + 
			    		"Ver2=0\r\n" + 
			    		"Drugi=0\r\n" + 
			    		"cb_dlug=0\r\n" + 
			    		"przes_h=0\r\n" + 
			    		"ovc_dlug=1e20\r\n" + 
			    		"ovc_m0=1e20\r\n" + 
			    		"jednostki=0\r\n" + 
			    		"rodzaj=0\r\n" + 
			    		"\r\n");
			   
				for(int i=0; i<sortedData.size(); i++) {
					odczyt=sortedData.get(i);
					writer.write("["+(i+1)+"]"+"\r\n");
					if(odczyt.getPointNumber()!=null)
						writer.write("1="+odczyt.getPointNumber()+"\r\n");
					else 
						writer.write("1=\r\n");
					if(odczyt.getBackOrForeSight1()!=null)
						writer.write("4="+odczyt.getBackOrForeSight1()+"\r\n");
					if(odczyt.getBackOrForeSight2()!=null)
						writer.write("5="+odczyt.getBackOrForeSight2()+"\r\n");
					if(odczyt.getIntermediateSight1()!=null)
						writer.write("6="+odczyt.getIntermediateSight1()+"\r\n");
					if(odczyt.getElevation()!=null) 
						writer.write("7="+odczyt.getElevation()+"\r\n");
					if(odczyt.getIntermediateSight2()!=null)
						writer.write("8="+odczyt.getIntermediateSight2()+"\r\n");
				}
				writer.write("\r\n");
			} catch (IOException ex) {
			    // Report
			} finally {
			   try {writer.close();} catch (Exception ex) {System.err.println("Exception when closing writer");}
			}
			
		}
	}
	
	public void sortForCgeo() {
		Sight odczyt=null;
		Sight nextOdczyt=null;
		for(int i=0; i<sortedData.size(); i++) {
			odczyt=sortedData.get(i);
			if(i+1<sortedData.size())
				nextOdczyt=sortedData.get(i+1);
			else break;
			if(odczyt.isBackSight && nextOdczyt.isSightIntermediate) {
				Sight nextWprzod=null;
				int j = i+1;
				while(nextWprzod==null && j<sortedData.size()) {
					if(sortedData.get(j).isBackSight == false && sortedData.get(j).isSightIntermediate == false) {
						nextWprzod = sortedData.get(j);
					} else j++;
				}
				Sight temp=sortedData.get(i+1); // poœredni do temp
				sortedData.set(i+1, nextWprzod);
				sortedData.set(j, temp);
			}
		}
	}
	
}
