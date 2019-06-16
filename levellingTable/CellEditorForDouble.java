package levellingTable;

import levelling.*;
import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class CellEditorForDouble extends DefaultCellEditor{
	private static final long serialVersionUID = 1L;
		
	private Object value;
	JTable table;
	int currentRow;
	
    public CellEditorForDouble(JTable table) {
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
        try {
	            String editingValue = (String)super.getCellEditorValue();
	
	            /*  Don't allow user to enter "."
	
	            if (editingValue.contains(".")){
	                JTextField textField = (JTextField)getComponent();
	                textField.setBorder(new LineBorder(Color.red));
	                return false;
	            } */
	
	            // Replace local specific character
	            if(editingValue.isEmpty()) {
	            	value = null;
	            	return super.stopCellEditing();
	            }
	
	            int offset = editingValue.lastIndexOf(",");
	
	            if (offset != -1){
	                StringBuilder sb = new StringBuilder(editingValue);
	                sb.setCharAt(offset, '.');
	                editingValue = sb.toString();
	            }
	            value = Double.parseDouble( editingValue );
	            
	            int indexInModel = table.convertRowIndexToModel(currentRow);
	            Sight odczyt = ((NiwelacjaTableModel)table.getModel()).getOdczytAtIndex(indexInModel);
	            if(currentRow==0)
					odczyt.setAsBackSight(true);
	            if( ! odczyt.isBackSight() && ! odczyt.isSightIntermediate()) {
	            	table.changeSelection(currentRow+2, 7, false, false);
	            } 
	            if (currentRow == 0 && ! odczyt.isLock()) {
	            	((NiwelacjaTableModel)table.getModel()).setFirstAndLastPoint(currentRow);
	            }
        }
        catch(NumberFormatException exception){
            JTextField textField = (JTextField)getComponent();
            textField.setBorder(new LineBorder(Color.red));
            value = null;
           return false;
        }
        return super.stopCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
    	
    	currentRow=row;
        Component c = super.getTableCellEditorComponent(table, value, isSelected, row, column);
        

        JTextField textField = (JTextField)c;
        textField.setBorder( new LineBorder(new Color(45, 165, 255) ,1) );
        Sight odczyt = ((NiwelacjaTableModel)table.getModel()).getOdczytAtIndex(currentRow);
   	 		if(odczyt.isSightIntermediate())
   	 			textField.setBackground(Color.GRAY);
   	 		else textField.setBackground(null);
   	 		
        String text = textField.getText();
        int offset = text.lastIndexOf(".");
        
        // Display local specific character

        if (offset != -1){
            StringBuilder sb = new StringBuilder(text);
            sb.setCharAt(offset, ',');
            textField.setText( sb.toString() );
        }
        return c;
    }
        
}

