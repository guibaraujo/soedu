package simuos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import simuos.controller.Kernel;
import simuos.controller.Page;
import simuos.util.Mylib;

public class VConfig extends JFrame {

	Kernel kernelSoEdu = new Kernel(this);

	/**
	 * 
	 */
	int numColsGantt = 2000;
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	public JLabel lblBuscapagina;
	public JLabel lblExecucao;
	public JLabel lblPaginasProcessoExec;
	public JLabel lblBloqueado;
	public JLabel lblPronto;
	public JLabel lblEncerrado;
	public JLabel lblAcertoDePg;
	public JLabel lblFaltaDePg;
	public JLabel lblTurnaround;

	public JTextField txtNumProcessos;
	public JTextField txtQuantum;
	public JTextField txtSobrecarga;
	private JTextField txtNumpaginas;

	public JRadioButton rdbtnSjf;
	public JRadioButton rdbtnFifo;
	public JRadioButton rdbtnRr;
	public JRadioButton rdbtnEdf;
	public JRadioButton rdbtnMemFifo;
	public JRadioButton rdbtnMemLru;

	public JTable tableProcessos;
	public JTable tableRam;
	public JTable tableSwap;
	public JTable tableGantt;
	public JTable tableTerminal;

	public JProgressBar progressBarRam;
	public JProgressBar progressBarSwap;
	public JProgressBar progressBarPaginacao;

	public JSlider slider;

	private static VConfig instance = null;

	public static VConfig getInstance() {
		if (instance == null)
			instance = new VConfig();
		return instance;
	}

	public static void resetInstance() {
		instance = null;
	}

	private void PrepareProcessTable(Boolean flagReset) {
		int numProcessos = Integer.parseInt(txtNumProcessos.getText());
		if (flagReset || numProcessos != tableProcessos.getRowCount()) {
			String[][] tableData = new String[numProcessos][5];

			for (int i = 0; i < tableData.length; i++) {
				tableData[i][0] = i + 1 + ""; // PID
				tableData[i][1] = "1"; // arrive
				tableData[i][2] = "1"; // duration
				tableData[i][3] = "1"; // number of page
				tableData[i][4] = "0"; // deadline
			}
			Mylib.PrepareTable(tableProcessos, tableData);
		}
	}

	private Boolean ValidateProcessTable() {
		try {
			for (int col = 0; col < tableProcessos.getColumnCount(); col++) {
				for (int row = 0; row < tableProcessos.getRowCount(); row++) {
					if (Integer.parseInt(tableProcessos.getModel().getValueAt(row, col).toString()) < 0) {
						Mylib.alert("Alerta! Processso " + row + " preenchido de forma incompatível com o sistema.");
						return false;
					} else if (col == 3) { // page number
						if (Integer.parseInt(tableProcessos.getModel().getValueAt(row, col).toString()) > Integer
								.parseInt(txtNumpaginas.getText().toString())) {
							int id = row + 1;
							Mylib.alert("Alerta! Processo " + id
									+ " excedeu o limite máximo de páginas permitido pelo sistema.");
							return false;
						}
					} else if (col == 4) { // deadline
						if (!rdbtnEdf.isSelected()
								&& Integer.parseInt(tableProcessos.getModel().getValueAt(row, col).toString()) > 0) {
							Mylib.alert(
									"Alerta! A política de escalonamento selecionada não deve utilizar o atributo Deadline. \nPara continuar os valores devem ser zerados.");
							return false;
						}

					}
				}
			}
			return true;
		} catch (Exception e) {
			Mylib.alert("Alerta! Existe(m) Bloco(s) de Controle de Processo(s) com campo não preenchido.");
			return false;
		}
	}

