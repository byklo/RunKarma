// package com.hubhead.support;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@SuppressWarnings({ "PMD.DoNotUseThreads",
					"PMD.AvoidInstantiatingObjectsInLoops",
					"PMD.CyclomaticComplexity",
					"PMD.NPathComplexity",
					"PMD.ExcessiveMethodLength"
})

public final class RunKarma {

	// private static final Logger LOGGER = LoggerFactory.getLogger(RunKarma.class.getName());

	private RunKarma(){}

	public static void main (String[] args){

		ModuleValidator moduleValidator = new ModuleValidator(args);

		// see if module has karma tests to run
		try{
			moduleValidator.checkModule();
		}catch(FileNotFoundException e){
			return;
		}

		// makes sure arguments are supplied
		try{
			moduleValidator.checkArguments();
		}catch(IllegalArgumentException e){
			return;
		}

		AhModule[] modules = new AhModule[args.length];
		String input = null;
		String moduleDir = null;

		// loops thru args, kicking off module threads
		for(int i=0; i<args.length; i++)
		{
			input = args[i];
			moduleDir = "../" + input + "/src/test/ts/karma/";
			modules[i] = new AhModule(input, moduleDir);
			modules[i].start();
		}

		// wait for compilation to end
		while(true){
			boolean status = true;
			for(AhModule module : modules){
				status = status && module.getStatus();
			}
			if(status){
				break;
			}else{
				try{
					Thread.sleep(2000);
				}catch(InterruptedException e){}
			}
		}

		int compileMsgs = 0;

		// checks for compile errors
		for(AhModule module : modules){
			compileMsgs += module.getErrorMsgsCount();
			if(module.getErrorMsgsCount() >= 1){
				// LOGGER.error(module.getName() + " compiled with errors.");
				System.out.println(module.getName() + " compiled with errors.");
			}
		}

		// exits if there were compile errors
		if(compileMsgs >= 1){
			return;
		}

		// copies karma.config.js and edits it.
		Path copy = Paths.get("../karma.conf.js");
		Path paste = Paths.get("../build/karma/karma.conf.js");

		ConfigGenerator configGen = new ConfigGenerator(copy, paste);

		configGen.create();
		configGen.fix();

		// creates command for karma execution. os variance is accounted for
		CommandGenerator karmaCommand;
		try{
			karmaCommand = new CommandGenerator("karma start");
		}catch(IllegalStateException e){
			// LOGGER.info("Could not identify OS.");
			System.out.println("Could not identify OS.");
			return;
		}

		karmaCommand.addArg("../build/karma/karma.conf.js");
		String executeKarma = karmaCommand.gen();

		// run karma
		try{

			// LOGGER.info("Starting karma.");
			System.out.println("Starting karma.");

			Process p = Runtime.getRuntime().exec(executeKarma);

			Streamer karmaOut = new Streamer(p);
			karmaOut.streamStdOut();

			p.waitFor();

		}catch(IOException | InterruptedException e){
			// LOGGER.info("Failed to run karma: " + e);
			System.out.println("Failed to run karma: " + e);
		}

		// copies test results
		try{
			Path testResults = Paths.get("../build/karma/test-results.xml");

            new File("build/karma").mkdirs();

			Path resultsDestination = Paths.get("build/karma/test-results.xml");

			Files.copy(testResults, resultsDestination, StandardCopyOption.REPLACE_EXISTING);
		}catch(IOException e){
			// LOGGER.error("Could not copy " + args[0] + "'s test-results.xml", e);
			System.out.println("Could not copy " + args[0] + "'s test-results.xml", e);
		}

		// delete build files
		File buildDir = new File("../build/karma");

		String[] fileList = buildDir.list();

		if(fileList != null){
			for(int j=0; j<fileList.length; j++)
			{
				File deleteMe = new File(buildDir, fileList[j]);
				deleteMe.delete();
			}
		}

	}
}