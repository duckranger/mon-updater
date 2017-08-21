package com.duckranger.tasks;

public class TaskFactory {

	private String target;
	private String updateNumber;
	
	public TaskFactory(String target,String updateNumber) {
		this.target = target;
		this.updateNumber = updateNumber;
	}
	
	public TarBackup createTarBackTask() {
		return new TarBackup().withTarget(target).withUpdateNumber(updateNumber);
	}
	
	public MD5Checker createMD5CheckerTask() {
		return new MD5Checker().withTarget(target).withUpdateNumber(updateNumber);
	}


}
