package levellingTable;

import levelling.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;


public class CellEditorForDouble extends DefaultCellEditor{
	private static final long serialVersionUID = 1L;
		
	private Object value;
	JTable table;
	LevellingTableModel model;
	int currentRow;
	
    public CellEditorForDouble(JTable table, LevellingTableModel model) {
        super( new JTextField() );
        ((JTextField)getComponent()).setHorizontalAlignment(JTextField.RIGHT);
        this.table = table;
        this.model = model;
    }

    @Override
    public Object getCellEditorValue() {
        return value;
    }

    @Override
    public boolean stopCellEditing(){
    	Sight sight = null;
        try {
	            String editingValue = (String)super.getCellEditorValue();
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
	            sight = ((LevellingTableModel)table.getModel()).getSightAtIndex(currentRow);
	            
        }
        catch(NumberFormatException exception){
            JTextField textField = (JTextField)getComponent();
            textField.setBorder(new LineBorder(Color.red));
            value = null;
           return false;
        }
        
        if(currentRow==0)
			sight.setAsBackSight(true);
    	
        if (currentRow == 0 && ! sight.isLock()) {
        	((LevellingTableModel)table.getModel()).setAsFirstBenchmark();
        }
        
        if(model.shuoldBeAsBackSight2(currentRow) && ! sight.isIntermediate()) {
        	table.changeSelection(table.getEditingRow()+2, 7, false, false);
        }
        
        return super.stopCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
    											 boolean isSelected, int row, int column) {
    	currentRow=row;
        Component comp = super.getTableCellEditorComponent(table, value, isSelected, row, column);

        JTextField textField = (JTextField)comp;
        textField.setBorder( new LineBorder(new Color(45, 165, 255) ,1) );
        Sight sight = ((LevellingTableModel)table.getModel()).getSightAtIndex(currentRow);
   	 		if(sight.isIntermediate())
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
        return comp;
    }
    
    
}

