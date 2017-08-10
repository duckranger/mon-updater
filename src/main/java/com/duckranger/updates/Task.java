package com.duckranger.updates;

import java.io.File;
import java.io.IOException;

public abstract class Task {

	private boolean failed = false;
	private boolean rollbackFailed = false;
	
	File logFile;
	
	void info(String message) {
		//log.info(message);
	}
	
	void error(String message) {
		//log.error(message);
	}
	
	void setLogFile(String name) {
		logFile = new File(name);
	}
	
	/**
	 * Used to wrap a command 
	 * @param command
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	void execute(String command) {
		ProcessBuilder builder = new ProcessBuilder(command);
		builder.redirectErrorStream(true);
		builder.redirectOutput(logFile);
		try {
			Process process = builder.start();
			int error = process.waitFor();
			if (error!=0) 
				throw new Exception("Command "+command+" returned RC="+error);
			info(report()+ " successful");
		} catch (Exception e) {
			error(e.getMessage());
			error(report()+ " failed");
			markAsFailed();
		}
	}
	
	public abstract void run();
	public abstract void reverse();
	public abstract String report();
	
	void markAsFailed() {
		this.failed = true;
	}
	
	void markRollbackAsFailed() {
		this.rollbackFailed = true;
	}
	
	public boolean failed() {
		return failed;
	}
	
	public boolean rollbackFailed() {
		return rollbackFailed;
	}
}
