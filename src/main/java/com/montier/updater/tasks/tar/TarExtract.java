package com.montier.updater.tasks.tar;

// A task to extract a single tar file
public class TarExtract extends TarTask {

	public TarExtract() {
		this.tarCommandModifiers = "xzvf";
	}

	@Override
	public void run() {
		verifyDirectory(source);
		verifyDirectory(target);
		execute("tar", tarCommandModifiers, source + "/" + getFullFileName(),"-C",  target);
	}

	@Override
	public void rollback() {
		info("--------------------------------------------------------------------------------");
		info("recovering " + description);
		ProcessBuilder builder = new ProcessBuilder();
		builder.redirectErrorStream(true);

		// Redirect the process output to the log file. This is the equivalent of > 1 &
		// 2
		builder.redirectOutput(logFile);

		// Delete the files in the directory. if the file mask is empty (which works
		// well for tar, but not for rm) then make it a '*'.
		try {
			builder.command("rm", "-rf", target + "/" + cpFileMask());
			int error = builder.start().waitFor();
			if (error != 0) {
				error("Failed to delete content of the target directory " + target + " with RC=" + error);
				markRollbackAsFailed();
			} else {
				info("Target directory content " + target + " was deleted successfully");
			}
		} catch (Exception e) {
			error(e.getMessage());
			// Mark the rollback for this task as failed. This can be used as a signal to
			// stop the entire rollback process
			markRollbackAsFailed();
		}

	}

	@Override
	public String defaultReport() {
		return "Extract tar from " + source + "/" + fileName + " to " + target;
	}

	public TarExtract withSource(String source) {
		this.source = source;
		return this;
	}

}
