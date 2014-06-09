package com.hubhead.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class ModuleValidator {
	private final String[] moduleArguments;

	ModuleValidator(String[] args){
		moduleArguments = args.clone();
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ConfigGenerator.class.getName());

		// checks for a karma test folder; if not found, skips module
		public void checkModule() throws FileNotFoundException{
			Path moduleKarmaPath = Paths.get("src/test/ts/karma");

			if(!Files.exists(moduleKarmaPath)){
				LOGGER.info("Irrelevant module... Skipping!");
				throw new FileNotFoundException("Karma directory not found.");
			}
		}

		// makes sure arguments are passed.
		public void checkArguments() throws IllegalArgumentException{
			if(moduleArguments.length < 1)
			{
				LOGGER.info("\nPlease specify module(s) to test (as command line arguments).\n");
				LOGGER.info("Modules: ui-main, datamodel, tenant");
				throw new IllegalArgumentException("No arguments supplied.");
			}
		}

}