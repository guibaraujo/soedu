package simuos.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import simuos.util.Mylib;
import simuos.view.VConfig;

public class Kernel extends Thread {

	private VConfig vconfig;

	private MMU mmu;

	private ArrayList<String> LCDTerminal;

	private List<PCB> listAllProcess;
	private List<PCB> listPronto;
	private List<PCB> listExecucao;
	private List<PCB> listBloqueado;
	private List<PCB> listEncerrado;

	private Integer clock;
	private Integer systemQuantum;

	NumberFormat nfDouble = new DecimalFormat("#0.00");

	public Kernel(VConfig vconfig) {
		clock = 0;
		this.vconfig = vconfig;
		mmu = new MMU(10, 5);
		listAllProcess = new ArrayList<PCB>();
		LCDTerminal = new ArrayList<String>();
		listPronto = new ArrayList<PCB>();
		listExecucao = new ArrayList<PCB>();
		listBloqueado = new ArrayList<PCB>();
		listEncerrado = new ArrayList<PCB>();
	}

	// start block -- kernel run commands --

	public void rc_initTerminal() {
		LCDTerminal.add(0, rc_prepareMsgTerminal("Bem vindo ao SO.EDU!"));
		LCDTerminal.add(0, rc_prepareMsgTerminal("Carregando kernel monolítico... Ok!"));
		LCDTerminal.add(0, rc_prepareMsgTerminal("INIT: Aguarde configurando o sistema..."));
		LCDTerminal.add(0, rc_prepareMsgTerminal("MMU configurada para paginação antecipada."));
		LCDTerminal.add(0, rc_prepareMsgTerminal("Pressione o botão iniciar para começar."));
		rc_printTerminal();
	}

	public void rc_initGantt() {
		int row = Integer.parseInt(vconfig.txtNumProcessos.getText());
		Mylib.PrepareTable(vconfig.tableGantt, new String[row][vconfig.tableGantt.getColumnCount()]);
	}

	private String rc_prepareMsgTerminal(String msg) {
		return SystemTime.getSystemTime() + "  " + msg;
	}

	public void rc_printRam() {
		String[][] tableDataRam = new String[10][5];
		/*
		 * tableDataRam[0][0] = "0x00000"; tableDataRam[0][1] = "0x0A000";
		 * tableDataRam[0][2] = "0x14000"; tableDataRam[0][3] = "0x1E000";
		 * tableDataRam[0][4] = "0x28000";
		 */
		mmu.getMatrixRam(tableDataRam);
		Mylib.PrepareTable(vconfig.tableRam, tableDataRam);
	}

	public void rc_printSwap() {
		String[][] tableDataSwap = new String[20][5];
		/*
		 * tableDataSwap[0][0] = "0x00000"; tableDataSwap[0][1] = "0x14000";
		 * tableDataSwap[0][2] = "0x28000"; tableDataSwap[0][3] = "0x3C000";
		 * tableDataSwap[0][4] = "0x50000";
		 */
		mmu.getMatrixSwap(tableDataSwap);
		Mylib.PrepareTable(vconfig.tableSwap, tableDataSwap);
	}

	private void rc_printTerminal() {
		Mylib.PrepareTable(vconfig.tableTerminal, new String[10][1]);
		for (int i = 0; i < LCDTerminal.size() && i < 10; i++)
			vconfig.tableTerminal.getModel().setValueAt(LCDTerminal.get(i).toString(), i, 0);
	}

	private void rc_printGantt(String msg, PCB processInCPU) {
		vconfig.tableGantt.getModel().setValueAt(msg, processInCPU.getPosRowInTable(),
				processInCPU.getPosColInTable() + clock);
	}

	private void rc_printGanttStatistics(PCB p) {
		vconfig.tableGantt.getModel().setValueAt(p.getPid(), p.getPosRowInTable(), 0);// PID
		vconfig.tableGantt.getModel().setValueAt(p.getTempoChegada(), p.getPosRowInTable(), 1);
		vconfig.tableGantt.getModel().setValueAt(p.getTempoCPU(), p.getPosRowInTable(), 2);
		vconfig.tableGantt.getModel().setValueAt(p.getTempoEspera(), p.getPosRowInTable(), 3);
		vconfig.tableGantt.getModel().setValueAt(p.getTotalPaginas(), p.getPosRowInTable(), 4);
		vconfig.tableGantt.getModel().setValueAt(p.getDeadlineInicial(), p.getPosRowInTable(), 5);
	}

