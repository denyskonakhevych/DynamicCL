package cloader;

public class Test {

	public static void main(String[] args) throws Exception {
		
		TestModule tm;
		while (true) {
			DynamicClassLoader loader = new DynamicClassLoader(
					Test.class.getClassLoader());
			Class<?> clazz = Class.forName("cloader.impl.TestModuleVersions",
					true, loader);
			tm = (TestModule) clazz.newInstance();
			System.out.println(tm);
			Thread.sleep(1500);
		}
	}
}