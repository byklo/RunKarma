package com.hubhead.support;

import java.io.*;
import java.util.*;

public class OsChecker {
	private final String os = System.getProperty("os.name").toLowerCase();

	public boolean isWindows()
	{
		return os.indexOf("win") >= 0;
	}

	public boolean isUnix()
	{
		return os.indexOf("nix") >=0 || os.indexOf("nux") >=0;
	}
}