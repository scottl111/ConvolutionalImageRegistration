package scl10.uk.ac.aber.users.pyramids;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Size;

import lombok.NonNull;
import scl10.uk.ac.aber.users.manipulators.WarpManipulator;
import scl10.uk.ac.aber.users.neuralnetwork.ImageWarpNormalizer;

/**
 * A class to represent a warp pyramid created by upsampling an 2d array of
 * doubles a series of times.
 * 
 * @author Scott Lockett
 */
public final class WarpPyramid
{

	/**
	 * The number of levels of the pyramid
	 */
	private final int PYRAMID_LEVELS;

	/**
	 * The original warp which is the basis of the pyramid
	 */
	private final double[][] originalWarp;

	/**
	 * The pyramid of the warps at each level
	 */
	private final List<double[][]> warpPyramidList = new ArrayList<>();

	/**
	 * The associated sizes of the pyramid
	 */
	private final ArrayList<Size> warpSizes;

	/**
	 * Creates an up sampled warp pyramid
	 * 
	 * @param warp
	 *            The original warp that is to be the first level of the pyramid
	 * @param imageSizes
	 *            The associated sizes of the images which relate to each warp
	 */
	public WarpPyramid(@NonNull final double[][] warp, @NonNull final List<Size> imageSizes)
	{
		originalWarp = warp;

		/*
		 * It needs a concrete size which it can upsample from else left to its
		 * own devices it does not size properly with the down sampled version.
		 * The whole arrayList of sizes had to be used as the openCV
		 * downsampling used a rounding method which when it got to this side of
		 * the application the number was just doubled and its unknown if if it
		 * was rounded or not. e.g.
		 * 
		 * Downsampling (top down) 25 18, 12 9, 6 4
		 * 
		 * Upsampling (bottom up) 6 4, 12 8, 24 16
		 * 
		 * Even though they both start with 6 and 4, simply doubling does not
		 * find the correct size and the margin of error then snowballs.
		 * 
		 * Bad design here - there's not really high cohesion and low coupling.
		 * Come back to this if there's time.
		 */
		warpSizes = (ArrayList<Size>) imageSizes;

		PYRAMID_LEVELS = imageSizes.size() - 1;

		createWarpPyramid();
	}

	/**
	 * Creates a new warp pyramid. Loops through each of the associated images
	 * sizes and upsamples the warp to image sizes size.
	 */
	private void createWarpPyramid()
	{
		/*
		 * The warp sizes is in the format from largest to smallest so reverse
		 * it as the warp pyramid goes from smallest to largest.
		 */
		Collections.reverse(warpSizes);

		/*
		 * Set the sizes of all the warps from the image sizes
		 */
		for (Size size : warpSizes)
		{
			warpPyramidList.add(new double[(int) size.height][(int) size.width]);
		}

		/*
		 * Un-normalise the output from the artificial neural network
		 */
		double[][] unnormalisedWarp = ImageWarpNormalizer.unnormalize(originalWarp);

		/*
		 * add the original warp file to the pyramid to the lowest level.
		 */
		warpPyramidList.set(0, unnormalisedWarp);

		/*
		 * Apply the upsampling to each level of the pyramid
		 */
		for (int i = 0; i < PYRAMID_LEVELS; i++)
		{

			/*
			 * Perform the upsampling of the warp file
			 */
			double[][] biggerWarp = WarpManipulator.upsample(warpPyramidList.get(i), warpSizes.get(i + 1));

			/*
			 * update warp in the position
			 */
			warpPyramidList.set(i + 1, biggerWarp);
		}

		Collections.reverse(warpPyramidList);
	}

	/**
	 * Gets the warp array from a specified level of the warp pyramid.
	 * 
	 * @param index
	 *            The level of the position of the warp which is required
	 * @return The warp array at that level of the pyramid
	 */
	public double[][] getWarpAtIndex(final int index)
	{
		return warpPyramidList.get(index);
	}

	/**
	 * Get the size of the warp Pyramid
	 * 
	 * @return the number of levels in the warp pyramid
	 */
	public int getPyramidSize()
	{
		return warpPyramidList.size();
	}

}
