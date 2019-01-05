package scl10.uk.ac.aber.users.trainingdatageneration;

import java.awt.image.BufferedImage;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import scl10.uk.ac.aber.users.manipulators.ImageManipulator;
import scl10.uk.ac.aber.users.manipulators.WarpManipulator;
import scl10.uk.ac.aber.users.readers.ImageReader;
import scl10.uk.ac.aber.users.readers.TrainingDataReader;
import scl10.uk.ac.aber.users.readers.WarpReader;

/**
 * This class is used to produce training data for the warp artificial neural
 * network.
 * 
 * @author Scott Lockett
 */
public final class TrainingDataGenerator
{

	/**
	 * Create a logger to log messages.
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(TrainingDataGenerator.class);

	/**
	 * The number of times the data is to be downsampled.
	 */
	private static int TIMES_TO_DOWNSAMPLE;

	/**
	 * private constructor to hide the public implicit one
	 */
	private TrainingDataGenerator()
	{

	}

	/*
	 * Example parameters for the main method.
	 * ".\\pathsOfWarpsToDownsample.txt", ".\\new_downsampled_warps", "7"
	 */

	/**
	 * Main method for producing the training data.
	 * 
	 * @param args
	 *            agrs [0] The path of the file that contains the paths to all
	 *            of the training data paths. Can be image data or warp data.
	 *            args [1] The folder where your downsampled files are to be
	 *            written to. If the folder does not exist yet, it wil be
	 *            created. args [2] The number of times the data is to be
	 *            downsampled between 1 - 10.
	 */
	public static void main(final String[] args)
	{

		/*
		 * Lets do some basic validation on the users input firstly.
		 */
		if (isUsersInputValid(args) == false)
		{
			LOGGER.info("1. The path of the file that contains the paths to all of the training data paths \n"
					+ "2. The folder where your downsamples files are to be written to. If the folder does not exist yet, it wil be created.\n"
					+ "3. The number of times the data is to be downsampled between 1 - 10 ");
			return;
		}

		File trainingFile = new File(args[0]);
		File trainingFolder = new File(args[1]);

		/*
		 * Create a training data read so that we can read the files that are to
		 * be downsampled.
		 */
		TrainingDataReader trainingDataReader = new TrainingDataReader(trainingFile);

		/*
		 * We need to find out what type of data is being created. Either image
		 * based or text based. Start by checking if null was returned then make
		 * sure we have something within the files array
		 */
		File[] trainingFiles = trainingDataReader.getFilesOfTrainingData();

		if (trainingFiles.length > 0)
		{
			/*
			 * Are we downsampling images or warps? There will be a MUCH better
			 * way of checking this. Also need to ensure that other file formats
			 * can be read e.g. PNG, TIFF, etc.
			 */
			if ((trainingFiles[0].getName().contains(".jpg")) || (trainingFiles[0].getName().contains(".JPG")))
			{
				createDownsampledImageTrainingDataAndWriteToFolder(trainingFiles, trainingFolder);
			} else if (trainingFiles[0].getName().contains(".txt"))
			{
				createDownsampledWarpTrainingDataAndWriteToFolder(trainingFiles, trainingFolder);
			} else
			{
				throw new IllegalArgumentException(
						"Can not recognise file type. Images should be .jpg and warp file .txt");
			}
		} else
		{
			LOGGER.error("No file paths have been read from the file.");
			return;
		}
	}

	/**
	 * Performs downsampling of the original sized image based training data and
	 * writes the files out to the specified folder
	 * 
	 * @param trainingImageFiles
	 *            The files that are the training images that will be
	 *            downsampled a series of times to create the new training data
	 * @param folderToWriteTo
	 *            The folder in which the training images are to be written to.
	 *            If the folder does not exist, then it will be created.
	 */
	private static void createDownsampledImageTrainingDataAndWriteToFolder(@NonNull final File[] trainingImageFiles,
			@NonNull final File folderToWriteTo)
	{

		checkFolderExistsAndCreateIfNot(folderToWriteTo);

		for (File imageFile : trainingImageFiles)
		{
			/*
			 * Load the image to be downsampled.
			 */
			BufferedImage currentImage = ImageReader.readImageFile(imageFile);
			BufferedImage smallerImage = currentImage;

			/*
			 * Downsample however many times the image needs halving
			 */
			for (int i = 0; i < TIMES_TO_DOWNSAMPLE; i++)
			{
				smallerImage = ImageManipulator.downsample(smallerImage);
			}

			/*
			 * Create output file from the destination folder and the current
			 * files names
			 */
			File outputFile = new File(folderToWriteTo + "\\" + imageFile.getName());

			/*
			 * Convert the image to grey scale to save training time on the
			 * artificial neural network
			 */
			BufferedImage smallerGreyImage = ImageManipulator.convertColorImageToGreyscale(smallerImage);

			/*
			 * Write the image to the output file. Ensure that it has correctly
			 * been written to and log the result.
			 */
			if (ImageManipulator.writeImageFile(outputFile, smallerGreyImage))
			{
				LOGGER.info("Image file has been written: " + folderToWriteTo + "\\" + imageFile.getName());
			} else
			{
				LOGGER.warn("Image file has failed to be written: " + folderToWriteTo + "\\" + imageFile.getName());
			}
		}
	}

