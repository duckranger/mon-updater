package com.duckranger.tasks;

import java.nio.file.Paths;

public class CopyFileTask extends Task{

	private String source;
	private String target;
	private String fileName;						//The file name to use for the compressed tar. 

	
	@Override
	public void run() {
		verifyDirectory(source);
		verifyDirectory(target);
		execute("cp","-f", source+"/"+fileName,target);
	}

	@Override
	public void reverse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String report() {
		return description==null?"File copy task "+source+"/"+fileName+" to "+target:description;
	}
	
	public CopyFileTask withSource(String source) {
		this.source = source;
		return this;
	}


	public CopyFileTask withTarget(String target) {
		this.target = target;
		return this;
	}
	
	public CopyFileTask withFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}

	private void verifyDirectory(String directory) {
		if (Paths.get(directory) == null) {
			throw new IllegalArgumentException("Directory " + directory + " does not exist.");
		}
	}
}
