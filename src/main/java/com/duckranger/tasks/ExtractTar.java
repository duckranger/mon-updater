package com.duckranger.tasks;

public class ExtractTar extends Task<ExtractTar> {

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
	public String defaultReport() {	
		return "Extract tar from "+source+"/"+fileName+" to "+target;
	}

	public ExtractTar withSource(String source) {
		this.source = source;
		return this;
	}

}
