package simuos.controller;

import java.awt.Point;
import java.util.Date;

public class Page {
	public static int totalPaginas;

	/*
	 * 0 = Page Create
	 * 1 = Awaiting process (just load to memory)
	 * 2 = -
	 * 3 = Page eligible for replacement 
	 */
	private Integer bitPresent;
	private int id;
	private Point posRam;
	private Point posSwap;
	private long lastAccess;
	
	public Page() {
		bitPresent = 0;
		id = ++totalPaginas;
		posRam = new Point();
		posSwap = new Point();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getBitPresent() {
		return bitPresent;
	}

	public void setBitPresent(Integer bitPresent) {
		this.bitPresent = bitPresent;
	}

	public long getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess() {
		this.lastAccess = new Date().getTime();
	}

	public void setLastAccess(long lastAccess) {
		this.lastAccess = lastAccess;
	}

	public Page(int id) {
		super();
		this.id = id;
	}

	public Point getPosRam() {
		return posRam;
	}

	public void setPosRam(int x, int y) {
		this.posRam.x = x;
		this.posRam.y = y;
	}

	public Point getPosSwap() {
		return posSwap;
	}

	public void setPosSwap(Point posSwap) {
		this.posSwap = posSwap;
	}
}