	private void rc_printLabels() {
		String temp = "";
		if (listExecucao.size() > 0) {
			vconfig.lblExecucao.setText(" Executando: " + listExecucao.get(0).getPid());
			//
			temp = "";
			for (int i = 0; i < listExecucao.get(0).getPageTable().size(); i++)
				temp += "" + listExecucao.get(0).getPageTable().get(i).getId() + " ";
			vconfig.lblPaginasProcessoExec.setText(" PG: " + temp);
		} else {
			vconfig.lblExecucao.setText(" Executando: ");
			vconfig.lblPaginasProcessoExec.setText("");
		}

		temp = "";
		for (int i = 0; i < listBloqueado.size(); i++)
			temp += listBloqueado.get(i).getPid() + " ";
		vconfig.lblBloqueado.setText(" Bloqueado: " + temp);

		temp = "";
		for (int i = 0; i < listPronto.size(); i++)
			temp += listPronto.get(i).getPid() + " ";
		vconfig.lblPronto.setText(" Pronto: " + temp);

		temp = "";
		for (int i = 0; i < listEncerrado.size(); i++)
			temp += listEncerrado.get(i).getPid() + " ";
		vconfig.lblEncerrado.setText(" Encerrado: " + temp);

		int sum = 0;
		for (int i = 0; i < listAllProcess.size(); i++)
			sum += listAllProcess.get(i).getTempoEspera();
		for (int i = 0; i < listEncerrado.size(); i++)
			sum += listEncerrado.get(i).getTempoEspera();
		if (listAllProcess.isEmpty() && listBloqueado.isEmpty() && listPronto.isEmpty() && listExecucao.isEmpty())
			vconfig.lblTurnaround.setText(" Turnaround do Sistema: " + sum + "/"
					+ (listEncerrado.size() + " = " + nfDouble.format(sum / (1.0 * listEncerrado.size())) + " u.t."));
		else
			vconfig.lblTurnaround.setText(" Calculando Turnaround do Sistema: " + sum);

		vconfig.lblAcertoDePg.setText(" # Acerto de Página: " + mmu.getAcertoPg());
		vconfig.lblFaltaDePg.setText(" # Falta de Página: " + mmu.getFaltaPg());
		vconfig.lblBuscapagina.setText(" # Buscas de Página: " + (mmu.getAcertoPg() + mmu.getFaltaPg()));
		vconfig.progressBarPaginacao.setValue(mmu.getTaxaPaginacao());
	}

	private void rc_printMemoryUsage() {
		vconfig.progressBarRam.setValue(mmu.getUtilizacaoRam());
		vconfig.progressBarSwap.setValue(mmu.getUtilizacaoSwap());
	}

	private void rc_loadAllProcess() {
		for (int row = 0; row < vconfig.tableProcessos.getRowCount(); row++) {
			PCB p = JTable2PCB(row);
			listAllProcess.add(p);
			rc_printGanttStatistics(p);
		}
	}

	// end block -- kernel run commands --

