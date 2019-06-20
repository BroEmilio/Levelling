package levellingTable;

import levelling.*;
import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRendererRzedna extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	LevellingTableModel model;
	private static final DecimalFormat formatter = new DecimalFormat("#0.###");
	
	public CellRendererRzedna(LevellingTableModel model) {
		super();
		this.model=model;
        formatter.setMinimumFractionDigits(3);
        this.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) {
		if(value instanceof Double) {
	         value = formatter.format((Number)value);
		}
		Component comp =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		int indexInModel = table.convertRowIndexToModel(row);
		Sight odczyt = model.getOdczytAtIndex(indexInModel);
		
				 if(odczyt.isLock() || odczyt.isSightIntermediate() || value != null ) {
							 if(odczyt.isBackSight()) {
								 comp.setBackground(Color.YELLOW);
								 if(row>1) {
									 
								 }
							 } else comp.setBackground(Color.RED);
							 
							 
					 		if(odczyt.isLock()) {
									comp.setBackground(new Color(90,175,240));	
						 
									if(isSelected)
											table.setSelectionBackground(new Color(90,175,240));
					 		}
					 		
					 		if(odczyt.isSightIntermediate()) {
									comp.setBackground(Color.GRAY);	
				 		}
						
				}  else {
					comp.setBackground(null);
					table.setSelectionBackground(null);
					}
		 return comp;
	}
}
