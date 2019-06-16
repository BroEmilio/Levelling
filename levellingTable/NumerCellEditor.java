package levellingTable;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class NumerCellEditor extends DefaultCellEditor{
	private static final long serialVersionUID = 1L;
		
	private Object value;
	JTable table;
	JTextField textField;
	int currentRow;
	
    public NumerCellEditor(JTable table) {
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
        Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        textField = (JTextField)c;
        textField.setBorder( new LineBorder(new Color(45, 165, 255) ,1) );
        

        return c;
    }
}
