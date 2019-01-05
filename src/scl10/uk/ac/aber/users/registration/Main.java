package scl10.uk.ac.aber.users.registration;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.IIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scl10.uk.ac.aber.users.readers.ImageReader;

/**
 * The entry point of the program. This class contains the main method which
 * starts the registration program.
 * 
 * @author Scott Lockett
 *
 */
public class Main
{

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	/*
	 * Example input for this main method
	 * 
	 * ".\\resources\\registration-data\\female1.jpg",
	 * ".\\resources\\registration-data\\female2.jpg",
	 * ".\\fullyConnectedLayerTrainingImagesPaths.txt",
	 * ".\\fullyConnectedLayerTrainingWarpsPaths.txt", "7"
	 * 
	 */

	/**
	 * Main method and entry point of the program. Validates all the users input
	 * and starts the registration process.
	 * 
	 * @param args
	 *            args [0] - The path of source image args [1] - The path of
	 *            target image args [2] - The path of the file which holds all
	 *            of the paths to the images for training the artificial neural
	 *            network args [3] - The path of the file which holds all of the
	 *            paths to the warps for training the artificial neural network
	 *            args [4] optional - The number of levels within the pyramid. 7
	 *            will be used if no preferred value is given.
	 * @throws IIOException
	 *             If either of the test images can not be loaded.
	 */
	public static void main(String[] args) throws IIOException
	{
		/*
		 * Check if the user has given a number of level pyramids or not. If
		 * not, then assign one.
		 */
		if (args.length == 4)
		{
			args = new String[] { args[0], args[1], args[2], args[3], "7" };
		}

		/*
		 * Start by checking the number of parameters
		 */
		if (args.length != 5)
		{
			LOGGER.error(
					"Error in number of parameters: \n" + "0. Path of source image \n " + "1. Path of target image. \n"
							+ "2. Path of the file which holds the paths of the image for training the artifical neural network. \n"
							+ "3. Path of the file which holds the paths of the warps for training the artifical neural network. \n"
							+ "4. Optional - The number of levels in the pyramid.\n ",
					new IllegalArgumentException());
			return;
		}

		/*
		 * Parse the pyramid levels and validate the parsed number.
		 */
		int userPyramidLevel = 1;
		try
		{
			userPyramidLevel = Integer.parseInt(args[4]);
			if (userPyramidLevel < 0 || userPyramidLevel > 11)
			{
				throw new IllegalArgumentException("Your final parameter needs to be greater than 0 and less than 11.");
			}
		} catch (NumberFormatException e)
		{
			LOGGER.error("Your final parameter does not appear to be a number.", e);
		}

		/*
		 * Set the users image files into an array.
		 */
		File[] imageFilesToBeRegistered = new File[] { new File(args[0]), new File(args[1]) };

		/*
		 * The files are going to be converted into BufferedImages so create an
		 * array for holding the buffered images
		 */
		BufferedImage[] usersImages = new BufferedImage[2];

		for (int i = 0; i < 2; i++)
		{
			File currentFile = imageFilesToBeRegistered[i];
			/*
			 * Read the files as images.
			 */
			BufferedImage imageOfFile = ImageReader.readImageFile(currentFile);

			if (imageOfFile == null)
			{
				throw new javax.imageio.IIOException("Test image could not be loaded. " + currentFile.getPath());
			} else
			{
				usersImages[i] = imageOfFile;
			}
		}

		/*
		 * Ensure that both of the images are the same size else the
		 * registration will not work as intended.
		 */
		BufferedImage imageOne = usersImages[0];
		BufferedImage imageTwo = usersImages[1];

		if (imageOne.getHeight() != imageTwo.getHeight() || imageOne.getWidth() != imageTwo.getWidth())
		{
			LOGGER.error(
					"Image one dimensions: " + new Dimension(imageOne.getWidth(), imageOne.getHeight()).toString()
							+ "\n" + "Image two dimensions: "
							+ new Dimension(imageTwo.getWidth(), imageTwo.getHeight()).toString(),
					new IllegalArgumentException(
							"The input images are not the same size! Please choose image the same size"));
		}

		/*
		 * Set the users Strings input as file objects
		 */
		File[] trainingFiles = new File[] { new File(args[2]), new File(args[3]) };

		/*
		 * Once all the validation has completed run the autowarp method!
		 * 
		 * ********************************* NOTICE ****************************
		 * 
		 * 7 is used as the levels of pyramids. This is because the training
		 * data used to train the artificial neural network was downsampled 7
		 * times. To downsample a series of images a number of times to use as
		 * training data on the fly would be computationally costly and would
		 * run quite slowly. Hence the need for the training data to be pre
		 * generated before running. Downsampling lots of training data on the
		 * fly would be quite simple though with the use of the
		 * TrainingDataReader class and the WarpManipulator and ImageManipulator
		 * classes but for performance I thought it best to have the data pre
		 * generated. The pre generated data was created with the
		 * TrainingDataFactory class. Should the data be generated on the fly I
		 * have accounted for this by allowing users to choose file that
		 * contains the training data of the images and the warp. Also you will
		 * notice in the main method by parsing the userPyramidLevel parameter,
		 * and by making it optional.
		 */
		RegistrationApplication.autoWarp(usersImages[0], usersImages[1], trainingFiles[0], trainingFiles[1],
				userPyramidLevel);

	}
}
