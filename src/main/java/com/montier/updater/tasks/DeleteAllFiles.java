package com.montier.updater.tasks;

public class DeleteAllFiles extends Task<DeleteAllFiles>{

	@Override
	public void run() {
		verifyDirectory(source);
		execute("rm","-rf", source +"/*");
		
	}

	@Override
	public void rollback() {
		//There is no going back on rm -rf
	}

	@Override
	public String defaultReport() {
		return "Delete all files from "+source;
	}

}
