package com.hubhead.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;
import java.nio.file.*;

@SuppressWarnings("PMD.DoNotUseThreads")
public class Streamer {
	private final Process process;
	private int errorCount;

	private static final Logger LOGGER = LoggerFactory.getLogger(Streamer.class.getName());

	Streamer(Process p){
		process = p;
		errorCount = 0;
	}

	public void streamStdOut(){
		new Thread(new Runnable() {
			public void run() {
				InputStreamReader reader = new InputStreamReader(process.getInputStream());
				Scanner scan = new Scanner(reader);
				while (scan.hasNextLine()) {
					LOGGER.info(scan.nextLine());
				}
			}
		}).start();
	}

	public void streamStdErr(){
		new Thread(new Runnable() {
			public void run() {
				InputStreamReader reader = new InputStreamReader(process.getErrorStream());
				Scanner scan = new Scanner(reader);
				while (scan.hasNextLine()) {
					LOGGER.error(scan.nextLine());
					errorCount++;
				}
			}
		}).start();
	}

	public int getErrorCount(){
		return errorCount;
	}
}