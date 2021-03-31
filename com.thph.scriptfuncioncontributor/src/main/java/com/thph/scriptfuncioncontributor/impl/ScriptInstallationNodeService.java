package com.thph.scriptfuncioncontributor.impl;

import java.util.Locale;

import com.ur.urcap.api.contribution.ViewAPIProvider;
import com.ur.urcap.api.contribution.installation.ContributionConfiguration;
import com.ur.urcap.api.contribution.installation.CreationContext;
import com.ur.urcap.api.contribution.installation.InstallationAPIProvider;
import com.ur.urcap.api.contribution.installation.swing.SwingInstallationNodeService;
import com.ur.urcap.api.domain.data.DataModel;

public class ScriptInstallationNodeService
		implements SwingInstallationNodeService<ScriptInstallationNodeContribution, ScriptInstallationNodeView> {

	public ScriptInstallationNodeService() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void configureContribution(ContributionConfiguration configuration) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitle(Locale locale) {
		// TODO Auto-generated method stub
		return "Script Function Contributor";
	}

	@Override
	public ScriptInstallationNodeView createView(ViewAPIProvider apiProvider) {
		// TODO Auto-generated method stub
		return new ScriptInstallationNodeView(apiProvider);
	}

	@Override
	public ScriptInstallationNodeContribution createInstallationNode(InstallationAPIProvider apiProvider,
			ScriptInstallationNodeView view, DataModel model, CreationContext context) {
		// TODO Auto-generated method stub
		return new ScriptInstallationNodeContribution(apiProvider, view, model, context);
	}

}