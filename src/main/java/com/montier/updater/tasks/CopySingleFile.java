package com.montier.updater.tasks;

public class CopySingleFile extends Task<CopySingleFile>{

	@Override
	public void run() {
		verifyDirectory(source);
		verifyDirectory(target);
		execute("cp","-f", source+"/"+fileName,target);
	}

	@Override
	public void rollback() {
		execute("cp","-f", target+"/"+fileName,source);
	}

	@Override
	public String defaultReport() {
		return "File copy task "+source+"/"+fileName+" to "+target;
	}
	
}
