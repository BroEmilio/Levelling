package levellingTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

public class CellEditorForInteger extends DefaultCellEditor{
	private static final long serialVersionUID = 1L;
		
	JFormattedTextField fTextField;
    NumberFormat integerFormat;
    private Integer minimum, maximum;
    private boolean DEBUG = false;
    int row, column;
    JTable table;
 
    public CellEditorForInteger(int min, int max) {
        super(new JFormattedTextField());
        fTextField = (JFormattedTextField)getComponent();
        minimum = new Integer(min);
        maximum = new Integer(max);
 
        //Set up the editor for the integer cells.
        NumberFormatter intFormatter = new NumberFormatter(integerFormat);
        intFormatter.setMinimum(minimum);
        intFormatter.setMaximum(maximum);
 
        fTextField.setFormatterFactory(new DefaultFormatterFactory(intFormatter));
        fTextField.setValue(minimum);
        fTextField.setHorizontalAlignment(JTextField.TRAILING);
        fTextField.setFocusLostBehavior(JFormattedTextField.PERSIST);
 
        //React when the user presses Enter while the editor is
        //active.  (Tab is handled as specified by
        fTextField.getInputMap().put(KeyStroke.getKeyStroke(
                                        KeyEvent.VK_ENTER, 0),
                                        "check");
        fTextField.getActionMap().put("check", new AbstractAction() {
            private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				if (!fTextField.isEditValid()) { //The text is invalid.
					if(fTextField.getText().isEmpty()) {
						fTextField.setValue(null);
						fTextField.postActionEvent();
						table.changeSelection(row+1, column, false, false);
					} else  if (userSaysRevert()) { //reverted
						fTextField.postActionEvent(); //inform the editor
					}
				} else try {              //The text is valid,
                    fTextField.commitEdit();     //so use it.
                    fTextField.postActionEvent(); //stop editing
                } catch (java.text.ParseException exc) { }
            }
        });
    }
 
    //Override to invoke setValue on the formatted text field.
    public Component getTableCellEditorComponent(JTable table,Object value, boolean isSelected, int row, int column) {
    	this.row = row;
    	this.column = column;
    	this.table = table;
    	JFormattedTextField ftf = (JFormattedTextField)super.getTableCellEditorComponent(table, value, isSelected, row, column);
    	if(isSelected) {
    		ftf.setBorder(new LineBorder(new Color(45, 165, 255) ,1));
    	}
        ftf.setValue(value);

        return ftf;
    }
 
    //Override to ensure that the value remains an Integer.
    public Object getCellEditorValue() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        Object o = ftf.getValue();
        if(o==null) {
        	return null;
        }
        if (o instanceof Integer) {
            return o;
        } else if (o instanceof Number) {
            return new Integer(((Number)o).intValue());
        } else {
            if (DEBUG) {
                System.err.println("getCellEditorValue: o isn't a Number");
            }
            try {
                return integerFormat.parseObject(o.toString());
            } catch (ParseException exc) {
                System.err.println("getCellEditorValue: can't parse o: " + o);
                return null;
            }
        }
    }
 
    //Override to check whether the edit is valid,
    //setting the value if it is and complaining if
    //it isn't.  If it's OK for the editor to go
    //away, we need to invoke the superclass's version 
    //of this method so that everything gets cleaned up.
    public boolean stopCellEditing() {
        JFormattedTextField ftf = (JFormattedTextField)getComponent();
        if (ftf.isEditValid()) {
            try {
                ftf.commitEdit();
            } catch (java.text.ParseException exc) { }
         
        } else { //text is invalid
        	if(ftf.getText().isEmpty()) {
        		ftf.setValue(null);
        		return super.stopCellEditing();
        		}
            if (!userSaysRevert()) { //user wants to edit
            return false; //don't let the editor go away
        } 
        }
        return super.stopCellEditing();
    }
 
    /** 
     * Lets the user know that the text they entered is 
     * bad. Returns true if the user elects to revert to
     * the last good value.  Otherwise, returns false, 
     * indicating that the user wants to continue editing.
     */
    protected boolean userSaysRevert() {
        Toolkit.getDefaultToolkit().beep();
        fTextField.selectAll();
        Object[] options = {"Edytuj",
                            "Przywr�c"};
        int answer = JOptionPane.showOptionDialog(
            SwingUtilities.getWindowAncestor(fTextField),
            "Warto�� powinna by� z zakresu  "
            + minimum + " a "
            + maximum + ".\n"
            + "Mo�esz edytowa� bie��c� warto�� "
            + "lub przywr�ci� ostatni� poprawn�.",
            "Wprowadzono warto�� spoza zakresu",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.ERROR_MESSAGE,
            null,
            options,
            options[1]);
         
        if (answer == 1) { //Revert!
            fTextField.setValue(fTextField.getValue());
        return true;
        }
    return false;
    }
}
