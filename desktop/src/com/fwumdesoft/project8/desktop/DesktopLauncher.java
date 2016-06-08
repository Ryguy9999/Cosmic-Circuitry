package com.fwumdesoft.project8.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.fwumdesoft.project8.App;

public class DesktopLauncher 
{
	public static void main(String[] arg) 
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.addIcon("icon-64.png", FileType.Internal);
		config.addIcon("icon-32.png", FileType.Internal);
		config.addIcon("icon-16.png", FileType.Internal);
		new LwjglApplication(new App(), config);
	}
}
