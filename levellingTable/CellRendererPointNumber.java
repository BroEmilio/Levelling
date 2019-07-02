package levellingTable;

import levelling.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class CellRendererPointNumber extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	LevellingTableModel model;
	
	public CellRendererPointNumber(LevellingTableModel model) {
		this.model=model;
        this.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
												   boolean hasFocus,int row,int col) {
		Component comp =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		Sight sight = model.getSightAtIndex(row);
		if(sight.isLock()) {
			setBackground(new Color(90,175,240));	
			if(sight.isLock() && isSelected)
				table.setSelectionBackground(new Color(90,175,240));
			else table.setSelectionBackground(null);
		} else {
			comp.setBackground(null);
			table.setSelectionBackground(null);
			if(table.getSelectedRow() == row) 
				comp.setBackground(Color.LIGHT_GRAY);
			}
		 return comp;
	}
}
