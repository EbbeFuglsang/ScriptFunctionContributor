package com.thph.scriptfuncioncontributor.impl;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.domain.InstallationAPI;
import com.ur.urcap.api.domain.URCapInfo;
import com.ur.urcap.api.domain.function.Function;
import com.ur.urcap.api.domain.function.FunctionException;
import com.ur.urcap.api.domain.function.FunctionModel;

/**
 * This class was inspired and contains code from following link:
 * https://github.com/BomMadsen/URCap-ScriptWrapper. Responsible for handling
 * the readings of script files.
 * 
 * @author ur
 *
 */
public class ScriptHandler {

	private InstallationAPI api;
	private static final String SYMBOLICNAME = "com.ur.thph.scriptfunctioncontributor";

	public ScriptHandler(InstallationAPI api) {
		this.api = api;
	}

	/**
	 * This method is used to find method name and parameters to add to
	 * functionmodel.
	 * 
	 * @param line
	 */
	private Matcher extractMethodSignature(String line) {

		String pattern = "(?i)def\\s+(\\b.*)\\((\\b.*)\\)";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(line);

		String[] parameters;

		while (m.find()) {
			String name = m.group(1);
			String parameter = m.group(2);

			System.out.println("Name: " + m.group(1));
			System.out.println("Found parameter: (" + m.group(2) + ")");

			if (parameter.contains(",")) {
				parameters = parameter.split(",");

				for (int i = 0; i < parameters.length; i++) {
					String temp = parameters[i];
					String param = temp.replace(" ", "");
					System.out.println("Param: " + param);
					parameters[i] = param;
				}
				
				this.addFunction(name, parameters);

			} else {
				this.addFunction(name, parameter);
			}
		}

		return m;

	}

	public void addFunctionModels(String path) {
		try {

			InputStream stream = getClass().getResourceAsStream("/script/robotics.script");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {

				extractMethodSignature(line);

				stringBuffer.append(line);
				stringBuffer.append("\n");

			}
			bufferedReader.close();
			stream.close();
			

			// System.out.println("Read the file: " + file.getPath());
			// System.out.println("The content was:");
			// System.out.println(stringBuffer.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * This methods reads the specified file at the absolute file path The file is
	 * read line-by-line (for processor load concerns) The complete content is
	 * returned with \n for each line break
	 */
	public String readScriptFile(String path) {

		try {

			InputStream stream = getClass().getResourceAsStream("/script/robotics.script");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {

				stringBuffer.append(line);
				stringBuffer.append("\n");

			}
			bufferedReader.close();
			stream.close();

			// System.out.println("Read the file: " + file.getPath());
			// System.out.println("The content was:");
			// System.out.println(stringBuffer.toString());

			return stringBuffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "# No data read";

	}

	/*
	 * This method finds all script files in the programs folder. Argument needed is
	 * the path for the directory to search in. It returns a File[] (file array) of
	 * all available files.
	 */
	public File[] findScriptFiles(String directory) {

		URL resource;
		File[] files;
		
		ClassLoader classLoader = getClass().getClassLoader();
		
		resource = getClass().getResource(directory);
		
		System.out.println("PATH: " + resource.getPath());

		File dir = new File(classLoader.getResource("fileTest.txt").getFile());

		if (!dir.isDirectory()) {
			System.out.println("NOT a directory!");
		}

		files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".script");
			}
		});
		Arrays.sort(files);

		// Just for debugging
		System.out.println("Number of script files found: " + files.length);
		for (int i = 0; i < files.length; i++) {
			String filename = files[i].getName();
			System.out.println("File " + i + " is " + filename);
		}

		return files;
	}

	private void addFunction(String name, String... argumentNames) {
		FunctionModel functionModel = api.getFunctionModel();
		if (functionModel.getFunction(name) == null) {
			try {
				functionModel.addFunction(name, argumentNames);
			} catch (FunctionException e) {
				// See e.getMessage() for explanation
			}
		}
	}

	private void removeFunction(String name) {
		FunctionModel functionModel = api.getFunctionModel();
		Function f = functionModel.getFunction(name);
		if (f != null) {
			URCapInfo info = f.getProvidingURCapInfo();
			if (info.getSymbolicName().equals(SYMBOLICNAME)) {
				functionModel.removeFunction(f);
			}
		}
	}

}
