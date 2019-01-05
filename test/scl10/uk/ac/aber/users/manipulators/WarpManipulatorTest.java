package scl10.uk.ac.aber.users.manipulators;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.opencv.core.Size;

import scl10.uk.ac.aber.users.readers.WarpReader;

/**
 * A test class used for testing the functionality of the
 * {@link WarpManipulator} class.
 * 
 * @author Scott Lockett
 *
 */
public class WarpManipulatorTest
{

	/**
	 * The file that will be used as the test data
	 */
	private static final File testWarpFile = new File(".\\resources\\original\\warp1.txt");

	/**
	 * The test image file above read as a buffered image.
	 */
	private static final double[][] testWarp = WarpReader.readWarpFile(testWarpFile);

	/**
	 * A resources folder where the test images are stored that will be compared
	 * against.
	 */
	private static final File testResourcesFolder = new File(".\\resources\\ground-truth-warps\\");

	/**
	 * The file that holds the ground truth for upsampling the test warp file
	 */
	private static final File upsampledWarpFile = new File(testResourcesFolder + "\\uped-warp.txt");

	/**
	 * The file that holds the ground truth for downsampling the test warp file
	 */
	private static final File downsampledWarpFile = new File(testResourcesFolder + "\\downed-warp.txt");

	/**
	 * Test the functionality of the upsampling of a warp
	 */
	@Test
	public void upsampleWarpTest()
	{
		/*
		 * Create a new size double that of the original warp file
		 */
		Size doubleSize = new Size(testWarp[0].length * 2.0D, testWarp.length * 2.0D);

		/*
		 * Read in the ground truth for the upsampled warp.
		 */
		double[][] groundTruthWarp = WarpReader.readWarpFile(upsampledWarpFile);

		/*
		 * Perform the upsampling on the warp
		 */
		double[][] largerTestWarp = WarpManipulator.upsample(testWarp, doubleSize);

		Assert.assertArrayEquals(groundTruthWarp, largerTestWarp);
	}

	/**
	 * Tests the writing of a warp to the disk
	 */
	@Test
	public void writtingWarpToDiskTest()
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
		File fileToWriteTo = new File(tempFolder + "\\testWarp.txt");
		boolean resultOfWriting = WarpManipulator.writeWarpFile(fileToWriteTo, testWarp);

		/*
		 * Assert that its been written. This should really be read back in and
		 * an assert equals on both the arrays
		 */
		Assert.assertTrue(resultOfWriting);

		/*
		 * Clean up, and ensure deleted correctly
		 */
		Assert.assertTrue(fileToWriteTo.delete() && tempFolder.delete());
	}

	/**
	 * Tests the functionality of the downsampling of a warp
	 */
	@Test
	public void downsampleWarpTest()
	{
		/*
		 * Perform the downsampling on the warp
		 */
		double[][] smallerTestWarp = WarpManipulator.downsample(testWarp);

		/*
		 * Read in the ground truth for the downsampled warp.
		 */
		double[][] groundTruthWarp = WarpReader.readWarpFile(downsampledWarpFile);

		Assert.assertArrayEquals(groundTruthWarp, smallerTestWarp);
	}

}