package net.fornwall.eclipsecoder.javasupport;

import net.fornwall.eclipsecoder.util.AbstractLauncher;

import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

/**
 * A class for launching JUnit tests specifying a java class.
 */
class JUnitLauncher extends AbstractLauncher {

	// internal constant:
	// org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfiguration.ID_JUNIT_APPLICATION:
	public static final String ID_JUNIT_APPLICATION = "org.eclipse.jdt.junit.launchconfig";

	private static final String JUNIT_TEST_KIND_ATTRIBUTE = "org.eclipse.jdt.junit.TEST_KIND";

	// internal constant
	// org.eclipse.jdt.internal.junit.launcher.TestKindRegistry.JUNIT4_TEST_KIND_ID:
	private static final String JUNIT4_TEST_KIND_ID = "org.eclipse.jdt.junit.loader.junit4";

	private ICompilationUnit unit;

	/**
	 * Create a new launcher for a new java class file (ICompilationUnit).
	 * 
	 * @param unit
	 *            The .java file to run
	 */
	public JUnitLauncher(ICompilationUnit unit) {
		this.unit = unit;
	}

	private String getClassName() {
		String className = null;
		try {
			IType type = this.unit.getTypes()[0];
			className = type.getFullyQualifiedName();
		} catch (JavaModelException e) {
			throw new RuntimeException(e);
		}
		return className;
	}

	@Override
	protected String getLauncherName() {
		return getClassName();
	}

	@Override
	protected String getLauncherTypeId() {
		return ID_JUNIT_APPLICATION;
	}

	@Override
	protected void setUpConfiguration(ILaunchConfigurationWorkingCopy config) throws Exception {
		String projectName = unit.getJavaProject().getElementName();

		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME, projectName);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, getClassName());
		config.setAttribute(JUNIT_TEST_KIND_ATTRIBUTE, JUNIT4_TEST_KIND_ID);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, "-ea");
	}
}
