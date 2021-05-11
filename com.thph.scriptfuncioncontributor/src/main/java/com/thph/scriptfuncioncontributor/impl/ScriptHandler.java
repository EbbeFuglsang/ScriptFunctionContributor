package com.thph.scriptfuncioncontributor.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ur.urcap.api.domain.InstallationAPI;
import com.ur.urcap.api.domain.URCapInfo;
import com.ur.urcap.api.domain.function.Function;
import com.ur.urcap.api.domain.function.FunctionException;
import com.ur.urcap.api.domain.function.FunctionModel;
import com.ur.urcap.api.domain.script.ScriptWriter;

/**
 * This class handles reading script files located in resource folder of the
 * project. It also insert the recognized function in the script file into the
 * functionmodel, which is accessible through polyscrope interface.
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
	 * This method  straps the a parameter default 
	 * assignment from the string 
	 * @param the parameter String
	 */
	private String removeDefaultAssignment(String parameter) {
		if(parameter.contains("=")) {
			parameter = parameter.substring(0,parameter.indexOf("="));
		}
		return parameter;
	}

	/**
	 * This method  straps the lists/poses from script arguments 
	 * Sensitive construction if the script file have issues
	 * @param the parameters String
	 */
	private String removeLists(String parameters) {
		while ( parameters.contains("[")) {
			parameters = parameters.substring(0,parameters.indexOf("[")) + parameters.substring(parameters.indexOf("]")+1);
		}
		return parameters;
	}
	
	/**
	 * This method  extracts the method name and parameters in each
	 * line read from the script file and add to it to the
	 * function model.
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

			parameter = removeLists(parameter); 

			if (parameter.contains(",")) {
				parameters = parameter.split(",");
			}
			else {
				parameters = new String[1];
				parameters[0]=parameter;
			}

			for (int i = 0; i < parameters.length; i++) {
				String temp = parameters[i];
				String param = temp.replace(" ", "");
				param = removeDefaultAssignment(param);
				System.out.println("Param: " + param);
				parameters[i] = param;
			}
			this.addFunction(name, parameters);

		}

		return m;

	}

	/**
	 * This methods reads the specified file at the absolute file path The file is
	 * read line-by-line (for processor load concerns) The complete content is
	 * returned with \n for each line break
	 * @param path
	 */
	public void addFunctionModels(String path) {
		try {

			InputStream stream = getClass().getResourceAsStream(path);
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

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This methods reads the specified file at the absolute file path The file is
	 * read line-by-line (for processor load concerns) The complete content is
	 * returned with \n for each line break
	 * @param path
	 * @return a string of the read script file.
	 */
	public String readScriptFile(String path, ScriptWriter writer) {

		try {

			InputStream stream = getClass().getResourceAsStream(path);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {

				writer.appendLine(line);
				stringBuffer.append(line);
				stringBuffer.append("\n");

			}
			bufferedReader.close();
			stream.close();

			return stringBuffer.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return "# No data read";

	}


	/**
	 * This method finds all script files in the programs folder. Argument needed is
	 * the path for the directory to search in. 
	 * all available files.
	 * @param directory
	 * @return It returns a String array of file directory.
	 */
	public ArrayList<String> findScriptFiles(String directory) {

		ArrayList<String> filepaths = new ArrayList<String>();

		InputStream stream = getClass().getResourceAsStream(directory);

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

		try {

			String line;
			while ((line = bufferedReader.readLine()) != null) {

				if (line.endsWith(".script")) {

					filepaths.add(directory + line);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return filepaths;
	}

	private void addFunction(String name, String... argumentNames) {
		FunctionModel functionModel = api.getFunctionModel();
		if (functionModel.getFunction(name) == null) {
			try {
				functionModel.addFunction(name, argumentNames);
			} catch (FunctionException e) {
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
	
	public static void main(String[] args){	
		new ScriptHandler(null).extractMethodSignature("def test(pose=p[0,0,0,0,0,0], register  =   [23,34], ttt)");
		
	}	

}
