package scl10.uk.ac.aber.users.registration;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Size;

import lombok.NonNull;
import scl10.uk.ac.aber.users.manipulators.ImageManipulator;
import scl10.uk.ac.aber.users.neuralnetwork.ConvolutionLayerPyramid;
import scl10.uk.ac.aber.users.neuralnetwork.FullyConnectedLayerWarp;
import scl10.uk.ac.aber.users.pyramids.ImagePyramid;
import scl10.uk.ac.aber.users.pyramids.WarpPyramid;
import scl10.uk.ac.aber.users.readers.ImageReader;

/**
 * The application part of the program. Once the users input has been parsed and
 * validated then the registration application can begin.
 * 
 * @author Scott Lockett
 *
 */
public class RegistrationApplication
{

	/**
	 * The levels within the pyramid.
	 * 
	 * Maybe shouldn't be static but it felt wrong passing this values all over
	 * every class and creating an attribute for it for (more or less) every
	 * class and setting it just so it can be passed somewhere into the method
	 * of another object.
	 */
	public static int PYRAMID_LEVELS = 0;

	/**
	 * Private constructor to hide the implicit public one.
	 */
	private RegistrationApplication()
	{

	}

	/**
	 * The registration method. This is the central method where the
	 * registration takes place.
	 * 
	 * @param sourceImage
	 *            The image that will act as the source image to which the
	 *            registration will be performed on
	 * @param targetImage
	 *            The image that will act as the target image to which the
	 *            registration will be performed on
	 * @param trainingImagesFile
	 *            The file that holds the paths to the training images
	 * @param trainingWarpFile
	 *            The file that holds the paths to the training warps
	 * @param levels
	 *            The number of levels within the image and warp pyramid
	 */
	static void autoWarp(@NonNull final BufferedImage sourceImage, @NonNull final BufferedImage targetImage,
			@NonNull final File trainingImagesFile, @NonNull final File trainingWarpFile, final int levels)
	{
		/*
		 * Set number of levels in the pyramid.
		 */
		PYRAMID_LEVELS = levels;

		/*
		 * We create the downsampled version of the images needed for the fully
		 * connected layer by creating an image pyramid
		 */
		ImagePyramid sourceImagePyramid = new ImagePyramid(sourceImage, levels);
		ImagePyramid targetImagePyramid = new ImagePyramid(targetImage, levels);

		/*
		 * Size of the input to the neural network is equal to the lowest level
		 * of the pyramids * 2.
		 */
		int nnInputSize = (sourceImagePyramid.getLowestLevelImage().getHeight()
				* sourceImagePyramid.getLowestLevelImage().getWidth()) * 2;

		/*
		 * Size of the hidden layer is the same as the input size
		 */
		int nnHiddenLayerSize = nnInputSize;

		/*
		 * The output is the x y shift between each of the pixel. This is also
		 * the same as the input size.
		 */
		int nnOutputSize = nnInputSize;

		/*
		 * This fully connected layer warp is used to predict the shift between
		 * two images at a very downsampled version of the images.
		 */
		FullyConnectedLayerWarp fullyConnectedLayer = new FullyConnectedLayerWarp(nnInputSize, nnHiddenLayerSize,
				nnOutputSize, trainingImagesFile, trainingWarpFile);

		/*
		 * Setup the basic neural network parameters and train the network
		 */
		fullyConnectedLayer.setUpNeuralNetwork(0.05, 0.7, 500);
		fullyConnectedLayer.trainNetwork();

		/*
		 * Predict the warp from the input images at the lowest level for
		 * performance.
		 */
		double[][][] xAndYPredictedWarp = fullyConnectedLayer.generateWarp(sourceImagePyramid.getLowestLevelImage(),
				targetImagePyramid.getLowestLevelImage());

		/*
		 * Separate out the x and the y warp into their own 2D arrays.
		 */
		double[][] predictedXWarp = xAndYPredictedWarp[0];
		double[][] predictedYWarp = xAndYPredictedWarp[1];

		/*
		 * Create a warp pyramid based from the output from the artificial
		 * neural network for both the x shift and the y shift.
		 */
		WarpPyramid xWarpPyramid = new WarpPyramid(predictedXWarp, sourceImagePyramid.getImageSizes());
		WarpPyramid yWarpPyramid = new WarpPyramid(predictedYWarp, targetImagePyramid.getImageSizes());

		/*
		 * Now that we have an initial x and y warp pyramid, we can apply it to
		 * each level of the image pyramids but it probably wont be very good!
		 * 
		 * The next part is to create an artificial neural network pyramid that
		 * corresponds to the sizes of the warp and image pyramid. This neural
		 * network pyramid will be trained using a series of image patches
		 * derived from the warped image, and the ground truth from out training
		 * data. The aim of the neural network pyramid is to determine how far
		 * away a pixel is from its desired warp location and take the
		 * difference away. This will create an updated warp that should be
		 * closer to the desired warp. We can then recursively train and update
		 * each level of the pyramid with an updated warp till we reach the
		 * original sized image, and should hopefully produce a reasonably
		 * registered image.
		 */

		/*
		 * Start by creating an ideal patch size which is 7 x 7 pixels.
		 */
		Dimension patchSize = new Dimension(7, 7);

		/*
		 * Some of the images in the pyramid might be downsampled too much to
		 * create patches 7 x 7 pixels if the images are only 6 x 4 say. In
		 * order to avoid this we can find out which images at which level are
		 * less than 49 pixels and start the patch generation at a higher level.
		 * 
		 * Start this by getting the image sizes from one of the pyramids.
		 */
		ArrayList<Size> sizes = targetImagePyramid.getImageSizes();

		/*
		 * numberOfLevelsUnsutiableForPatches will be used to indicate how many
		 * levels of the pyramids are too small for 7 x 7 patches to be derived
		 * from.
		 */
		int numberOfLevelsUnsutiableForPatches = 0;

		/*
		 * Loop through each of the sizes and count how many sizes are too small
		 * for 7 x 7 patches to be derived from.
		 */
		for (Size currentSize : sizes)
		{
			if (currentSize.height <= patchSize.getHeight() || currentSize.width <= patchSize.getWidth())
			{
				numberOfLevelsUnsutiableForPatches++;
			}
		}

		/*
		 * The number of suitable levels for training the convolutional layers
		 * pyramid is the (total level - the number of unsuitable levels)
		 */
		int suitableLevels = levels - numberOfLevelsUnsutiableForPatches;

		/*
		 * Create the convolutional neural network pyramid with the number of
		 * suitable levels as the number of levels in the pyramid.
		 */
		ConvolutionLayerPyramid convolutionalNeuralNetworkPyramid = new ConvolutionLayerPyramid(suitableLevels + 1);

		/*
		 * Work backwards from the smallest to the largest in each of the
		 * pyramids. Only use levels that are suitable so that patches are NOT
		 * derived for unsuitable levels (those that have images that are less
		 * than 7 x 7 pixels large)
		 */
		for (int i = suitableLevels; i >= 0; i--)
		{
			/*
			 * On the image pyramid and warp pyramid, the largest image is at
			 * position 0 and the smallest is at 7. We're starting from the
			 * smallest and working our way up to the largest.
			 */
			BufferedImage currentSourceImage = sourceImagePyramid.getImageAtIndex(i);
			BufferedImage currentTargetImage = targetImagePyramid.getImageAtIndex(i);
			double[][] warpY = xWarpPyramid.getWarpAtIndex(i);
			double[][] warpX = yWarpPyramid.getWarpAtIndex(i);

			/*
			 * Apply the warp function to the source image
			 */
			BufferedImage warpedSource = ImageManipulator.applyWarpFunctionToImage(currentSourceImage, warpX, warpY);

			/*
			 * Display the image
			 */
			ImageReader.displayImage(warpedSource, "Registed image at index " + i);

			/*
			 * This area of this project is still incomplete. But a lot of the
			 * project does work as we can see from displayed registered image.
			 * See above.
			 * 
			 * A nice analogy is that a car works, even with its wheels missing.
			 * This section is my car's missing wheels.
			 * 
			 * But this section would go something like this.
			 */

			/*
			 * The convolutional neural networks need training with a series of
			 * image patches. The code at moment only works for a single image.
			 * 
			 * convolutionalNeuralNetworkPyramid.
			 * trainConvolutionalLayerFromLevel(i, source [], target [], )
			 */

			/*
			 * Use the convolutional neural network pyramid at this level to
			 * update the current warp to move closer to the target.
			 * 
			 * double[][][] updatedXAndYWarp =
			 * convolutionalNeuralNetworkPyramid.calculateWarpFromLevel(i,
			 * warpedSource, targetImagePyramid.getImageAtIndex(i));
			 * 
			 * double[][] updatedXWarp = updatedXAndYWarp[0]; NEW LINE
			 * double[][] updatedYWarp = updatedXAndYWarp[1];
			 * 
			 * Apply updated warp to image in order to move closer to the target
			 * and create a closer registered image.
			 * 
			 * ImageManipulator.applyWarpFunctionToImage(currentTargetImage,
			 * updatedXWarp, updatedYWarp);
			 */
		}
	}
}
