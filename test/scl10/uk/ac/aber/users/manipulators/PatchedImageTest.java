package scl10.uk.ac.aber.users.manipulators;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.sikuli.script.Pattern;

import scl10.uk.ac.aber.users.readers.ImageReader;
import scl10.uk.ac.aber.users.sikuli.SikuliUtils;

/**
 * A class for testing the functionality of the {@link PatchedImage} class
 * 
 * @author Scott Lockett
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PatchedImageTest
{

	/**
	 * The folder to the original data used the generate the ground truth data
	 * in most cases.
	 */
	private static final File originalDataFolder = new File(".\\resources\\original\\");

	/**
	 * A resources folder where the test images are stored that will be compared
	 * against.
	 */
	private static final File groundTruthImagesFolder = new File(".\\resources\\ground-truth-images\\");

	/**
	 * The test file of the image that will be used for the tests.
	 * 
	 * The reason this specific image chosen was due to the fact that every
	 * patch from this image will always be different. In many images, there is
	 * a large amount of black or white around the sides (such was the case for
	 * the artificial neural network training data) and so if something goes
	 * wrong in the test it might not be detected if it's already been compared
	 * with a large black area. Using an image like this allows for every patch
	 * to be unique across all corners annd allows for a reliable test.
	 * 
	 * Image was taken from
	 * <a href="http://c1.staticflickr.com/6/5348/7146593913_87683d75eb_k.jpg">
	 * this </a>source.
	 * 
	 * This image is licensed under the Attribution-NonCommercial-ShareAlike 2.0
	 * Generic (CC BY-NC-SA 2.0) license.
	 * 
	 * And is attributed to <a href="https://www.flickr.com/photos/eob/"> Tolka
	 * Rover </a>
	 */
	private static final File testFile = new File(originalDataFolder + "\\pollock.jpg");

	/**
	 * Read the image file as a buffered image into the program.
	 */
	private static final BufferedImage testImage = ImageReader.readImageFile(testFile);

	/**
	 * An array of the ground truth images for each patch we're going to be
	 * testing against. Its not feasible to test every single pixel to we're
	 * going to set the top left, top right, bottom left, bottom right and
	 * central pixel locations.
	 */
	private static final Pattern[] patchedGroundTruthPatterns = new Pattern[] {
			new Pattern(groundTruthImagesFolder + "\\pollock-top-left.jpg"),
			new Pattern(groundTruthImagesFolder + "\\pollock-top-right.jpg"),
			new Pattern(groundTruthImagesFolder + "\\pollock-bottom-left.jpg"),
			new Pattern(groundTruthImagesFolder + "\\pollock-bottom-right.jpg"),
			new Pattern(groundTruthImagesFolder + "\\pollock-central.jpg") };

	/**
	 * Create a patched image based on the original test image. This is the
	 * object that will be test.
	 */
	private static final PatchedImage patchedImage = new PatchedImage(testImage);

	/**
	 * The height of the test image
	 */
	private static final int imageHeight = testImage.getHeight();

	/**
	 * The width of the test image
	 */
	private static final int imageWidth = testImage.getWidth();

	/**
	 * A series of points that go outside of the images range. They're the three
	 * points surrounding each corner
	 */
	private static final Point[] pointsOffImage = new Point[] { new Point(-1, 0), new Point(0, -1), new Point(-1, -1),
			new Point(-1, imageWidth), new Point(-1, imageWidth + 1), new Point(0, imageWidth + 1),
			new Point(imageWidth + 1, imageHeight + 1), new Point(imageWidth, imageHeight + 1),
			new Point(imageWidth + 1, imageHeight), new Point(-1, imageHeight), new Point(-1, imageHeight + 1),
			new Point(-1, imageHeight - 1) };

	/**
	 * The patch size the the patched imaged will be.
	 */
	private static final Dimension patch = new Dimension(500, 500);

	/**
	 * Tests the creation of a series of patches derived from the patched image
	 * at certain positions. These positions are the top left, top right, bottom
	 * left, bottom right and central.
	 * 
	 * The position used within this test are also a method of stress testing by
	 * choosing patches from the very edges of the image.
	 * 
	 * This could probably be broken down into much smaller tests for each
	 * position.
	 */
	@Test
	public void imagePatchsFromValidCentralPixelsTest()
	{
		Point[] positions = new Point[5];

		/*
		 * Top left
		 */
		positions[0] = new Point(0, 0);

		/*
		 * Top right
		 */
		positions[1] = new Point(imageWidth, 0);

		/*
		 * Bottom left
		 */
		positions[2] = new Point(0, imageHeight);

		/*
		 * bottom right
		 */
		positions[3] = new Point(imageWidth, imageHeight);

		/*
		 * central
		 */
		positions[4] = new Point(imageWidth / 2, imageHeight / 2);

		/*
		 * Loop over each position and derive the patch. Use Sikuli to assert
		 * that the image patch we're testing matches that of the ground truth
		 * image patches.
		 */
		for (int i = 0; i < positions.length; i++)
		{
			BufferedImage imagePatch = patchedImage.getImagePatchFromCentralPixel(positions[i], patch);
			SikuliUtils.assertImages(imagePatch, patchedGroundTruthPatterns[i]);
		}
	}

	/**
	 * Tests that a series of positions outside of the images range and ensures
	 * that {@link ArrayIndexOutOfBoundsException} are thrown
	 */
	@Test
	public void imagePatchsFromInvalidCentralPixelsTest()
	{
		/*
		 * Becomes true once the exception has been thrown
		 */
		boolean thrown = false;

		/*
		 * Loop through all of the points
		 */
		for (Point point : pointsOffImage)
		{
			try
			{
				patchedImage.getImagePatchFromCentralPixel(point, patch);
			} catch (ArrayIndexOutOfBoundsException e)
			{
				/*
				 * Not the best way of testing this. I tried to using the
				 * ExpectedException rule but once the first exception was
				 * thrown it broke out of the loop, and wouldn't test the
				 * others, so I had to use this method instead.
				 */
				thrown = true;
			}
			/*
			 * Assert that the exception has been thrown and reset thrown back
			 * to false for the next point
			 */
			Assert.assertTrue(thrown);
			thrown = false;
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPatchIsTooSmall()
	{
		Dimension patchTooSamll = new Dimension(1, 1);
		Point p = new Point(imageWidth / 2, imageHeight / 2);
		patchedImage.getImagePatchFromCentralPixel(p, patchTooSamll);
	}
}