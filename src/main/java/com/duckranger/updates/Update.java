package com.duckranger.updates;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.duckranger.tasks.ExtractTar;
import com.duckranger.tasks.TaskFactory;
import com.duckranger.tasks.Task;

public class Update {
	
	public static void main(String[] args) {
		List<Task<?>> tasks = new LinkedList<>();
		
		TaskFactory taskFactory = new TaskFactory("${BACKUP_DIR}","updateNumber");
		
		//Step1
		tasks.add(taskFactory.createTarBackTask().withDescription("Backup current ui directory")
			.withSource("${UI_APP_DIR}")
			.withFileName("ui.before_update")
		);
		tasks.add(taskFactory.createTarBackTask().withDescription("Backup current reports directory")
			.withSource("${REPORTS_APP_DIR}")
			.withFileName("reports.before_update")
		);
		
		tasks.add(taskFactory.createTarBackTask().withDescription("Backup current retention TOMEE instance")
			.withSource("${RETENTION_APP_DIR}")
			.withFileName("retention.before_update")
		);	
		tasks.add(taskFactory.createTarBackTask().withDescription("Backup current syslog keepalive TOMEE instance")
			.withSource("${SYSLOGKEEPALIVE_APP_DIR}")
			.withFileName("syslogkeepalive.before_update")
		);	
		tasks.add(taskFactory.createTarBackTask().withDescription("Backup current wsm keepalive TOMEE instance")
			.withSource("${WSMKEEPALIVE_APP_DIR}")
			.withFileName("wsmkeepalive.before_update")
		);
		tasks.add(taskFactory.createTarBackTask().withDescription("Backup current device resources TOMEE instance")
			.withSource("${DEVICERESOURCES_APP_DIR}")
			.withFileName("deviceresources.before_update")
		);
		tasks.add(taskFactory.createTarBackTask().withDescription("Backup current service resources TOMEE instance")
			.withSource("${SERVICERESOURCE_APP_DIR}")
			.withFileName("serviceresources.before_update")
		);
		tasks.add(taskFactory.createMD5CheckerTask().withDescription("TOMME archive")
			.withMD5Filename("${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME}.md5")
			.withFileName("${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME}")
		);
		tasks.add(new ExtractTar().withDescription("new TOMEE installation extract")
			.withTarget("${BACKUP_DIR}")
			.withSource("${current_installation_directory}")
			.withFileName("${TOMEE_174_TAR_FILE_NAME}")
		);
		
		perform(tasks);
	}

	
	private static void perform(List<Task<?>> tasks) {
		boolean rollbackRequired = false;
		for (Task<?> task : tasks) {
			task.run();
			if (task.failed())
				rollbackRequired=true;
				break;
		}
		if (rollbackRequired) {
			for (ListIterator<Task<?>> iter = tasks.listIterator(tasks.size());  iter.hasPrevious(); ) {
				Task<?> task = iter.previous();
				if (task.failed())
					task.reverse();
				if (task.rollbackFailed())
					break;
			}
		}
	}

}
