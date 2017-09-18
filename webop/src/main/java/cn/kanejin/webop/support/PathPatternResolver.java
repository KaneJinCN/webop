package cn.kanejin.webop.support;

import org.apache.tools.ant.DirectoryScanner;

import java.io.File;
import java.io.IOException;

public class PathPatternResolver {

	public static String[] resolve(String basePath, String pattern) throws IOException {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[]{ pattern });
		scanner.setBasedir(basePath);
		scanner.setCaseSensitive(true);
		scanner.scan();
		String[] fileNames = scanner.getIncludedFiles();
		
		String[] files = new String[fileNames.length];
		
		for (int i = 0; i < fileNames.length; i++)  {
			files[i] = basePath + File.separatorChar + fileNames[i];
		}
		
		return files;
	}
	
	public static void main(String[] args) throws IOException {
		String[] r1 = resolve("/Users/Kane/Desktop", "**/*.java");
		if (r1 != null) {
			for (String r : r1)
				System.out.println(r);
			System.out.println("Matched: " + r1.length);
		}
		
	}
}
