package scl10.uk.ac.aber.users.manipulators;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;

import lombok.NonNull;

/**
 * A class used to represent an image from which patches can be derived from.
 * This class creates a reflected image on all sides of the original image which
 * can be used to derive patches from. By reflecting at all sides we ensure that
 * ArrayOutOfBoundsException wont be thrown if the patch goes off the image's
 * area.
 * <p>
 * <strong> Possible improvement </strong>
 * <p>
 * 
 * Instead of creating an image 3 times as large image and then adding the
 * reflected image to it, if the pixels go into the minus then (x position * -1)
 * or (y position * -1) which will also return the reflected pixel.
 * 
 * e.g. reference position (-5, -5) return pixel 5, 5 which is also a reflection
 * of the original image.
 * 
 * @author Scott Lockett
 *
 */
public final class PatchedImage
{
	/**
	 * The image from where the patches will be taken from.
	 */
	private final BufferedImage originalImage;

	/**
	 * A reflected version of the original image from which the patches will be
	 * taken from. A reflected Image is used to ensure that if a patch is taken
	 * from a central pixel and the patch is formed from pixels that are out of
	 * bounds, then the reflected sides are used.
	 */
	private final BufferedImage reflectedImage;

	/*
	 * Create some string literals as the position we're going to be writing any
	 * reflected images too.
	 */

	/**
	 * String literal for "original"
	 */
	private static final String ORIGINAL = "original";

	/**
	 * String literal for "top"
	 */
	private static final String TOP = "top";

	/**
	 * String literal for "bottom"
	 */
	private static final String BOTTOM = "bottom";

	/**
	 * String literal for "left"
	 */
	private static final String LEFT = "left";

	/**
	 * String literal for "right"
	 */
	private static final String RIGHT = "right";

	/**
	 * String literal for "topRight"
	 */
	private static final String TOPRIGHT = "topRight";

	/**
	 * String literal for "topLeft"
	 */
	private static final String TOPLEFT = "topLeft";

	/**
	 * String literal for "bottomLeft"
	 */
	private static final String BOTTOMLEFT = "bottmLeft";

	/**
	 * String literal for "bottomRight"
	 */
	private static final String BOTTOMRIGHT = "bottomRight";

	/**
	 * A concatenation of all of the positions.
	 */
	private static final String[] ALL_POSITIONS = new String[] { ORIGINAL, TOP, BOTTOM, LEFT, RIGHT, TOPRIGHT, TOPLEFT,
			BOTTOMLEFT, BOTTOMRIGHT };

	/**
	 * Creates a new patched image from which patches can be taken from.
	 * 
	 * @param image
	 *            The image from which patches are going to be drawn from. Can
	 *            not be null.
	 */
	public PatchedImage(@NonNull final BufferedImage image)
	{
		/*
		 * Save the original image
		 */
		originalImage = image;

		/*
		 * Generate the reflected image
		 */
		reflectedImage = reflectImage(originalImage);

	}

	/**
	 * Creates a reflected image from all sides of the original image. By
	 * reflecting all of the sides of the image this ensure that if a patch at a
	 * central pixel is required and the patches start position is off image's
	 * range, the values are still associated to the image (as opposed to just
	 * setting it all to black) and will not throw any out of bounds exceptions.
	 * 
	 * @param imageToReflect
	 *            The image which is going to be reflected. cannot be null.
	 * @return An image 3 times as large which holds a reflected version of the
	 *         original image from all sides.
	 */
	private BufferedImage reflectImage(@NonNull final BufferedImage imageToReflect)
	{
		/*
		 * Create a new bufferedImage that is three times larger than the
		 * original image to account for the accumulated mirrored images that
		 * will be added.
		 */
		BufferedImage largerReflectedImage = new BufferedImage(originalImage.getWidth() * 3,
				originalImage.getHeight() * 3, BufferedImage.TYPE_3BYTE_BGR);

		/*
		 * Create a hash map in order to reference the position of the image as
		 * the key and hold the reflected image as the value.
		 */
		HashMap<String, BufferedImage> reflectedImages = new HashMap<>();

		/*
		 * Create a hash map in order to reference the offset where the
		 * reflected images are going to be drawn. The position again the key
		 * and the offset is the value.
		 */
		HashMap<String, Integer[]> offsets = new HashMap<>();

		/*
		 * The image that needs reflecting's width and height
		 */
		int width = imageToReflect.getWidth();
		int height = imageToReflect.getHeight();

		/*
		 * Create an integer array with the offset position between the
		 * quadrants of the larger image and add them to a hash map. The key is
		 * their position in the image, and the value is the x and y offset.
		 */
		Integer[] originalOffset = new Integer[] { width, height };

		Integer[] topOffset = new Integer[] { width, 0 };
		Integer[] bottomOffset = new Integer[] { width, height * 2 };
		Integer[] leftOffset = new Integer[] { 0, height };
		Integer[] rightOffset = new Integer[] { width * 2, height };

		Integer[] topRightOffset = new Integer[] { width * 2, 0 };
		Integer[] bottomLeftOffset = new Integer[] { 0, height * 2 };
		Integer[] topLeftOffset = new Integer[] { 0, 0 };
		Integer[] bottomRightOffset = new Integer[] { width * 2, height * 2 };

		offsets.put(ORIGINAL, originalOffset);

		offsets.put(TOP, topOffset);
		offsets.put(BOTTOM, bottomOffset);
		offsets.put(LEFT, leftOffset);
		offsets.put(RIGHT, rightOffset);

		offsets.put(TOPLEFT, topLeftOffset);
		offsets.put(TOPRIGHT, topRightOffset);
		offsets.put(BOTTOMLEFT, bottomLeftOffset);
		offsets.put(BOTTOMRIGHT, bottomRightOffset);

		/*
		 * Add the reflected images to a hash map. Using their position as the
		 * key and the reflected image as the value.
		 */
		reflectedImages.put(ORIGINAL, originalImage);
		reflectedImages.put(RIGHT, ImageManipulator.flipImage(originalImage, 1));
		reflectedImages.put(LEFT, ImageManipulator.flipImage(originalImage, 1));
		reflectedImages.put(TOP, ImageManipulator.flipImage(originalImage, 0));
		reflectedImages.put(BOTTOM, ImageManipulator.flipImage(originalImage, 0));

		reflectedImages.put(TOPLEFT, ImageManipulator.flipImage(reflectedImages.get(TOP), 1));
		reflectedImages.put(TOPRIGHT, ImageManipulator.flipImage(reflectedImages.get(TOP), 1));
		reflectedImages.put(BOTTOMRIGHT, ImageManipulator.flipImage(reflectedImages.get(BOTTOM), 1));
		reflectedImages.put(BOTTOMLEFT, ImageManipulator.flipImage(reflectedImages.get(BOTTOM), 1));

		/*
		 * The offset for each position
		 */
		Integer[] offset;

		/*
		 * The image for each position
		 */
		BufferedImage reflection;

		/*
		 * For each position, get the image, get the offset and draw the
		 * reflection into the larger reflected image.
		 */
		for (String position : ALL_POSITIONS)
		{
			reflection = reflectedImages.get(position);
			offset = offsets.get(position);
			drawSubImageintoImage(largerReflectedImage, reflection, offset);
		}

		return largerReflectedImage;
	}

