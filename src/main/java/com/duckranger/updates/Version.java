package com.duckranger.updates;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Version implements Serializable{

	
	/**
	 * Construct a version object by joining all the int parts together with separating dots.
	 * The method only takes Integers in order to use the compiler to prevent using any non-numbers
	 * as version parts.
	 * There is no limit on the number of parts you can use.
	 * @param parts
	 */
	public static String version(Integer... parts) {
		return String.join(".",	Arrays.stream(parts).map(x->x.toString()).collect(Collectors.toList()));  
		
	}
	
}
