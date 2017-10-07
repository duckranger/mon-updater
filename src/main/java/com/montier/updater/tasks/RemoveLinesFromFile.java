package com.montier.updater.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class RemoveLinesFromFile extends Task<RemoveLinesFromFile>{

	private int linesToRemove;
	private String searchText;
	private List<String> removed = new ArrayList<>();
	
	@Override
	public void run() {
		String line;
		boolean replacing = false;
		int removeIndex = linesToRemove;
		StringBuffer newFile = new StringBuffer();
		try (BufferedReader input = new BufferedReader(new FileReader(new File(source+"/"+fileName)))) {
		    while ((line = input.readLine()) != null) {
		    	if (replacing) {
		    		removed.add(line);
		    		replacing = (removeIndex-- != 0);
		    		continue;
		    	}
		    	if (line.contains(searchText)) {
		    		replacing = true;
		    	}
		    	newFile.append(line);
		    }
		    
		} catch (IOException e) {
			markAsFailed();
		}
		if (!isFailed()) {
			try (PrintWriter writer = new PrintWriter(source+"/"+fileName)) {
				writer.print(newFile.toString());
			} catch (FileNotFoundException e) {
				markAsFailed();
			}
		}
		
	}

	@Override
	public void rollback() {
		String line;
		StringBuffer newFile = new StringBuffer();
		try (BufferedReader input = new BufferedReader(new FileReader(new File(source+"/"+fileName)))) {
		    while ((line = input.readLine()) != null) {
		    	newFile.append(line);
		    	if (line.contains(searchText)) {
		    		for (String s: removed) {
		    			newFile.append(s);
		    		}
		    	}
		    }
		    
		} catch (IOException e) {
			markRollbackAsFailed();
		}
		if (!isRollbackFailed()) {
			try (PrintWriter writer = new PrintWriter(source+"/"+fileName)) {
				writer.print(newFile.toString());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				error("Failed to rollback the deletion of the lines");
			}
		}
	}

	@Override
	public String defaultReport() {
		return "Deleting " + linesToRemove + " lines after line containing " + searchText;
	}
	
	
	public RemoveLinesFromFile withNumberOfLinesToDelete(int i) {
		linesToRemove = i;
		return this;
	}
	public RemoveLinesFromFile deleteAfterText(String text) {
		searchText = text;
		return this;
	}

}
