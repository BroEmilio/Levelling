package levellingTable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import levelling.*;

public class LevellingTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	List<Sight> levellingData = new ArrayList<Sight>();

	public LevellingTableModel() {
		for(int i=0; i<500; i++)
			levellingData.add(new Sight());
	}
	
	String[] columnNames = {"Lp", "Numer",  "Wstecz/Wprzód I","Wstecz/Wprzód II",
							"Poœredni I", "Poœredni II", "B³¹d w mm", "Rzêdna"};
	
	int columnIndexFor(String columnName) {
		columnName = columnName.toLowerCase();
		switch(columnName) {
			case "id" : return 0;
			case "pointnumber" : return 1;
			case "backorforesight1" : return 2;
			case "backorforesight2" : return 3;
			case "intermediatesight1" : return 4;
			case "intermediatesight2" : return 5;
			case "difference" : return 6;
			case "elevation" : return 7;
			default : throw new IllegalArgumentException("Not recognize columnName:"+columnName);
		}
	}
	
	@Override
	 public String getColumnName(int column) {
         return columnNames[column];
     }
	
	@Override
	 public boolean isCellEditable(int rowIndex, int columnIndex) {
		Sight sight = levellingData.get(rowIndex);
		if (columnIndex==columnIndexFor("Id") || (sight.isLock() && columnIndex==columnIndexFor("Elevation")) )
			return false;
		if(columnIndex == columnIndexFor("Elevation"))
			return sight.isEditable();
		
		return true;
	 }
	

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return levellingData.size();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		@SuppressWarnings("rawtypes")
		Class[] columnsTypes = new Class[]{Integer.class, String.class,Integer.class,Integer.class,Integer.class,Integer.class,Integer.class,Double.class};
		return columnsTypes[columnIndex];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Sight sight = levellingData.get(rowIndex);
		switch(columnIndex) {
			case 0 : return rowIndex+1;
			case 1 : return sight.getPointNumber();
			case 2 : return sight.getBackOrForeSight1();
			case 3 : return sight.getBackOrForeSight2();
			case 4 : return sight.getIntermediateSight1();
			case 5 : return sight.getIntermediateSight2();
			case 6 : return sight.getDifference();
			case 7 : return sight.getElevation();
			
			default : return null;
		}
	}
	
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if(rowIndex<levellingData.size()) {
		Sight sight = levellingData.get(rowIndex);
		switch(columnIndex) {
		case 1 : sight.setPointNumber((String) aValue);break;									
		case 2 : sight.setBackOrForeSight1((Integer) aValue);									
					if(rowIndex==0 || isShouldBeAsBackSight(rowIndex, columnIndex)) {
						sight.setAsBackSight(true);
						if(rowIndex==0) 
							sight.setEditable(true);
					}
					MainFrame.secondCalcButton.setEnabled(false);
					break;
		case 3 :sight.setBackOrForeSight2( (Integer) aValue);break;
		case 4 : if(sight.getBackOrForeSight1()==null && sight.getBackOrForeSight2()==null) {
					 		sight.setIntermediateSight1( (Integer) aValue);
					 		sight.setIntermediate(true);
					 }		
							MainFrame.secondCalcButton.setEnabled(false);
					 		break;
		case 5 : sight.setIntermediateSight2( (Integer) aValue);break;
		case 6 : sight.setDifference( (Integer) aValue);break;
		case 7 :  if(aValue != null) {
								Double value = Calculating.round((Double) aValue, 3);
								sight.setElevation(value);
								if((rowIndex==0 || isShouldBeAsBackSight(rowIndex, columnIndex)) && ! sight.isSightIntermediate()) {
									sight.setAsBackSight(true);
									if(rowIndex == 0)
										sight.setEditable(true);
								}
								if( ! sight.isBackSight() && ! sight.isSightIntermediate() && rowIndex+1<levellingData.size()) {
									Sight nextSight = levellingData.get(rowIndex+1);
									if(nextSight.getElevation() == null && ! nextSight.isSightIntermediate()) {		
										nextSight.setElevation(value);			// set elevation for next sight 
										nextSight.setAsBackSight(true);			// and mark it as backsight
									}  else {											// if elevation of next sight is not null
										Sight nextBackSight = getNextBackSightFromIndex(rowIndex+1); 
										nextBackSight.setElevation(value);				// change elevation of next backsight to the same as last foresight
									}
								}
						} else { sight.setElevation(null);			// if change value to the null
								  if( ! sight.isBackSight() && ! sight.isSightIntermediate() && rowIndex+1<levellingData.size()) {
									 Sight nextBackSight = getNextBackSightFromIndex(rowIndex+1); 
									 nextBackSight.setElevation(null); // if setting null for elevation of foresight, set null also for elevation of next backsight
								  }
								};
		
						if(rowIndex<getLastNoNullIndexAtColumn(levellingData.size(), 7))
							Calculating.updateBackAndForeSightSequence(this);
						
						MainFrame.secondCalcButton.setEnabled(false);
						break;
		default: System.err.println("Error in method setValueAt()");
		}
		fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
		
	
	public List<Sight> getLevellingData(){
		return levellingData;
	}

	public void addRow(int rowIndex) {
		levellingData.add(rowIndex, new Sight());
		fireTableRowsInserted(rowIndex, rowIndex);
	}
	
	public void deleteRow(int rowIndex) {
		levellingData.remove(rowIndex);
		fireTableRowsDeleted(rowIndex,rowIndex);
		Calculating.updateBackAndForeSightSequence(this);
	}
	
	public int getLastNoNullIndexAtColumn(int row, int column) {
		int index=-1;
			for(int i=row-1; i>=0; i--) {
				Object object = getValueAt(i, column);
				if(object != null)
					return i;
			}
		return index;
	}
	
	public boolean isShouldBeAsBackSight(int row, int column) {
		int lastBackOrForeSightIndex = getLastNoIntermediateSightIndex(row, column);
		if(lastBackOrForeSightIndex > -1) {
			Sight sight = levellingData.get(lastBackOrForeSightIndex);
			if(sight.isBackSight())
				return false;
			else return true;
		} return false;
	}
	
	public int getLastNoIntermediateSightIndex(int row, int column) {
		for(int i=row-1; i>=0; i--) {
			Object object = getValueAt(i, column);
			Sight sight = getOdczytAtIndex(i);
			if(object != null && ! sight.isSightIntermediate())
				return i;
		}
		return -1;
	}
	
	Sight getNextBackSightFromIndex(int index) {
		Sight nextBackSight=null;
		ListIterator<Sight> it = levellingData.listIterator(index);
		while(nextBackSight == null && it.hasNext()) {
			if(it.next().isBackSight())
				nextBackSight=it.previous();
		}
		return nextBackSight;
	}
	
	public void setFirstAndLastPoint(int rowIndex) {
		if(rowIndex<=levellingData.size()) {
			Sight odczyt = levellingData.get(rowIndex);
			int dispalyIndex = rowIndex+1;
			String name = odczyt.getPointNumber();
			if(name==null) 
				name = "bez nazwy";
			if(rowIndex == 0) {
				odczyt.setLock(! odczyt.isLock());
				if(odczyt.isLock()) {
					odczyt.setLock(true);
				} 
			} else {
					if(! odczyt.isLock()) {
							if(odczyt.isBackSight() || odczyt.isSightIntermediate()){
								JOptionPane.showMessageDialog(null,
								        "Odczyt wstecz lub poœredni nie mo¿e byæ punktem koñcowym niwelacji.",
								        "Nieprawid³owy punkt koñcowy niwelacji",
								        JOptionPane.INFORMATION_MESSAGE);
							} else {
									Object[] options = {"Tak","Nie"};
									int answer = JOptionPane.showOptionDialog(
								            null,
								            "         Czy oznaczyæ punkt "+name+" (wiersz "+dispalyIndex+") jako punkt koñcowy niwelacji ? \n"+
								            "Spowoduje to usuniêcie wszystkich nastêpnych wierszy (oprócz odczytów poœrednich).",
								            "Oznaczenie punktu koñcowego niwelacji",
								            JOptionPane.YES_NO_OPTION,
								            JOptionPane.WARNING_MESSAGE,
								            null,
								            options,
								            options[0]);
									if(answer == 0) {
										odczyt.setLock(true);
										odczyt.setIntermediate(false);
										for(int i=levellingData.size()-1;i>rowIndex;i--)
											if( ! getOdczytAtIndex(i).isSightIntermediate())
											levellingData.remove(i);
									}
							}
					} else  odczyt.setLock(! odczyt.isLock());	
			}
		}
	}
	
	public void changePosredniStatus(int rowIndex) {
		Sight odczyt = levellingData.get(rowIndex);
		odczyt.setIntermediate(! odczyt.isSightIntermediate());
		if(odczyt.isSightIntermediate())
			odczyt.setIntermediate(true);
		Calculating.updateBackAndForeSightSequence(this);
	}
	
	public Sight getOdczytAtIndex(int index) {
		if(index<levellingData.size() && index>=0)
			return levellingData.get(index);
		else return null;
	}
	
}
