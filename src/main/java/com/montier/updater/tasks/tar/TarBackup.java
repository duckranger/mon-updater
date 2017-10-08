package com.montier.updater.tasks.tar;


/**
 * This task runs a tar command to backup a set of files.
 * It needs a source directory and a target directory, and a target file name (for the compressed file)
 * By default, the task will run tar zcvf of all the files in the source directory into the 
 * filename.tar.gz on the target directory.
 * 
 * The various methods let the user alter the command 
 * 
 * @author nimo
 *
 */
public class TarBackup extends TarTask {

	public TarBackup() {
		this.tarCommandModifiers="zcvf";
	}
	/**
	 * Execute the tar backup task.
	 * This will run:
	 * - tar ${modifiers} ${target}/${filename}_${updateNumber}.tar.gz ${source}/${fileMasks}
	 * 
	 * Note on filemask: if we want '*' then it needs to be empty, because tar will not work with *.
	 */
	@Override
	public void run() {
		verifyDirectory(source);
		verifyDirectory(target);
		execute("tar" , tarCommandModifiers,getTarget()+"/"+getFullFileName(),
				source + "/" + fileMask);
	}
	
	@Override
	public String defaultReport() {
		return "Tar backup task "+source+" to "+target;
	}

	// This is the reverse function (undo) of this task.
	// It will:
	// 1. delete all files in the source directory - because these may have been overwritten (changing file mask to be fine for rm)
	// 2. copy the backup tar to the source
	// 3. unpacks the tar at the source
	//
	// ProcessBuilder creates a Unix process.
	public void rollback() {
		info("--------------------------------------------------------------------------------");
		info("recovering "+description);
		ProcessBuilder builder = new ProcessBuilder();
		builder.redirectErrorStream(true);
		
		// Redirect the process output to the log file. This is the equivalent of > 1 & 2
		builder.redirectOutput(logFile);
		
		// Delete the files in the directory. if the file mask is empty (which works well for tar, but not
		// for rm) then make it a '*'.
		try {
			builder.command("rm","-rf",source +"/" + cpFileMask());
			int error = builder.start().waitFor();
			if (error !=0) {
				error( "Failed to delete content of the original directory " + source +" with RC="+error);
				markRollbackAsFailed();
			} else {
				info( "Original directory content "+source+" was deleted successfully");
			}
		} catch (Exception e) {
			error(e.getMessage());
			// Mark the rollback for this task as failed. This can be used as a  signal to stop the entire 
			// rollback process
			markRollbackAsFailed();
		}
		
		// If the rollback failed - do not try to continue the rollback for this task
		if (isRollbackFailed()) 
			return;
		
		// Copy tar file from target (backup) to source
		try {
			builder.command("cp","-f",target+"/"+getFullFileName(),source);
			int error = builder.start().waitFor();
			if (error!=0) {
				error( "Failed to copy tar backup file "+target+"/"+getFullFileName()+" to "+source+" with RC="+error);
				markRollbackAsFailed();
			}
		}catch (Exception e) {
			error(e.getMessage());
			markRollbackAsFailed();
		}
		
		// Do not try to unpack tar if rolback has already failed
		if (isRollbackFailed()) 
			return;
		
		// untar the tar file back into 'source'
		try {
			builder.command("tar","xvzf",source+"/"+getFullFileName());
			int error = builder.start().waitFor();
			if (error!=0) {
				error( "Failed to extract tar backup file "+source+"/"+getFullFileName()+" with RC="+error);
				markRollbackAsFailed();
			}
		}catch (Exception e) {
			error(e.getMessage());
			markRollbackAsFailed();
		}
	}
}
