package org.iteam.mina.utils;

public class EUtils {
	public static String getExceptionStack(Throwable e) {
		// e.printStackTrace();
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		StringBuffer result = new StringBuffer(e.toString() + "\n");
		for (int index = stackTraceElements.length - 1; index >= 0; --index) {
			result.append("at [" + stackTraceElements[index].getClassName());
			result.append(",\t");
			result.append(stackTraceElements[index].getFileName());
			result.append(",\t");
			result.append(stackTraceElements[index].getMethodName());
			result.append(",\t");
			result.append(stackTraceElements[index].getLineNumber());
			result.append(",\t");
			result.append(stackTraceElements[index].getFileName() + "]\n");
		}
		return result.toString();
	}
}
