package com.montier.updater.tasks;

// A task to extract a single tar file
public class ExtractTar extends TarTask {

	public ExtractTar() {
		this.tarCommandModifiers = "xvf";	
	}
	
	@Override
	public void run() {
		verifyDirectory(source);
		verifyDirectory(target);
		execute("tar",tarCommandModifiers, source+"/"+getFullFileName(),target);
	}

	@Override
	public void rollback() {
		
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
