package net.fornwall.eclipsecoder.javasupport;

import java.io.ByteArrayInputStream;

import net.fornwall.eclipsecoder.languages.LanguageSupport;
import net.fornwall.eclipsecoder.stats.CodeGenerator;
import net.fornwall.eclipsecoder.stats.ProblemStatement;
import net.fornwall.eclipsecoder.util.Utilities;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.ui.JavaUI;
import org.osgi.framework.Version;

/**
 * For more information on the JDT java model, see:
 * http://www.eclipsecon.org/2005/presentations/EclipseCON2005_Tutorial29.pdf
 */
public class JavaLanguageSupport extends LanguageSupport {

	/** TopCoder supports java 1.8. */
	private static String eclipseAndJvmSupportedJavaVersion() {
		boolean jvm18Installed = false;
		for (IVMInstallType vm : JavaRuntime.getVMInstallTypes()) {
			for (IVMInstall inst : vm.getVMInstalls()) {
				if (inst instanceof IVMInstall2) {
					String jvmVersion = ((IVMInstall2) inst).getJavaVersion();
					String[] jvmVersionParts = jvmVersion.split("\\.");
					int major = Integer.parseInt(jvmVersionParts[0]);
					int minor = Integer.parseInt(jvmVersionParts[1]);
					if ((major == 1 && minor >= 8) || major >= 2) {
						jvm18Installed = true;
					}
				}
			}
		}

		Version jdtVersion = JavaCore.getJavaCore().getBundle().getVersion();
		boolean jdtSupports18 = jdtVersion.getMajor() >= 4
				|| (jdtVersion.getMajor() == 3 && jdtVersion.getMinor() >= 10)
				|| (jdtVersion.getMajor() == 3 && jdtVersion.getMinor() >= 9 && jdtVersion.getMicro() >= 50);

		return jvm18Installed && jdtSupports18 ? "1.8" : "1.7";
	}

	public static final String DEFAULT_CODE_TEMPLATE = "public class " + CodeGenerator.TAG_CLASSNAME + " {\n\n"
			+ "    public " + CodeGenerator.TAG_RETURNTYPE + " " + CodeGenerator.TAG_METHODNAME + "("
			+ CodeGenerator.TAG_METHODPARAMS + ") {\n" + "        return " + CodeGenerator.TAG_DUMMYRETURN + ";\n"
			+ "    }\n\n" + "}\n";

	@Override
	protected CodeGenerator createCodeGenerator(ProblemStatement problemStatement) {
		return new JavaCodeGenerator(problemStatement);
	}

	@Override
	public IFile createLanguageProject(IProject project) throws CoreException, JavaModelException {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IJavaProject javaProject = JavaCore.create(project);

		IProjectDescription newProjectDescription = workspace.newProjectDescription(project.getName());
		newProjectDescription.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(newProjectDescription, null);

		final String JAVA_VERSION = eclipseAndJvmSupportedJavaVersion();

		IClasspathEntry sourceEntry = JavaCore.newSourceEntry(javaProject.getPath());
		IClasspathEntry conEntry = JavaCore.newContainerEntry(new Path(JavaRuntime.JRE_CONTAINER
				+ "/org.eclipse.jdt.internal.debug.ui.launcher.StandardVMType/JavaSE-" + JAVA_VERSION));
		IClasspathEntry junitEntry = JavaCore.newContainerEntry(new Path("org.eclipse.jdt.junit.JUNIT_CONTAINER/4"));
		javaProject.setRawClasspath(new IClasspathEntry[] { sourceEntry, conEntry, junitEntry }, null);

		javaProject.setOption(JavaCore.COMPILER_COMPLIANCE, JAVA_VERSION);
		javaProject.setOption(JavaCore.COMPILER_SOURCE, JAVA_VERSION);
		javaProject.setOption(JavaCore.COMPILER_CODEGEN_TARGET_PLATFORM, JAVA_VERSION);

		IFile testsFile = project.getFile(getProblemStatement().getSolutionClassName() + "Test.java");
		testsFile.create(new ByteArrayInputStream(getCodeGenerator().getTestsSource().getBytes()), false, null);

		IFile sourceFile = project.getFile(getSolutionFileName());
		sourceFile.create(new ByteArrayInputStream(getInitialSource().getBytes()), true, null);

		IJavaElement javaElement = javaProject.findElement(new Path(getProblemStatement().getSolutionClassName()
				+ "Test.java"));

		if (javaElement != null) {
			// Run initial JUnit test run
			ICompilationUnit compilationUnit = (ICompilationUnit) javaElement;
			Utilities.buildAndRun(project, new JUnitLauncher(compilationUnit));
		}

		return sourceFile;
	}

	@Override
	public String getCodeEditorID() {
		return JavaUI.ID_CU_EDITOR;
	}

	@Override
	public String getCodeTemplate() {
		return JavaSupportPlugin.getInstance().getCodeTemplate();
	}

	@Override
	public String getLanguageName() {
		return LanguageSupport.LANGUAGE_NAME_JAVA;
	}

	@Override
	public String getPerspectiveID() {
		return JavaUI.ID_PERSPECTIVE;
	}

	@Override
	protected String getSolutionFileName() {
		return getProblemStatement().getSolutionClassName() + ".java";
	}

}