	/**
	 * Writes a smaller image into a larger image at a given x y offset from the
	 * original images x, y. None of the parameter can be null.
	 * 
	 * @param superImage
	 *            The larger image which is going to have the smaller image
	 *            written into.
	 * @param subImage
	 *            The smaller image which is going to be written into the larger
	 *            image.
	 * @param offset
	 *            The offset where the start of the image is going to be drawn
	 *            from.
	 * @return The superImage with the subImage written into at the position the
	 *         offset indicated.
	 */
	private BufferedImage drawSubImageintoImage(@NonNull final BufferedImage superImage,
			@NonNull final BufferedImage subImage, @NonNull final Integer[] offset)
	{
		/*
		 * Loop over the width and height of the image to be drawn
		 */
		for (int i = 0; i < subImage.getWidth(); i++)
		{
			for (int j = 0; j < subImage.getHeight(); j++)
			{
				/*
				 * Write the reflected image into its designated area by of
				 * offset
				 */
				superImage.setRGB(i + offset[0], j + offset[1], subImage.getRGB(i, j));
			}
		}

		return superImage;
	}

	/**
	 * Create a patch of a given size from a given central pixel. If the patch
	 * goes out of the images bounds, then the out of bounds area will be
	 * replaced with a reflection from the side of the image where the out of
	 * bounds has occurred. None of the parameter can be null.
	 * 
	 * @param centralPixel
	 *            The central pixel from where the patch will be created from
	 * @param patchSize
	 *            The size of the patch
	 * @return A patch from the original image at the given size. If the patch
	 *         goes out of the images bounds then the corners are reflected.
	 */
	public BufferedImage getImagePatchFromCentralPixel(@NonNull final Point centralPixel,
			@NonNull final Dimension patchSize)
	{
		/*
		 * Ensure that we're keeping to a position within the original image's
		 * co-ordinate space, as really the patches shouldn't be aware that
		 * there is a reflection happening or that the image we're getting the
		 * patch from is larger than the original image.
		 */
		if (centralPixel.getX() > originalImage.getWidth() || centralPixel.getX() < 0)
		{
			throw new ArrayIndexOutOfBoundsException(
					"Can not reference " + (int) centralPixel.getX() + ", " + (int) centralPixel.getY());
		}

		if (centralPixel.getY() > originalImage.getHeight() || centralPixel.getY() < 0)
		{
			throw new ArrayIndexOutOfBoundsException(
					"Can not reference " + (int) centralPixel.getX() + ", " + (int) centralPixel.getY());
		}

		/*
		 * You can't create a patch that's 0 pixels in height or width so check
		 * for this.
		 */
		if (patchSize.getHeight() <= 1 || patchSize.getWidth() <= 1)
		{
			throw new IllegalArgumentException("The patch must be at least 2 pixels by 2 pixels!");
		}

		/*
		 * Add the original images width and height to the central pixel's x and
		 * y to ensure that we're within the original images co-ordinate space
		 * within the reflected image. Just add the offset essentially.
		 * 
		 * As we're working off a central pixel divide the patch's width and
		 * height in order to move back by half to ensure that we're starting
		 * the subimage from the 0, 0 point.
		 * 
		 * Top left corner is h/2 and w/2
		 */
		int startXPosition = (int) (centralPixel.x + originalImage.getWidth() - (Math.round(patchSize.width / 2.0D)));
		int startYPosition = (int) (centralPixel.y + originalImage.getHeight() - (Math.round(patchSize.height / 2.0D)));

		return reflectedImage.getSubimage(startXPosition, startYPosition, (int) patchSize.getWidth(),
				(int) patchSize.getHeight());

	}

}
