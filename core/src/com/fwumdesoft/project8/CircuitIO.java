package com.fwumdesoft.project8;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class CircuitIO extends SynchronousAssetLoader<CircuitComponent[][], CircuitIO.CircuitParameters> {
	
	public CircuitIO(FileHandleResolver resolver) {
		super(resolver);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public CircuitComponent[][] load(AssetManager assetManager, String fileName, FileHandle file, CircuitParameters parameter) {
		Object o = null;
		try {
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file.file()));
			o = stream.readObject();
			stream.close();
		} catch(IOException | ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return (CircuitComponent[][])o;
	}
	
	public static void write(FileHandle file, CircuitComponent[][] component) {
		try {
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file.file()));
			stream.writeObject(component);
			stream.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, CircuitParameters parameter) {
		return null;
	}

	static class CircuitParameters extends AssetLoaderParameters<CircuitComponent[][]> {
		
	}
	
}