	public void scheduler() {
		clock++;
		updateListPronto();
		while (listAllProcess.size() != 0 || listPronto.size() != 0) {
			// There is process to scheduler...
			if (listPronto.size() != 0) {
				PCB selectProcess = listPronto.get(0);
				// Is process page(s) in the RAM memory?
				if (mmu.checkPagesInRam(selectProcess)) {
					listExecucao.add(selectProcess);
					mmu.acertoPg++; // Page hit
					listPronto.remove(0);
				} else { // Page fault - page isn't in the RAM
					listExecucao.add(selectProcess);
					rc_printLabels();
					try {
						sleep(vconfig.slider.getValue() * 100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					listBloqueado.add(listExecucao.remove(0));
					mmu.faltaPg++;
					rc_printGantt("f", selectProcess);
					// Prepare msg to print in terminal
					String temp = rc_prepareMsgTerminal(
							"Falta de página(s). Processo " + selectProcess.getPid() + " bloqueado.");
					LCDTerminal.add(0, temp);
					rc_printTerminal();
					// Remove blocked process from ready list
					listPronto.remove(0); //testar e comentar a linha abaixo
					//for (int i = 0; i < listPronto.size(); i++) {
					//	if (listPronto.get(i).getPid() == selectProcess.getPid())
					//		listPronto.remove(i);
					//}
				}
			}
			// There is at least one process in the CPU
			if (!listExecucao.isEmpty()) {
				if (listPronto.size() == 0) {
					listPronto.add(listExecucao.remove(0));
					rc_printLabels();
					try {
						sleep(vconfig.slider.getValue() * 100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					listExecucao.add(listPronto.remove(0));
					rc_printLabels();
				}
				// Prepare msg to print in terminal
				String temp = rc_prepareMsgTerminal(
						"Processo " + listExecucao.get(0).getPid() + " é escalonado para CPU.");
				LCDTerminal.add(0, temp);
				rc_printTerminal();
				Boolean processPreemption = false;
				//
				while ((listExecucao.get(0).getTempoCPU() < listExecucao.get(0).getTempoExecucao())
						&& !processPreemption) {
					try {
						// Update process statistics
						if (vconfig.rdbtnEdf.isSelected())
							listExecucao.get(0).updateDeadline();
						listExecucao.get(0).setTempoEspera(
								clock - listExecucao.get(0).getTempoCPU() - listExecucao.get(0).getTempoChegada());
						// Load the process page(s) to RAM memory?
						mmu.loadPageProcess(listExecucao.get(0));
						processPreemption = verifyPreemption(); // Depend of the scheduling policy
						if (!processPreemption) {
							temp = (listExecucao.get(0).getDeadline() >= 0) ? "r" : "x";
							rc_printGantt(temp, listExecucao.get(0));
							listExecucao.get(0).updateTempoCPU();
						} else {
							rc_printGantt("s", listExecucao.get(0));
							temp = rc_prepareMsgTerminal(
									"Processo " + listExecucao.get(0).getPid() + " sofreu preempção.");
							LCDTerminal.add(0, temp);
							rc_printTerminal();
						}
						clock++;
						updateDeadline();
						updateListPronto();
						rc_printMemoryUsage();
						rc_printRam();
						rc_printSwap();
						rc_printGanttStatistics(listExecucao.get(0));
						rc_printLabels();
						sleep(vconfig.slider.getValue() * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				// Finalizing process
				if (!processPreemption) {
					listExecucao.get(0).freePageTable();
					listEncerrado.add(listExecucao.get(0));
					listExecucao.remove(0);
					try {
						sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				} else { // The process is suffering preemption
					if (listExecucao.get(0).getTempoCPU() < listExecucao.get(0).getTempoExecucao())
						listPronto.add(listExecucao.get(0));
					listExecucao.get(0).freePageTable();
					listExecucao.remove(0);
					updateListPronto();
				}
			} else { // CPU is idle
				try {
					updateDeadline();
					rc_printLabels();
					sleep(vconfig.slider.getValue() * 1000);
					clock++;
					updateListPronto();
					rc_printMemoryUsage();
					rc_printRam();
					rc_printSwap();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		rc_printLabels();
	}

	private PCB JTable2PCB(int posx) {
		PCB p = new PCB(Integer.parseInt(vconfig.tableProcessos.getModel().getValueAt(posx, 3).toString()));// Num Pág.
		p.setPid(Integer.parseInt(vconfig.tableProcessos.getModel().getValueAt(posx, 0).toString()));// PID
		p.setTempoChegada(Integer.parseInt(vconfig.tableProcessos.getModel().getValueAt(posx, 1).toString()));// Temp.Cheg
		p.setTempoExecucao(Integer.parseInt(vconfig.tableProcessos.getModel().getValueAt(posx, 2).toString()));// Temp.Exec
		p.setDeadline(Integer.parseInt(vconfig.tableProcessos.getModel().getValueAt(posx, 4).toString()));// Deadline

		p.setTempoEspera(0);
		p.resetQuantum(systemQuantum);

		// Set position to print in correct table log position
		p.setPosRowInTable(p.getPid() - 1);
		p.setPosColInTable(5);// Position after statistical info column
		return p;
	}

	// Only for EDF algorithm
	private void updateDeadline() {
		if (vconfig.rdbtnEdf.isSelected())
			for (int i = 0; i < listPronto.size(); i++) {
				listPronto.get(i).updateDeadline();
				if (listPronto.get(i).getDeadline() == -1) { // Message deadline overflow
					String temp = rc_prepareMsgTerminal(
							"Alerta! Processo " + listPronto.get(i).getPid() + " estourou sua deadline.");
					LCDTerminal.add(0, temp);
					rc_printTerminal();
				}
			}
	}

	// Scheduler module to manage process in the ready queue 
	private void updateListPronto() {
		/*
		 * ////////////////////////////////////
		 * 
		 * 
		 * Algorithm FIFO and RR
		 */
		if (vconfig.rdbtnRr.isSelected() || vconfig.rdbtnFifo.isSelected()) {
			for (int i = 0; i < listAllProcess.size(); i++)
				if (clock == listAllProcess.get(i).getTempoChegada()) {
					listPronto.add(listAllProcess.get(i));
				}
		}

		/*
		 * ////////////////////////////////////
		 * 
		 * 
		 * Algorithm SJF non preemptive
		 */
		if (vconfig.rdbtnSjf.isSelected()) {
			for (int i = 0; i < listAllProcess.size(); i++)
				if (clock == listAllProcess.get(i).getTempoChegada())
					listPronto.add(listAllProcess.get(i));

			Comparator<PCB> compareById = (PCB p1, PCB p2) -> p1.getTempoExecucao().compareTo(p2.getTempoExecucao());
			Collections.sort(listPronto, compareById);
			// Collections.reverse(listPronto);
		}

		/*
		 * ////////////////////////////////////
		 * 
		 * 
		 * Algorithm EDF preemptive
		 */
		if (vconfig.rdbtnEdf.isSelected()) {
			for (int i = 0; i < listAllProcess.size(); i++)
				if (clock == listAllProcess.get(i).getTempoChegada())
					listPronto.add(listAllProcess.get(i));

			Comparator<PCB> compareById;

			compareById = (PCB p1, PCB p2) -> p1.getCreateTime().compareTo(p2.getCreateTime());
			Collections.sort(listPronto, compareById);
			Collections.reverse(listPronto);

			compareById = (PCB p1, PCB p2) -> p1.getDeadline().compareTo(p2.getDeadline());
			Collections.sort(listPronto, compareById);
		}

		/*
		 * ////////////////////////////////////
		 * 
		 * 
		 * All
		 */
		ArrayList<Integer> deleteFrom = new ArrayList<Integer>();
		for (int i = 0; i < listBloqueado.size(); i++) {
			if (mmu.checkPagesInRam(listBloqueado.get(i))) {
				if (vconfig.rdbtnEdf.isSelected() && listPronto.size() > 1) // EDF ready queue
					listPronto.add(1, listBloqueado.get(i));
				else
					listPronto.add(listBloqueado.get(i));
				deleteFrom.add(i);
			}
		}
		//
		if (deleteFrom.size() > 0) {
			for (int i = deleteFrom.size() - 1; i >= 0; i--) {
				int index = deleteFrom.get(i);
				listBloqueado.remove(index);
			}
		}
		//
		for (int i = 0; i < listPronto.size(); i++) {
			for (int j = 0; j < listAllProcess.size(); j++) {
				if (listPronto.get(i).getPid() == listAllProcess.get(j).getPid()) {
					listAllProcess.remove(j);
					break;
				}
			}
		}
	}

	// RR and EDF
	private Boolean verifyPreemption() {
		if (vconfig.rdbtnRr.isSelected() || vconfig.rdbtnEdf.isSelected()) {
			if (listExecucao.get(0).updateQuantum() == 0) {
				listExecucao.get(0).resetQuantum(systemQuantum);
				return true; // Preemption must occur
			}
		}
		return false;
	}

	private void dmesg() {
		String temp;
		if (vconfig.rdbtnFifo.isSelected())
			temp = "FIFO";
		else if (vconfig.rdbtnRr.isSelected())
			temp = "RR";
		else if (vconfig.rdbtnSjf.isSelected())
			temp = "SJF";
		else
			temp = "EDF";
		temp = rc_prepareMsgTerminal("Política de escalonamento: " + temp);
		LCDTerminal.add(0, temp);
		if (vconfig.rdbtnMemLru.isSelected()) {
			temp = "LRU";
			mmu.setPageReplacementLru();
		} else {
			temp = "FIFO";
			mmu.setPageReplacementFifo();
		}
		temp = rc_prepareMsgTerminal("Política de substituição de página: " + temp);
		LCDTerminal.add(0, temp);
		temp = rc_prepareMsgTerminal("Escalonador monitorando fila de processos!");
		LCDTerminal.add(0, temp);
		rc_printTerminal();
	}

	public void run() {
		systemQuantum = Integer.parseInt(vconfig.txtQuantum.getText());
		String temp;
		dmesg();
		rc_initGantt();
		rc_loadAllProcess();
		scheduler();
		temp = rc_prepareMsgTerminal("Fim da simulação!");
		LCDTerminal.add(0, temp);
		rc_printTerminal();
	}
}
