package com.winbook.exceptions;
/*
 * Generic exception class used to inform the error occuring at the DAO layer. The message should be meaningful enough to help during debugging.
 */
public class MissingOrIncorrectDataException extends Exception {

	public MissingOrIncorrectDataException(String message) {
			super(message);
	
	}
	
}
