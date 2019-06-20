package levellingTable;

import levelling.*;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRendererWsteczWprzod extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	LevellingTableModel model;
	
	public CellRendererWsteczWprzod(LevellingTableModel model) {
		this.model=model;
	}
	
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		Sight odczyt = model.getOdczytAtIndex(row);
		 if (value != null && ! odczyt.isSightIntermediate() ) {
			 if(odczyt.isBackSight()) {
				 comp.setBackground(Color.YELLOW);
			 }
			 else {
				 comp.setBackground(Color.RED);
			 }
			 
			 if((Integer)value>5000 || (Integer)value<0)
				 comp.setBackground(Color.MAGENTA);
		}
		 else comp.setBackground(null);
			
		 table.repaint();
         return comp;  
	}
}
