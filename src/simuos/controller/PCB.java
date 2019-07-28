package simuos.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PCB {

	private Long createTime;	
	private Integer posRowInTable;
	private Integer posColInTable;
	private Integer pid;
	private Integer tempoChegada;
	private Integer tempoExecucao;
	private Integer tempoCPU;
	private Integer tempoEspera;
	private Integer deadline;
	private Integer deadlineInicial;
	private Integer totalPaginas;
	private Integer quantum;
	private List<Page> pageTable;

	public PCB(int totalPaginas) {
		this.totalPaginas = totalPaginas;
		this.tempoCPU = 0;
		this.quantum = -1; // Disable by default
		this.pageTable = new ArrayList<Page>();
		this.createTime=new Date().getTime();
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime() {
		this.createTime = new Date().getTime();
	}

	public Integer getPosRowInTable() {
		return posRowInTable;
	}

	public void setPosRowInTable(Integer posRowInTable) {
		this.posRowInTable = posRowInTable;
	}

	public Integer getPosColInTable() {
		return posColInTable;
	}

	public void setPosColInTable(Integer posColInTable) {
		this.posColInTable = posColInTable;
	}

	public Integer getPid() {
		return pid;
	}

	public void setPid(Integer pid) {
		this.pid = pid;
	}

	public Integer getTempoChegada() {
		return tempoChegada;
	}

	public void setTempoChegada(Integer tempoChegada) {
		this.tempoChegada = tempoChegada;
	}

	public Integer getTempoExecucao() {
		return tempoExecucao;
	}

	public void setTempoExecucao(Integer tempoExecucao) {
		this.tempoExecucao = tempoExecucao;
	}

	public Integer getTempoCPU() {
		return tempoCPU;
	}

	public void updateTempoCPU() {
		this.tempoCPU++;
	}

	public Integer getTotalPaginas() {
		return totalPaginas;
	}

	public void setTotalPaginas(Integer totalPaginas) {
		this.totalPaginas = totalPaginas;
	}

	public Integer getDeadline() {
		return deadline;
	}

	public void setDeadline(Integer deadline) {
		this.deadline = deadline;
		this.deadlineInicial=deadline;
	}

	public void updateDeadline() {
		//if (this.deadline >= 0)
			this.deadline--;
	}
	
	public Integer getDeadlineInicial() {
		return deadlineInicial;
	}

	public Integer getTempoEspera() {
		return tempoEspera;
	}

	public void setTempoEspera(Integer tempoEspera) {
		this.tempoEspera = tempoEspera;
	}

	public Integer getQuantum() {
		return quantum;
	}

	public void resetQuantum(Integer quantum) {
		this.quantum = quantum;
	}

	public Integer updateQuantum() {
		if (quantum > 0)
			return quantum--;
		else
			return quantum;
	}

	public List<Page> getPageTable() {
		return pageTable;
	}

	public void setPageTable(List<Page> pageTable) {
		this.pageTable = new ArrayList<Page>();
		for (int i = 0; i < pageTable.size(); i++) {
			Page page = new Page();
			page.setBitPresent(pageTable.get(i).getBitPresent());
			page.setId(pageTable.get(i).getId());
			page.setLastAccess(pageTable.get(i).getLastAccess());
			this.pageTable.add(page);
		}
	}

	public void createPageTable() {
		if (pageTable.size() == 0) {
			for (int i = 0; i < totalPaginas; i++) {
				Page page = new Page();
				pageTable.add(page);
			}
		}
	}

	public void freePageTable() {
		for (int i = 0; i < pageTable.size(); i++) {
			pageTable.get(i).setBitPresent(3);
		}
	}
}