	/**
	 * Create the frame.
	 */
	public VConfig() {
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		setResizable(false);
		setTitle("Simulador de Sistemas Operacionais");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1440, 900);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);

		JPanel panelConfig = new JPanel();
		tabbedPane.addTab("Configuração do Sistema", null, panelConfig, null);
		panelConfig.setLayout(new BorderLayout(0, 0));

		JPanel panelConfigNorth = new JPanel();
		panelConfig.add(panelConfigNorth, BorderLayout.NORTH);
		panelConfigNorth.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblNumProcessos = new JLabel("# Processo(s):");
		panelConfigNorth.add(lblNumProcessos);

		txtNumProcessos = new JTextField();
		txtNumProcessos.setText("5");
		panelConfigNorth.add(txtNumProcessos);
		txtNumProcessos.setColumns(10);

		JLabel lblQuantum = new JLabel("Quantum do Sistema:");
		panelConfigNorth.add(lblQuantum);

		txtQuantum = new JTextField();
		txtQuantum.setText("1");
		txtQuantum.setToolTipText("Padrão: 1");
		panelConfigNorth.add(txtQuantum);
		txtQuantum.setColumns(10);
		txtQuantum.setToolTipText("Unidade de tempo (u.t.)");

		JLabel lblNumpaginas = new JLabel("# Máximo de Página(s) por Processo:");
		panelConfigNorth.add(lblNumpaginas);

		txtNumpaginas = new JTextField();
		txtNumpaginas.setForeground(SystemColor.window);
		txtNumpaginas.setBackground(Color.WHITE);
		txtNumpaginas.setEnabled(false);
		txtNumpaginas.setEditable(false);
		txtNumpaginas.setText("10");
		panelConfigNorth.add(txtNumpaginas);
		txtNumpaginas.setColumns(10);

		JLabel lblSobrecarga = new JLabel("Sobrecarga:");
		lblSobrecarga.setToolTipText("Troca de Contexto");
		panelConfigNorth.add(lblSobrecarga);

		txtSobrecarga = new JTextField();
		txtSobrecarga.setBackground(Color.WHITE);
		txtSobrecarga.setForeground(SystemColor.window);
		txtSobrecarga.setEnabled(false);
		txtSobrecarga.setEditable(false);
		txtSobrecarga.setText("1");
		txtSobrecarga.setToolTipText("Padrão: 1");
		panelConfigNorth.add(txtSobrecarga);
		txtSobrecarga.setColumns(10);
		txtSobrecarga.setToolTipText("Unidade de tempo (ut)");

		JPanel panelConfigCenter = new JPanel();
		panelConfig.add(panelConfigCenter, BorderLayout.CENTER);
		panelConfigCenter.setLayout(new GridLayout(4, 5, 15, 15));

		JLabel lblEscalonamentocpu = new JLabel(" Algoritmo de Escalonamento da CPU");
		panelConfigCenter.add(lblEscalonamentocpu);

		rdbtnFifo = new JRadioButton("FIFO (First in First Out)");
		rdbtnFifo.setSelected(true);
		rdbtnFifo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnSjf.setSelected(false);
				rdbtnFifo.setSelected(true);
				rdbtnRr.setSelected(false);
				rdbtnEdf.setSelected(false);
			}
		});
		panelConfigCenter.add(rdbtnFifo);

		rdbtnRr = new JRadioButton("RR (Round Robin)");
		rdbtnRr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnSjf.setSelected(false);
				rdbtnFifo.setSelected(false);
				rdbtnRr.setSelected(true);
				rdbtnEdf.setSelected(false);
			}
		});
		panelConfigCenter.add(rdbtnRr);

		rdbtnSjf = new JRadioButton("SJF (Shortest Job First)");
		rdbtnSjf.setToolTipText("Sem Preempção");
		rdbtnSjf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnSjf.setSelected(true);
				rdbtnFifo.setSelected(false);
				rdbtnRr.setSelected(false);
				rdbtnEdf.setSelected(false);
			}
		});
		panelConfigCenter.add(rdbtnSjf);

		rdbtnEdf = new JRadioButton("EDF (Earliest Deadline First)");
		rdbtnEdf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnSjf.setSelected(false);
				rdbtnFifo.setSelected(false);
				rdbtnRr.setSelected(false);
				rdbtnEdf.setSelected(true);
			}
		});
		panelConfigCenter.add(rdbtnEdf);

		JLabel lblEscalonamentomem = new JLabel(" Algoritmo de Substituição de Página");
		lblEscalonamentomem.setToolTipText("Paginação Sob Demanda");
		panelConfigCenter.add(lblEscalonamentomem);

		rdbtnMemFifo = new JRadioButton("FIFO (First in First Out)");
		rdbtnMemFifo.setSelected(true);
		rdbtnMemFifo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnMemFifo.setSelected(true);
				rdbtnMemLru.setSelected(false);
			}
		});
		panelConfigCenter.add(rdbtnMemFifo);

		rdbtnMemLru = new JRadioButton("LRU (Least Recently Used)");
		rdbtnMemLru.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rdbtnMemFifo.setSelected(false);
				rdbtnMemLru.setSelected(true);
			}
		});
		panelConfigCenter.add(rdbtnMemLru);

		Component horizontalStrut = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut);

		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut_1);

		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut_4);

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut_2);

		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut_3);

		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut_5);

		Component horizontalStrut_6 = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut_6);
		
				JLabel lblLogo = new JLabel("");
				panelConfigCenter.add(lblLogo);
				lblLogo.setIcon(new ImageIcon(VConfig.class.getResource("/simuos/images/logo_so.edu.png")));

		Component horizontalStrut_7 = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut_7);

		Component horizontalStrut_8 = Box.createHorizontalStrut(20);
		panelConfigCenter.add(horizontalStrut_8);

		JPanel panelConfProcessos = new JPanel();
		tabbedPane.addTab("Processos", null, panelConfProcessos, null);
		panelConfProcessos.setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panelConfProcessos.add(panel, BorderLayout.NORTH);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblTitulo = new JLabel("Bloco(s) de Controle do(s) Processo(s)");
		panel.add(lblTitulo);

		JButton btnRecarregar = new JButton("Recarregar");
		btnRecarregar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PrepareProcessTable(true);
			}
		});
		panel.add(btnRecarregar);

		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ValidateProcessTable();
			}
		});
		panel.add(btnSalvar);

		JPanel panel_6 = new JPanel();
		panelConfProcessos.add(panel_6, BorderLayout.CENTER);
		panel_6.setLayout(new GridLayout(1, 0, 0, 0));

		DefaultTableModel model = new DefaultTableModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] titulo = { "ID do Processo (PID)", "Tempo de Chegada (s)", "Tempo de Execução (s)",
					"Número de Páginas (#)", "Deadline (s)" };

			@Override
			public int getColumnCount() {
				return titulo.length;
			}

			@Override
			public String getColumnName(int index) {
				return titulo[index];
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return column != 0 ? true : false;
			}

			@Override
			public Class getColumnClass(int col) {
				if (col == -1) // No column
					return String.class;
				else
					return Integer.class; // Other columns accept Integer values
			}
		};

		tableProcessos = new JTable(model);
		tableProcessos.setRowSelectionAllowed(false);
		tableProcessos.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		tableProcessos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		for (int i = 0; i < tableProcessos.getColumnCount(); i++) {
			tableProcessos.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			TableCellRenderer rendererFromHeader = tableProcessos.getTableHeader().getDefaultRenderer();
			JLabel headerLabel = (JLabel) rendererFromHeader;
			headerLabel.setHorizontalAlignment(JLabel.CENTER);
		}

		panel_6.add(tableProcessos, BorderLayout.CENTER);
		JScrollPane scrollPane = new JScrollPane(tableProcessos);
		PrepareProcessTable(false);

		panel_6.add(scrollPane);

		JPanel panelSimulacao = new JPanel();
		tabbedPane.addTab("Simulação", null, panelSimulacao, null);
		panelSimulacao.setLayout(new GridLayout(2, 1, 0, 0));

		JPanel panel_superior = new JPanel();
		panel_superior.setBorder(new MatteBorder(1, 1, 0, 1, (Color) null));
		panelSimulacao.add(panel_superior);
		panel_superior.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel_superior_1 = new JPanel();
		panel_superior.add(panel_superior_1);
		panel_superior_1.setLayout(new GridLayout(2, 2, 0, 0));

		JPanel panel_1 = new JPanel();
		panel_superior_1.add(panel_1);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);

		JButton btnIniciar = new JButton("Iniciar");
		btnIniciar.setBounds(25, 6, 83, 29);
		btnIniciar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ValidateProcessTable()) {
					tableProcessos.setEnabled(false);
					txtNumProcessos.setEnabled(false);
					txtQuantum.setEnabled(false);
					btnRecarregar.setEnabled(false);
					btnSalvar.setEnabled(false);
					rdbtnFifo.setEnabled(false);
					rdbtnRr.setEnabled(false);
					rdbtnSjf.setEnabled(false);
					rdbtnEdf.setEnabled(false);
					rdbtnMemFifo.setEnabled(false);
					rdbtnMemLru.setEnabled(false);
					Page.totalPaginas = 0;
					kernelSoEdu.start();
					btnIniciar.setEnabled(false);
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							tabbedPane.setSelectedIndex(1);
						}
					});
				}
			}
		});
		panel_2.setLayout(null);

		panel_2.add(btnIniciar);

		slider = new JSlider(JSlider.HORIZONTAL, 0, 30, 5);
		slider.setBounds(106, 6, 225, 62);
		slider.setToolTipText("Ajustar temporizador para visualizar simulação");
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(5);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		panel_2.add(slider);

		JLabel lblImage = new JLabel("");
		lblImage.setBounds(6, 70, 311, 129);
		lblImage.setHorizontalAlignment(SwingConstants.CENTER);
		lblImage.setIcon(new ImageIcon(VConfig.class.getResource("/simuos/images/estados-processos.png")));
		panel_2.add(lblImage);

		JButton btnFechar = new JButton("Fechar");
		btnFechar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				instance = null;
			}
		});
		btnFechar.setBounds(25, 36, 83, 29);
		panel_2.add(btnFechar);

		JPanel panel_3 = new JPanel();
		panel_3.setToolTipText("Informações sobre os Processos");
		panel_superior_1.add(panel_3);
		panel_3.setLayout(new GridLayout(7, 0, 5, 5));

		Component horizontalStrut_9 = Box.createHorizontalStrut(20);
		panel_3.add(horizontalStrut_9);

		lblExecucao = new JLabel(" Executando:");
		lblExecucao.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		panel_3.add(lblExecucao);

		lblPaginasProcessoExec = new JLabel("");
		lblPaginasProcessoExec.setForeground(Color.DARK_GRAY);
		lblPaginasProcessoExec.setToolTipText("Páginas do processo em execução");
		lblPaginasProcessoExec.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		panel_3.add(lblPaginasProcessoExec);

		lblBloqueado = new JLabel(" Bloqueado:");
		lblBloqueado.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		panel_3.add(lblBloqueado);

		lblPronto = new JLabel(" Pronto:");
		lblPronto.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		panel_3.add(lblPronto);

		lblEncerrado = new JLabel(" Encerrado:");
		lblEncerrado.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		panel_3.add(lblEncerrado);

		lblTurnaround = new JLabel(" Turnaround do Sistema:");
		lblTurnaround.setForeground(Color.RED);
		lblTurnaround.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		panel_3.add(lblTurnaround);

		JPanel panel_superior_2 = new JPanel();
		panel_superior.add(panel_superior_2);
		panel_superior_2.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_5 = new JPanel();
		panel_superior_2.add(panel_5);
		panel_5.setLayout(null);

		JLabel lblTerminal = new JLabel("Terminal");
		lblTerminal.setBounds(0, 0, 349, 26);
		panel_5.add(lblTerminal);
		lblTerminal.setHorizontalAlignment(SwingConstants.CENTER);
		lblTerminal.setIcon(new ImageIcon(VConfig.class.getResource("/simuos/images/term.png")));

		/*
		 * Table Terminal
		 */

		DefaultTableModel modelTela = new DefaultTableModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] titulo = { "Message" };

			@Override
			public int getColumnCount() {
				return titulo.length;
			}

			@Override
			public String getColumnName(int index) {
				return titulo[index];
			}
		};

		tableTerminal = new JTable(modelTela);
		tableTerminal.setBounds(0, 26, 349, 160);
		tableTerminal.setShowVerticalLines(false);
		tableTerminal.setShowHorizontalLines(false);
		tableTerminal.setShowGrid(false);
		tableTerminal.setRowSelectionAllowed(false);
		tableTerminal.setForeground(new Color(102, 255, 0));
		tableTerminal.setEnabled(false);
		tableTerminal.setBackground(Color.DARK_GRAY);
		panel_5.add(tableTerminal);

		JPanel panel_4 = new JPanel();
		panel_superior_2.add(panel_4);
		panel_4.setLayout(new GridLayout(7, 0, 5, 1));

		Component horizontalStrut_10 = Box.createHorizontalStrut(20);
		panel_4.add(horizontalStrut_10);

		JLabel lblTamanhoDasPginas = new JLabel(" Tamanho da Página: 4kB");
		panel_4.add(lblTamanhoDasPginas);
		lblTamanhoDasPginas.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		JLabel lblTamanhoDaRam = new JLabel(" Tamanho da RAM: 200kB");
		panel_4.add(lblTamanhoDaRam);
		lblTamanhoDaRam.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		JLabel lblTamanhoDoDisco = new JLabel(" Tamanho da SWAP: 400kB");
		panel_4.add(lblTamanhoDoDisco);
		lblTamanhoDoDisco.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		lblAcertoDePg = new JLabel(" # Acerto de Página: ");
		panel_4.add(lblAcertoDePg);
		lblAcertoDePg.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		lblFaltaDePg = new JLabel(" # Falta de Página: ");
		panel_4.add(lblFaltaDePg);
		lblFaltaDePg.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		lblBuscapagina = new JLabel(" # Buscas de Página:");
		lblBuscapagina.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		panel_4.add(lblBuscapagina);

		JPanel panel_superior_3 = new JPanel();
		panel_superior.add(panel_superior_3);
		panel_superior_3.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_superior_3all = new JPanel();
		panel_superior_3.add(panel_superior_3all);
		panel_superior_3all.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblMemoriaram = new JLabel("Memória Primária (RAM)  ");
		panel_superior_3all.add(lblMemoriaram);
		lblMemoriaram.setForeground(Color.BLUE);
		lblMemoriaram.setHorizontalAlignment(SwingConstants.CENTER);

		/*
		 * Table RAM
		 */

		DefaultTableModel modelTableRam = new DefaultTableModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] titulo = { "", "", "", "", "" };

			@Override
			public int getColumnCount() {
				return titulo.length;
			}

			@Override
			public String getColumnName(int index) {
				return titulo[index];
			}
		};

		tableRam = new JTable(modelTableRam);
		tableRam.setEnabled(false);
		tableRam.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		tableRam.setBorder(new LineBorder(Color.BLUE, 1, true));
		tableRam.setSurrendersFocusOnKeystroke(true);
		tableRam.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableRam.setForeground(Color.BLUE);
		tableRam.setRowSelectionAllowed(false);
		tableRam.setBackground(SystemColor.window);
		tableRam.setGridColor(Color.BLUE);
		panel_superior_3all.add(tableRam);

		JPanel panel_7 = new JPanel();
		panel_superior_3.add(panel_7);
		panel_7.setLayout(new GridLayout(7, 0, 5, 1));

		Component horizontalStrut_11 = Box.createHorizontalStrut(20);
		panel_7.add(horizontalStrut_11);

		JLabel lblUtilizaoDaRam = new JLabel(" Utilização da RAM:");
		panel_7.add(lblUtilizaoDaRam);
		lblUtilizaoDaRam.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		progressBarRam = new JProgressBar();
		panel_7.add(progressBarRam);
		progressBarRam.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		progressBarRam.setStringPainted(true);

		JLabel lblUtilizaoDaSwap = new JLabel(" Utilização da Swap:");
		panel_7.add(lblUtilizaoDaSwap);
		lblUtilizaoDaSwap.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		progressBarSwap = new JProgressBar();
		panel_7.add(progressBarSwap);
		progressBarSwap.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		progressBarSwap.setStringPainted(true);

		JLabel lblTaxaDePaginao = new JLabel(" Taxa de Paginação:");
		panel_7.add(lblTaxaDePaginao);
		lblTaxaDePaginao.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

		progressBarPaginacao = new JProgressBar();
		panel_7.add(progressBarPaginacao);
		progressBarPaginacao.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		progressBarPaginacao.setStringPainted(true);

		for (int i = 0; i < tableRam.getColumnCount(); i++) {
			tableRam.getColumnModel().getColumn(i).setPreferredWidth(60);
			tableRam.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		JPanel panel_superior_4 = new JPanel();
		panel_superior.add(panel_superior_4);
		panel_superior_4.setLayout(new GridLayout(1, 1, 0, 0));

		JPanel panel_superior_4all = new JPanel();
		panel_superior_4.add(panel_superior_4all);
		panel_superior_4all.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel lblSwap = new JLabel("Memória Secundária (SWAP | Disco)  ");
		lblSwap.setForeground(new Color(0, 102, 0));
		lblSwap.setHorizontalAlignment(SwingConstants.CENTER);
		panel_superior_4all.add(lblSwap);

		DefaultTableModel modelTableSwap = new DefaultTableModel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			String[] titulo = { "", "", "", "", "" };

			@Override
			public int getColumnCount() {
				return titulo.length;
			}

			@Override
			public String getColumnName(int index) {
				return titulo[index];
			}
		};

		/*
		 * Table SWAP
		 */

		tableSwap = new JTable(modelTableSwap);
		tableSwap.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
		tableSwap.setEnabled(false);
		tableSwap.setBorder(new LineBorder(new Color(0, 102, 0), 1, true));
		tableSwap.setSurrendersFocusOnKeystroke(true);
		tableSwap.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableSwap.setForeground(new Color(0x006600));
		tableSwap.setRowSelectionAllowed(false);
		tableSwap.setBackground(SystemColor.window);
		tableSwap.setGridColor(new Color(0x006600));
		panel_superior_4all.add(tableSwap);

		for (int i = 0; i < tableSwap.getColumnCount(); i++) {
			tableSwap.getColumnModel().getColumn(i).setPreferredWidth(60);
			tableSwap.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		}

		JPanel panel_inferior = new JPanel();
		panelSimulacao.add(panel_inferior);
		panel_inferior.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_inferior_all = new JPanel();
		panel_inferior.add(panel_inferior_all);
		panel_inferior_all.setLayout(new BorderLayout(15, 0));

		JLabel lblGantt = new JLabel("Log do Escalonamento de Processos");
		lblGantt.setHorizontalAlignment(SwingConstants.CENTER);
		panel_inferior_all.add(lblGantt, BorderLayout.NORTH);

		/*
		 * Table GANTT
		 */

		DefaultTableModel modelGantt = new DefaultTableModel();
		modelGantt.addColumn("PID");
		modelGantt.addColumn("TCH");
		modelGantt.addColumn("TCP");
		modelGantt.addColumn("TES");
		modelGantt.addColumn("NPG");
		modelGantt.addColumn("DIN");

		for (int i = 1; i < numColsGantt; i++) {
			if (i % 5 == 0)
				modelGantt.addColumn("|");
			else
				modelGantt.addColumn(".");
		}

		tableGantt = new JTable(modelGantt) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public String getToolTipText(MouseEvent e) {
				String tip = null;
				java.awt.Point p = e.getPoint();
				int rowIndex = rowAtPoint(p);
				int colIndex = columnAtPoint(p);

				try {
					if (getValueAt(rowIndex, colIndex).toString().equals("r"))
						tip = "rodando";
					else if (getValueAt(rowIndex, colIndex).toString().equals("x"))
						tip = "rodando com estouro de deadline";
					else if (getValueAt(rowIndex, colIndex).toString().equals("f"))
						tip = "falta de página";
					else if (getValueAt(rowIndex, colIndex).toString().equals("s"))
						tip = "sobrecarga (troca de contexto)";

					switch (colIndex) {
					case 0:
						tip = "PID: identificador do processo";
						break;
					case 1:
						tip = "TCH: tempo de chegada";
						break;
					case 2:
						tip = "TCU: tempo de CPU";
						break;
					case 3:
						tip = "TES: tempo de espera";
						break;
					case 4:
						tip = "NPG: número de página(s) do processo";
						break;
					case 5:
						tip = "DIN: deadline inicial do processo";
						break;
					}
				} catch (RuntimeException e1) {
				}
				return tip;
			}
		};
		tableGantt.setEnabled(false);

		for (int i = 0; i < tableGantt.getColumnCount(); i++) {
			tableGantt.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			if (i < 6) {
				tableGantt.getColumnModel().getColumn(i).setPreferredWidth(30);
				tableGantt.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			} else
				tableGantt.getColumnModel().getColumn(i).setPreferredWidth(5);
		}

		tableGantt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		tableGantt.setShowVerticalLines(false);
		tableGantt.setShowHorizontalLines(false);
		tableGantt.setRowSelectionAllowed(false);
		tableGantt.setVisible(true);
		tableGantt.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		panel_inferior_all.add(tableGantt, BorderLayout.CENTER);

		JScrollPane scrollPaneTableGantt = new JScrollPane(tableGantt);
		panel_inferior_all.add(scrollPaneTableGantt);
		SwingUtilities.updateComponentTreeUI(scrollPaneTableGantt);

		if (tableTerminal.getModel().getRowCount() < 1) {
			kernelSoEdu.rc_initTerminal();
			kernelSoEdu.rc_printRam();
			kernelSoEdu.rc_printSwap();
		}

		// Events
		tableGantt.getModel().addTableModelListener(new TableModelListener() {
			public void tableChanged(TableModelEvent e) {
			}
		});

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				Page.totalPaginas = 0;
				VConfig.resetInstance();
				kernelSoEdu.stop();
				System.out.println("Thread do Kernel finalizada com sucesso.");
			}
		});

		txtNumProcessos.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				String oldTxt = "";
				if (txtNumProcessos.getText().length() > 0)
					oldTxt = txtNumProcessos.getText().substring(0, txtNumProcessos.getText().length() - 1);
				try {
					Long.parseLong(txtNumProcessos.getText());
				} catch (Exception e) {
					txtNumProcessos.setText(oldTxt);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							txtNumProcessos.requestFocus();
						}
					});
				}
			}
		});

		txtQuantum.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyReleased(java.awt.event.KeyEvent evt) {
				String oldTxt = "";
				if (txtQuantum.getText().length() > 0)
					oldTxt = txtQuantum.getText().substring(0, txtQuantum.getText().length() - 1);

				try {
					Long.parseLong(txtQuantum.getText());
				} catch (Exception e) {
					txtQuantum.setText(oldTxt);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							txtQuantum.requestFocus();
						}
					});
				}
			}
		});

		txtQuantum.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			};

			public void focusLost(FocusEvent e) {
				if (txtQuantum.getText().length() < 1)
					txtQuantum.setText("1");
				if (!e.isTemporary()) {
					Integer content = Integer.parseInt(txtQuantum.getText());
					if (content > 10 || content < 1) {
						Mylib.alert("O quantum do sistema deve ser no máximo 10 e no mínimo 1. ");
						txtQuantum.setText("1");
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								txtQuantum.requestFocus();
							}
						});
					}
				}
			}
		});

		txtNumProcessos.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
			};

			public void focusLost(FocusEvent e) {
				if (txtNumProcessos.getText().length() < 1)
					txtNumProcessos.setText("1");
				PrepareProcessTable(false);
				if (!e.isTemporary()) {
					Integer content = Integer.parseInt(txtNumProcessos.getText());
					if (content > 15 || content < 1) {
						Mylib.alert("O número de processos é no máximo 15 e no mínimo 1. ");
						txtNumProcessos.setText("1");
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								txtNumProcessos.requestFocus();
							}
						});
					}
				}
			}
		});
	}
}
