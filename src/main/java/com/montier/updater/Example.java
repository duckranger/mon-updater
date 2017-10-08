package com.montier.updater;

import com.montier.updater.tasks.TaskFactory;
import com.montier.updater.tasks.tar.TarExtract;

/**
 * An example update script.
 * 
 * @author nimo
 *
 */
public class Example {

	public static final String BACKUP_DIR = "/tmp/montier/backup";
	public static final String UPDATE_NUMBER = "0.12";
	public static final String UI_APP_DIR = "/tmp/montier/ui_app";
	public static final String REPORTS_APP_DIR = "/tmp/montier/reports_app";
	public static final String RETENTION_APP_DIR = "/tmp/montier/retention_app";
	public static final String SYSLOGKEEPALIVE_APP_DIR = "/tmp/montier/syslogkeepalive_app";
	
	public static void main(String[] args) {
		Script script = new Script();
		TaskFactory taskFactory = new TaskFactory(BACKUP_DIR,UPDATE_NUMBER);
		
		script.addStep(taskFactory.createTarBackupTask()
			.withDescription("Backup current ui directory")
			.withSource(UI_APP_DIR)
			.withFileName("ui.before_update")
		)
		.addStep(taskFactory.createTarBackupTask()
			.withDescription("Backup current reports directory")
			.withSource(REPORTS_APP_DIR)
			.withFileName("reports.before_update")
		)
		.addStep(taskFactory.createTarBackupTask()
			.withDescription("Backup current retention TOMEE instance")
			.withSource(RETENTION_APP_DIR)
			.withFileName("retention.before_update")
		)
		.addStep(taskFactory.createTarBackupTask()
			.withDescription("Backup current syslog keepalive TOMEE instance")
			.withSource(SYSLOGKEEPALIVE_APP_DIR)
			.withFileName("syslogkeepalive.before_update")
		)	
		.addStep(taskFactory.createTarBackupTask()
			.withDescription("Backup current wsm keepalive TOMEE instance")
			.withSource("${WSMKEEPALIVE_APP_DIR}")
			.withFileName("wsmkeepalive.before_update")
		)
		.addStep(taskFactory.createTarBackupTask().withDescription("Backup current device resources TOMEE instance")
			.withSource("${DEVICERESOURCES_APP_DIR}")
			.withFileName("deviceresources.before_update")
		)
		.addStep(taskFactory.createTarBackupTask().withDescription("Backup current service resources TOMEE instance")
			.withSource("${SERVICERESOURCE_APP_DIR}")
			.withFileName("serviceresources.before_update")
		)
		.addStep(taskFactory.createMD5CheckerTask().withDescription("TOMME archive")
			.withMD5Filename("${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME}.md5")
			.withFileName("${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME}")
		)
		.addStep(new TarExtract().withDescription("new TOMEE installation extract")
			.withTarget("${BACKUP_DIR}")
			.withSource("${current_installation_directory}")
			.withFileName("${TOMEE_174_TAR_FILE_NAME}")
		);

	}
}
