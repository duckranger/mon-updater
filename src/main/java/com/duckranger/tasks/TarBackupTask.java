package com.duckranger.tasks;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
public class TarBackupTask extends Task {

	private String description;		//Used for logging success/failure reports and undo.
	private String source;			//Source directory - all files in there will be tar'd and copied.
	private String target; 			//Destination directory - the tar ball is going to be stored there.
	
	//File name and update number together are used to create the full file name of the backup tar
	//in the format of ${filename}_${updateNumber}
	private String updateNumber; 					//Update Number identifier
	private String fileName;						//The file name to use for the compressed tar. 
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
		execute("tar " , tarCommandModifiers + " " + target + "/"+fileName+"_"+updateNumber +".tar.gz "+
				source + "/" + fileMask);
	}
	
	public String report() {
		return description==null?"Tar backup task "+source+" to "+target:description;
	}

	private void verifyDirectory(String directory) {
		if (Paths.get(directory) == null) {
			throw new IllegalArgumentException("Directory " + directory + " does not exist.");
		}
	}
	
	public TarBackupTask withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public TarBackupTask withSource(String source) {
		this.source = source;
		return this;
	}


	public TarBackupTask withTarget(String target) {
		this.target = target;
		return this;
	}

	public TarBackupTask withUpdateNumber(String updateNumber) {
		this.updateNumber = updateNumber;
		return this;
	}

	public TarBackupTask withFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	
	public TarBackupTask withFileMask(String fileMask) {
		this.fileMask = fileMask;
		return this;
	}

	public TarBackupTask withTarModifiers(String modifiers) {
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
