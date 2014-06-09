// package com.hubhead.support;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.*;
import java.nio.file.*;

public class ConfigGenerator {
	private final Path copyPath;
	private final Path pastePath;
	// private static final Logger LOGGER = LoggerFactory.getLogger(ConfigGenerator.class.getName());

	ConfigGenerator(Path copy, Path paste)
	{
		copyPath = copy;
		pastePath = paste;
	}

	public void create(){
		try{
			Files.copy(copyPath, pastePath, StandardCopyOption.REPLACE_EXISTING);
			// LOGGER.info("Created build/karma/karma.conf.js");
			System.out.println("Created build/karma/karma.conf.js");
		}catch(IOException e){
			// LOGGER.info("Failed to create build/karma/karma.conf.js");
			System.out.println("Failed to create build/karma/karma.conf.js");
			// System.exit(0);
			return;
		}
	}

	@SuppressWarnings("PMD.AssignmentInOperand")
	public void fix(){
		try{
			File configFile = pastePath.toFile();
			BufferedReader reader = new BufferedReader(new FileReader(configFile));
			String line = "";
			StringBuilder oldText = new StringBuilder("");

			while((line = reader.readLine()) != null)
			{
				oldText.append(line);
				oldText.append("\r\n");
			}
			reader.close();

			String newText = oldText.toString();
			newText = newText.replaceAll("// browser", "browser");
			newText = newText.replaceAll("'./", "'../../");
			newText = newText.replaceAll("'../../\\*\\*/src/test/js/karma/\\*\\*/\\*.js'", "'./*Test.js'");

			FileWriter writer = new FileWriter(pastePath.toFile());

			writer.write(newText);
			writer.close();

			// LOGGER.info("Fixed build/karma/karma.conf.js");
			System.out.println("Fixed build/karma/karma.conf.js");
		}
		catch(IOException e)
		{
			// LOGGER.info("Failed to fix build/karma/karma.conf.js");
			System.out.println("Failed to fix build/karma/karma.conf.js");
			// System.exit(0);
			return;
		}
	}

}