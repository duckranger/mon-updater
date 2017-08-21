package com.duckranger.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;

public abstract class Task<T> {

	private boolean failed = false;
	private boolean rollbackFailed = false;
	protected String description;		//Used for logging success/failure reports and undo.
	
	protected String source;
	protected String target;
	protected String updateNumber;
	protected String fileName;
	
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
	
	@SuppressWarnings("unchecked")
	public T withSource(String source) {
		this.source = source;
		return (T)this;
	}
	@SuppressWarnings("unchecked")
	public T withTarget(String target) {
		this.target = target;
		return (T)this;
	}

	@SuppressWarnings("unchecked")
	public T withUpdateNumber(String updateNumber) {
		this.updateNumber = updateNumber;
		return (T) this;
	}
	
	@SuppressWarnings("unchecked")
	public T withFileName(String fileName) {
		this.fileName = fileName;
		return (T)this;
	}
	
	@SuppressWarnings("unchecked")
	public T withDescription(String description) {
		this.description = description;
		return (T)this;
	}
	
	public void verifyDirectory(String directory) {
		if (!Files.exists(Paths.get(directory))) {
			throw new IllegalArgumentException("Directory " + directory + " does not exist.");
		}
	}
	
	public void verifyOrCreateDirectory(String directory,FileAttribute<?> attr) {
		if (!Files.exists(Paths.get(directory))) {
			try {
				Files.createDirectories(Paths.get(directory),attr);
			} catch (Exception e) {
				throw new IllegalArgumentException("Directory " + directory +" can not be created." + e.getMessage() );
			}
		}
	}

	
	/**
	 * Used to wrap a command 
	 * @param command
	 * @return
	 * @throws InterruptedException 
	 * @throws IOException 
	 */
	void execute(String... commandAndArgs) {
		ProcessBuilder builder = new ProcessBuilder(commandAndArgs);
		builder.redirectErrorStream(true);
		builder.redirectOutput(logFile);
		try {
			Process process = builder.start();
			int error = process.waitFor();
			if (error!=0) 
				throw new Exception("Command "+Arrays.toString(commandAndArgs)+" returned RC="+error);
			info(report()+ " successful");
		} catch (Exception e) {
			error(e.getMessage());
			error(report()+ " failed");
			markAsFailed();
		}
	}
	
	
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
	
	
	
	public final String report() {
		if (description==null)
			return defaultReport();
		else
			return description;
	}
	
	public abstract void run();
	public abstract void reverse();
	public abstract String defaultReport();
	
}
