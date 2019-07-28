package simuos.controller;

import java.util.ArrayList;
import java.util.List;

public class MMU {
	/* Policy
	 * 0 = FIFO 
	 * 1 = LRU
	 */
	private Integer pageReplacementPolicy;
	public Integer acertoPg;
	public Integer faltaPg;
	public List<Page> matrixRam;
	public List<Page> matrixSwap;
	public List<Page> allPagesSystem;

	public MMU(int row, int col) {
		matrixRam = new ArrayList<Page>();
		matrixSwap = new ArrayList<Page>();
		allPagesSystem = new ArrayList<Page>();
		setMatrixRam(row * col);
		setMatrixSwap(2 * (row * col));
		faltaPg = 0;
		acertoPg = 0;
		pageReplacementPolicy = 0;
	}

	public Integer getPageReplacementPolicy() {
		return pageReplacementPolicy;
	}
	
	public void setPageReplacementFifo() {
		this.pageReplacementPolicy = 0;
	}
	
	public void setPageReplacementLru() {
		this.pageReplacementPolicy = 1;
	}

	public Integer getAcertoPg() {
		return acertoPg;
	}

	public Integer getFaltaPg() {
		return faltaPg;
	}

	public int getUtilizacaoRam() {
		double temp = 100 * ((matrixRam.size() - freeQuantityRam()) / (1.0 * matrixRam.size()));
		return (int) temp;
	}

	public int getUtilizacaoSwap() {
		double temp = 100 * ((matrixSwap.size() - freeQuantitySwap()) / (1.0 * matrixSwap.size()));
		return (int) temp;
	}

	public int getTaxaPaginacao() {
		double temp = 100 * (faltaPg / (1.0 * (faltaPg + acertoPg)));
		return (int) temp;
	}

	public boolean checkPagesInRam(PCB processo) {
		for (int i = 0; i < processo.getPageTable().size(); i++) {
			// Check at least one page to confirm the others
			if (containsInRam(processo.getPageTable().get(i)))
				return true;
		}
		loadPageProcess(processo);
		return false;
	}

	public void loadPageProcess(PCB processo) {
		if (processo.getPageTable().size() == 0) {
			processo.createPageTable();
			for (int i = 0; i < processo.getPageTable().size(); i++)
				allPagesSystem.add(processo.getPageTable().get(i));
		}

		for (int i = 0; i < processo.getPageTable().size(); i++)
			if (!containsInRam(processo.getPageTable().get(i))) {
				swapIn(processo.getPageTable().get(i));
				processo.getPageTable().get(i).setLastAccess();
			}
	}

	public void getMatrixRam(String[][] matrix) {
		int cont = 0;
		for (int col = 0; col < matrix[0].length; col++) {
			for (int row = 0; row < matrix.length; row++) {
				String value = "";
				if (matrixRam.get(cont).getId() == -1)
					value = "";
				else
					value = "PG" + matrixRam.get(cont).getId();
				matrix[row][col] = value;
				cont++;
			}
		}
	}

	public void getMatrixSwap(String[][] matrix) {
		int cont = 0;
		for (int col = 0; col < matrix[0].length; col++) {
			for (int row = 0; row < matrix.length; row++) {
				String value = "";
				if (matrixSwap.get(cont).getId() == -1)
					value = "";
				else
					value = "PG" + matrixSwap.get(cont).getId();
				matrix[row][col] = value;
				cont++;
			}
		}
	}

	private boolean containsInRam(Page pg) {
		boolean res = false;
		for (int i = 0; i < matrixRam.size(); i++) {
			if (matrixRam.get(i).getId() == pg.getId()) {
				if (pageReplacementPolicy == 1) // LRU algorithm flag
					pg.setLastAccess();
				res = true;
			}
		}
		return res;
	}

	private boolean containsInSwap(Page pg) {
		for (int i = 0; i < matrixSwap.size(); i++) {
			if (matrixSwap.get(i).getId() == pg.getId())
				return true;
		}
		return false;
	}

	private void swapIn(Page idPage) {
		int pos = getPositionRam();
		if (pos != -1) {
			idPage.setBitPresent(1);
			if (matrixRam.get(pos).getId() != -1)
				swapOut(pos);
			matrixRam.remove(pos);
			matrixRam.add(pos, idPage);
		}
	}

	private void swapOut(int position) {
		int pos = getPositionSwap();
		if (!containsInSwap(matrixRam.get(position))) {
			matrixSwap.add(pos, matrixRam.get(position));
			matrixSwap.remove(matrixSwap.size() - 1);
		}
	}

	private int getPositionSwap() {
		for (int i = 0; i < matrixSwap.size(); i++) {
			if (matrixSwap.get(i).getId() == -1)
				return i;
		}
		return -1;
	}

	private int getPositionRam() {
		for (int i = 0; i < matrixRam.size(); i++) {
			if (matrixRam.get(i).getId() == -1)
				return i;
		}
		int index = -1;
		for (int i = 0; i < matrixRam.size(); i++) {
			if (matrixRam.get(i).getBitPresent() > 2) {
				if (index == -1) {
					index = i;
				} else {
					if (matrixRam.get(index).getLastAccess() > matrixRam.get(i).getLastAccess()) {
						index = i;
					}
				}
			}
		}
		return index;
	}

	private void setMatrixRam(int size) {
		for (int i = 0; i < size; i++)
			matrixRam.add(new Page(-1));
	}

	private void setMatrixSwap(int size) {
		for (int i = 0; i < size; i++)
			matrixSwap.add(new Page(-1));
	}

	public int freeQuantityRam() {
		int count = 0;
		for (int i = 0; i < matrixRam.size(); i++) {
			if (matrixRam.get(i).getId() == -1)
				count++;
		}
		return count;
		// return Collections.frequency(matrixRam, -1);
	}

	public int freeQuantitySwap() {
		int count = 0;
		for (int i = 0; i < matrixSwap.size(); i++) {
			if (matrixSwap.get(i).getId() == -1)
				count++;
		}
		return count;
	}
}
