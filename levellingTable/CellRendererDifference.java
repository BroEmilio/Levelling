package levellingTable;

import levelling.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

public class CellRendererDifference extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	LevellingTableModel model;
	public static boolean isOverRange = false;
	
	public CellRendererDifference(LevellingTableModel model) {
		this.model=model;
		this.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
												   boolean hasFocus,int row,int col) {
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		Sight sight = model.getSightAtIndex(row);
		if(sight.getDifference() != null) {
			if( Math.abs(sight.getDifference()) > 3 ) 
				 comp.setBackground(Color.RED);
			else comp.setBackground(null);	
		} else comp.setBackground(null);
			
		 table.repaint();
         return comp;  
	}
}
