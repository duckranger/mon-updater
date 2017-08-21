package com.duckranger.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReplaceInFile extends Task<ReplaceInFile>{

	private String originalText;
	private String replacementText;
	
	
	@Override
	public void run() {
		String line;
		try (BufferedReader input = new BufferedReader(new FileReader(new File(source+"/"+fileName)))) {
		    while ((line = input.readLine()) != null) {
		         line = line.replace(originalText,replacementText);
		    }
		} catch (IOException e) {
			markAsFailed();
		}
	}

	@Override
	public void reverse() {
		String line;
		try (BufferedReader input = new BufferedReader(new FileReader(new File(source+"/"+fileName)))) {
		    while ((line = input.readLine()) != null) {
		         line = line.replace(replacementText,originalText);
		    }
		} catch (IOException e) {
			markRollbackAsFailed();
		}
		
	}

	@Override
	public String defaultReport() {
		return "Replacing all occurences of " + originalText+" with " +replacementText +" in file "+source+"/"+fileName;
	}
	
	public ReplaceInFile replaceAll(String text) {
		originalText = text;
		return this;
	}
	
	public ReplaceInFile withNewText(String text) {
		replacementText= text;
		return this;
	}
	

}
