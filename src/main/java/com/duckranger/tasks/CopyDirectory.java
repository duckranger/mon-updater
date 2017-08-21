package com.duckranger.tasks;

import java.nio.file.attribute.PosixFilePermissions;

public class CopyDirectory extends Task<CopyDirectory> {

	
	private boolean createAllowed = false;
	private boolean force = true;
	
	@Override
	public void run() {
		verifyDirectory(source);
		if (createAllowed) {
			verifyOrCreateDirectory(target, 
					PosixFilePermissions.asFileAttribute(PosixFilePermissions.fromString("rwxrwxrwx")));
		} else {
			verifyDirectory(target);
		}
		execute("cp","-r"+(force?"f":""), source,target);
		
	}

	@Override
	public void reverse() {
		execute("cp","-r", target,source);
	}

	@Override
	public String defaultReport() {
		return "Copy entire directory: "+source+" to "+target;
	}
	
	public CopyDirectory withCreateTargetIfDoesNotExist() {
		createAllowed = true;
		return this;
	}
	
	public CopyDirectory doNotForce() {
		force = false;
		return this;
	}
	

}
