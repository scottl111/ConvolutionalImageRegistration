package scl10.uk.ac.aber.users.neuralnetwork;

import java.awt.image.BufferedImage;
import java.io.File;

import org.apache.commons.lang3.ArrayUtils;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.data.norm.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import scl10.uk.ac.aber.users.readers.ImageReader;
import scl10.uk.ac.aber.users.readers.TrainingDataReader;
import scl10.uk.ac.aber.users.readers.WarpReader;

/**
 * A fully connected layer neural network used for predicted the warp between
 * images.
 * 
 * @author Scott Lockett
 *
 */
public class FullyConnectedLayerWarp extends FullyConnectedLayer
{

	/**
	 * Create the logger for logging messages
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(FullyConnectedLayerWarp.class);

	/**
	 * The images used in the training of the fully connected layer
	 */
	private final File[] imagesFiles;

	/**
	 * The warps used in the training of the fully connected layer
	 */
	private final File[] warpFiles;

	/**
	 * 
	 * @param inputSize
	 *            The size of the input for fully connected layer
	 * @param hiddenLayerSize
	 *            The size of the hidden layer for the fully connected layer
	 * @param outputSize
	 *            The size of the output for the fully connected layer
	 * @param imageTrainingDataFile
	 *            The file that contains all of the paths of the training data
	 *            for the images for the training of the artificial neural
	 *            network
	 * @param warpTrainingDataFile
	 *            The file that contains all of the paths of the training data
	 *            for the warps for the training of the artificial neural
	 *            network
	 */
	public FullyConnectedLayerWarp(final int inputSize, final int hiddenLayerSize, final int outputSize,
			@NonNull final File imageTrainingDataFile, @NonNull final File warpTrainingDataFile)
	{

		/*
		 * Read from the files which contain the paths of the training images
		 * data and the training warp data.
		 */
		TrainingDataReader imageDataReader = new TrainingDataReader(imageTrainingDataFile);
		TrainingDataReader warpDataReader = new TrainingDataReader(warpTrainingDataFile);

		/*
		 * Get the files for the the images and the warps training data
		 */
		imagesFiles = imageDataReader.getFilesOfTrainingData();
		warpFiles = warpDataReader.getFilesOfTrainingData();

		/*
		 * Do some super rudimentary validation on the data. Prevents null
		 * pointers further in the program too.
		 * 
		 * BUG - Just because it ends in ".jpg" or ".jpeg" doesn't mean that it
		 * actually is a jpg file.
		 */
		for (File imageFile : imagesFiles)
		{
			if (!imageFile.getPath().endsWith(".jpg") && !imageFile.getPath().endsWith(".jpeg"))
			{
				throw new IllegalArgumentException(
						"One or more of the data within your image training data file does not contain all jpg images. Such as "
								+ imageFile.getName());
			}
		}

		/*
		 * BUG - same as before, just because it ends with .txt doesn't mean it
		 * really is a text file. Prevents null pointers also.
		 */
		for (File warpFile : warpFiles)
		{
			if (!warpFile.getPath().endsWith(".txt"))
			{
				throw new IllegalArgumentException(
						"One or more of the data within your warp training data file does not contain text files. Such as "
								+ warpFile.getName());
			}
		}

		/*
		 * Instantiate the multilayer perceptron with the sizes and the type of
		 * transfer function the perceptron intends to use.
		 */
		mlp = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputSize, hiddenLayerSize, outputSize);

		/*
		 * Instantiate the training data data set with the sizes of the input
		 * and output.
		 */
		trainingData = new DataSet(inputSize, outputSize);
	}

	/**
	 * Train the neural network based on the images and warp files
	 */
	public void trainNetwork()
	{
		/*
		 * Read all of the images and the warps and the images as part of the
		 * training data for the artificial neural network and add them all to
		 * the training data dataset.
		 */
		for (int i = 0; i < imagesFiles.length; i = i + 2)
		{
			/*
			 * Read the images from the files using the ImageReader and get them
			 * as flat pixel arrays.
			 */
			double[] flatImg1 = new ImageReader(imagesFiles[i]).getAsFlatArray();
			double[] flatImg2 = new ImageReader(imagesFiles[i + 1]).getAsFlatArray();

			/*
			 * Read the warp from the files using the WarpReader and get them as
			 * flat warp arrays.
			 */
			double[] flatXWarp = new WarpReader(warpFiles[i]).getAsFlatArray();
			double[] flatYWarp = new WarpReader(warpFiles[i + 1]).getAsFlatArray();

			/*
			 * Combine the the flat images together and combine the flat warps
			 * together
			 */
			double[] bothImages = ArrayUtils.addAll(flatImg1, flatImg2);
			double[] bothWarps = ArrayUtils.addAll(flatXWarp, flatYWarp);

			/*
			 * Add it to the training data dataset. The images as the input and
			 * the warps as the output.
			 */
			trainingData.addRow(new DataSetRow(bothImages, bothWarps));
		}

		/*
		 * Normalise the data so it's values are between 0 - 1 so it can be used
		 * in the neural network.
		 */
		Normalizer warpImgNormaliser = new ImageWarpNormalizer();
		warpImgNormaliser.normalize(trainingData);

		LOGGER.info("Learning....");

		mlp.learn(trainingData);

	}

	/**
	 * Use the input images as testing data for the fully connected layer which
	 * will predict the x and y warp between the two images.
	 * 
	 * @param source
	 *            The source image. cannot be null.
	 * @param target
	 *            The target image. cannot be null.
	 * @return a 3D array of doubles that represent the x warp and y warp output
	 *         from the fully connected layer warp.
	 */
	@NonNull
	public double[][][] generateWarp(@NonNull final BufferedImage source, @NonNull final BufferedImage target)
	{
		double[][] predictedX = new double[source.getHeight()][source.getWidth()];
		double[][] predictedY = new double[source.getHeight()][source.getWidth()];

		/*
		 * Flatten image one into a single array
		 */
		ImageReader imageReaderOne = new ImageReader(source);
		double[] flatImg1 = imageReaderOne.getAsFlatArray();

		/*
		 * Flatten image two into a single array
		 */
		ImageReader imageReaderTwo = new ImageReader(target);
		double[] flatImg2 = imageReaderTwo.getAsFlatArray();

		/*
		 * Amalgamate the two images into a single flat array
		 */
		double[] bothImages = ArrayUtils.addAll(flatImg1, flatImg2);

		/*
		 * Set the input and calculate the prediction
		 */
		mlp.setInput(bothImages);
		mlp.calculate();

		/*
		 * Get the output from the neural network.
		 */
		double[] predictedWarpFlat = mlp.getOutput();

		/*
		 * The output from the predicted warp will first be the x prediction and
		 * then the y prediction. The difference/ offset between the two will be
		 * the size of the prediction divided by 2.
		 */
		int flatXWarpPosition = 0;
		int flatYWarpPosition = (int) Math.floor(predictedWarpFlat.length / 2.0D);

		/*
		 * convert warp X into a 2D array
		 */
		for (int i = 0; i < source.getHeight(); i++)
		{
			for (int j = 0; j < source.getWidth(); j++)
			{
				/*
				 * Loop through all and add all the x and y warps
				 */
				predictedX[i][j] = predictedWarpFlat[flatXWarpPosition];
				predictedY[i][j] = predictedWarpFlat[flatXWarpPosition + flatYWarpPosition];
				flatXWarpPosition++;
			}
		}

		return new double[][][] { predictedX, predictedY };
	}
}
