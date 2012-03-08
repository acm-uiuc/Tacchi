package tacchi.particlegame;

import java.io.File;
import java.util.*;
import java.util.regex.*;

public final class Utils {
	
	/**
	 * Concatenates two string arrays
	 * @param s1
	 * @param s2
	 * @return
	 */
	public static String[] arrayConcat(String[] s1, String[] s2) {
		String[] concat = new String[s1.length + s2.length];
		for (int i = 0; i < s1.length; i++)
			concat[i] = s1[i];
		for (int i = 0; i < s2.length; i++)
			concat[i + s1.length] = s2[i];
		
		return concat;
	}
	
	/**
	 * Removes all empty elements from and array of strings
	 * @param arr
	 * @return
	 */
	public static String[] removeEmpty(String[] arr) {
		ArrayList<String> ret = new ArrayList<String>();
		for (String s : arr) {
			if (!s.isEmpty())
				ret.add(s);
		}
		return (String[]) ret.toArray(new String[ret.size()]);
	}
	
	public static String join(Object[] arr) {
		return join(arr, "");
	}
	
	/**
	 * Joins the elements of an array as a string, placing "sep" between each item.
	 * @param arr
	 * @param sep
	 * @return
	 */
	public static String join(Object[] arr, String sep) {
		if (arr.length == 0)
			return "";
		
		StringBuilder s = new StringBuilder();
		for (int i = 0; i < arr.length - 1; i++) 
			s.append(arr[i].toString()).append(sep);
		s.append(arr[arr.length - 1]);
		
		return s.toString();
	}
	
	/**
	 * Parses a line of a text file and returns an info object describing it.
	 * Syntax is: <br>
	 * <code>symbol: arg1, arg2, arg3...</code>
	 * @param line the string to parse
	 * @return
	 */
	public static ParseInfo parseStageLine(String line) {
		//strip comments
		int comment = line.indexOf(";");
		if (comment >= 0)
			line = line.substring(0, comment);
		
		//Get identifier and arguments.  Skip if line has no ":"
		int sepIndex = line.indexOf(":");
		if (sepIndex == -1)
			return null;
		
		ParseInfo info = new ParseInfo();
		
		//Get whether line is tabbed or not
		info.tabbed = line.startsWith("\t") || line.startsWith("  ");
		
		info.symbol = line.substring(0, sepIndex).trim().toLowerCase();
		info.arglist = line.substring(sepIndex + 1).trim();
		info.args = info.arglist.split(",");
		for (int i = 0; i < info.args.length; i++)
			info.args[i] = info.args[i].trim();

		info.args = removeEmpty(info.args);
		return info;
	}

	public static class ParseInfo {
		public String symbol = "";
		public boolean tabbed = false;
		public String arglist = "";
		public String[] args = new String[0];
	}

	public static final class Path {
	
		/**
		 * Returns "path" added to "base" with all "." and ".." entries evaluated.
		 * If "path" is an absolute path, "path" is returned.  All backslashes will
		 * be converted to forward slashes.
		 * @param base 
		 * @param path
		 * @return
		 */
		public static String combine(String base, String path) {
			base = base.trim().replace('\\', '/');
			path = path.trim().replace('\\', '/');
			
			//If path is absolute, ignore base
			if (path.substring(0, 1).equals("/") || path.substring(1,3).equals(":/"))
				return path;
			
			String[] baseParts = base.split("/");
			String[] pathParts = path.split("/");
			
			int remove = 0;
			int start = 0;
			for (int i = 0; i < pathParts.length; i++) {
				if (pathParts[i].equals(".")) 
					continue;
				else if (pathParts[i].equals("..")) {
					remove++;
				}
				else {
					start = i;
					break;
				}
			}
			
			int len = baseParts.length - remove + pathParts.length - start;
			String[] newParts = new String[len];
			int i = 0;
			for (int j = 0; j < baseParts.length - remove; j++) 
				newParts[i++] = baseParts[j];
			for (int j = start; j < pathParts.length; j++) 
				newParts[i++] = pathParts[j];
			
			return Utils.join(newParts, "/");
		}
	
		/**
		 * Replaces all variables in {curly braces} with the replacements given in vars
		 * @param path the path with variables
		 * @param vars a hashmap where each key is a variable name and each value is its replacement
		 * @return
		 */
		public static String replace(String path, HashMap<String, String> vars) {
			Pattern p = Pattern.compile("\\{(.*?)\\}");
			Matcher m = p.matcher(path);
			String replaced = path;
			int matchStart = 0;
			while (m.find(matchStart)) {
				int start = m.start();
				int end = m.end();
				matchStart = end;
				String var = vars.get(m.group(1));
				if (var != null) {
					replaced = replaced.substring(0, start) + var + replaced.substring(end);
					matchStart = start + var.length();
				}
				m = p.matcher(replaced);
			}
			return replaced;
		}
		
		public static String getDirectory(File file) {
			return file.getParentFile().getAbsolutePath();
		}
	}
}
