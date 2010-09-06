package net.fornwall.eclipsecoder.javasupport;

import net.fornwall.eclipsecoder.stats.CodeGenerator;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/**
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = JavaSupportPlugin.getInstance()
				.getPreferenceStore();

		store.setDefault(JavaSupportPlugin.GENERATE_JUNIT_TIMEOUT_PREFERENCE,
				true);

		store.setDefault(JavaSupportPlugin.CODE_TEMPLATE_PREFERENCE,
				"public class " + CodeGenerator.TAG_CLASSNAME + " {\n\n"
						+ "\tpublic " + CodeGenerator.TAG_RETURNTYPE + " "
						+ CodeGenerator.TAG_METHODNAME + "("
						+ CodeGenerator.TAG_METHODPARAMS + ") {\n"
						+ "\t\treturn " + CodeGenerator.TAG_DUMMYRETURN
						+ ";\n" + "\t}\n\n" + "}\n");
	}

}
