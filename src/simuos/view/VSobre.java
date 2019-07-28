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

public class VSobre extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Mylib myLib = new Mylib();

	private static VSobre instance = null;

	public static VSobre getInstance() {
		if (instance == null)
			instance = new VSobre();
		return instance;
	}

	/**
	 * Create the frame.
	 */
	public VSobre() {
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

		JLabel lblSobre = new JLabel("Sobre o Simulador");
		lblSobre.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		lblSobre.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblSobre, BorderLayout.NORTH);

		JTextPane txtpnProjetoDaDisciplina = new JTextPane();
		txtpnProjetoDaDisciplina.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		txtpnProjetoDaDisciplina.setBackground(Color.WHITE);
		txtpnProjetoDaDisciplina.setEditable(false);
		txtpnProjetoDaDisciplina.setText(
				"   \n   Simulador Educacional para Ensino de Sistemas Operacionais\n\n   Projeto Final da Disciplina de Sistemas Operacionais 2019.01\n   UFBA  Universidade Federal da Bahia \n   \n\n   \n\n\n    \n\n          \n\n       .--.\n      |o_o|\n   \\-| _ |-/");
		panel.add(txtpnProjetoDaDisciplina, BorderLayout.CENTER);

		JLabel lblLogo = new JLabel("");
		lblLogo.setIcon(new ImageIcon(VSobre.class.getResource("/simuos/images/logo_so.edu.png")));
		panel.add(lblLogo, BorderLayout.SOUTH);
		myLib.CenteredFrame(this);
	}

}
