package levellingTable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import levelling.*;

public class LevellingTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;

	public LevellingTableModel() {
		for(int i=0; i<500; i++)
			data.add(new Sight());
	}
	
	List<Sight> data = new ArrayList<Sight>();
	String[] columnNames = {"Lp", "Numer",  "Wstecz/Wprzód I","Wstecz/Wprzód II", "Poœredni I", "Poœredni II", "B³¹d w mm", "Rzêdna"};
	
	/*int columnIndexFor(String columnName) {
		for(int index=0; index<columnNames.length; index++){
			if(columnNames[index].equalsIgnoreCase(columnName))
				return index;
		}
		return -1;
	}*/
	
	@Override
	 public String getColumnName(int column) {
         return columnNames[column];
     }
	
	@Override
	 public boolean isCellEditable(int rowIndex, int columnIndex) {
		Sight sight = data.get(rowIndex);
		if (columnIndex==0 || (sight.isLock() && columnIndex==7) )
			return false;
		if(columnIndex == 7)
			return sight.isEditable();
		
		return true;
	 }
	

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		return data.size();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		@SuppressWarnings("rawtypes")
		Class[] columnsTypes = new Class[]{Integer.class, String.class,Integer.class,Integer.class,Integer.class,Integer.class,Integer.class,Double.class};
		return columnsTypes[columnIndex];
	}
	
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Sight sight = data.get(rowIndex);
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
		if(rowIndex<data.size()) {
		Sight sight = data.get(rowIndex);
		switch(columnIndex) {
		case 1 : sight.setPointNumber((String) aValue);break;
		case 2 : sight.setBackOrForeSight1((Integer) aValue);
					if(rowIndex==0 || isBackSight(rowIndex, columnIndex)) {
						sight.setAsBackSight(true);
						if(rowIndex==0) 
							sight.setEditable(true);
					}
					if(rowIndex<getLastNoNullIndexAtColumn(data.size(), 2))
						Calculating.updateWsteczWprzod(this);
					
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
								double value = (Double) aValue;
								value = Calculating.round(value, 3);
								sight.setElevation(value);
								if((rowIndex==0 || isBackSight(rowIndex, columnIndex)) && ! sight.isSightIntermediate()) {
									sight.setAsBackSight(true);
									if(rowIndex == 0)
										sight.setEditable(true);
								}
								if( ! sight.isBackSight() && ! sight.isSightIntermediate() && rowIndex+1<data.size()) {
									Sight nextOdczyt = data.get(rowIndex+1);
									if(nextOdczyt.getElevation() == null && ! nextOdczyt.isSightIntermediate()) {
									nextOdczyt.setElevation(value);
									nextOdczyt.setAsBackSight(true);
									}  else {
										Sight nextWstecz = nextWstecz(rowIndex+1);
										nextWstecz.setElevation(value);
									}
								}
						} else { sight.setElevation(null);
									if( ! sight.isBackSight() && ! sight.isSightIntermediate() && rowIndex+1<data.size())
										setValueAt(null, rowIndex+1, columnIndex);
								   };
		
						if(rowIndex<getLastNoNullIndexAtColumn(data.size(), 7))
							Calculating.updateWsteczWprzod(this);
						
						MainFrame.secondCalcButton.setEnabled(false);
						break;
		default: System.err.println("B³¹d w metodzie setValueAt()");
		}
		fireTableCellUpdated(rowIndex, columnIndex);
		}
	}
		
	
	public List<Sight> getData(){
		return data;
	}

	public void addRow(int rowIndex) {
		data.add(rowIndex, new Sight());
		fireTableRowsInserted(rowIndex, rowIndex);
	}
	
	public void deleteRow(int rowIndex) {
		data.remove(rowIndex);
		fireTableRowsDeleted(rowIndex,rowIndex);
		Calculating.updateWsteczWprzod(this);
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
	
	public boolean isBackSight(int row, int column) {
		int lastNoNullIndex = getLastNoIntermediateSightIndex(row, column);
		if(lastNoNullIndex > -1) {
		Sight odczyt = getOdczytAtIndex(lastNoNullIndex);
		if(odczyt.isBackSight())
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
	
	Sight nextWstecz(int index) {
		Sight nextWstecz=null;
		ListIterator<Sight> it = data.listIterator(index);
		while(nextWstecz == null && it.hasNext()) {
			if(it.next().isBackSight())
				nextWstecz=it.previous();
		}
		return nextWstecz;
	}
	
	public void setFirstAndLastPoint(int rowIndex) {
		if(rowIndex<=data.size()) {
			Sight odczyt = data.get(rowIndex);
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
										for(int i=data.size()-1;i>rowIndex;i--)
											if( ! getOdczytAtIndex(i).isSightIntermediate())
											data.remove(i);
									}
							}
					} else  odczyt.setLock(! odczyt.isLock());	
			}
		}
	}
	
	public void changePosredniStatus(int rowIndex) {
		Sight odczyt = data.get(rowIndex);
		odczyt.setIntermediate(! odczyt.isSightIntermediate());
		if(odczyt.isSightIntermediate())
			odczyt.setIntermediate(true);
		Calculating.updateWsteczWprzod(this);
	}
	
	public List<Integer> getOdczyt1List(){
		List<Integer> list = new ArrayList<Integer>();
		Integer odczyt1;
		for(int index=0;index<data.size();index++) {
				odczyt1 =(Integer) getValueAt(index, 2);
				list.add(odczyt1);
			}
			
		return list;
	}
	
	public Sight getOdczytAtIndex(int index) {
		if(index<data.size() && index>=0)
			return data.get(index);
		else return null;
	}
	
}
