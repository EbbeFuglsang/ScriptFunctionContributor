package com.thph.scriptfuncioncontributor.impl;

import java.io.File;
import com.ur.urcap.api.contribution.InstallationNodeContribution;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.domain.InstallationAPI;
import com.ur.urcap.api.domain.data.DataModel;
import com.ur.urcap.api.domain.script.ScriptWriter;

public class ScriptInstallationNodeContribution implements InstallationNodeContribution {

	private static final String SCRIPT_FILE_PATH = "/script/robotics.script";
	
	private ScriptHandler handler;


	private InstallationAPI api;

	public ScriptInstallationNodeContribution(InstallationAPIProvider apiProvider, ScriptInstallationNodeView view,
			DataModel model, CreationContext context) {

		this.api = apiProvider.getInstallationAPI(); 
		 
		this.handler = new ScriptHandler(this.api);
		
		handler.addFunctionModels(SCRIPT_FILE_PATH);
		

	}

	@Override
	public void openView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void closeView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void generateScript(ScriptWriter writer) {
		
		String fileText = handler.readScriptFile(SCRIPT_FILE_PATH);
		
		writer.appendRaw(fileText);

	}


}