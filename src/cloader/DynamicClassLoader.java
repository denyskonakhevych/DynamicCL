package cloader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DynamicClassLoader extends ClassLoader {

	public DynamicClassLoader(ClassLoader parent) {
		super(parent);
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		if (name.startsWith("cloader.impl")) {
			return getClass(name);
		}
		return super.loadClass(name);
	}

	private Class<?> getClass(String name) throws ClassNotFoundException {

		resolve(name);

		StringBuilder classFilename = new StringBuilder(
				System.getProperty("user.dir")).append(File.separatorChar)
				.append("bin").append(File.separatorChar)
				.append(name.replace('.', File.separatorChar)).append(".class");
		File classFile = new File(classFilename.toString());

		byte[] b = null;
		try {
			b = loadClassData(classFile);
			Class<?> c = defineClass(name, b, 0, b.length);
			resolveClass(c);
			return c;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private void resolve(String name) throws ClassNotFoundException {

		String fileStub = name.replace('.', File.separatorChar);
		StringBuilder javaFilename = new StringBuilder(
				System.getProperty("user.dir")).append(File.separatorChar)
				.append("src").append(File.separatorChar).append(fileStub)
				.append(".java");
		StringBuilder classFilename = new StringBuilder(
				System.getProperty("user.dir")).append(File.separatorChar)
				.append("bin").append(File.separatorChar).append(fileStub)
				.append(".class");

		File javaFile = new File(javaFilename.toString());
		File classFile = new File(classFilename.toString());

		if (javaFile.exists()
				&& (!classFile.exists() || javaFile.lastModified() > classFile
						.lastModified())) {
			try {
				if (!compile(javaFile.getAbsolutePath()) || !classFile.exists()) {
					throw new ClassNotFoundException("Compile failed: "
							+ javaFile.getAbsolutePath());
				}
			} catch (IOException ie) {
				throw new ClassNotFoundException(ie.toString());
			}
		}
	}

	private boolean compile(String filename) throws IOException {

		System.out.println("DCO: Compiling " + filename);
		File self = new File(getClass().getClassLoader().getResource(".")
				.getFile());
		StringBuilder command = new StringBuilder("javac -d ")
				.append(self.getAbsolutePath()).append(" ").append(filename);
		Process p = Runtime.getRuntime().exec(command.toString(), null, self);
		try {
			p.waitFor();
		} catch (InterruptedException ie) {
			System.out.println(ie);
		}
		int ret = p.exitValue();
		return ret == 0;
	}

	private byte[] loadClassData(File classFile) throws IOException {

		InputStream stream = new FileInputStream(classFile);
		int size = stream.available();
		byte buff[] = new byte[size];
		DataInputStream in = new DataInputStream(stream);
		in.readFully(buff);
		in.close();
		return buff;
	}
}