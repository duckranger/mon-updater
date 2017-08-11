package com.duckranger.updates;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import com.duckranger.tasks.ExtractTarTask;
import com.duckranger.tasks.MD5CheckerTask;
import com.duckranger.tasks.TarBackupTaskFactory;
import com.duckranger.tasks.Task;

public class Update {
	
	public static void main(String[] args) {
		List<Task> tasks = new LinkedList<>();
		
		TarBackupTaskFactory factory = new TarBackupTaskFactory("${BACKUP_DIR}","updateNumber");
		
		//Step1
		tasks.add(factory.create().withDescription("Backup current ui directory")
			.withSource("${UI_APP_DIR}")
			.withFileName("ui.before_update")
		);
		tasks.add(factory.create().withDescription("Backup current reports directory")
			.withSource("${REPORTS_APP_DIR}")
			.withFileName("reports.before_update")
		);
		tasks.add(factory.create().withDescription("Backup current retention TOMEE instance")
			.withSource("${RETENTION_APP_DIR}")
			.withFileName("retention.before_update")
		);	
		tasks.add(factory.create().withDescription("Backup current syslog keepalive TOMEE instance")
			.withSource("${SYSLOGKEEPALIVE_APP_DIR}")
			.withFileName("syslogkeepalive.before_update")
		);	
		tasks.add(factory.create().withDescription("Backup current wsm keepalive TOMEE instance")
			.withSource("${WSMKEEPALIVE_APP_DIR}")
			.withFileName("wsmkeepalive.before_update")
		);
		tasks.add(factory.create().withDescription("Backup current device resources TOMEE instance")
			.withSource("${DEVICERESOURCES_APP_DIR}")
			.withFileName("deviceresources.before_update")
		);
		tasks.add(factory.create().withDescription("Backup current service resources TOMEE instance")
			.withSource("${SERVICERESOURCE_APP_DIR}")
			.withFileName("serviceresources.before_update")
		);
		tasks.add(new MD5CheckerTask().withDescription("TOMME archive")
			.withMD5Filename("${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME}.md5")
			.withFilename("${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME}"));
		tasks.add(new ExtractTarTask().withDescription("new TOMEE installation extract")
			.withTarget("${BACKUP_DIR}")
			.withSource("${current_installation_directory}")
			.withFileName("${TOMEE_174_TAR_FILE_NAME}")
		);
	}

/*    
         
     
  
     
     
     
   
         
    ### deploy new TOMEE installations ####
     
    # loop over components
     
    # loop over to TOMEE components
    for component_name in ${TOMEE_COMPONENTS[@]}; do
 
        if [[ ${STEP_1_RC} -eq 0 ]]; then      
         
            # set the component current directory and jmxport
             
            current_installation_directory=""
            jmx_port=""
            init_file_name=""
             
            case "${component_name}" in
                ui)
                    current_installation_directory=${UI_APP_DIR}                    
                    ;;
                retention)
                    current_installation_directory=${RETENTION_APP_DIR}                 
                    ;;
                syslogkeepalive)
                    current_installation_directory=${SYSLOGKEEPALIVE_APP_DIR}
                    ;;
                wsmkeepalive)
                    current_installation_directory=${WSMKEEPALIVE_APP_DIR}                  
                    ;;  
                deviceresources)
                    current_installation_directory=${DEVICERESOURCES_APP_DIR}                   
                    ;;
                serviceresource)
                    current_installation_directory=${SERVICERESOURCE_APP_DIR}                   
                    ;;      
                reports)
                    current_installation_directory=${REPORTS_APP_DIR}                   
                    ;;
            esac
             
            # make sure current directory was set
            if [[ -z ${current_installation_directory} ]]; then
                error "current TOMEE directory for ${component_name} was not set. abort"
                STEP_1_RC=1
            else
                info "current TOMEE directory for ${component_name} was set to ${current_installation_directory} "
                 
                 
                # invoke TOMEE update for component
                 
                deploy_new_tomee ${component_name} ${current_installation_directory} 
                 
                if [ $? -ne 0 ]; then
                    error "failed to deploy new TOMEE for ${component_name}. abort"
                    STEP_1_RC=1             
                else               
                    info  "new TOMEE for ${component_name} deployed successfully"
                fi         
            fi 
         
        fi
    done
     
     
    return ${STEP_1_RC}
 
}
 */
	
	private static void perform(List<Task> tasks) {
		boolean rollbackRequired = false;
		for (Task task : tasks) {
			task.run();
			if (task.failed())
				rollbackRequired=true;
				break;
		}
		if (rollbackRequired) {
			for (ListIterator<Task> iter = tasks.listIterator(tasks.size());  iter.hasPrevious(); ) {
				Task task = iter.previous();
				if (task.failed())
					task.reverse();
				if (task.rollbackFailed())
					break;
			}
		}
	}

}
