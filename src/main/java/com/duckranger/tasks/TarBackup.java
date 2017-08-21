package com.duckranger.tasks;


/**
 * This task runs a tar command to backup a set of files.
 * It needs a source directory and a target directory, and a target file name (for the compressed file)
 * By default, the task will run tar zcvf of all the files in the source directory into the 
 * filename.tar.gz on the source directory.
 * 
 * The various methods let the user alter the command 
 * 
 * @author nimo
 *
 */
public class TarBackup extends Task<TarBackup> {

	private String tarCommandModifiers = "zcvf";	//The modifiers to the tar command eg. tar zcvf
	private String fileMask = "*";					//The file mask to tar and backup from source
	
	/**
	 * Execute the tar backup task.
	 * This will run:
	 * - tar ${modifiers} ${target}/${filename}_${updateNumber}.tar.gz ${source}/${fileMasks}
	 */
	@Override
	public void run() {
		verifyDirectory(source);
		verifyDirectory(target);
		execute("tar" , tarCommandModifiers,target,"/"+fileName+"_"+updateNumber +".tar.gz ",
				source + "/" + fileMask);
	}
	
	@Override
	public String defaultReport() {
		return "Tar backup task "+source+" to "+target;
	}

	public TarBackup withFileMask(String fileMask) {
		this.fileMask = fileMask;
		return this;
	}

	public TarBackup withTarModifiers(String modifiers) {
		this.tarCommandModifiers = modifiers;
		return this;
	}
	
	public void reverse() {
		info("--------------------------------------------------------------------------------");
		info("recovering "+description);
		ProcessBuilder builder = new ProcessBuilder();
		builder.redirectErrorStream(true);
		builder.redirectOutput(logFile);
		try {
			builder.command("rm","-rf "+source +"/" + fileMask);
			int error = builder.start().waitFor();
			if (error !=0) {
				error( "Failed to delete content of the original directory " + source +" with RC="+error);
				markRollbackAsFailed();
			} else {
				info( "Original directory content "+source+" was deleted successfully");
			}
		} catch (Exception e) {
			error(e.getMessage());
			markRollbackAsFailed();
		}
		if (rollbackFailed()) return;
		try {
			builder.command("cp","-f "+target+"/"+fileName+"_"+updateNumber+".tar.gz "+source);
			int error = builder.start().waitFor();
			if (error!=0) {
				error( "Failed to copy tar backup file "+source+"/"+fileName+"_"+updateNumber+".tar.gz to "+source+" with RC="+error);
				markRollbackAsFailed();
			}
		}catch (Exception e) {
			error(e.getMessage());
			markRollbackAsFailed();
		}
		if (rollbackFailed()) return;
		try {
			builder.command("tar","xvfz "+source+"/"+fileName+"_"+updateNumber+".tar.gz");
			int error = builder.start().waitFor();
			if (error!=0) {
				error( "Failed to extract tar backup file "+source+"/"+fileName+"_"+updateNumber+".tar.gz with RC="+error);
				markRollbackAsFailed();
			}
		}catch (Exception e) {
			error(e.getMessage());
			markRollbackAsFailed();
		}
	}
	
}
