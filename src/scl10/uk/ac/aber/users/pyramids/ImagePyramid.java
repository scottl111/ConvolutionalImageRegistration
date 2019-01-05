package scl10.uk.ac.aber.users.pyramids;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Size;

import lombok.Getter;
import lombok.NonNull;
import scl10.uk.ac.aber.users.manipulators.ImageManipulator;

/**
 * A class to represent an image pyramid created by downsampling an image a
 * series of times.
 * 
 * @author Scott Lockett
 *
 */
public final class ImagePyramid
{

	/**
	 * The number of the time the image is going to be sampled.
	 */
	private final int PYRAMID_LEVELS;

	/**
	 * The original image which the pyramid is formed from
	 */
	@Getter
	private final BufferedImage originalImage;

	/**
	 * The pyramid of the images at each level
	 */
	private final List<BufferedImage> imagePyramidList = new ArrayList<>();

	/**
	 * The size of each of the sampled images at each layer of the pyramid
	 */
	private final ArrayList<Size> sizePyramid = new ArrayList<>();

	/**
	 * Creates a new image pyramid from the input image.
	 * 
	 * @param img
	 *            The image which the pyramid is going to be formed from. Can
	 *            not be null.
	 * @param levels
	 *            The number of levels within the pyramid
	 */
	public ImagePyramid(@NonNull final BufferedImage img, final int levels)
	{
		/*
		 * Set the original image
		 */
		originalImage = img;

		/*
		 * Set the number of levels within the pyramid
		 */
		PYRAMID_LEVELS = levels;

		/*
		 * Create the pyramid
		 */
		createImagePyramid();
	}

	/**
	 * Creates the image pyramid by downsampeling the original input image
	 * {@link ImagePyramid#PYRAMID_LEVELS} times.
	 */
	private void createImagePyramid()
	{
		/*
		 * Add the original image to the bottom level of the pyramid and also
		 * add the image size to the size pyramid
		 */
		imagePyramidList.add(originalImage);
		sizePyramid.add(new Size(originalImage.getWidth(), originalImage.getHeight()));

		/*
		 * Loop through each image and down sample and add it the image pyramid
		 * and also create a pyramid for the size of each of the images.
		 */
		for (int i = 0; i < PYRAMID_LEVELS; i++)
		{
			/*
			 * Get the current image from the pyramid
			 */
			BufferedImage currentImage = imagePyramidList.get(i);

			/*
			 * Downsample the image using the image operator
			 */
			BufferedImage smallerImg = ImageManipulator.downsample(currentImage);

			/*
			 * Add the smaller image to the image pyramid
			 */
			imagePyramidList.add(smallerImg);

			/*
			 * Create a new size object which is the size of the recently
			 * downsampled image.
			 */
			Size smallerImageSize = new Size(smallerImg.getWidth(), smallerImg.getHeight());

			/*
			 * Add the size of the smaller image to the image sizes pyramid.
			 */
			sizePyramid.add(smallerImageSize);
		}
	}

	/**
	 * Get all of the sizes of each of the images within the pyramid
	 * 
	 * @return an arraylist of sizes of each image in the pyramid.
	 */
	public ArrayList<Size> getImageSizes()
	{
		return sizePyramid;
	}

	/**
	 * Getter for the image at a given index within the image pyramid.
	 * 
	 * @param index
	 *            The position of the image in the pyramid which is required
	 * @return The image at the position within the image pyramid.
	 */
	public BufferedImage getImageAtIndex(final int index)
	{
		return imagePyramidList.get(index);
	}

	/**
	 * Getter to return the smallest image of the pyramid
	 * 
	 * @return the smallest image in the pyramid
	 */
	public BufferedImage getLowestLevelImage()
	{
		return imagePyramidList.get(imagePyramidList.size() - 1);
	}

}
