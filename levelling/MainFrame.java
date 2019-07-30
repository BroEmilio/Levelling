package levelling;

import levellingTable.*;
import tests.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.GroupLayout.*;
import javax.swing.LayoutStyle.*;


public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	LevellingTableModel model = new LevellingTableModel();
	LevellingMetaData levellingMetaData = new LevellingMetaData();
	RXTable table = new RXTable(model);
	ControlData controlData = new ControlData(model, levellingMetaData);
	Calculating calc = new Calculating(model, levellingMetaData);
	SecondCalculating secondCalc = new SecondCalculating(model, levellingMetaData);
	public static JButton secondCalcButton;
	JCheckBox leaveCurrentValues;
	Font style1 = new Font("Arial", Font.ITALIC, 14);
	Font style2 = new Font("Arial", Font.ITALIC, 12);

	public MainFrame() {

        initUI();
    }

    private void initUI() {
    	
    	try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception e) {
			e.printStackTrace();
		}
    	setPolish();
        createMenuBar();
        createPanel();

        setTitle("NIWELACJA v0.9531");
        setMinimumSize(new Dimension(750, 450));
        setSize(new Dimension(750, 695));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createMenuBar() {
        JMenuBar menubar = new JMenuBar();
        ImageIcon fileIcon = new ImageIcon("./icons/file.png");
        ImageIcon exitIcon = new ImageIcon("./icons/exit.png");
        ImageIcon loadIcon = new ImageIcon("./icons/open.png");
        ImageIcon saveIcon = new ImageIcon("./icons/save.png");
        ImageIcon infoIcon = new ImageIcon("./icons/info.png");

        JMenu file = new JMenu("Plik");
        file.setIcon(fileIcon);
        file.setFont(style2);
        //file.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newItem = new JMenuItem("Nowy", fileIcon);
        newItem.setFont(style2);
        //newItem.setMnemonic(KeyEvent.VK_N);
        newItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
	        	model.getLevellingData().clear();
	        	for(int i=0; i<500; i++)
	    			model.getLevellingData().add(new Sight());
	        	model.fireTableDataChanged();
	        	secondCalcButton.setEnabled(false);
        	}
        });
        
        JMenuItem saveItem = new JMenuItem("Zapisz", saveIcon);
        saveItem.setFont(style2);
        //saveItem.setMnemonic(KeyEvent.VK_S);
        saveItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
	        	SaveLevellingFile saver = new SaveLevellingFile(model);
	        	saver.saveToNiwFile();
        	}
        });
        
        JMenuItem loadItem = new JMenuItem("Wczytaj", loadIcon);
        loadItem.setFont(style2);
        //loadItem.setMnemonic(KeyEvent.VK_L);
        loadItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
	        	LoadLevellingFile loader = new LoadLevellingFile(model);
	        	if(loader.loadFromNiwFile()==false) {
	        		JOptionPane.showMessageDialog(null,
	    					"Wyst¹pi³ b³ad przy odczycie pliku",
	    			        "B³ad odczytu pliku",
	    			        JOptionPane.ERROR_MESSAGE);
		        	model.getLevellingData().clear();
		        	for(int i=0; i<500; i++)
		    			model.getLevellingData().add(new Sight());
		        	model.fireTableDataChanged();
		        	secondCalcButton.setEnabled(false);
	        	}
        	}
        });

        JMenuItem exitItem = new JMenuItem("Wyjœcie", exitIcon);
        exitItem.setFont(style2);
        //exitItem.setMnemonic(KeyEvent.VK_E);
        //exitItem.setToolTipText("Zamknij program");
        exitItem.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		System.exit(0);
        	}
        });
        
        file.add(newItem);
        file.add(saveItem);
        file.add(loadItem);
        file.add(exitItem);
        
        final JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");//set content as html
        editorPane.setEditable(false);
        editorPane.setBorder(null);
        editorPane.setText(
        		"<body bgcolor=\"rgb(214,217,223)\">"+
        		"<h2 align=\"center\"><u>NIWELACJA v0.9531</u></h2>"
        		+"Film ukazuj¹cy obs³ugê programu w celu doliczenia niektórych odczytów niwelacji<br>"
        		+"https://youtu.be/GFjh4u5o28g          (<a href=https://youtu.be/GFjh4u5o28g'>link</a>)<br>"
        		+"<br>Film prezentuj¹cy jak korzystaæ z aplikacji aby wykreowaæ ca³¹ niwelacjê <br>"
        		+"https://youtu.be/ReyWcnZMvvc          (<a href='https://youtu.be/ReyWcnZMvvc'>link</a>)<br><br>"
        		+"Ewentualne uwagi proszê kierowaæ na adres: bro.emilio.1.1@gmail.com</body>");
        editorPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    //System.out.println(hle.getURL());
                    Desktop desktop = Desktop.getDesktop();
                    try {
                        desktop.browse(hle.getURL().toURI());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        JButton info = new JButton("Info",infoIcon);
        info.setBorderPainted(false);
        info.setFocusPainted(false);
        info.setContentAreaFilled(false);
        info.setFont(style2);
        info.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		JOptionPane.showMessageDialog(null,editorPane,
    			        "O programie",
    			        JOptionPane.INFORMATION_MESSAGE);
        	}
        });
        
        
        
        menubar.add(file);
        menubar.add(info);

        setJMenuBar(menubar);
    }

    private void createPanel() {
    	ImageIcon calcIcon = new ImageIcon("./icons/calc.png");
    	ImageIcon secondCalcIcon = new ImageIcon("./icons/secondCalc.png");
    	
    	
    	secondCalcButton = new JButton("Oblicz drugie odczyty", secondCalcIcon);
    	secondCalcButton.setFont(style1);
    	secondCalcButton.setPreferredSize(new Dimension(210,35));
    	secondCalcButton.setEnabled(false);
    	
    	JButton calcButton = new JButton("Oblicz niwelacjê", calcIcon);
    	calcButton.setFont(style1);
    	calcButton.setPreferredSize(new Dimension(210,35));
    	calcButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
	    		if(controlData.controlData()) {
	    			calc.calcLeveling(levellingMetaData.getCalculatingMode(), leaveCurrentValues.isSelected());
	    		}
        	}
    		
    	});
    	secondCalcButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if(leaveCurrentValues.isSelected())
        			secondCalc.complementSecondCalc();
        		else
        			secondCalc.secondCalc();
        	}
    	});
    	JLabel F5Label = new JLabel("F5");
    	F5Label.setForeground(Color.WHITE);
    	F5Label.setOpaque(true);
    	F5Label.setBackground(new Color(90,175,240));
    	F5Label.setHorizontalAlignment(SwingConstants.CENTER);
    	F5Label.setFont(style1);
    	JLabel F1Label = new JLabel("F1");
    	F1Label.setForeground(Color.WHITE);
    	F1Label.setOpaque(true);
    	F1Label.setBackground(Color.GRAY);
    	F1Label.setHorizontalAlignment(SwingConstants.CENTER);
    	F1Label.setFont(style1);
    	JLabel constFieldsLabel = new JLabel("-  oznacz/odblokuj punkt pocz¹tkowy lub koñcowy niwelacji");
    	constFieldsLabel.setFont(style1);
    	JLabel posredniInstructionLabel = new JLabel("-  oznacz punkt poœredni niwelacji (tylko w trybie kreowania)");
    	posredniInstructionLabel.setFont(style1);
    	JLabel newRowLabel = new JLabel("Ctrl+W - wstaw nowy wiersz,  Ctrl+K - skasuj zaznaczony wiersz");
    	newRowLabel.setFont(style1);
    	JLabel wsteczLabel = new JLabel("odczyt wstecz");
    	wsteczLabel.setOpaque(true);
    	wsteczLabel.setBackground(Color.YELLOW);
    	wsteczLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	wsteczLabel.setFont(style1);
    	JLabel wprzodLabel = new JLabel("odczyt wprzód");
    	wprzodLabel.setOpaque(true);
    	wprzodLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	wprzodLabel.setFont(style1);
    	wprzodLabel.setBackground(Color.RED);
    	JLabel posredniLabel = new JLabel("odczyt poœredni");
    	posredniLabel.setOpaque(true);
    	posredniLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	posredniLabel.setFont(style1);
    	posredniLabel.setBackground(Color.GRAY);
    	
    	leaveCurrentValues = new JCheckBox(" nie zmieniaj istniej¹cych odczytów", false);
    	leaveCurrentValues.setHorizontalAlignment(SwingConstants.LEFT);
    	leaveCurrentValues.setFont(style1);
    	
    	setTableView();
    	addKeyBindings();
    	
    	JScrollPane tablePanel = new JScrollPane(table);
    	
    	
    	
    	GroupLayout groupLayout = new GroupLayout(getContentPane());
    	groupLayout.setHorizontalGroup(
    		groupLayout.createParallelGroup(Alignment.LEADING)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addGap(10)
    				.addGroup(groupLayout.createParallelGroup(Alignment.CENTER)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, 719, Short.MAX_VALUE)
    						.addContainerGap())
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    							.addGroup(groupLayout.createSequentialGroup()
    								.addGap(5)
    								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    									.addGroup(groupLayout.createSequentialGroup()
    										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    											.addComponent(F1Label, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE)
    											.addComponent(F5Label, GroupLayout.PREFERRED_SIZE, 30, GroupLayout.PREFERRED_SIZE))
    										.addPreferredGap(ComponentPlacement.RELATED)
    										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    											.addComponent(constFieldsLabel, GroupLayout.PREFERRED_SIZE, 386, GroupLayout.PREFERRED_SIZE)
    											.addComponent(posredniInstructionLabel, GroupLayout.PREFERRED_SIZE, 394, GroupLayout.PREFERRED_SIZE)))
    									.addGroup(groupLayout.createSequentialGroup()
    										.addPreferredGap(ComponentPlacement.RELATED)
    										.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
    											.addComponent(leaveCurrentValues, GroupLayout.PREFERRED_SIZE, 366, GroupLayout.PREFERRED_SIZE)
    											.addComponent(newRowLabel, GroupLayout.PREFERRED_SIZE, 404, GroupLayout.PREFERRED_SIZE))))
    								.addGap(38)
    								.addGroup(groupLayout.createParallelGroup(Alignment.LEADING, false)
    									.addComponent(wprzodLabel, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
    									.addComponent(posredniLabel, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE)
    									.addComponent(wsteczLabel, GroupLayout.PREFERRED_SIZE, 112, GroupLayout.PREFERRED_SIZE))
    								.addGap(12))
    							.addGroup(groupLayout.createSequentialGroup()
    								.addGap(109)
    								.addComponent(calcButton, GroupLayout.PREFERRED_SIZE, 0, 200)
    								.addGap(88)
    								.addComponent(secondCalcButton, GroupLayout.PREFERRED_SIZE, 0, 200)))
    						.addGap(132))))
    	);
    	groupLayout.setVerticalGroup(
    		groupLayout.createParallelGroup(Alignment.CENTER)
    			.addGroup(groupLayout.createSequentialGroup()
    				.addContainerGap()
    				.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    					.addComponent(secondCalcButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
    					.addComponent(calcButton, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
    				.addPreferredGap(ComponentPlacement.UNRELATED)
    				.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
    					.addGroup(groupLayout.createSequentialGroup()
    						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    							.addComponent(constFieldsLabel, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
    							.addComponent(F5Label, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
    							.addComponent(wsteczLabel))
    						.addGap(7)
    						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
    							.addComponent(F1Label)
    							.addComponent(posredniInstructionLabel)
    							.addComponent(wprzodLabel, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
    						.addPreferredGap(ComponentPlacement.RELATED)
    							.addComponent(newRowLabel))
    							.addComponent(posredniLabel, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE))
    						.addComponent(leaveCurrentValues, GroupLayout.PREFERRED_SIZE, 25, GroupLayout.PREFERRED_SIZE)
    						.addPreferredGap(ComponentPlacement.UNRELATED)
    				.addComponent(tablePanel, GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
    				.addGap(16))
    	);
    	getContentPane().setLayout(groupLayout);
    	
    }
    
    void setTableView() {
    	table.setSelectAllForEdit(true);
    	table.setShowGrid(true);
    	table.getColumnModel().getColumn(0).setMaxWidth(35);
    	table.getColumnModel().getColumn(1).setPreferredWidth(80);
    	table.getColumnModel().getColumn(2).setPreferredWidth(100);
    	table.getColumnModel().getColumn(3).setPreferredWidth(105);
    	table.getColumnModel().getColumn(4).setPreferredWidth(100);
    	table.getColumnModel().getColumn(5).setPreferredWidth(100);
    	table.getColumnModel().getColumn(6).setPreferredWidth(70);
    	table.getColumnModel().getColumn(7).setPreferredWidth(100);
    	table.setRowHeight(22);
    	table.setCellSelectionEnabled(true);
    	
    	//set cells renderers
    	table.getColumnModel().getColumn(1).setCellRenderer(new CellRendererPointNumber(model));
    	table.getColumnModel().getColumn(2).setCellRenderer(new CellRendererBackOrForeSight(model));
    	table.getColumnModel().getColumn(3).setCellRenderer(new CellRendererBackOrForeSight(model));
    	table.getColumnModel().getColumn(4).setCellRenderer(new CellRendererIntermediateSight(model));
    	table.getColumnModel().getColumn(5).setCellRenderer(new CellRendererIntermediateSight(model));
    	table.getColumnModel().getColumn(6).setCellRenderer(new CellRendererDifference(model));
    	table.getColumnModel().getColumn(7).setCellRenderer(new CellRendererElevation(model));
    	
    	//set cells editors
    	table.getColumnModel().getColumn(1).setCellEditor(new CellEditorForPointNumber(table));
    	table.getColumnModel().getColumn(2).setCellEditor(new CellEditorForInteger(0,5000));
    	table.getColumnModel().getColumn(3).setCellEditor(new CellEditorForInteger(0,5000));
    	table.getColumnModel().getColumn(4).setCellEditor(new CellEditorForInteger(0,5000));
    	table.getColumnModel().getColumn(5).setCellEditor(new CellEditorForInteger(0,5000));
    	table.getColumnModel().getColumn(7).setCellEditor(new CellEditorForDouble(table));
    	
    }
    
    private void addKeyBindings() {
        //root maps
        InputMap im = table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        ActionMap am = table.getActionMap();
        
        //add custom action
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK), "addRow");
        am.put("addRow", addRow());
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_K, KeyEvent.CTRL_MASK), "deleteRow");
        am.put("deleteRow", deleteRow());
        im.put(KeyStroke.getKeyStroke("F5"), "lockRzednaAction");
        am.put("lockRzednaAction", lockRzednaAction());
        im.put(KeyStroke.getKeyStroke("F1"), "changePosredniStatusAction");
        am.put("changePosredniStatusAction", changePosredniStatusAction());

    }
    
    Action handleEnter = new AbstractAction() {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
            table.getCellEditor().stopCellEditing(); // store user input
            int row = table.getSelectedRow();
            int col = table.getSelectedColumn();
            int indexInModel = table.convertRowIndexToModel(row);
            Object object = table.getValueAt(row, col);
            Sight odczyt = model.getSightAtIndex(indexInModel);
            if(object != null) {
            	 table.getCellEditor().stopCellEditing(); // store user input
            	 if(col==7 && ! odczyt.isBackSight);
            	 table.setValueAt((Double)object, ++row, col);
            	 row++;
            }
            table.changeSelection(row, col, false, false);
            table.editCellAt(row, col);
            //}
        }
    };
        
    private AbstractAction addRow() {
        AbstractAction addRow = new AbstractAction() {
		private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
				int rowIndex = table.getSelectedRow();
                 if(rowIndex!=-1)
                	model.addRow(rowIndex);
                 else System.err.println("-1 rowSelected-add");
            }
        };
        return addRow;
    }

    private AbstractAction deleteRow() {
        AbstractAction deleteRow = new AbstractAction() {
		private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
				int rowIndex = table.getSelectedRow();
				int columnIndex = table.getSelectedColumn();
				if(rowIndex!=-1) {
					model.deleteRow(rowIndex);
					table.changeSelection(rowIndex, columnIndex, false, false);
				}
				else System.err.println("-1 rowSelected-delete");
            }
        };
        return deleteRow;
    }
    
    private AbstractAction lockRzednaAction() {
        AbstractAction lock = new AbstractAction() {
		private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
				int rowIndex = table.getSelectedRow();
				model.changeLockStatus(rowIndex);
                if(rowIndex>0 && model.getSightAtIndex(rowIndex).isLock())
                	model.setAsLastBenchmark(rowIndex);
            }
        };
        return lock;
    }
    
    private AbstractAction changePosredniStatusAction() {
        AbstractAction change = new AbstractAction() {
		private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
				int rowIndex = table.getSelectedRow();
                 if(rowIndex!=-1)
                	 model.changeIntermediateSightStatus(rowIndex);
                 else System.err.println("-1 rowSelected - change posredni status");
            }
        };
        return change;
    }
    
    public void setPolish() {
		UIManager.put("FileChooser.lookInLabelText","Szukaj w");
        UIManager.put("FileChooser.lookInLabelMnemonic",""+KeyEvent.VK_W);
 
        UIManager.put("FileChooser.saveInLabelText","Zapisz w");
        UIManager.put("FileChooser.saveInLabelMnemonic",""+KeyEvent.VK_W);
 
        UIManager.put("FileChooser.fileNameLabelText","Nazwa pliku:");
        UIManager.put("FileChooser.fileNameLabelMnemonic",""+KeyEvent.VK_N);
 
        UIManager.put("FileChooser.folderNameLabelText","Nazwa katalogu:");
        UIManager.put("FileChooser.folderNameLabelMnemonic",""+KeyEvent.VK_N);      
 
        UIManager.put("FileChooser.filesOfTypeLabelText","Pliki typu:");
        UIManager.put("FileChooser.filesOfTypeLabelMnemonic",""+KeyEvent.VK_P);
 
        UIManager.put("FileChooser.upFolderToolTipText","PrzejdŸ wy¿ej");
        UIManager.put("FileChooser.homeFolderToolTipText","Pulpit");
        UIManager.put("FileChooser.newFolderToolTipText","Nowy katalog");
        UIManager.put("FileChooser.listViewButtonToolTipText","Lista");
        UIManager.put("FileChooser.detailsViewButtonToolTipText","Szczegó³y");
 
        UIManager.put("FileChooser.fileNameHeaderText","Nazwa");
        UIManager.put("FileChooser.fileSizeHeaderText","Rozmiar");
        UIManager.put("FileChooser.fileTypeHeaderText","Typ");
        UIManager.put("FileChooser.fileDateHeaderText","Modyfikacja");
        UIManager.put("FileChooser.fileAttrHeaderText","Atrybuty");
 
        UIManager.put("FileChooser.newFolderErrorText","B³¹d podczas tworzenia katalogu");
 
        UIManager.put("FileChooser.saveButtonText","Zapisz");
        UIManager.put("FileChooser.saveButtonMnemonic",""+KeyEvent.VK_Z);
 
        UIManager.put("FileChooser.openButtonText","Otwórz");
        UIManager.put("FileChooser.openButtonMnemonic",""+KeyEvent.VK_O);
 
        UIManager.put("FileChooser.cancelButtonText","Anuluj");
        UIManager.put("FileChooser.openButtonMnemonic",""+KeyEvent.VK_R);
 
        UIManager.put("FileChooser.openDialogTitleText","Otwieranie");
        UIManager.put("FileChooser.saveDialogTitleText","Zapisywanie");
 
        UIManager.put("FileChooser.saveButtonToolTipText","Zapisanie pliku");
        UIManager.put("FileChooser.openButtonToolTipText","Otwarcie pliku");
        UIManager.put("FileChooser.cancelButtonToolTipText","Anuluj");
        UIManager.put("FileChooser.acceptAllFileFilterText","Wszystkie pliki");
	}
    

    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
	            MainFrame ex = new MainFrame();
	            ex.setVisible(true);
            }
        });
        
        @SuppressWarnings("unused")
		InsertElevationsAndCalculateLevelling test1 = new InsertElevationsAndCalculateLevelling();
        
    }
}