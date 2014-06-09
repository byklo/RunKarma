// package com.hubhead.support;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;

@SuppressWarnings({"PMD.DoNotUseThreads", "PMD.AvoidInstantiatingObjectsInLoops"})
public class AhModule implements Runnable {
	private Thread t;
	private final String threadName;
	private final String directory;
	private boolean status;
	private int compileErrorMsgs;

	// private static final Logger LOGGER = LoggerFactory.getLogger(AhModule.class.getName());

	AhModule(String name, String dir){
		threadName = name;
		directory = dir;
		compileErrorMsgs = 0;
	}

	public String getName(){
		return threadName;
	}

	public boolean getStatus(){
		return status;
	}

	public int getErrorMsgsCount(){
		return compileErrorMsgs;
	}

	public void run(){
		try{

			// Get the typescript files
			File searchDirectory = new File(directory);
			File[] searchResults = searchDirectory.listFiles();

			ArrayList<Process> processList = new ArrayList<Process>();
			ArrayList<Streamer> tscOutputs = new ArrayList<Streamer>();

			// handles command building
			CommandGenerator tsc = new CommandGenerator("tsc --out");

			// Streamer tscOutput;

			for(File d : searchResults)
			{
				if(d.isDirectory()){
					File[] filesInFolder = d.listFiles();
					for(File f : filesInFolder)
					{
						String outputName = "../AHCloud/Src/build/karma/" + f.getName().substring(0, f.getName().length()-3) + ".js";
						String arguments = outputName + " " + f.getAbsolutePath();

						tsc.addArg(arguments);

						String executeTsc = tsc.gen();

						Process p = Runtime.getRuntime().exec(executeTsc);
						processList.add(p);

						tsc.reset();

						Streamer tscOutput = new Streamer(p);
						tscOutput.streamStdErr();

						tscOutputs.add(tscOutput);
					}
				}
			}

			// Waits for processes to finish
			for(Process process : processList){
				process.waitFor();
			}

			// LOGGER.info("Compiled Typescript: " + threadName);
			System.out.println("Compiled Typescript: " + threadName);

			for(Streamer stream : tscOutputs){
				compileErrorMsgs += stream.getErrorCount();
			}

			status = true;

		}catch(InterruptedException | IOException e){
			// LOGGER.info("Failed to compile: " + threadName + " -> " + e);
			System.out.println("Failed to compile: " + threadName + " -> " + e);
		}
	}

	public void start(){
		if(t == null)
		{
			t = new Thread (this, threadName);
			t.start();
		}
	}
}