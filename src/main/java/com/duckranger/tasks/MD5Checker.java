package com.duckranger.tasks;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Checker extends Task<MD5Checker>{

	private String md5FileName;
	
	@Override
	public void run() {
		String md5Checksum = null;
		try {
			md5Checksum = new String(Files.readAllBytes(Paths.get(md5FileName)));
			info("md5 for " + description +" file is : " + md5Checksum);
			MessageDigest md = MessageDigest.getInstance("MD5");
			try (InputStream inputStream = Files.newInputStream(Paths.get(fileName));
					DigestInputStream digestStream = new DigestInputStream(inputStream,md)) {
				byte[] buffer = new byte[1024];
				while (digestStream.read(buffer) != -1);
				
				String md5OfFile = String.valueOf(md.digest());
				if (md5OfFile==null || md5OfFile=="") {
					error("failed to calculate "+description+" md5. abort");
					markAsFailed();
				} else if (!md5OfFile.equals(md5Checksum)) {
					error("md5 value of "+description+" does not match expected md5 value. abort");
					markAsFailed();
				} else {
					info("md5 value of "+description+" is a match");
				}
			}
			
		} catch (IOException | NoSuchAlgorithmException e) {
			error("failed to extract " + description + " file md5 from update package. abort");
			markAsFailed();
		}
		
	}

	/**
	 * There is nothing to do to reverse an MD5 Check
	 */
	@Override
	public void reverse() {
		//No OP
	}

	@Override
	public String defaultReport() {
	
		return "MD5 checking task for " + description+" with file " + fileName;
	}
	

	public MD5Checker withMD5Filename(String md5FileName) {
		this.md5FileName = md5FileName;
		return this;
	}
	
}
