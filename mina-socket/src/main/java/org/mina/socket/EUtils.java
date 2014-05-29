package org.mina.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

public class EUtils {
	public static String getExceptionStack(Throwable e) {
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(out));
			// // e.printStackTrace();
			// StackTraceElement[] stackTraceElements = e.getStackTrace();
			// StringBuffer result = new StringBuffer(e.toString() + "\n");
			// for (int index = stackTraceElements.length - 1; index >= 0;
			// --index)
			// {
			// result.append("at [" + stackTraceElements[index].getClassName());
			// result.append(",\t");
			// result.append(stackTraceElements[index].getFileName());
			// result.append(",\t");
			// result.append(stackTraceElements[index].getMethodName());
			// result.append(",\t");
			// result.append(stackTraceElements[index].getLineNumber());
			// result.append(",\t");
			// result.append(stackTraceElements[index].getFileName() + "]\n");
			// }
			// return result.toString();
			StringBuffer result = new StringBuffer(out.toString());
			return result.toString();
		} catch (Exception e1) {
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e1) {
				}
			}
		}
		return e.toString();
	}
}
