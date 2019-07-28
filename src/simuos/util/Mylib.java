package simuos.util;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class Mylib {
	public void CenteredFrame(javax.swing.JFrame objFrame) {
		PositionFrame(objFrame, 0, 0);
	}

	public static void alert(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}

	public void LeftCenteredFrame(javax.swing.JFrame objFrame) {
		Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int iCoordX = (objDimension.width - objFrame.getWidth()) / -2;
		PositionFrame(objFrame, iCoordX, 0);
	}

	public void PositionFrame(javax.swing.JFrame objFrame, int offsetX, int offsetY) {
		Dimension objDimension = Toolkit.getDefaultToolkit().getScreenSize();
		int iCoordX = (objDimension.width - objFrame.getWidth()) / 2;
		int iCoordY = (objDimension.height - objFrame.getHeight()) / 2;
		objFrame.setLocation(iCoordX + offsetX, iCoordY + offsetY);
	}

	public static void ClearTable(javax.swing.JTable table) {
		while (table.getRowCount() > 0)
			((DefaultTableModel) table.getModel()).removeRow(0);
	}

	public static void PrepareTable(javax.swing.JTable table, String[][] data) {
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		ClearTable(table);
		for (int i = 0; i < data.length; i++)
			tableModel.addRow(data[i]);
		table.setModel(tableModel);
	}
}
