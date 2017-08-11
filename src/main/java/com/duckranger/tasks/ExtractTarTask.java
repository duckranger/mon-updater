package com.duckranger.tasks;

import java.nio.file.Paths;

public class ExtractTarTask extends Task {

	private String source;			//Source directory - all files in there will be tar'd and copied.
	private String target; 			//Destination directory - the tar ball is going to be stored there.
	private String fileName;		//The file name to use for the compressed tar. 

	@Override
	public void run() {
		verifyDirectory(source);
		verifyDirectory(target);
		execute("tar","xvf", source+"/"+fileName,target);
	}

	@Override
	public void reverse() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String report() {
		// TODO Auto-generated method stub
		return null;
	}

	public ExtractTarTask withSource(String source) {
		this.source = source;
		return this;
	}


	public ExtractTarTask withTarget(String target) {
		this.target = target;
		return this;
	}
	
	private void verifyDirectory(String directory) {
		if (Paths.get(directory) == null) {
			throw new IllegalArgumentException("Directory " + directory + " does not exist.");
		}
	}
	
	public ExtractTarTask withFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	
	public ExtractTarTask withDescription(String description) {
		this.description = description;
		return this;
	}

}
