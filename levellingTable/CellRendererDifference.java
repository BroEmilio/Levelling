package levellingTable;

import levelling.*;
import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRendererDifference extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	LevellingTableModel model;
	public static boolean isOverRange = false;
	
	public CellRendererDifference(LevellingTableModel model) {
		this.model=model;
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		int indexInModel = table.convertRowIndexToModel(row);
		Sight odczyt = model.getOdczytAtIndex(indexInModel);
		if(odczyt.getDifference() != null) {
			if((isOverRange && odczyt.isLock() && ! odczyt.isBackSight()) || (Math.abs(odczyt.getDifference()) > 3 && ! odczyt.isLock())) 
				 comp.setBackground(Color.RED);
			else comp.setBackground(null);	
		} else comp.setBackground(null);
			
		 table.repaint();
         return comp;  
	}
}
