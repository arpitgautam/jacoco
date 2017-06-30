package com.exp.tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jacoco.core.runtime.LoggerRuntime;

public class Instrumentor {

	public static final String ROOT = "E:\\playground\\jacoco\\classes";

	public void iterateFolder() {

	}

	public void visit(InputStream sourceClassFileStream, String outPath,
			String targetName) throws IOException {
		final IRuntime runtime = new LoggerRuntime();

		// The Instrumenter creates a modified version of our test target class
		// that contains additional probes for execution data recording:
		final Instrumenter instr = new Instrumenter(runtime);
		final byte[] instrumented = instr.instrument(sourceClassFileStream,
				targetName);
		FileUtils.writeByteArrayToFile(new File(outPath), instrumented);

	}

	private InputStream getTargetClass(final String name) {
		final String resource = '/' + name.replace('.', '/') + ".class";
		return getClass().getResourceAsStream(resource);
	}

	public static void main(String... s) throws IOException {
		Path startingDir = Paths.get(ROOT);
		JavaClassVisitor visitor = new JavaClassVisitor();
		Instrumentor instrumentor = new Instrumentor();
		visitor.accept(instrumentor);
		Files.walkFileTree(startingDir, visitor);
	}

}
