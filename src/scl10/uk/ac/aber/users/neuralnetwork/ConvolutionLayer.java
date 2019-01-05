package scl10.uk.ac.aber.users.neuralnetwork;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;

import org.apache.commons.lang3.ArrayUtils;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.util.data.norm.Normalizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import scl10.uk.ac.aber.users.manipulators.PatchedImage;
import scl10.uk.ac.aber.users.readers.ImageReader;

/**
 * <strong> This class is incomplete </strong>
 * <p>
 * 
 * This class represents the concepts of convolution layer within a neural
 * network.
 * 
 * @author Scott Lockett
 *
 */
class ConvolutionLayer extends FullyConnectedLayer
{

	/**
	 * The CNN layer's input is trained using a set patch size that is (7 x 7 =)
	 * 49 pixels. Two flattened patches are used to train and test the CNN so it
	 * will always be a set size.
	 */
	private static final int CNN_INPUT_SIZE = 49 * 2;

	/**
	 * This hidden layer will be the same size as the input layer.
	 */
	private static final int CNN_HIDDEN_LAYER_SIZE = CNN_INPUT_SIZE / 2;

	/**
	 * The CNN layer's output is trained using the ground truth x and y shift
	 * between the central pixel. This will also always be a set size.
	 */
	private static final int CNN_OUTPUT_SIZE = 2;

	/**
	 * Logger for logging user information
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ConvolutionLayer.class);

	/**
	 * Creates a new convolution layer.
	 */
	public ConvolutionLayer()
	{
		/*
		 * Create the multi layer perceptron with an input size of 49 * 2, a
		 * hidden layer size of 49 * 2 and an output of 2. The input size is
		 * going to be the size of the patch which is always going to be 7 x 7 =
		 * 49 * 2 as we're using two flattened patches. The hidden layer we'll
		 * say is the same size as the input and the output will be an x and y
		 * shift, for the central pixel of that patch which will be 2.
		 */
		mlp = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, CNN_INPUT_SIZE, CNN_HIDDEN_LAYER_SIZE,
				CNN_OUTPUT_SIZE);

		/*
		 * Set the training input and output size
		 */
		trainingData = new DataSet(ConvolutionLayer.CNN_INPUT_SIZE, ConvolutionLayer.CNN_OUTPUT_SIZE);

