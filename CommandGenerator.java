package com.hubhead.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;
import java.nio.file.*;

@SuppressWarnings("PMD.AvoidStringBufferField")
public class CommandGenerator {

	private String osPrefix;
	private final String catCommand;
	private final StringBuilder command;

	private final OsChecker osChecker = new OsChecker();

	private static final Logger LOGGER = LoggerFactory.getLogger(CommandGenerator.class.getName());

	// constructor checks os, adds prefix if necessary. adds the basecommand as well
	CommandGenerator(String newCommand) throws IllegalStateException{
		String baseCommand = newCommand;
		command = new StringBuilder();

		if(osChecker.isWindows()){
			osPrefix = "cmd /C ";
		}
		else if(osChecker.isUnix()){
			osPrefix = "";
		}
		else{
			LOGGER.info("Could not identify OS.");
			throw new IllegalStateException("Could not identify OS.");
		}

		command.append(osPrefix);
		command.append(baseCommand);
		command.append(' ');
		catCommand = command.toString();
	}

	public void addArg(String arg){
		command.append(arg);
	}

	public void reset(){
		command.delete(catCommand.length(), command.length());
	}

	public String gen(){
		return command.toString();
	}

}