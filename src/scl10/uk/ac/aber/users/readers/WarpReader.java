package scl10.uk.ac.aber.users.readers;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.NonNull;

/**
 * An immutable class for the reading and parsing of warp file data.
 * 
 * @author Scott Lockett
 */
public final class WarpReader
{

	/**
	 * Logger for user information
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(WarpReader.class);

	/**
	 * The associated warp as a 2 dimensional array of shift values
	 */
	@Getter
	private final double[][] as2DArray;

	/**
	 * The associated warp as a flat/ single array of shift values
	 */
	@Getter
	private final double[] asFlatArray;

	/**
	 * The dimensions of the 2D warp
	 */
	private final Dimension warpDimension;

	/**
	 * Creates a new warp reader object based on an already read warp object.
	 * 
	 * @param warp
	 *            The warp object which is to be read into the reader object.
	 *            cannot be null.
	 */
	public WarpReader(@NonNull final double[][] warp)
	{
		as2DArray = warp;
		warpDimension = new Dimension(warp[0].length, warp.length);
		asFlatArray = convertToFlatArray();
	}

	/**
	 * Creates a new warp reader object which reads in the warp file and allows
	 * the values to be accessed.
	 * 
	 * @param path
	 *            The path of the warp file to be read. cannot be null.
	 */
	public WarpReader(@NonNull final File path)
	{
		/*
		 * Read the warp from the file and call the other constructor
		 */
		this(readWarpFile(path));
	}

	/**
	 * Reads in a warp file's path and the values from the file and returns a 2D
	 * array of the values within the file
	 * 
	 * This method is static as it does not feel like it would be an instance
	 * specific method.
	 * 
	 * @param warpPath
	 *            The path of the file which holds the warp values
	 * @return The values from the read warp file in a 2D array of doubles
	 */
	public static double[][] readWarpFile(@NonNull final File warpPath)
	{
		/*
		 * The 2d array which will hold the warp values
		 */
		double[][] warpArray = null;

		/*
		 * We need to know the width and height of the warp file
		 */
		int width = 0;
		int height = 0;

		/*
		 * This can definitely be improved. No need to create all of these
		 * readers.
		 */
		FileReader widthFileReader = null;
		BufferedReader widthReader = null;

		FileReader heightFileReader = null;
		BufferedReader heightReader = null;

		FileReader warpFileReader = null;
		BufferedReader warpReader = null;

		try
		{
			widthFileReader = new FileReader(warpPath);
			widthReader = new BufferedReader(widthFileReader);

			heightFileReader = new FileReader(warpPath);
			heightReader = new BufferedReader(heightFileReader);

			/*
			 * The width will be the number of elements in the first line of the
			 * warp file
			 */
			String[] firstLine = widthReader.readLine().trim().split(" ");
			width = firstLine.length;

			/*
			 * The height will be the number of non-null lines in the file
			 */
			while (heightReader.readLine() != null)
			{
				height++;
			}

			/*
			 * Set the size once the warp's measurements have been discovered
			 */
			warpArray = new double[height][width];

			/*
			 * Create a reader for parsing the values in the warp file
			 */
			warpFileReader = new FileReader(warpPath);
			warpReader = new BufferedReader(warpFileReader);

			/*
			 * Finally read in the warp values and add them to the array
			 */
			for (int i = 0; i < height; i++)
			{
				String[] currentLine = warpReader.readLine().trim().split(" ");
				for (int j = 0; j < width; j++)
				{
					warpArray[i][j] = Double.parseDouble(currentLine[j]);
				}
			}

			/*
			 * Close all of the readers to prevent data leaks
			 */
			warpFileReader.close();
			widthFileReader.close();
			heightFileReader.close();

			widthReader.close();
			heightReader.close();
			warpReader.close();

		} catch (IOException e)
		{
			LOGGER.error("Warp has failed to read. ", e);
		}
		return warpArray;
	}

	/**
	 * Converts the 2D warp array which is associated with this reader object
	 * into a single array or a flat version of the original 2D array.
	 * 
	 * @return A flattened version of the warp array
	 */
	private double[] convertToFlatArray()
	{
		double[] flatWarp = new double[(int) warpDimension.getHeight() * (int) warpDimension.getWidth()];
		int flatWarpPosition = 0;

		for (int i = 0; i < warpDimension.getHeight(); i++)
		{
			for (int j = 0; j < warpDimension.getWidth(); j++)
			{
				flatWarp[flatWarpPosition] = as2DArray[i][j];
				flatWarpPosition++;
			}
		}
		return flatWarp;
	}

}
