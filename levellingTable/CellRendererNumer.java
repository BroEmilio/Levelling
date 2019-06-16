package levellingTable;

import levelling.*;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class CellRendererNumer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	NiwelacjaTableModel model;
	
	public CellRendererNumer(NiwelacjaTableModel model) {
		this.model=model;
        this.setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,int row,int col) {
		Component comp =  super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
		Sight odczyt = model.getOdczytAtIndex(row);
			if(odczyt.isLock()) {
				setBackground(new Color(90,175,240));	
				if(odczyt.isLock() && isSelected)
						table.setSelectionBackground(new Color(90,175,240));
				else table.setSelectionBackground(null);
			} else {
				comp.setBackground(null);
				table.setSelectionBackground(null);
						if(table.getSelectedRow() == row) {
							 comp.setBackground(Color.LIGHT_GRAY);
						} 
				}
		 return comp;
	}
}
