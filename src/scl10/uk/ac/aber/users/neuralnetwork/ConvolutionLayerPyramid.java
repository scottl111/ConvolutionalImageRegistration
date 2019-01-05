package scl10.uk.ac.aber.users.neuralnetwork;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import lombok.NonNull;

/**
 * <strong>This class is incomplete class and part of the final
 * requirement. </strong> <br>
 * <p>
 * 
 * This class represents a pyramid of convolutional neural network layers in
 * which the tightening of the outputed warp from the initial neural network is
 * done within.
 * 
 * @author Scott Lockett
 *
 */
public class ConvolutionLayerPyramid
{

	/**
	 * A series of convolution layers that represent the convolutional neural
	 * network pyramid.
	 */
	private final List<ConvolutionLayer> convolutionLayerList = new ArrayList<>();

	/**
	 * Creates a new convolutional neural network pyramid, with a series of
	 * levels.
	 *
	 * @param levels
	 *            The number of levels within the neural network pyramid.
	 */
	public ConvolutionLayerPyramid(final int levels)
	{
		for (int i = 0; i < levels; i++)
		{
			/*
			 * Create the CNN pyramid by creating a series of convolution layers
			 */
			ConvolutionLayer convolutionLayer = new ConvolutionLayer();

			/*
			 * Add this convolution layer onto the convolution pyramid
			 */
			convolutionLayerList.add(convolutionLayer);
		}

	}

	/**
	 * Using the given pyramid level, the convolutional layer will create a
	 * series of images patches and derives an improved warp based on patches
	 * 
	 * @param pyramidLevel
	 *            The level in the warp pyramid that the predicted warp is to be
	 *            generated for.
	 * @param source
	 *            The source image. cannot be null.
	 * @param target
	 *            The target image. cannot be null.
	 * @return Both the x and y updated warp predicted from the convolutional
	 *         neural network at that level of the pyramid.
	 */
	public double[][][] calculateWarpFromLevel(final int pyramidLevel, @NonNull final BufferedImage source,
			@NonNull final BufferedImage target)
	{
		/*
		 * Get the level of the neural network that we're going to be passing
		 * the testing data to
		 */
		ConvolutionLayer layer = convolutionLayerList.get(pyramidLevel);

		/*
		 * Test the convolution layer with the source and the target image and
		 * return the input that will the updated warp for that level of the
		 * pyramid
		 */
		return layer.calculate(source, target);
	}

	/**
	 * Train a convolutional layer within the pyramid
	 * 
	 * @param pyramidLevel
	 *            The level of the pyramid in which the training will occur on.
	 * @param sourceImages
	 *            The sourceImages that will be used as training data for the
	 *            network
	 * @param tagretImages
	 *            The targetImages that will be used as training data for the
	 *            network
	 * @param warps
	 *            The x and Y warps for each of the training images
	 */
	public void trainConvolutionalLayerFromLevel(final int pyramidLevel, @NonNull final BufferedImage[] sourceImages,
			final BufferedImage[] tagretImages, @NonNull final double[][][][] warps)
	{
		/*
		 * Get the level of the neural network that we're going to be passing
		 * the testing data to
		 */
		ConvolutionLayer layer = convolutionLayerList.get(pyramidLevel);

		/*
		 * Train the network with the images and the warps.
		 */
		for (int i = 0; i < sourceImages.length; i++)
		{
			/*
			 * Get the x and y warps from the array of x and y warps
			 */
			double[][] xWarp = warps[i][0];
			double[][] yWarp = warps[i][1];

			/*
			 * Train the network
			 */
			layer.trainNetwork(sourceImages[i], tagretImages[i], xWarp, yWarp);
		}
	}
}
