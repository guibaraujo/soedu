package simuos.view;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

import simuos.controller.SystemTime;
import simuos.util.Mylib;

public class VPrincipal extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Mylib myLib = new Mylib();
	public JLabel lblClock;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VPrincipal frame = new VPrincipal();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	Timer timer = new Timer(1000, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			lblClock.setText(SystemTime.getSystemTime());
		}
	});

	/**
	 * Create the frame.
	 */
	public VPrincipal() {
		timer.start();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 160, 350);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));

		JPanel panel = new JPanel();
		contentPane.add(panel);
		panel.setLayout(new GridLayout(5, 1, 15, 15));

		JLabel lblLogo = new JLabel("SO.EDU");
		lblLogo.setIcon(new ImageIcon(VPrincipal.class.getResource("/simuos/images/logo_so.edu.png")));
		lblLogo.setForeground(Color.DARK_GRAY);
		lblLogo.setFont(new Font("Arial Black", Font.PLAIN, 20));
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblLogo);

		JButton btnSimulador = new JButton("Simulador");
		btnSimulador.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VConfig vconfig = VConfig.getInstance();
				vconfig.setExtendedState(vconfig.getExtendedState() | JFrame.MAXIMIZED_BOTH);
				vconfig.setVisible(true);
			}
		});

		JButton btnInfo = new JButton("Info do Sistema");
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VInfo vajuda = VInfo.getInstance();
				vajuda.setVisible(true);
			}
		});
		btnInfo.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		panel.add(btnInfo);
		btnSimulador.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		panel.add(btnSimulador);

		JButton btnSobre = new JButton("Sobre");
		btnSobre.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				VSobre vcreditos = VSobre.getInstance();
				vcreditos.setVisible(true);
			}
		});
		btnSobre.setFont(new Font("Lucida Grande", Font.PLAIN, 14));
		panel.add(btnSobre);

		lblClock = new JLabel("");
		lblClock.setForeground(Color.DARK_GRAY);
		lblClock.setFont(new Font("Arial Black", Font.PLAIN, 16));
		lblClock.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblClock);
		myLib.LeftCenteredFrame(this);
	}
}
