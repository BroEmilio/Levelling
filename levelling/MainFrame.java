package levelling;

import levellingTable.*;
//import tests.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


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
	public static JCheckBox complementElevationsChoosed;
	AttachedFile attachedFile = new AttachedFile(model);
	public static JLabel labelFileName;
	public static JLabel labalFileInstrution;
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
    	
        createMenuBar();
        createPanel();
        setPolish();

        setTitle("NIWELACJA v1.1");
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
        		"<body bgcolor=\"rgb(214,217,223)\" align=\"center\">"+
        		"<h2 ><u>NIWELACJA v1.1</u></h2>"
        		+"Filmy instrukta¿owe na YouTube:<br><br>"
        		+"1. Jak doliczyæ kilka odczytów w niwelacji<br>"
        		+"https://youtu.be/GFjh4u5o28g          (<a href=https://youtu.be/GFjh4u5o28g'>link</a>)<br>"
        		+"<br>2. Jak wykreowaæ ca³¹ niwelacjê<br>"
        		+"https://youtu.be/ReyWcnZMvvc          (<a href='https://youtu.be/ReyWcnZMvvc'>link</a>)<br>"
        		+"<br>3. Zmiany w wersji 1.1 programu<br>"
        		+"https://youtu.be/HNM-BNTBxic          (<a href='https://youtu.be/HNM-BNTBxic'>link</a>)<br><br>"
        		+"Je¿eli doceniasz pracê w³o¿on¹ w powstanie tej aplikacji - zostaw ³apkê w górê pod filmem<br><br>"
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
    	
    	setTableView();
    	addKeyBindings();
    	GridBagLayout gridBagLayout = new GridBagLayout();
    	gridBagLayout.columnWidths = new int[]{5, 17, 69, 280, 120, 30, 18};
    	gridBagLayout.rowHeights = new int[]{35, 17, 17, 17, 0, 25, 34,404};
    	gridBagLayout.columnWeights = new double[]{0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    	gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0};
    	getContentPane().setLayout(gridBagLayout);
    	
    	JButton calcButton = new JButton("Oblicz niwelacjê", calcIcon);
    	calcButton.setFont(style1);
    	calcButton.setPreferredSize(new Dimension(200, 33));
    	calcButton.setMinimumSize(new Dimension(200, 33));
    	calcButton.setMaximumSize(new Dimension(200, 33));
    	calcButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
	    		if(controlData.controlData()) {
	    			calc.calcLeveling(levellingMetaData.getCalculatingMode(), leaveCurrentValues.isSelected());
	    		}
        	}
    		
    	});
    	GridBagConstraints gbc_calcButton = new GridBagConstraints();
    	gbc_calcButton.anchor = GridBagConstraints.NORTH;
    	gbc_calcButton.insets = new Insets(5, 0, 0, 0);
    	gbc_calcButton.gridx = 3;
    	gbc_calcButton.gridy = 0;
    	getContentPane().add(calcButton, gbc_calcButton);
    	
    	
    	secondCalcButton = new JButton("Oblicz drugie odczyty", secondCalcIcon);
    	secondCalcButton.setFont(style1);
    	secondCalcButton.setPreferredSize(new Dimension(200, 33));
    	secondCalcButton.setMinimumSize(new Dimension(200, 33));
    	secondCalcButton.setMaximumSize(new Dimension(200, 33));
    	secondCalcButton.setEnabled(false);
    	secondCalcButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if(leaveCurrentValues.isSelected())
        			secondCalc.complementSecondCalc();
        		else
        			secondCalc.setSecondValues();
        	}
    	});
    	GridBagConstraints gbc_secondCalcButton = new GridBagConstraints();
    	gbc_secondCalcButton.anchor = GridBagConstraints.NORTH;
    	gbc_secondCalcButton.insets = new Insets(5, 0, 5, 5);
    	gbc_secondCalcButton.gridx = 4;
    	gbc_secondCalcButton.gridy = 0;
    	getContentPane().add(secondCalcButton, gbc_secondCalcButton);
    	
    	JLabel F5Label = new JLabel(" F5 ");
    	F5Label.setForeground(Color.WHITE);
    	F5Label.setOpaque(true);
    	F5Label.setBackground(new Color(90,175,240));
    	F5Label.setHorizontalAlignment(SwingConstants.CENTER);
    	F5Label.setFont(style1);
    	GridBagConstraints gbc_F5Label = new GridBagConstraints();
    	gbc_F5Label.anchor = GridBagConstraints.SOUTHEAST;
    	gbc_F5Label.insets = new Insets(0, 0, 5, 5);
    	gbc_F5Label.gridx = 2;
    	gbc_F5Label.gridy = 1;
    	getContentPane().add(F5Label, gbc_F5Label);
    	
    	JLabel F1Label = new JLabel(" F1 ");
    	F1Label.setForeground(Color.WHITE);
    	F1Label.setOpaque(true);
    	F1Label.setBackground(Color.GRAY);
    	F1Label.setHorizontalAlignment(SwingConstants.CENTER);
    	F1Label.setFont(style1);
    	GridBagConstraints gbc_F1Label = new GridBagConstraints();
    	gbc_F1Label.anchor = GridBagConstraints.SOUTHEAST;
    	gbc_F1Label.insets = new Insets(0, 0, 5, 5);
    	gbc_F1Label.gridx = 2;
    	gbc_F1Label.gridy = 2;
    	getContentPane().add(F1Label, gbc_F1Label);
    	
    	JLabel constFieldsLabel = new JLabel("-  oznacz/odblokuj punkt pocz¹tkowy lub koñcowy niwelacji");
    	constFieldsLabel.setFont(style1);
    	GridBagConstraints gbc_constFieldsLabel = new GridBagConstraints();
    	gbc_constFieldsLabel.gridwidth = 2;
    	gbc_constFieldsLabel.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_constFieldsLabel.insets = new Insets(0, 0, 5, 5);
    	gbc_constFieldsLabel.gridx = 3;
    	gbc_constFieldsLabel.gridy = 1;
    	getContentPane().add(constFieldsLabel, gbc_constFieldsLabel);
    	
    	JLabel posredniInstructionLabel = new JLabel("-  oznacz punkt poœredni niwelacji (tylko w trybie kreowania)");
    	posredniInstructionLabel.setFont(style1);
    	GridBagConstraints gbc_posredniInstructionLabel = new GridBagConstraints();
    	gbc_posredniInstructionLabel.gridwidth = 2;
    	gbc_posredniInstructionLabel.anchor = GridBagConstraints.SOUTH;
    	gbc_posredniInstructionLabel.fill = GridBagConstraints.HORIZONTAL;
    	gbc_posredniInstructionLabel.insets = new Insets(0, 0, 5, 5);
    	gbc_posredniInstructionLabel.gridx = 3;
    	gbc_posredniInstructionLabel.gridy = 2;
    	getContentPane().add(posredniInstructionLabel, gbc_posredniInstructionLabel);
    	
    	JLabel newRowLabel = new JLabel("Ctrl+W - wstaw nowy wiersz,  Ctrl+K - skasuj zaznaczony wiersz");
    	newRowLabel.setFont(style1);
    	GridBagConstraints gbc_newRowLabel = new GridBagConstraints();
    	gbc_newRowLabel.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_newRowLabel.insets = new Insets(0, 0, 5, 5);
    	gbc_newRowLabel.gridwidth = 3;
    	gbc_newRowLabel.gridx = 2;
    	gbc_newRowLabel.gridy = 3;
    	getContentPane().add(newRowLabel, gbc_newRowLabel);
    	
    	leaveCurrentValues = new JCheckBox(" nie zmieniaj istniej¹cych odczytów", false);
    	leaveCurrentValues.setHorizontalAlignment(SwingConstants.LEFT);
    	leaveCurrentValues.setFont(style1);
    	GridBagConstraints gbc_leaveCurrentValues = new GridBagConstraints();
    	gbc_leaveCurrentValues.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_leaveCurrentValues.insets = new Insets(0, 0, 5, 5);
    	gbc_leaveCurrentValues.gridwidth = 2;
    	gbc_leaveCurrentValues.gridx = 2;
    	gbc_leaveCurrentValues.gridy = 4;
    	getContentPane().add(leaveCurrentValues, gbc_leaveCurrentValues);
    	
    	complementElevationsChoosed = new JCheckBox(" automatycznie uzupe³niaj puste rzêdne", true);
    	complementElevationsChoosed.setHorizontalAlignment(SwingConstants.RIGHT);
    	complementElevationsChoosed.setFont(style1);
    	GridBagConstraints gbc_complementElevationsChoosed = new GridBagConstraints();
    	gbc_complementElevationsChoosed.gridwidth = 2;
    	gbc_complementElevationsChoosed.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_complementElevationsChoosed.insets = new Insets(0, 0, 5, 5);
    	gbc_complementElevationsChoosed.gridx = 4;
    	gbc_complementElevationsChoosed.gridy = 4;
    	getContentPane().add(complementElevationsChoosed, gbc_complementElevationsChoosed);
    	
    	JLabel wsteczLabel = new JLabel("  odczyt wstecz  ");
    	wsteczLabel.setOpaque(true);
    	wsteczLabel.setBackground(Color.YELLOW);
    	wsteczLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	wsteczLabel.setFont(style1);
    	GridBagConstraints gbc_wsteczLabel = new GridBagConstraints();
    	gbc_wsteczLabel.anchor = GridBagConstraints.SOUTH;
    	gbc_wsteczLabel.insets = new Insets(0, 0, 5, 5);
    	gbc_wsteczLabel.gridx = 5;
    	gbc_wsteczLabel.gridy = 1;
    	getContentPane().add(wsteczLabel, gbc_wsteczLabel);
    	
    	JLabel wprzodLabel = new JLabel(" odczyt w przód ");
    	wprzodLabel.setOpaque(true);
    	wprzodLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	wprzodLabel.setFont(style1);
    	wprzodLabel.setBackground(Color.RED);
    	GridBagConstraints gbc_wprzodLabel = new GridBagConstraints();
    	gbc_wprzodLabel.anchor = GridBagConstraints.SOUTH;
    	gbc_wprzodLabel.insets = new Insets(0, 0, 5, 5);
    	gbc_wprzodLabel.gridx = 5;
    	gbc_wprzodLabel.gridy = 2;
    	getContentPane().add(wprzodLabel, gbc_wprzodLabel);
    	
    	JLabel posredniLabel = new JLabel("odczyt poœredni");
    	posredniLabel.setOpaque(true);
    	posredniLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	posredniLabel.setFont(style1);
    	posredniLabel.setBackground(Color.GRAY);
    	GridBagConstraints gbc_posredniLabel = new GridBagConstraints();
    	gbc_posredniLabel.anchor = GridBagConstraints.SOUTH;
    	gbc_posredniLabel.insets = new Insets(0, 0, 5, 5);
    	gbc_posredniLabel.gridx = 5;
    	gbc_posredniLabel.gridy = 3;
    	getContentPane().add(posredniLabel, gbc_posredniLabel);
    	
    	labalFileInstrution = new JLabel("Za³¹czony plik z rzêdnymi (<NR> <X> <Y> <H>) :");
    	labalFileInstrution.setFont(new Font("Arial", Font.ITALIC, 14));
    	GridBagConstraints gbc_labalFileInstrution = new GridBagConstraints();
    	gbc_labalFileInstrution.anchor = GridBagConstraints.SOUTHWEST;
    	gbc_labalFileInstrution.insets = new Insets(0, 0, 5, 5);
    	gbc_labalFileInstrution.gridwidth = 2;
    	gbc_labalFileInstrution.gridx = 2;
    	gbc_labalFileInstrution.gridy = 5;
    	getContentPane().add(labalFileInstrution, gbc_labalFileInstrution);
    	
    	JButton buttonChooseAttachedFile = new JButton("Wybierz");
    	buttonChooseAttachedFile.setFont(style2);
    	buttonChooseAttachedFile.setPreferredSize(new Dimension(80, 25));
    	buttonChooseAttachedFile.setMinimumSize(new Dimension(80, 25));
    	buttonChooseAttachedFile.setMaximumSize(new Dimension(80, 25));
    	buttonChooseAttachedFile.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		attachedFile = new AttachedFile(model);
	    		attachedFile.chooseAttachedFile();
        	}
    	});
    	GridBagConstraints gbc_buttonChooseAttachedFile = new GridBagConstraints();
    	gbc_buttonChooseAttachedFile.insets = new Insets(0, 0, 5, 5);
    	gbc_buttonChooseAttachedFile.gridx = 2;
    	gbc_buttonChooseAttachedFile.gridy = 6;
    	getContentPane().add(buttonChooseAttachedFile, gbc_buttonChooseAttachedFile);
    	
    	labelFileName = new JLabel("brak");
    	labelFileName.setFont(new Font("Arial", Font.ITALIC, 12));
    	GridBagConstraints gbc_labelFileName = new GridBagConstraints();
    	gbc_labelFileName.gridwidth = 2;
    	gbc_labelFileName.anchor = GridBagConstraints.WEST;
    	gbc_labelFileName.insets = new Insets(0, 0, 5, 5);
    	gbc_labelFileName.gridx = 3;
    	gbc_labelFileName.gridy = 6;
    	getContentPane().add(labelFileName, gbc_labelFileName);
    	
    	JButton buttonUnlink = new JButton("Odepnij");
    	buttonChooseAttachedFile.setFont(style2);
    	buttonUnlink.setPreferredSize(new Dimension(90, 22));
    	buttonUnlink.setMinimumSize(new Dimension(90, 22));
    	buttonUnlink.setMaximumSize(new Dimension(90, 22));
    	buttonUnlink.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			attachedFile.unlinkAttachedFile();
    		}
    	});
    	buttonUnlink.setFont(new Font("Arial", Font.ITALIC, 14));
    	GridBagConstraints gbc_buttonUnlink = new GridBagConstraints();
    	gbc_buttonUnlink.insets = new Insets(0, 0, 5, 5);
    	gbc_buttonUnlink.gridx = 5;
    	gbc_buttonUnlink.gridy = 6;
    	getContentPane().add(buttonUnlink, gbc_buttonUnlink);
    	
    	JScrollPane tablePanel = new JScrollPane(table);
    	GridBagConstraints gbc_tablePanel = new GridBagConstraints();
    	gbc_tablePanel.gridheight = 2;
    	gbc_tablePanel.fill = GridBagConstraints.BOTH;
    	gbc_tablePanel.gridwidth = 7;
    	gbc_tablePanel.gridx = 0;
    	gbc_tablePanel.gridy = 7;
    	getContentPane().add(tablePanel, gbc_tablePanel);
    	
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
    	table.getColumnModel().getColumn(7).setCellEditor(new CellEditorForDouble(table, model));
    	
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
            	 if(col==7 && ! odczyt.isBackSight());
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
        
        //@SuppressWarnings("unused")
		//InsertElevationsAndCalculateLevelling test1 = new InsertElevationsAndCalculateLevelling();
        
    }
}