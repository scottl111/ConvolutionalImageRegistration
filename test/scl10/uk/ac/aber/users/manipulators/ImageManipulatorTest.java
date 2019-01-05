package scl10.uk.ac.aber.users.manipulators;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.opencv.core.Size;
import org.sikuli.script.Pattern;

import lombok.NonNull;
import scl10.uk.ac.aber.users.readers.ImageReader;
import scl10.uk.ac.aber.users.readers.WarpReader;
import scl10.uk.ac.aber.users.sikuli.SikuliUtils;

/**
 * A class used for testing the functionality the {@link ImageManipulator}
 * class.
 * 
 * @author Scott Lockett
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ImageManipulatorTest
{

	/*
	 * FOLDERS
	 */
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

	/*
	 * ORIGINAL FILES
	 */
	/**
	 * A file that will be used as the test data
	 */
	private static final File fileFemaleOne = new File(originalDataFolder + "\\female1.jpg");

	/**
	 * The read test image of female1 from the above file read as a buffered
	 * image.
	 */
	private static final BufferedImage imageFemaleOne = ImageReader.readImageFile(fileFemaleOne);

	/**
	 * The x warp file to apply to female 1 to register it to female 2
	 */
	private static final File xWarpFile = new File(originalDataFolder + "\\f1f2X.txt");

	/**
	 * The y warp file to apply to female 1 to register it to female 2
	 */
	private static final File yWarpFile = new File(originalDataFolder + "\\f1f2Y.txt");

	/**
	 * The read shift values from the x warp file to register female 1 to female
	 * 2
	 */
	private static final double[][] xWarp = WarpReader.readWarpFile(xWarpFile);

	/**
	 * The read shift values from the y warp file to register female 1 to female
	 * 2
	 */
	private static final double[][] yWarp = WarpReader.readWarpFile(yWarpFile);

	/*
	 * GROUND TRUTH FILES
	 */
	/**
	 * A downsampled version of the test image used as the ground truth to
	 * compare against.
	 */
	private static final File downsampledFile = new File(groundTruthImagesFolder + "\\female1-downsampled.jpg");

	/**
	 * An upsampled version of the test image used as the ground truth to
	 * compare against.
	 */
	private static final File upsampledFile = new File(groundTruthImagesFolder + "\\female1-upsampled.jpg");

	/**
	 * A series of version of the test image flipped along difference axis.
	 * These are the ground truth for the flipping test.
	 */
	private static final File[] flippedFiles = new File[] { new File(groundTruthImagesFolder + "\\female1-flip--1.jpg"),
			new File(groundTruthImagesFolder + "\\female1-flip-0.jpg"),
			new File(groundTruthImagesFolder + "\\female1-flip-1.jpg") };

	/*
	 * SIKULI FILES AND PATTERNS
	 */
	/**
	 * A file that will be used as a piece of test data. This file is an image
	 * that is 50% red and 50% yellow.
	 */
	private static final File fileRedAndYellow = new File(originalDataFolder + "\\redAndYellow.jpg");

	/**
	 * The above file read as a buffered image
	 */
	private static final BufferedImage imageRedAndYellow = ImageReader.readImageFile(fileRedAndYellow);

	/**
	 * A pattern of the file that is the average colouration of the red and
	 * yellow image. Red + Yellow = Orange.
	 */
	private static final Pattern orangePattern = new Pattern(groundTruthImagesFolder + "\\orange.jpg");

	/**
	 * A pattern of the file that is the grey scale equivalent
	 */
	private static final Pattern greyScalePattern = new Pattern(groundTruthImagesFolder + "\\greyscale-female1.jpg");

	/**
	 * A pattern of the register image of female 1 regitered to female 2
	 */
	private static final Pattern imageRegisteredFemale1To2 = new Pattern(groundTruthImagesFolder + "\\female1to2.jpg");

	/**
	 * Test the image manipulator's downsampling method.
	 */
	@Test
	public void testImageDownsampling()
	{
		/*
		 * Downsample the test image
		 */
		BufferedImage downsampledTestImage = ImageManipulator.downsample(imageFemaleOne);

		/*
		 * Read the true downsampled image from file.
		 */
		BufferedImage downsampledImage = ImageReader.readImageFile(downsampledFile);

		/*
		 * Assert they are the same
		 */
		assertImages(downsampledTestImage, downsampledImage);
	}

	/**
	 * Test the image manipulator's upsampling method.
	 */
	@Test
	public void testImageUpsampling()
	{

		Size newSize = new Size(imageFemaleOne.getWidth() * 2, imageFemaleOne.getHeight() * 2);
		/*
		 * Downsample the test image
		 */
		BufferedImage upsampledTestImage = ImageManipulator.upsample(imageFemaleOne, newSize);

		/*
		 * Read the ground truth upsampled image from file.
		 */
		BufferedImage upsampledImage = ImageReader.readImageFile(upsampledFile);

		/*
		 * Assert they are the same
		 */
		assertImages(upsampledTestImage, upsampledImage);
	}

	/**
	 * Tests that an image can be correctly flipped/ reflected.
	 */
	@Test
	public void testFlippingImage()
	{
		/*
		 * Loop 3 times [-1, 0 1] as a way of applying the flip direction to an
		 * image.
		 */
		for (int i = -1; i < 2; i++)
		{

			/*
			 * Read the ground truth image that we're going to be comparing to
			 * from file. Read from flippedFiles array [0, 1, 2] not [-1, 0, 1]
			 * hence the + 1.
			 */
			BufferedImage readFlippedImage = ImageReader.readImageFile(flippedFiles[i + 1]);

			/*
			 * Perform the flip on the test image
			 */
			BufferedImage flippedImage = ImageManipulator.flipImage(imageFemaleOne, i);

			/*
			 * Assert that both flipped images are the same.
			 */
			assertImages(readFlippedImage, flippedImage);
		}
	}

	/**
	 * This test ensures that given a half red and half yellow image (primary
	 * colours) a average colour is taken of the two (orange) and will be
	 * returned the original images size.
	 * 
	 * This test makes use of the Sikuli library as a proof of concept as to its
	 * use within JUnit testing. Sikuli was chosen as an improved method of
	 * image assertion than that of my
	 * {@link ImageManipulatorTest#assertImages(BufferedImage, BufferedImage)}
	 * method. This is because Sikuli uses OpenCV's pattern matching methods and
	 * fuzzy logical in order to find images within a screen as opposed to using
	 * a very large delta which was my original way.
	 */
	@Test
	public void testAveragedColouredImageFromImage()
	{
		/*
		 * Run the method that we're testing on the test image. Given a half red
		 * and half yellow image this method should return an averaged of the
		 * two being an orange image
		 */
		BufferedImage averagedImage = ImageManipulator.createAveragedColouredImageFromImage(imageRedAndYellow);

		/*
		 * Assert the result of the test method image with the ground truth -
		 * the orange image/ pattern.
		 */
		SikuliUtils.assertImages(averagedImage, orangePattern);
	}

	/**
	 * This test ensures that given a half red and half yellow image (primary
	 * colours) a average colour is taken of the two (orange) and will be
	 * returned the original images size.
	 * 
	 * This test makes use of the Sikuli library as a proof of concept as to its
	 * use within JUnit testing. Sikuli was chosen as an improved method of
	 * image assertion than that of my
	 * {@link ImageManipulatorTest#assertImages(BufferedImage, BufferedImage)}
	 * method. This is because Sikuli uses OpenCV's pattern matching methods and
	 * fuzzy logical in order to find images within a screen as opposed to using
	 * a very large delta which was my original way.
	 */
	@Test
	public void testConvertingColoredImageToGreyScaleImage()
	{
		/*
		 * Run the method that we're testing on the test image. Given a coloured
		 * image this method should return a grey scale image
		 */
		BufferedImage greyScaleImage = ImageManipulator.convertColorImageToGreyscale(imageFemaleOne);

		/*
		 * Assert the result of the test method image with the ground truth -
		 * the grey scale image/ pattern.
		 */
		SikuliUtils.assertImages(greyScaleImage, greyScalePattern);
	}

	/**
	 * Tests the writing of an image file to disk
	 */
	@Test
	public void writeImageToDiskTest()
	{

		/*
		 * Create a temp folder for the files
		 */
		File tempFolder = new File(".\\resources\\temp\\");

		/*
		 * Create it if it does not exist
		 */
		if (!tempFolder.exists())
		{
			tempFolder.mkdir();
		}

		/*
		 * Create a file to write to and write it!
		 */
		File fileToWriteTo = new File(tempFolder + "\\testImage.jpg");
		boolean resultOfWriting = ImageManipulator.writeImageFile(fileToWriteTo, imageFemaleOne);

		/*
		 * This assertion should be done with a Sikuli test really.
		 */
		Assert.assertTrue(resultOfWriting);

		/*
		 * Clean up, and ensure they're deleted correctly.
		 */
		Assert.assertTrue(fileToWriteTo.delete() && tempFolder.delete());
	}

	/**
	 * Tests the application of applying a warp function to an image to register
	 * it
	 */
	@Test
	public void applyWarpFunctionToImage()
	{
		/*
		 * Try and apply a warp x and y shift to female 1 in order to register
		 * it to female 2
		 */
		BufferedImage hopefulImageRegisteredFemale1To2 = ImageManipulator.applyWarpFunctionToImage(imageFemaleOne,
				xWarp, yWarp);

		/*
		 * Test that it has applied the warps correctly.
		 * 
		 * NOTE
		 *
		 * The images are slight different in that the original data handles
		 * areas that have no pixels different than how I handle it. My method
		 * takes an average of the colour of the whole image and uses that
		 * whereas the original images used the pixels around the edges and
		 * protruded them outwards till the end of the image. The test does pass
		 * though as the find method has a default similarity of 0.7
		 * 
		 * See here http://doc.sikuli.org/region.html#Region.find
		 * 
		 */
		SikuliUtils.assertImages(hopefulImageRegisteredFemale1To2, imageRegisteredFemale1To2);
	}

	/**
	 * Test to ensure that if the sizes of each of the warps and the image are
	 * not the same, then an illegal argument exception is thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public void applyWarpFunctionToTestWithDifferentSizedParameters()
	{
		/*
		 * Buffered image is female one
		 */
		BufferedImage image = imageFemaleOne;

		/*
		 * Create an x double array 4 x 4
		 */
		double[][] x = new double[][] { { 10, 40, 10, 20 }, { 10, 40, 10, 20 }, { 10, 40, 10, 20 },
				{ 10, 40, 10, 20 } };
		/*
		 * And a y double array 3 x 3
		 */
		double[][] y = new double[][] { { 10, 40, 10 }, { 10, 40, 10 }, { 10, 40, 10 } };

		/*
		 * Try to apply the different sized warps to a different sizes image. An
		 * IllegalArgumentException should be thrown.
		 */
		ImageManipulator.applyWarpFunctionToImage(image, x, y);
	}

	/**
	 * Method used for asserting that each pixel within two images are within a
	 * certain range of +/- 25 in each channel. If not then the assertion will
	 * fail.
	 * 
	 * @param image1
	 *            The first image to asserted as being the same as the second
	 *            image
	 * @param image2
	 *            The second image to asserted as being the same as the first
	 *            image
	 */
	private void assertImages(@NonNull final BufferedImage image1, @NonNull final BufferedImage image2)
	{

		/*
		 * Start the assertion by checking that both of the images are the same
		 * size. 1) If, they're not the same size, then they can't be equal 2)
		 * To prevent an ArrayPositionOutOfBoundsException while looping through
		 * the pixels getting the RGB values.
		 */
		Assert.assertTrue(image1.getHeight() == image2.getHeight() && image1.getWidth() == image2.getWidth());

		for (int i = 0; i < image1.getHeight(); i++)
		{
			for (int j = 0; j < image1.getWidth(); j++)
			{
				/*
				 * Create colour objects based on the RGB from the current pixel
				 * from each image.
				 */
				Color imgOneColor = new Color(image1.getRGB(j, i));
				Color imgTwoColor = new Color(image2.getRGB(j, i));

				/*
				 * Compare each pixels channel individually within both images
				 * with a delta of 25. This size delta is due to an
				 * inconsistency in the processing of the images.
				 */
				Assert.assertEquals(imgOneColor.getBlue(), imgTwoColor.getBlue(), 25);
				Assert.assertEquals(imgOneColor.getGreen(), imgTwoColor.getGreen(), 25);
				Assert.assertEquals(imgOneColor.getRed(), imgTwoColor.getRed(), 25);
			}
		}
	}

}