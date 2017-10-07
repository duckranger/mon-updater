package com.montier.updater.tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Abstract task in the system.
 * It is parameterized by the type of task (for the withXX methods)
 * 
 * @author nimo
 *
 * @param <T> - The type of Task
 */
public abstract class Task<T> {

	// Flag: Is this task failed?
	private boolean failed = false;
	
	// Flag: Has the rollback failed?
	private boolean rollbackFailed = false;
	
	// Used for logging success/failure reports and undo.
	protected String description;		
	
	// Source for directory related tasks
	protected String source;
	
	// Target for directory related tasks
	protected String target;
	
	// Update number - appended to filenames when required
	protected String updateNumber;
	
	// File name for file related tasks
	protected String fileName;
	
	protected Logger log = Logger.getLogger(Task.class.getName());
	
	// Log file for system to log
	protected File logFile;
	
	// Write info message to logger
	void info(String message) {
		log.log(Level.INFO,message);
	}
	
	// Write error message to logger
	void error(String message) {
		log.log(Level.SEVERE,message);
	}
	
	// Assign a specific log file to this task.
	// Returns self for method chaining
	@SuppressWarnings("unchecked")
	public T withLogFile(String name) {
		logFile = new File(name);
		return (T)this;
	}
	
	// Set the source for this task.
	// Returns self for method chaining
	@SuppressWarnings("unchecked")
	public T withSource(String source) {
		this.source = source;
		return (T)this;
	}
	
	// Set the target for this task.
	// Returns self for method chaining
	@SuppressWarnings("unchecked")
	public T withTarget(String target) {
		this.target = target;
		return (T)this;
	}

	// Set the update number for this task
	// Returns self for method chaining
	@SuppressWarnings("unchecked")
	public T withUpdateNumber(String updateNumber) {
		this.updateNumber = updateNumber;
		return (T) this;
	}
	
	// Set the filename for this task
	// Returns self for method chaining
	@SuppressWarnings("unchecked")
	public T withFileName(String fileName) {
		this.fileName = fileName;
		return (T)this;
	}
	
	// Set the description for this task
	// Returns self for method chaining
	@SuppressWarnings("unchecked")
	public T withDescription(String description) {
		this.description = description;
		return (T)this;
	}
	
	// Verify that a directory exists before it tries to run commands against it
	public void verifyDirectory(String directory) {
		if (!Files.exists(Paths.get(directory))) {
			throw new IllegalArgumentException("Directory " + directory + " does not exist.");
		}
	}
	
	// Verify that a directory exists before it tries to run commands against it.
	// This method will create the directory if it doesn't exist
	public void verifyOrCreateDirectory(String directory,FileAttribute<?> attr) {
		if (!Files.exists(Paths.get(directory))) {
			try {
				Files.createDirectories(Paths.get(directory),attr);
			} catch (Exception e) {
				throw new IllegalArgumentException("Directory " + directory +" can not be created." + e.getMessage() );
			}
		}
	}

	
	// Execute the task.
	// 
	// To operate, send a String... with the first one being the command itself (e.g. cp, rm etc) and
	// the rest are the arguments to the command.
	//
	// eg: "rm", "-rf", "/tmp/file.txt"
	//
	// If the execution throws an exception (or ends with RC!=0) - the task will be
	// marked as failed. This can be used to stop the chain of tasks from processing
	// and start rollback.
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
	
	// Mark this task as failed
	void markAsFailed() {
		this.failed = true;
	}
	
	// Mark the rollback of this task as failed. This can be used
	// to stop the rollback process in cases where there is a severe error
	void markRollbackAsFailed() {
		this.rollbackFailed = true;
	}
	
	// Report of this task for display
	public final String report() {
		if (description==null)
			return defaultReport();
		else
			return description;
	}
	
	// Whether this task is marked as failed	
	public boolean isFailed() {
		return failed;
	}

	// Whether this task's rollback is marked as failed
	public boolean isRollbackFailed() {
		return rollbackFailed;
	}

	// getter: description
	public String getDescription() {
		return description;
	}

	// getter: source
	public String getSource() {
		return source;
	}

	// getter: target
	public String getTarget() {
		return target;
	}

	// getter: update number
	public String getUpdateNumber() {
		return updateNumber;
	}

	// getter the raw filename (filename only)
	public String getRawFileName() {
		return fileName;
	}
	
	// Print the full file name including the update number
	public String getFullFileName() {
		return fileName + "_" + updateNumber;
	}

	// Abstract method: Used to run the task.
	// Inheriting tasks must override this method to perform their duty
	public abstract void run();
	
	// Abstract method: Used to rollback the task.
	// Inheriting tasks must override this method to reverse their execution
	public abstract void rollback();

	// Abstract method: Used to display a description of the task in logs
	// Inheriting tasks must override this method to reverse their execution
	public abstract String defaultReport();
	
}
