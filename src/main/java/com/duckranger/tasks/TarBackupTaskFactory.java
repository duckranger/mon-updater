package com.duckranger.tasks;

public class TarBackupTaskFactory {

	private String target;
	private String updateNumber;
	
	public TarBackupTaskFactory(String target,String updateNumber) {
		this.target = target;
		this.updateNumber = updateNumber;
	}
	
	public TarBackupTask create() {
		return new TarBackupTask().withTarget(target).withUpdateNumber(updateNumber);
	}

}
