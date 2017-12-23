package io.hexaforce.generator;

import java.io.File;

public class CommandLineInterface {
	public static void main(String[] args) {
		File classpathRoot = new File(System.getProperty("user.dir"));
		System.out.println("Hello World!:" + classpathRoot.getAbsolutePath());
	}
}
