package pdftrans;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Launcher {
	
	public void launch()
	{
		String[] contextPaths=new String[]{"pdftrans/app-context.xml"};
		new ClassPathXmlApplicationContext(contextPaths);
	}

}
