package com.montier.updater.tasks.tar;

import com.montier.updater.tasks.Task;

public abstract class TarTask extends Task<TarTask> {

	public static final String EXTENSION = ".tar.gz";
	protected String tarCommandModifiers = "";
	protected String fileMask = "";

	
	public TarTask withFileMask(String mask) {
		if (mask!=null)
			this.fileMask = mask;
		return this;
	}

	public TarTask withTarModifiers(String modifiers) {
		if (modifiers!=null)
			this.tarCommandModifiers = modifiers;
		return this;
	}

	public String getFullFileName() {
		return super.getFullFileName()+EXTENSION;
	}
	
	protected String cpFileMask() {
		return fileMask == "" ? "*" : fileMask;
	}

}