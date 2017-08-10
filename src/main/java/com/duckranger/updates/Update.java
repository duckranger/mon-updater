package com.duckranger.updates;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

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
				.withSource("${RETENTION_APP_DIR}")
				.withFileName("retention.before_update")
			);	
		
	}



/*
 *   ### backup current TOMEE installations ####
     
   
     
    # backup syslog keepalive   
    if [[ ${STEP_1_RC} -eq 0 ]]; then
     
        info "backing up current syslog keepalive TOMEE instance "
     
        cd ${SYSLOGKEEPALIVE_APP_DIR} 2>> ${LOG_FILE}
     
        tar zcvf ${BACKUP_DIR}/syslogkeepalive.before_update_${UPDATE_NO}.tar.gz *  >> ${LOG_FILE} 2>&1
         
        if [ $? -ne 0 ]; then
            error "failed to backup current syslogkeepalive directory ${SYSLOGKEEPALIVE_APP_DIR} to ${BACKUP_DIR}/syslogkeepalive.before_update_${UPDATE_NO}.tar.gz . abort"
            STEP_1_RC=1             
        else               
            info  "current syslogkeepalive directory ${SYSLOGKEEPALIVE_APP_DIR} backed up to ${BACKUP_DIR}/syslogkeepalive.before_update_${UPDATE_NO}.tar.gz successfully"
            SYSLOG_KEEPALIVE_WAS_BACKEDUP=1
        fi 
    fi
     
    # backup wsm keepalive  
    if [[ ${STEP_1_RC} -eq 0 ]]; then
     
        info "backing up current wsm keepalive TOMEE instance "
     
        cd ${WSMKEEPALIVE_APP_DIR} 2>> ${LOG_FILE}
     
        tar zcvf ${BACKUP_DIR}/wsmkeepalive.before_update_${UPDATE_NO}.tar.gz * >> ${LOG_FILE} 2>&1
         
        if [ $? -ne 0 ]; then
            error "failed to backup current wsmkeepalive directory ${WSMKEEPALIVE_APP_DIR} to ${BACKUP_DIR}/wsmkeepalive.before_update_${UPDATE_NO}.tar.gz . abort"
            STEP_1_RC=1             
        else               
            info  "current wsmkeepalive directory ${WSMKEEPALIVE_APP_DIR} backed up to ${BACKUP_DIR}/wsmkeepalive.before_update_${UPDATE_NO}.tar.gz successfully"
            WSM_KEEPALIVE_WAS_BACKEDUP=1
        fi 
    fi
     
    # backup device resources   
    if [[ ${STEP_1_RC} -eq 0 ]]; then
     
        info "backing up current device resources TOMEE instance "
     
        cd ${DEVICERESOURCES_APP_DIR} 2>> ${LOG_FILE}
     
        tar zcvf ${BACKUP_DIR}/deviceresources.before_update_${UPDATE_NO}.tar.gz *  >> ${LOG_FILE} 2>&1
         
        if [ $? -ne 0 ]; then
            error "failed to backup current deviceresources directory ${DEVICERESOURCES_APP_DIR} to ${BACKUP_DIR}/deviceresources.before_update_${UPDATE_NO}.tar.gz . abort"
            STEP_1_RC=1             
        else               
            info  "current deviceresources directory ${DEVICERESOURCES_APP_DIR} backed up to ${BACKUP_DIR}/deviceresources.before_update_${UPDATE_NO}.tar.gz successfully"
            DEVICE_RESOURCES_WAS_BACKEDUP=1
        fi 
    fi
     
    # backup service resources  
    if [[ ${STEP_1_RC} -eq 0 ]]; then
     
        info "backing up current service resources TOMEE instance "
     
        cd ${SERVICERESOURCE_APP_DIR} 2>> ${LOG_FILE}
     
        tar zcvf ${BACKUP_DIR}/serviceresources.before_update_${UPDATE_NO}.tar.gz * >> ${LOG_FILE} 2>&1
         
        if [ $? -ne 0 ]; then
            error "failed to backup current serviceresources directory ${SERVICERESOURCE_APP_DIR} to ${BACKUP_DIR}/serviceresources.before_update_${UPDATE_NO}.tar.gz . abort"
            STEP_1_RC=1             
        else               
            info  "current serviceresources directory ${SERVICERESOURCE_APP_DIR} backed up to ${BACKUP_DIR}/serviceresources.before_update_${UPDATE_NO}.tar.gz successfully"
        fi 
    fi
     
         
     
    ### deploy new TOMEE ###
     
    # check TOMEE tar file md5
    if [[ ${STEP_1_RC} -eq 0 ]]; then
     
        # md5 from installation
        NEW_TOMEE_MD5=$(cat ${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME}.md5) >> ${LOG_FILE} 2>&1
         
        if [[ -z ${NEW_TOMEE_MD5} ]]; then
            error "failed to extract TOMEE archive file md5 from update package. abort"
            STEP_1_RC=1
        else
            info  "md5 for TOMEE archive file is : ${NEW_TOMEE_MD5}"
        fi
         
        # calculate TOMEE archive md5
        if [[ ${STEP_1_RC} -eq 0 ]]; then
         
            CURRENT_TOMEE_FILE_MD5=`md5sum ${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME} | awk '{print $1}'` >> ${LOG_FILE} 2>&1
             
            if [[ -z ${CURRENT_TOMEE_FILE_MD5} ]]; then
                error "failed to calculate TOMEE archive md5. abort"   
            else
                # comapare md5 values
                if [ "${CURRENT_TOMEE_FILE_MD5}" != "${NEW_TOMEE_MD5}" ]; then
                    error "the md5 value of the TOMEE archive does not matche to the expected md5 value. abort"
                    STEP_1_RC=1
                else
                    info  "the md5 value of the TOMEE archive match"
                fi
            fi         
        fi 
    fi
     
     
     
    # copy tar file to backup directory
    if [[ ${STEP_1_RC} -eq 0 ]]; then
        cp -f ${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME} ${BACKUP_DIR} >> ${LOG_FILE} 2>&1
         
        if  [[ $? -eq 0 ]]; then
            info "new TOMEE tar file ${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME} copied to ${BACKUP_DIR} successfully."
        else
            error "failed to copy new TOMEE tar file ${FILES_DIR}/${TOMEE_174_TAR_FILE_NAME} to  ${BACKUP_DIR}. abort"
            STEP_1_RC=1
        fi         
 
         
        # extarct new TOMEE tar file
        if [[ ${STEP_1_RC} -eq 0 ]]; then
             
            cd  ${BACKUP_DIR} >> ${LOG_FILE} 2>&1
             
            if  [[ $? -eq 0 ]]; then
                info "changed directory to ${BACKUP_DIR} successfully."
            else
                error "failed to change directory to ${BACKUP_DIR} . abort"
                STEP_1_RC=1
            fi 
             
            if [[ ${STEP_1_RC} -eq 0 ]]; then
             
                info "extracting new TOMEE tar file ${BACKUP_DIR}/${TOMEE_174_TAR_FILE_NAME} to ${BACKUP_DIR}"
             
                tar xvf ${BACKUP_DIR}/${TOMEE_174_TAR_FILE_NAME} >> ${LOG_FILE} 2>&1
                 
                if  [[ $? -eq 0 ]]; then
                    info "new TOMEE installation ${BACKUP_DIR}/${TOMEE_174_TAR_FILE_NAME} was extracted to ${BACKUP_DIR} successfully."
                else
                    error "failed to extract new TOMEE installation ${current_installation_directory}/${TOMEE_174_TAR_FILE_NAME} to ${BACKUP_DIR}. abort"
                    STEP_1_RC=1
                fi 
            fi
        fi
    fi
         
         
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
