package com.montier.updater;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.montier.updater.tasks.ExtractTar;
import com.montier.updater.tasks.Task;
import com.montier.updater.tasks.TaskFactory;

/**
 * This class uses the tasks mechanism to create an update script.
 * 
 * The logic:
 * 
 * Each <code>Task</code> is a step in the update script. E.g. - tar backup, copy directories,
 * MD5 check, replace variable in file etc.
 * 
 * A <code>Task</code> contains the logic for its execution and also for its rollback. 
 * 
 * The <code>Tasks</code> that make up the script are collected into an order LinkedList. Once all
 * the steps are configured, the <code>perform</code> method runs forward through the task-list and
 * calls <code>execute</code> on each of them.
 * If a task fails, <code>perform</code> runs backwards through the list, starting at the failed task - 
 * and calls <code>rollback</code> on each task.
 * 	 
 * @author nimo
 *
 */
public class Script {

	// The list of tasks to be performed in this script
	private List<Task<?>> steps;
	
	
	private Logger log = Logger.getLogger(Script.class.getName());

	
	// Create the Script runner.
	// Provide the details
	public Script() {
		steps = new LinkedList<>();
	}
	
	// Adds a task to the end of list of steps to perform.
	// Returns self for method chaining.
	public Script addStep(Task task) {
		steps.add(task);
		return this;
	}
	
	// Runs the steps one by one.
	public void run() {
		boolean rollbackRequired = false;
		for (Task<?> task : steps) {
			task.run();
			// Do not perform anymore tasks if this one failed.
			// Mark the entire script for rollback
			if (task.isFailed()) {
				log.log(Level.SEVERE,"Task failed: "+task.defaultReport());
				rollbackRequired=true;
				break;
			}
		}
		
		// Check whether the script needs to be rolled back.
		// If so - run backwards through the list and rollback each task in order
		if (rollbackRequired) {
			
			log.log(Level.INFO,"Starting rollback" );
			// A flag to mark that we found the failed task, so now the process of rolling back started.
			// Do not roll back tasks that are 'after' the failed task. Only those 'before'.
			// Since the loop below iterates on the linkedlist backwards - this flag is used to signal
			// that the rollback is in progress.
			boolean startedRollback = false;	 
									
			for (ListIterator<Task<?>> iter = steps.listIterator(steps.size());  iter.hasPrevious(); ) {
				Task<?> task = iter.previous();
				
				// Found the failed step? start rollback
				if (task.isFailed()) {
					startedRollback = true;
				}
				
				// Has rollback started? rollback the task
				if (startedRollback) {
					task.rollback();
				}
				
				// Has rollback of task failed? Stop rolling back.
				if (task.isRollbackFailed()) {
					log.log(Level.SEVERE, "Rollback failed at step "+ task.defaultReport());
					log.log(Level.SEVERE, "Stopping rollback");
					break;
				}
					
			}
		}
	}
	
}
