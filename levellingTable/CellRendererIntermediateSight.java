package levellingTable;

import levelling.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class CellRendererIntermediateSight extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	LevellingTableModel model;
	
	public CellRendererIntermediateSight(LevellingTableModel model) {
		this.model=model;
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
												   boolean hasFocus,int row,int col) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		Sight sight = model.getSightAtIndex(row);
		 if (value != null &&  sight.isIntermediate() ) {
			 if((Integer)value>5000 || (Integer)value<0)
				 comp.setBackground(Color.MAGENTA);
		} else comp.setBackground(null);
		 
         return comp;  
	}
}