		/*
		 * Set up some basic learning parameters
		 */
		setUpNeuralNetwork(0.05, 0.7, 50);
	}

	/**
	 * Trains the convolution layer with two images broken down into patches.
	 * The patch is used as input into the convolution layer. The central pixel
	 * from each patch has a x and y shift used as ground truth. This ground
	 * truth is used as the output from the neural network.
	 * 
	 * @param source
	 *            The source image. cannot be null.
	 * @param target
	 *            The target image. cannot be null.
	 * @param xWarp
	 *            The ground truth x warp between the target and source image.
	 *            cannot be null.
	 * @param yWarp
	 *            The ground truth y warp between the target and source image.
	 *            cannot be null.
	 */
	public void trainNetwork(@NonNull final BufferedImage source, @NonNull final BufferedImage target,
			@NonNull final double[][] xWarp, @NonNull final double[][] yWarp)
	{
		/*
		 * Create some new Patched Image objects from which patches can be
		 * derived from without throwing any array out of bounds exceptions.
		 */
		PatchedImage sourcePatchedImage = new PatchedImage(source);
		PatchedImage targetPatchedImage = new PatchedImage(target);

		/*
		 * Create a patch size 7 x 7
		 */
		Dimension patchSize = new Dimension(7, 7);

		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				/*
				 * Create a point from the position we're currently on.
				 */
				Point centralPoint = new Point(x, y);

				/*
				 * Get patches from both the source and target images from the
				 * central pixel.
				 */
				BufferedImage sourcePatch = sourcePatchedImage.getImagePatchFromCentralPixel(centralPoint, patchSize);
				BufferedImage targetPatch = targetPatchedImage.getImagePatchFromCentralPixel(centralPoint, patchSize);

				/*
				 * Flatten both the source patch and target patch into flat
				 * arrays.
				 */
				double[] flatSourcePatch = new ImageReader(sourcePatch).getAsFlatArray();
				double[] flatTargetPatch = new ImageReader(targetPatch).getAsFlatArray();

				/*
				 * Combine the patches into a single array
				 */
				double[] flatSourceAndTargetPatches = ArrayUtils.addAll(flatSourcePatch, flatTargetPatch);

				/*
				 * Grab the shift as the ground truth between the central pixel
				 * from both the x and y warp files
				 */
				double[] groundTruth = new double[] { xWarp[y][x], yWarp[y][x] };

				/*
				 * Add a new piece of training data to the artificial neural
				 * network consisting of the flattened patches of both the
				 * target and the source image as the input, and the output
				 * being the ground truth shift between the central pixel in
				 * both the x and y.
				 */
				trainingData.add(new DataSetRow(flatSourceAndTargetPatches, groundTruth));
			}
		}

		/*
		 * Normalise the data so it's values are between 0 - 1 so it can be used
		 * in the neural network.
		 */
		Normalizer warpImgNormaliser = new ImageWarpNormalizer();
		warpImgNormaliser.normalize(trainingData);

		LOGGER.info("About to learn convolution layer");

		/*
		 * Once all of the patch data has been added, start leaning
		 */
		mlp.learn(trainingData);
	}

	/**
	 * Tests the convolutional neural network with the two images. The images
	 * are broken into patches in order to create a predicted warp.
	 * 
	 * @param source
	 *            The source image. cannot be null.
	 * @param target
	 *            The target image. cannot be null.
	 * @return a 3D array of doubles that represent the x warp and y warp output
	 *         from the convolution layer.
	 */
	public double[][][] calculate(@NonNull final BufferedImage source, @NonNull final BufferedImage target)
	{
		/*
		 * Create an x and y warp the size of the images
		 */
		double[][] xWarp = new double[source.getHeight()][source.getWidth()];
		double[][] yWarp = new double[source.getHeight()][source.getWidth()];

		/*
		 * Create a patched image objects from the target and source image from
		 * which patches can be derived.
		 */
		PatchedImage patchedSourceImage = new PatchedImage(source);
		PatchedImage patchedTargetImage = new PatchedImage(target);

		/*
		 * Create the patch size we're going to be getting from the images.
		 */
		Dimension patchSize = new Dimension(7, 7);

		/*
		 * Loop through every pixel creating a patch from the source and target
		 * images
		 */
		for (int y = 0; y < source.getHeight(); y++)
		{
			for (int x = 0; x < source.getWidth(); x++)
			{
				/*
				 * Create a point for this current position
				 */
				Point currentPoint = new Point(x, y);

				/*
				 * Get a patch from the central point from both images.
				 */
				BufferedImage sourcePatch = patchedSourceImage.getImagePatchFromCentralPixel(currentPoint, patchSize);
				BufferedImage targetPatch = patchedTargetImage.getImagePatchFromCentralPixel(currentPoint, patchSize);

				/*
				 * Flatten both the source patch and target patch into flat
				 * arrays.
				 */
				double[] flatSourcePatch = new ImageReader(sourcePatch).getAsFlatArray();
				double[] flatTargetPatch = new ImageReader(targetPatch).getAsFlatArray();

				/*
				 * Combine both the flattened patches into a single array
				 */
				double[] bothFlattenedPatches = ArrayUtils.addAll(flatSourcePatch, flatTargetPatch);

				/*
				 * Set the input as the test data and calculate
				 */
				mlp.setInput(bothFlattenedPatches);
				mlp.calculate();

				/*
				 * Get the output from the neural network
				 */
				double[] cnnLayerOutput = mlp.getOutput();

				/*
				 * Set the x warp and the y warp at this position to the output
				 * from the neural network
				 */
				xWarp[y][x] = cnnLayerOutput[0];
				yWarp[y][x] = cnnLayerOutput[1];
			}
		}

		/*
		 * Once the x and y warps have been populated, put them into a 3D array
		 * to be returned
		 */
		return new double[][][] { xWarp, yWarp };
	}

}
