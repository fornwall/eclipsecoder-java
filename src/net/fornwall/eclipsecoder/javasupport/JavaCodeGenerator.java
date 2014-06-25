package net.fornwall.eclipsecoder.javasupport;

import java.lang.reflect.Array;

import net.fornwall.eclipsecoder.stats.CodeGenerator;
import net.fornwall.eclipsecoder.stats.ProblemStatement;

public class JavaCodeGenerator extends CodeGenerator {

	private final static String COMPARE_DOUBLE_ARRAYS = "    public static void assertArrayEquals(double[] expected, double[] actual) {\n"
			+ "        boolean failed = (actual == null || (expected.length != actual.length));\n"
			+ "        for (int i = 0; i < expected.length && !failed; i++) {\n"
			+ "            if (Double.isNaN(expected[i]) && !Double.isNaN(actual[i])) {\n"
			+ "                failed = true;\n"
			+ "            } else {\n"
			+ "                double delta = Math.max(1e-9, 1e-9 * Math.abs(expected[i]));\n"
			+ "                failed = (Math.abs(actual[i] - expected[i]) > delta);\n"
			+ "            }\n"
			+ "        }\n"
			+ "        if (failed) {\n"
			+ "            Assert.fail(\"expected:<\" + Arrays.toString(expected) + \">, but was:<\" + Arrays.toString(actual) + \">\");\n"
			+ "        }\n" + "    }\n\n";

	private final static String COMPARE_DOUBLES = "    public static void assertEquals(double expected, double actual) {\n"
			+ "        if (Double.isNaN(expected)) {\n"
			+ "            Assert.assertTrue(\"expected: <NaN> but was: <\" + actual + \">\", Double.isNaN(actual));\n"
			+ "            return;\n"
			+ "        }\n"
			+ "        double delta = Math.max(1e-9, 1e-9 * Math.abs(expected));\n"
			+ "        Assert.assertEquals(expected, actual, delta);\n" + "    }\n\n";

	public static final String DEFAULT_CODE_TEMPLATE = "import java.util.*;\n\npublic class "
			+ CodeGenerator.TAG_CLASSNAME + " {\n\n" + "    public " + CodeGenerator.TAG_RETURNTYPE + " "
			+ CodeGenerator.TAG_METHODNAME + "(" + CodeGenerator.TAG_METHODPARAMS + ") {\n" + "        return "
			+ CodeGenerator.TAG_DUMMYRETURN + ";\n" + "    }\n\n" + "}\n";

	public JavaCodeGenerator(ProblemStatement problemStatement) {
		super(problemStatement);
	}

	private String getCreateStatement(Object value, String variableName) {
		Class<?> type = value.getClass();
		if (type.isArray()) {
			StringBuilder builder = new StringBuilder(getTypeString(type) + " " + variableName + " = new "
					+ getTypeString(type) + "{");
			for (int i = 0; i < Array.getLength(value); i++) {
				if (i != 0) {
					builder.append(", ");
				}
				builder.append(getSimpleCreateStatement(Array.get(value, i)));
			}
			builder.append("};\n");
			return builder.toString();
		}

		return getTypeString(type) + " " + variableName + " = " + getSimpleCreateStatement(value) + ";\n";
	}

	@Override
	public String getDummyReturnString() {
		if (problemStatement.getReturnType() == Integer.class || problemStatement.getReturnType() == Long.class
				|| problemStatement.getReturnType() == Character.class) {
			return "0";
		} else if (problemStatement.getReturnType() == Double.class) {
			return "0.0";
		} else {
			return "null";
		}
	}

	/** One-dimensional */
	private static String getSimpleCreateStatement(Object value) {
		if (value.getClass() == Character.class) {
			char c = (Character) value;
			String result;
			switch (c) {
			case '\'':
				result = "\\'";
				break;
			case '\\':
				result = "\\\\";
				break;
			default:
				result = Character.toString(c);
			}
			return "'" + result + "'";
		} else if (value.getClass() == String.class) {
			return '"' + ((String) value) + '"';
		} else {
			return value.toString() + ((value instanceof Long) ? "L" : "");
		}
	}

	@Override
	public String getTestsSource() {
		boolean multiDimensional = problemStatement.getReturnType().isArray();
		boolean doubleReturn = (multiDimensional ? problemStatement.getReturnType().getComponentType()
				: problemStatement.getReturnType()) == Double.class;

		StringBuilder result = new StringBuilder();
		result.append("import org.junit.Assert;\nimport org.junit.Before;\nimport org.junit.Test;\n");
		if (multiDimensional && doubleReturn) {
			result.append("import java.util.Arrays;\n");
		}
		result.append("\npublic class " + problemStatement.getSolutionClassName() + "Test {\n\n" + "    protected "
				+ problemStatement.getSolutionClassName() + " solution;\n\n" + "    @Before\n"
				+ "    public void setUp() {\n" + "        solution = new " + problemStatement.getSolutionClassName()
				+ "();\n" + "    }\n\n");

		if (doubleReturn) {
			result.append(multiDimensional ? COMPARE_DOUBLE_ARRAYS : COMPARE_DOUBLES);
		}

		int i = -1;
		for (ProblemStatement.TestCase testCase : problemStatement.getTestCases()) {
			i++;
			result.append("    @Test");
			if (JavaSupportPlugin.getInstance().isGenerateJUnitTimeout()) {
				result.append("(timeout = 2000)");
			}
			result.append("\n");
			result.append("    public void testCase" + i + "() {\n");

			for (int param = 0; param < problemStatement.getParameterNames().size(); param++) {
				result.append("        "
						+ getCreateStatement(testCase.parameters[param], problemStatement.getParameterNames()
								.get(param)));
			}

			result.append("\n        " + getCreateStatement(testCase.getReturnValue(), "expected"));

			result.append("        " + getTypeString(problemStatement.getReturnType()) + " actual = solution."
					+ problemStatement.getSolutionMethodName() + "(");
			for (int j = 0; j < problemStatement.getParameterNames().size(); j++) {
				result.append(problemStatement.getParameterNames().get(j));
				if (j != problemStatement.getParameterNames().size() - 1) {
					result.append(", ");
				}
			}
			result.append(");\n");

			result.append("\n        " + (doubleReturn ? "" : "Assert.") + "assert" + (multiDimensional ? "Array" : "")
					+ "Equals(expected, actual);\n    }\n\n");
		}

		result.append("}\n");
		return result.toString();
	}

	@Override
	public String getTypeString(Class<?> type) {
		if (type == Integer.class) {
			return "int";
		} else if (type == Character.class) {
			return "char";
		} else if (type == Double.class) {
			return "double";
		} else if (type == Long.class) {
			return "long";
		} else if (type.isArray()) {
			return getTypeString(type.getComponentType()) + "[]";
		} else {
			// String
			return type.getSimpleName();
		}
	}
	
	@Override
	public String getModuloString() {
		String modulo = problemStatement.getModulo();
		if(modulo != null && modulo.length() > 0) {
			return "public static final int MOD = " + modulo;
		}
		return super.getModuloString();
	}

}
