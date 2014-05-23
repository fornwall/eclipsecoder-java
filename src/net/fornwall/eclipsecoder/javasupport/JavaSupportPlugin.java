package net.fornwall.eclipsecoder.javasupport;

import org.eclipse.ui.plugin.AbstractUIPlugin;

public class JavaSupportPlugin extends AbstractUIPlugin {

	public static final String CODE_TEMPLATE_PREFERENCE = "codeTemplatePreference";

	public static final String GENERATE_JUNIT_TIMEOUT_PREFERENCE = "generateJUnitTimeoutPreference";

	private static JavaSupportPlugin instance;

	public static JavaSupportPlugin getInstance() {
		return instance;
	}

	public JavaSupportPlugin() {
		JavaSupportPlugin.instance = this;
	}

	public String getCodeTemplate() {
		return getPreferenceStore().getString(CODE_TEMPLATE_PREFERENCE);
	}

	public boolean isGenerateJUnitTimeout() {
		return getPreferenceStore().getBoolean(GENERATE_JUNIT_TIMEOUT_PREFERENCE);
	}

}
