package levellingTable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import levelling.*;

public class LevellingTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	List<Sight> levellingData = new ArrayList<Sight>();
	CommonMethods commonMethods;
	Random random = new Random();

	public LevellingTableModel() {
		for(int i=0; i<500; i++)
			levellingData.add(new Sight());
		commonMethods = new CommonMethods(this);
	}
	
	public Sight getSightAtIndex(int index) {
		if(index<levellingData.size() && index>=0)
			return levellingData.get(index);
		else return null;
	}
	
	public List<Sight> getLevellingData(){
		return levellingData;
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
					if(rowIndex==0 || shouldBeAsBackSight(rowIndex)) {
						sight.setAsBackSight(true);
						if(rowIndex==0) 
							sight.setEditable(true);
					}
					MainFrame.secondCalcButton.setEnabled(false);
					commonMethods.updateSightsSequence(this);
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
					Double value = commonMethods.round((Double) aValue, 3);
					sight.setElevation(value);
					complementNeighborElevation(rowIndex);
					complementElevations(rowIndex);
					MainFrame.secondCalcButton.setEnabled(false);
					break;
				  }
		default: System.err.println("Error in method setValueAt()");
		}
		fireTableCellUpdated(rowIndex, columnIndex);
		commonMethods.updateSightsSequence(this);
		}
	}
		
	public void addRow(int rowIndex) {
		levellingData.add(rowIndex, new Sight());
		fireTableRowsInserted(rowIndex, rowIndex);
	}
	
	public void deleteRow(int rowIndex) {
		levellingData.remove(rowIndex);
		fireTableRowsDeleted(rowIndex,rowIndex);
		commonMethods.updateSightsSequence(this);
	}
	
	public void changeLockStatus(int rowIndex) {
		Sight sight = levellingData.get(rowIndex);
		boolean newStatus = ! sight.isLock();
		if(sight.isBackSight() && rowIndex>0)
			return;
		if( ! sight.isIntermediate())
			sight.setLock(newStatus);
	}
	
	public void changeIntermediateSightStatus(int sightRowIndex) {
		Sight sight = levellingData.get(sightRowIndex);
		boolean newStatus = ! sight.isIntermediate();
		sight.setIntermediate(newStatus);
		commonMethods.updateSightsSequence(this);
	}
	
	public void setAsFirstBenchmark() {
		Sight firstSight = levellingData.get(0);
		if(firstSight != null)
			firstSight.setLock(true);
	}
	
	public void setAsLastBenchmark(int rowIndex) {
		if(rowIndex<=levellingData.size()) {
			Sight sight = levellingData.get(rowIndex);
			int dispalyIndex = rowIndex+1;
			String name = sight.getPointNumber();
			if(name==null) 
				name = "bez nazwy";
			if(! sight.isBackSight() && ! sight.isIntermediate()) {
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
						sight.setLock(true);
						for(int i=levellingData.size()-1;i>rowIndex;i--)
							if( ! getSightAtIndex(i).isIntermediate())
								levellingData.remove(i);
					} else sight.setLock(false); // if denial
			}
		}
	}
	
	boolean shouldBeAsBackSight(int currentRow) {
		int lastBackSightIndex = commonMethods.getIndexOfLastBackSight(currentRow-1);
		int nullElevationsCounter = -1;
		if(lastBackSightIndex >-1) {
			nullElevationsCounter = countNullElevationsBetween(lastBackSightIndex, currentRow);
			if(nullElevationsCounter%2 != 0) {
				return true;
			}
			else return false;
		} else return false;
	}

	void complementNeighborElevation(int currentRow) {
		Sight currentSight = levellingData.get(currentRow);
		if(currentRow==0) {
			currentSight.setAsBackSight(true);
			currentSight.setEditable(true);
			return;
		}
		
		if(currentSight.isIntermediate())
			return;
		
		if(shouldBeAsBackSight(currentRow)) {	// instructions for inserted back sight
			currentSight.setAsBackSight(true);
			Sight previousSight = null;
			for(int i=currentRow-1 ; i>0; i--) {
				Sight tempSight = levellingData.get(i);
				if( ! tempSight.isIntermediate()) {
					previousSight=tempSight;
					break;
				}
			}
			if(previousSight!=null) {
				previousSight.setAsBackSight(false);
				previousSight.setElevation(currentSight.getElevation());
			}
		} else {								// instructions for inserted fore sight
			currentSight.setAsBackSight(false);
			if( ! currentSight.isIntermediate() && currentRow+1<levellingData.size()) {
				Sight nextSight = levellingData.get(currentRow+1);
				if(nextSight.getElevation() == null && ! nextSight.isIntermediate()) {		
					nextSight.setElevation(currentSight.getElevation());
					nextSight.setAsBackSight(true);
				}  else {		// if elevation of next sight is not null
					Sight firstEmptyElevation = null;
					int firstEmptyElevationIndex = -1;
					for(int i=currentRow+1; i<currentRow+30; i++ ) {
						Sight tempSight = levellingData.get(i);
						if(tempSight.getElevation()==null && ! tempSight.isIntermediate()) {
							firstEmptyElevation = tempSight;
							firstEmptyElevationIndex = i;
							break;
						}
					}
					Sight nextBackSight = getNextBackSightFromIndex(currentRow+1);
					int nextBackSightIndex = commonMethods.getIndexOfSight(nextBackSight);
					if(nextBackSightIndex > firstEmptyElevationIndex || nextBackSight==null) 
						firstEmptyElevation.setElevation(currentSight.getElevation());
					else
						nextBackSight.setElevation(currentSight.getElevation());
				}
			}
		}
	}
	
	void complementElevations(int currentRow) { // complement empty elevations between last inserted value and last no null elevation
		if(currentRow<2)
			return;
		Sight firstBackSight = commonMethods.lastBackSight(currentRow);
		Sight lastBackSight = commonMethods.lastBackSight(currentRow+2);
		int firstBackSightIndex = commonMethods.getIndexOfSight(firstBackSight);
		int lastBackSightIndex = commonMethods.getIndexOfSight(lastBackSight);
		if(firstBackSight==lastBackSight)
			return;
		int nullElevationsCounter = countNullElevationsBetween(firstBackSightIndex, lastBackSightIndex);
		int helper=0;
		Double estimatedElevation = firstBackSight.getElevation();
		for(int i=firstBackSightIndex; i<firstBackSightIndex+nullElevationsCounter; i+=2) {
			helper++;
			Sight firstEmptySight = getFirstEmptyElevationSightFromIndex(firstBackSightIndex);
			int firstEmptySightIndex = commonMethods.getIndexOfSight(firstEmptySight);
			while(firstEmptySight.isIntermediate()) {
				firstEmptySight = getFirstEmptyElevationSightFromIndex(firstEmptySightIndex+1);
				firstEmptySightIndex = commonMethods.getIndexOfSight(firstEmptySight);
			}
			Double averageDisparity = calculateAverageElevationsDisparity(firstBackSight, lastBackSight, nullElevationsCounter);
			Double randomShift = averageDisparity + (random.nextInt(Math.abs((int)(averageDisparity*1000)))*0.0004 * ( random.nextBoolean() ? 1 : -1 ));
			randomShift = commonMethods.round(randomShift, 3);
			estimatedElevation += randomShift;
			System.out.println(helper+": "+averageDisparity+" "+randomShift+" "+estimatedElevation);
			firstEmptySight.setElevation(estimatedElevation);
			complementNeighborElevation(firstEmptySightIndex);
		}
    }
	
	/*public int getLastNoNullIndexAtColumn(int row, int column) {
		int index=-1;
			for(int i=row-1; i>=0; i--) {
				Object object = getValueAt(i, column);
				if(object != null)
					return i;
			}
		return index;
	}*/
	
	/*public int getLastNoIntermediateSightIndex(int row, int column) {
		for(int i=row-1; i>=0; i--) {
			Object object = getValueAt(i, column);
			Sight sight = getSightAtIndex(i);
			if(object != null && ! sight.isIntermediate())
				return i;
		}
		return -1;
	}*/
	
	Sight getNextBackSightFromIndex(int index) {
		Sight nextBackSight=null;
		ListIterator<Sight> it = levellingData.listIterator(index);
		while(nextBackSight == null && it.hasNext()) {
			if(it.next().isBackSight())
				nextBackSight=it.previous();
		}
		return nextBackSight;
	}
	
	Sight getFirstEmptyElevationSightFromIndex(int index) {
		Sight emptyElevationSight = null;
		ListIterator<Sight> it = levellingData.listIterator(index);
		while(emptyElevationSight == null && it.hasNext()) {
			if(it.next().getElevation()==null)
				emptyElevationSight=it.previous();
		}
		return emptyElevationSight;
	}
	
	int countNullElevationsBetween(int startIndex, int endIndex) {
		int count = 0;
		for(int i=startIndex; i<=endIndex; i++) {
			Sight sight=levellingData.get(i);
			if(sight.getElevation() == null && ! sight.isIntermediate())
				count++;
		}
		return count;
	}
	
	Double calculateAverageElevationsDisparity(Sight firstBackSight, Sight lastBackSight, int nullElevationsCounter) {
		Double averageDisparity = lastBackSight.getElevation()-firstBackSight.getElevation();
		averageDisparity = averageDisparity / ((nullElevationsCounter/2)+1);
		return averageDisparity;
	}
	
}
