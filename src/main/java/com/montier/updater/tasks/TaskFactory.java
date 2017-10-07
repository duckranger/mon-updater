package com.montier.updater.tasks;

/**
 * A Task factory can be used to create tasks, as it contains attributes common
 * to many tasks throughout the update process.
 * 
 * @author nimo
 *
 */
public class TaskFactory {

	// Target directory name
	private String target;
	
	// Update number. Used for file names
	private String updateNumber;
	
	// Create the task factory, passing in the target directory name and the update number
	// for this update run
	public TaskFactory(String target,String updateNumber) {
		this.target = target;
		this.updateNumber = updateNumber;
	}
	
	// Create a TarBackup task, using the target and update number
	public TarTask createTarBackTask() {
		return new TarBackup().withTarget(target).withUpdateNumber(updateNumber);
	}
	
	// Create an MD5Checker task, using the target and update number
	public MD5Checker createMD5CheckerTask() {
		return new MD5Checker().withTarget(target).withUpdateNumber(updateNumber);
	}


}
