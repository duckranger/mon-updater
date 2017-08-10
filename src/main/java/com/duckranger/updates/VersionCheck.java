package com.duckranger.updates;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class VersionCheck {
	
	//The regex allows for any number of groups of digits, separated by dots. See http://regexr.com/3gg7m 
	private static final String VERSION_CHECK_REGEX = "(\\d+(\\.\\d+)+)";
	private static final Pattern CHECK_PATTERN = Pattern.compile(VERSION_CHECK_REGEX);
	private List<String> sourceVersions;
	private List<String> targetVersions;
	
	public VersionCheck() {
		sourceVersions = new LinkedList<>();
		targetVersions = new LinkedList<>();
	}
	
	
	/**
	 * Add a list of source versions.
	 * @param versions - a comma separated list of Strings containing version numbers for sources.
	 * 
	 * @return
	 */
	public VersionCheck withSourceVersions(String...versions) {
		Arrays.stream(versions).forEach(S -> sourceVersions.add(verifyVersionNumber(S)));
		return this;
	}
	
	public VersionCheck withTargetVersions(String...versions) {
		Arrays.stream(versions).forEach(S -> targetVersions.add(verifyVersionNumber(S)));
		return this;
	}
	
	/**
	 * Verify the version number is made of a set of numbers (digits only) separated by dots.
	 * There is no limit on the number parts, so all this is valid:
	 * 1.1
	 * 1.2.3.4.5
	 * 0.0.2.1.0.2.223232 
	 * etc
	 * 
	 * @param versionNumber the version number to check
	 * @return the version number itself if it is valid
	 * @throws IllegalArgumentException when the version number is null or invalid
	 */
	private String verifyVersionNumber(String versionNumber) {
		if (versionNumber==null || !CHECK_PATTERN.matcher(versionNumber).find()) 
			throw new IllegalArgumentException("Bad version number format: " + versionNumber);
		return versionNumber;
	}
}
