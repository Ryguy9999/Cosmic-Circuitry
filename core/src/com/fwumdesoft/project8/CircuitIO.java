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

/**
 * Handles saving and loading of circuits to and from files
 */
public class CircuitIO extends SynchronousAssetLoader<Circuit, CircuitIO.CircuitParameters>
{

	/**
	 * Create a new CircuitIO object
	 * 
	 * @param resolver
	 *            A resolver that should be acquired from an AssetManager
	 */
	public CircuitIO(FileHandleResolver resolver)
	{
		super(resolver);
		// TODO Auto-generated constructor stub
	}

	@Override
	/**
	 * Don't use this method, it's called by the AssetManager
	 */
	public Circuit load(AssetManager assetManager, String fileName, FileHandle file, CircuitParameters parameter)
	{
		Object o = null;
		try
		{
			ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file.file()));
			o = stream.readObject();
			stream.close();
		} catch (IOException | ClassNotFoundException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		Circuit c = (Circuit)o;
		c.name = fileName.substring(0, fileName.indexOf(".circuit"));
		return c;
	}

	/**
	 * Write a circuit to a file
	 * 
	 * @param file
	 *            The file to write to
	 * @param circuit
	 *            The circuit to write
	 */
	public static void write(FileHandle file, Circuit circuit)
	{
		try
		{
			ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file.file()));
			stream.writeObject(circuit);
			stream.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	/**
	 * Don't call this
	 */
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, CircuitParameters parameter)
	{
		return null;
	}

	static class CircuitParameters extends AssetLoaderParameters<Circuit>
	{

	}

}
