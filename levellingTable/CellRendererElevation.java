package levellingTable;

import levelling.*;
import java.awt.*;
import java.text.*;
import javax.swing.*;
import javax.swing.table.*;

public class CellRendererElevation extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	LevellingTableModel model;
	private static final DecimalFormat formatter = new DecimalFormat("#0.###");
	
	public CellRendererElevation(LevellingTableModel model) {
		super();
		this.model=model;
        formatter.setMinimumFractionDigits(3);
        this.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
												   boolean hasFocus,int row,int col) {
		if(value instanceof Double) {
	         value = formatter.format((Number)value);
		}
		Component comp =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		Sight sight = model.getSightAtIndex(row);
		if( value != null ) {
			if(sight.isBackSight()) 
				comp.setBackground(Color.YELLOW);
			else comp.setBackground(Color.RED);
							 
			if(sight.isLock()) 
				comp.setBackground(new Color(90,175,240));	
					
			if(isSelected)
				table.setSelectionBackground(new Color(90,175,240));
						
		}  else { // if cell is empty
					comp.setBackground(null);
					table.setSelectionBackground(null);
					}
				
		if(sight.isIntermediate()) 
			comp.setBackground(Color.GRAY);
		
		 return comp;
	}
}
