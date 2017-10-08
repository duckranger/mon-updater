package com.montier.updater.tasks;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.montier.updater.tasks.tar.TarBackup;
import com.montier.updater.tasks.tar.TarExtract;
import com.montier.updater.tasks.tar.TarTask;

public class ExtractTarTest {

	public static final String BACKUP_DIR = "/tmp/montier/backup";
	public static final String UPDATE_NUMBER = "upd";
	public static final String FILENAME = "tarfile";
	public static final String LOGFILE = "/tmp/montier/log.txt";
	public static final String DESCRIPTION = "Backup current ui directory";
	public static final String UIAPPDIR = "/tmp/montier/uiapp";

	private static TaskFactory taskFactory;
	List<Task<?>> tasks;

	@BeforeClass
	public static void setup() {
		taskFactory = new TaskFactory(BACKUP_DIR, UPDATE_NUMBER);
		File f = new File(BACKUP_DIR);
		if (!f.exists()) {
			f.mkdirs();
		}
		f = new File(UIAPPDIR);
		if (!f.exists()) {
			f.mkdirs();
		}
		try {
			new File(UIAPPDIR, "t.txt").createNewFile();
		} catch (IOException e) {
			Assert.fail();
		}

	}

	@AfterClass
	public static void tearDown() {
		File f = new File(BACKUP_DIR);
		if (f.exists()) {
			for (String s : f.list())
				new File(f, s).delete();
			f.delete();
		}
		f = new File(UIAPPDIR);
		if (f.exists()) {
			for (String s : f.list())
				new File(f, s).delete();
			f.delete();
		}
	}

	@Test
	public void testFactoryCreated() {
		Assert.assertNotNull(taskFactory);
	}

	private TarTask createBackupTask() {
		return taskFactory.createTarBackupTask()
			.withLogFile(LOGFILE)
			.withFileName(FILENAME)
			.withDescription(DESCRIPTION)
			.withUpdateNumber(UPDATE_NUMBER)
			.withSource(UIAPPDIR);
	}
	
	private TarTask createExtractTask() {
		return taskFactory.createTarExtractTask()
			.withLogFile(LOGFILE)
			.withFileName(FILENAME)
			.withDescription(DESCRIPTION)
			.withUpdateNumber(UPDATE_NUMBER)
			.withTarget("/")
			.withSource(BACKUP_DIR);
	}
	
	@Test
	public void testTarFileExtract() {
		TarBackup backup = (TarBackup) createBackupTask();
		backup.run();
		Assert.assertFalse(backup.isFailed());
		File f = new File(backup.getTarget(),backup.getFullFileName());
		Assert.assertTrue(f.exists());
		f = new File(UIAPPDIR,"t.txt");
		Assert.assertTrue(f.exists());
		f.delete();
		TarExtract extract = (TarExtract) createExtractTask();
		extract.run();
		Assert.assertFalse(extract.isFailed());
		f = new File(UIAPPDIR,"t.txt");
		Assert.assertTrue(f.exists());
	}

}