	/**
	 * Performs downsampling of the original sized warp training data and write
	 * the files out to a specified folder
	 * 
	 * @param trainingWarpFiles
	 *            The files that are the training warps that will be downsampled
	 *            a series of times to create the training data
	 * @param folderToWriteTo
	 *            The folder in which the training warps are to be written to.
	 *            If the folder does not exist, then it will be created.
	 */
	private static void createDownsampledWarpTrainingDataAndWriteToFolder(@NonNull final File[] trainingWarpFiles,
			@NonNull final File folderToWriteTo)
	{

		checkFolderExistsAndCreateIfNot(folderToWriteTo);

		for (File warpFile : trainingWarpFiles)
		{
			/*
			 * Load the image to be downsampled.
			 */
			double[][] currentWarp = WarpReader.readWarpFile(warpFile);
			double[][] smallerWarp = currentWarp;

			/*
			 * Downsample however many times the image needs halving
			 */
			for (int i = 0; i < TIMES_TO_DOWNSAMPLE; i++)
			{
				smallerWarp = WarpManipulator.downsample(smallerWarp);
			}

			/*
			 * Create output file from the destination folder and the current
			 * files names
			 */
			File outputFile = new File(folderToWriteTo + "\\" + warpFile.getName());

			/*
			 * Write the warp to the output file.
			 */
			if (WarpManipulator.writeWarpFile(outputFile, smallerWarp))
			{
				LOGGER.info("Warp file has been written: " + folderToWriteTo + "\\" + warpFile.getName());
			} else
			{
				LOGGER.warn("Warp file has failed to be written: " + folderToWriteTo + "\\" + warpFile.getName());
			}
		}
	}

	/**
	 * Checks that the users input conforms to the required format and that the
	 * training file exists.
	 * 
	 * @param usersInput
	 *            The input passed from the user to be validated.
	 * @return If true, the the users input is valid. Files may not exists but
	 *         that's for the reads to handle. If false then the input is not
	 *         valid.
	 */
	private static boolean isUsersInputValid(final String[] usersInput)
	{
		/*
		 * Make sure the parameter numbers are correct.
		 */
		if (usersInput.length != 3)
		{
			LOGGER.error("The number of parameters are incorrect. You should have 3 parameters. ");
			return false;
		}

		/*
		 * Try and parse the number of times the data will be downsampled.
		 */
		try
		{
			int parsedValue = Integer.parseInt(usersInput[2]);

			if (parsedValue < 0 || parsedValue > 11)
			{
				LOGGER.error("The downsampling times should be greater than 0 and less than 11.");
				return false;
			} else
			{
				TIMES_TO_DOWNSAMPLE = parsedValue;
			}
		} catch (NumberFormatException e)
		{
			LOGGER.error("Your final parameter does not appear to be a number. Check and try again.");
			return false;
		}

		File trainingFile = new File(usersInput[0]);

		/*
		 * Check that the training file actually exists.
		 */
		if (!trainingFile.exists())
		{
			LOGGER.error("The file containing the training data paths does not exist. Check and try again.");
			return false;
		}

		/*
		 * Otherwise the users input has passed the validation process so
		 * continue.
		 */
		return true;
	}

	/**
	 * Tests to see if a folder the training data is going to be written to
	 * exists or not. If it does not, the create the folder.
	 * 
	 * @param folderToWriteTo
	 *            The folder that needs checking to see if it exists, or created
	 *            if not. cannot be null.
	 */
	private static void checkFolderExistsAndCreateIfNot(@NonNull final File folderToWriteTo)
	{
		/*
		 * Check if the file exists firstly, if not then create it. If it does
		 * exist then check that its a directory. Throw an exception if its not.
		 * Can't write a series of files something thats not a directory.
		 */
		if (!folderToWriteTo.exists())
		{
			if (folderToWriteTo.mkdir())
			{
				LOGGER.info(folderToWriteTo.getAbsolutePath() + " has been created.");
			} else
			{
				throw new IllegalArgumentException(
						folderToWriteTo.getAbsolutePath() + " has not been created. Check the paths and files");
			}
		} else
		{
			if (!folderToWriteTo.isDirectory())
			{
				throw new IllegalArgumentException(
						folderToWriteTo.getAbsolutePath() + " exists but it is not a folder!");
			}
		}
	}
}