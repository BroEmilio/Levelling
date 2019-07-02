package levellingTable;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

public class CellEditorForPointNumber extends DefaultCellEditor{
	private static final long serialVersionUID = 1L;
	
	JTable table;	
	private Object value;
	JTextField textField;
	int currentRow;
	
    public CellEditorForPointNumber(JTable table) {
        super( new JTextField() );
        ((JTextField)getComponent()).setHorizontalAlignment(JTextField.RIGHT);
        this.table = table;
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }

    @Override
    public boolean stopCellEditing(){
    	value=super.getCellEditorValue();
    	table.changeSelection(currentRow, 2, false, false);

        return super.stopCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    	currentRow=row;
        Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        textField = (JTextField)comp;
        textField.setBorder( new LineBorder(new Color(45, 165, 255) ,1) );

        return comp;
    }
}
