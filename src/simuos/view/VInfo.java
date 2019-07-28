package simuos.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import simuos.util.Mylib;

public class VInfo extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Mylib myLib = new Mylib();

	private static VInfo instance = null;

	public static VInfo getInstance() {
		if (instance == null)
			instance = new VInfo();
		return instance;
	}

	/**
	 * Create the frame.
	 */
	public VInfo() {
		// default window settings
		setResizable(false);
		setTitle("Simulador SO.EDU");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 500, 350);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(1, 0, 0, 0));

		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new BorderLayout(15, 15));

		JLabel lblInfo = new JLabel("Informações do Sistema");
		lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
		lblInfo.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		panel.add(lblInfo, BorderLayout.NORTH);

		JTextPane txtpnEsseSistemaImplementa = new JTextPane();
		txtpnEsseSistemaImplementa.setEditable(false);
		txtpnEsseSistemaImplementa.setBackground(Color.WHITE);
		txtpnEsseSistemaImplementa.setText(
				"   Definição, arquitetura e limitações do Sistema:\n   -> Cada página tem tamanho de 4k\n   -> A RAM tem 200k de memória\n   -> Número máximo de processos 15 (para melhor visualização)\n   -> Cada processo pode ter até 10 páginas\n   -> A SWAP é o dobro da RAM \n   -> Sistema de paginação antecipada \n   -> Todas as páginas do processo em execução devem estar na RAM\n\n   Algoritmos de Escalonamento: FIFO, RR, SJF, EDF\n\n   Algoritmos de Substituição de Páginas: FIFO, LRU");
		panel.add(txtpnEsseSistemaImplementa, BorderLayout.CENTER);

		JLabel lblLogo = new JLabel("");
		lblLogo.setIcon(new ImageIcon(VInfo.class.getResource("/simuos/images/logo_so.edu.png")));
		panel.add(lblLogo, BorderLayout.SOUTH);
		myLib.CenteredFrame(this);
	}

}
