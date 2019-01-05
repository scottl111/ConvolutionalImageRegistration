package scl10.uk.ac.aber.users.manipulators;

import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;

/**
 * A class used for applying warp based operations to a 2d array of doubles.
 * 
 * @author Scott Lockett
 *
 */
public class WarpManipulator
{

	/**
	 * Logger for logging user information
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(WarpManipulatorTest.class);

	/**
	 * Loads openCV
	 */
	static
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * Private constructor to hide the implicit public one
	 */
	private WarpManipulator()
	{

	}

	/**
	 * Downsamples a given warp by a given size.
	 * 
	 * @param warp
	 *            The warp that is to be downsampled. cannot be null.
	 * @return A downsampled version of the input warp.
	 */
	public static double[][] downsample(@NonNull final double[][] warp)
	{
		/*
		 * Convert the warp into an OpenCV matrix
		 */
		Mat matrix = convertWarpToMatrix(warp);

		/*
		 * Create the new smaller size which is half both the width and height
		 * of the original
		 */
		Size smallerSize = new Size(Math.floor(matrix.width() / 2.0D), Math.floor(matrix.height() / 2.0D));

		/*
		 * Create a destination where the downsampled matrix will be stored
		 */
		Mat destination = new Mat((int) smallerSize.height, (int) smallerSize.width, matrix.type());

		/*
		 * Apply the downsampling
		 */
		Imgproc.pyrDown(matrix, destination, smallerSize);

		/*
		 * multiply each warp values by 0.5 due to the down sampling, the shift
		 * is halved
		 */
		destination = multiplyEachValueInMatByAFactor(destination, 0.5D);

		return convertMatrixToWarp(destination);
	}

	/**
	 * Upsamples a given warp file by a given size.
	 * 
	 * @param warp
	 *            The warp that is to be upsampled. Cannot be null.
	 * @param newSize
	 *            The desired size of the warp.cannot be null.
	 *            <p>
	 *            <strong> The new size MUST be the warp's (width * 2) +/- 1 and
	 *            the warp's (height * 2) +/- 1. </strong>
	 * @return An upsampled version of the input warp.
	 */
	public static double[][] upsample(@NonNull final double[][] warp, @NonNull final Size newSize)
	{
		/*
		 * Convert the warp into an OpenCV matrix
		 */
		Mat matrix = convertWarpToMatrix(warp);

		/*
		 * Create a destination where the upsampled matrix will be stored
		 */
		Mat destination = new Mat((int) (newSize.height), (int) (newSize.width), matrix.type());

		/*
		 * Apply the upsampeling
		 */
		Imgproc.pyrUp(matrix, destination, newSize);

		/*
		 * multiply each warp values by 2 due to the up sampling, the shift is
		 * doubled
		 */
		destination = multiplyEachValueInMatByAFactor(destination, 2.0D);

		return convertMatrixToWarp(destination);
	}

	/**
	 * Writes a warp 2D array of doubles out to a given file
	 * 
	 * @param fileToWriteTo
	 *            The file which is going to be written to, if the file does not
	 *            already exists, it will be created. Cannot be null.
	 * @param warp
	 *            The warp that holds the values that are going to be written to
	 *            within the file. cannot be null.
	 * @return true is the file was successfully written to, or false if an
	 *         exception was thrown during the writing.
	 */
	public static boolean writeWarpFile(@NonNull final File fileToWriteTo, @NonNull final double[][] warp)
	{
		/*
		 * First check if the file exists or not, if not then create it.
		 */
		if (!fileToWriteTo.exists())
		{
			try
			{
				boolean created = fileToWriteTo.createNewFile();

				if (!created)
				{
					LOGGER.error(fileToWriteTo.getPath() + " does not exist and can not be created. ");
					return false;
				}

			} catch (IOException e)
			{
				LOGGER.error(fileToWriteTo.getPath() + " does not exist and can not be created. ", e);
				return false;
			}
		}

		/*
		 * Try and write the warp to the file
		 */
		try
		{
			FileWriter fileWriter = new FileWriter(fileToWriteTo.getAbsolutePath());
			BufferedWriter writer = new BufferedWriter(fileWriter);

			for (double[] element : warp)
			{
				for (int j = 0; j < warp[0].length; j++)
				{
					writer.write(element[j] + " ");
				}
				writer.newLine();
			}

			/*
			 * Close the writers
			 */
			writer.close();
			fileWriter.close();

		} catch (IOException e)
		{
			LOGGER.error("An exception was thrown while trying to write to " + fileToWriteTo.getPath(), e);
			return false;
		}

		return true;
	}

	/**
	 * Loops through every values within a single channel matrix and multiplies
	 * each value by a given factor.
	 * 
	 * @param matrixToMultiply
	 *            The matrix that values need multiplying by a factor. Cannot be
	 *            null.
	 * @param factor
	 *            The factor that each value will be multiplied by.
	 * @return The matrix with each of its values multiplied by the factor.
	 */
	private static Mat multiplyEachValueInMatByAFactor(@NonNull final Mat matrixToMultiply, final double factor)
	{
		for (int i = 0; i < matrixToMultiply.height(); i++)
		{
			for (int k = 0; k < matrixToMultiply.width(); k++)
			{
				matrixToMultiply.put(i, k, new double[] { matrixToMultiply.get(i, k)[0] * factor });
			}
		}

		return matrixToMultiply;
	}

	/**
	 * Converts a 2D array of warp values into an openCV Mat object
	 * 
	 * @param warp
	 *            The warp which is to be converted into a mat object. Cannot be
	 *            null.
	 * @return The warp as a mat object
	 */
	private static Mat convertWarpToMatrix(@NonNull final double[][] warp)
	{
		/*
		 * Create a size of the warp
		 */
		Dimension size = new Dimension(warp[0].length, warp.length);

		/*
		 * Create a new matrix where the warp's values will be store within
		 */
		Mat matrix = new Mat(size.height, size.width, CvType.CV_64FC1);

		/*
		 * Create a flat array which is the the width x the height of the 2D
		 * warp
		 */
		double[] flatArray = new double[(int) ((int) size.getWidth() * size.getHeight())];
		int flatArrayPosition = 0;

		/*
		 * Add each value from the 2D array into the flat array
		 */
		for (int i = 0; i < size.getHeight(); i++)
		{
			for (int j = 0; j < size.getWidth(); j++)
			{
				flatArray[flatArrayPosition] = warp[i][j];
				flatArrayPosition++;
			}
		}

		/*
		 * Put the flat array values into the matrix
		 */
		matrix.put(0, 0, flatArray);

		return matrix;
	}

	/**
	 * Converts a matrix of warp values into a 2D array of warp values
	 * 
	 * @param matrix
	 *            The matrix which it to be converted into a 2D array. Cannot be
	 *            null.
	 * @return a 2D array of doubles containing the warp values.
	 */
	private static double[][] convertMatrixToWarp(@NonNull final Mat matrix)
	{
		double[][] warpArray = new double[matrix.height()][matrix.width()];

		for (int i = 0; i < matrix.height(); i++)
		{
			for (int j = 0; j < matrix.width(); j++)
			{
				warpArray[i][j] = matrix.get(i, j)[0];
			}
		}

		return warpArray;
	}

}
