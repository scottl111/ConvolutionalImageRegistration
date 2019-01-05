package scl10.uk.ac.aber.users.sikuli;

import java.awt.image.BufferedImage;
import java.io.File;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import junit.framework.Assert;
import lombok.NonNull;
import scl10.uk.ac.aber.users.readers.ImageReader;

/**
 * A class for Sikuli based utility operations such as asserting images or
 * closing a current frame.
 * 
 * @author Scott Lockett
 */
public final class SikuliUtils
{
	/**
	 * A resources folder where any sikuli based icons are stored.
	 */
	private static final File SikuliIconsFolder = new File(".\\resources\\sikuli-icons\\");

	/**
	 * The close icon in order to close a Windows based frame.
	 */
	private static final Pattern closeButton = new Pattern(SikuliIconsFolder + "\\close-cross.png");

	/**
	 * Private constructor to hide the implicit one. Ensures class can not be
	 * instantiated.
	 */
	private SikuliUtils()
	{

	}

	/**
	 * Asserts that two images are the same using Sikuli. The test image is
	 * displayed within a JFrame and compared against the actual image.
	 * 
	 * @param hopefulImage
	 *            The image that is hopefully going to be the same as the ground
	 *            truth images. The image we're testing on. cannot be null.
	 * @param actualImage
	 *            The ground truth image that we're comparing against. Can not
	 *            be null.
	 */
	public static void assertImages(@NonNull final BufferedImage hopefulImage, @NonNull final Pattern actualImage)
	{
		/*
		 * Display the hopeful image
		 */
		ImageReader.displayImage(hopefulImage, "Sikuli Test");

		/*
		 * Capture the screen with the hopeful image displayed
		 */
		Screen screen = new Screen();

		try
		{
			/*
			 * Try and find the ground truth image within the captured screen.
			 */
			screen.find(actualImage);

		} catch (FindFailed e)
		{
			/*
			 * If it can't be found, then something has gone wrong and so fail
			 * the test.
			 */
			Assert.fail("Failed to find image within the screen " + actualImage.getFilename());
		} finally
		{
			clickCloseButton();
		}
	}

	/**
	 * Clicks the close button on the focused application. Typically we only
	 * want this to be on the JFrame from the test image displayed.
	 */
	private static void clickCloseButton()
	{
		/*
		 * To ensure that you're not closing a main application like Eclipse, so
		 * reduce the region by 50 pixel in the width to make sure that
		 * Eclipse's close button is out of the way.
		 * 
		 * A better way to do this would be to minimise any open applications
		 * once the test starts, to ensure that only unit test based
		 * applications were open.
		 */
		Region regionWithoutMainApplicationCross = new Screen();
		regionWithoutMainApplicationCross.setW(regionWithoutMainApplicationCross.getW() - 50);

		/*
		 * Try and click the close button
		 */
		try
		{
			regionWithoutMainApplicationCross.click(closeButton, 0);
		} catch (FindFailed e)
		{
			/*
			 * Fail the test if the close button can't be found as the JFrame
			 * containing the image hasn't been shown correctly.
			 */
			Assert.fail("The close button has failed to be found.");
		}
	}
}
