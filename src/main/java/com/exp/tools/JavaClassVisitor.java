package com.exp.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import static java.nio.file.FileVisitResult.*;

public class JavaClassVisitor extends SimpleFileVisitor<Path> {
	public static final String ROOT = "E:\\playground\\jacoco\\classes";
	private Instrumentor instrumentor;

	// Print information about
	// each type of file.
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) {
		if (attr.isSymbolicLink()) {
			System.out.format("Symbolic link: %s ", file);
		} else if (attr.isRegularFile()) {
			instrumentFile(file);
		} else {
			System.out.format("Other: %s ", file);
		}
		System.out.println("(" + attr.size() + "bytes)");
		return CONTINUE;
	}

	private void instrumentFile(Path file) {
		try {
			InputStream istream = readClassFile(file);
			String newFolder = createNewFolderName(file);
			String fileName = file.getFileName().toString();
			String className = FilenameUtils.removeExtension(fileName);
			instrumentor.visit(istream,newFolder,className);
			System.out.println(file.toString() + "processed");

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	@Override
	public FileVisitResult preVisitDirectory(Path path,
			BasicFileAttributes attrib) throws IOException {
		String pathStr = path.toString();
		String newFolderName = createNewFolderName(path);
		
		new File(newFolderName).mkdir();
		System.out.println("***" + newFolderName + "created");
		return super.preVisitDirectory(path, attrib);
	}

	private String getSubFolder(String pathStr) {
		return pathStr.substring(ROOT.length(), pathStr.length());
	}

	private String createNewFolderName(Path path) {
		String pathStr = path.toString();
		String rootFolder = pathStr.substring(0, ROOT.length());
		String subFolder = getSubFolder(pathStr);
		return rootFolder + "_instrumented" + subFolder;
	}

	// If there is some error accessing
	// the file, let the user know.
	// If you don't override this method
	// and an error occurs, an IOException
	// is thrown.
	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		System.err.println(exc);
		return CONTINUE;
	}

	private InputStream readClassFile(Path path) throws FileNotFoundException {
		File initialFile = new File(path.toString());
		return new FileInputStream(initialFile);

	}

	public void accept(Instrumentor instrumentor) {
		this.instrumentor = instrumentor;
	}

}
