package com.duckranger.tasks;

public class DeleteAllFiles extends Task<DeleteAllFiles>{

	@Override
	public void run() {
		verifyDirectory(source);
		execute("rm","-rf", source +"/*");
		
	}

	@Override
	public void reverse() {
		//There is no going back on rm -rf
	}

	@Override
	public String defaultReport() {
		return "Delete all files from "+source;
	}

}
