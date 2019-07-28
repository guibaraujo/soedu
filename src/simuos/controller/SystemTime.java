package simuos.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemTime extends Thread {

	public static String getSystemTime() {
		SimpleDateFormat sdfDate = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		return sdfDate.format(now);
	}

	public void run() {
	}
}
