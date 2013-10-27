package util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import system.Settings;

public class Logger {
	private static Logger instance          = null;
	private static final String DATE_FORMAT = "MM-dd_HH:mm:ss";
	private static BufferedWriter bw        = null;
	private static final String folder      = Settings.getString("log_folder");
	private static final String pwd         = Settings.getString("pwd");
	
	private Logger (String dateTime) {
		try {
			File writeDir = new File(pwd+"/"+folder);
			File writeFile = new File(folder+dateTime+".txt");
			FileWriter fw = new FileWriter(writeFile);
			bw = new BufferedWriter(fw);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void write(String toWrite) {
		
	}
	
	public static void flush() {
		instance = null;
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void setInstance() {
		if (instance == null) {
			synchronized (Logger.class) {
				if (instance == null) {
					SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
					instance = new Logger(sdf.format(Calendar.getInstance().getTime()));
				}
			}
		}
	}
}
