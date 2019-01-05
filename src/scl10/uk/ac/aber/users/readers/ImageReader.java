package scl10.uk.ac.aber.users.readers;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.NonNull;

/**
 * An immutable class for the reading images and parsing the pixel values of
 * image data.
 * 
 * @author Scott Lockett
 */
public final class ImageReader
{

	/**
	 * Logger for logging information
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageReader.class);

	/**
	 * The associated image as a 2 dimensional array of pixel values
	 */
	@Getter
	private final double[][] as2DArray;

	/**
	 * The associated image as a flat/ single array of pixel values
	 */
	@Getter
	private final double[] asFlatArray;

	/**
	 * The image that has been read in from a file
	 */
	@Getter
	private final BufferedImage img;

	/**
	 * Creates a new reader based on an already read buffered image.
	 * 
	 * @param image
	 *            The Buffered image which is to be associated with this reader
	 *            object. cannot be null.
	 */
	public ImageReader(@NonNull final BufferedImage image)
	{
		img = image;
		as2DArray = convertImageTo2DArray();
		asFlatArray = convertToFlatArray();
	}

	/**
	 * Creates a new image reader based off the path of location of the image on
	 * disk.
	 * 
	 * @param imageFile
	 *            The file of the image which is to be read. cannot be null.
	 */
	public ImageReader(@NonNull final File imageFile)
	{
		/*
		 * Read the image from the file and call the other constructor.
		 */
		this(readImageFile(imageFile));
	}

	/**
	 * Load an image from a given file.
	 * 
	 * This method is static as it does not feel like it would be an instance
	 * specific method and can be used else where in the project for image
	 * loading.
	 * 
	 * @param imageFile
	 *            The file of the image to be loaded. cannot be null.
	 * @return A buffered image that has been read into the program from the
	 *         given file.
	 */
	public static BufferedImage readImageFile(@NonNull final File imageFile)
	{
		try
		{
			return ImageIO.read(imageFile);
		} catch (IOException e)
		{
			LOGGER.error(imageFile.getAbsolutePath() + " had filed to load", e);
		}
		return null;
	}

	/**
	 * Given a file, load said file and display the file as an image within a
	 * JFrame.
	 * 
	 * @param file
	 *            The file of the image to be loaded. cannot be null.
	 */
	public static void displayImage(@NonNull final File file)
	{
		/*
		 * Load the file into a buffered image.
		 */
		BufferedImage imgToDisplay = readImageFile(file);

		/*
		 * Call the overloaded displayImage with the bufferedImage
		 */
		displayImage(imgToDisplay, file.getName());
	}

	/**
	 * Given a buffered image, display the buffered image within a JFrame.
	 * 
	 * Code modified from <a href=
	 * "http://www.dummies.com/programming/java/how-to-write-java-code-to-show-an-image-on-the-screen">
	 * here. </a>
	 * 
	 * @param img
	 *            The image to be displayed within a JFrame.
	 * @param title
	 *            The name given to this JFrame that will be displayed as it's
	 *            title
	 */
	public static void displayImage(@NonNull final BufferedImage img, final String title)
	{
		JFrame frame = new JFrame();
		frame.setSize(img.getWidth(), img.getHeight());
		/*
		 * Ensure that the frame can't be resized. This method is used in
		 * automated testing and the images must be exactly the same.
		 */
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setLayout(new FlowLayout());

		/*
		 * Is the title null? if so then just call the frame displayed image,
		 * otherwise set the title.
		 */
		frame.setTitle(title == null ? "Displayed Image" : title);

		/*
		 * Create an image icon from the image to be displayed and add it to a
		 * JLabel
		 */
		ImageIcon imgIcon = new ImageIcon(img);
		JLabel imageLabel = new JLabel(imgIcon);
		imageLabel.setVisible(true);

		/*
		 * Finally add the label to the frame
		 */
		frame.add(imageLabel);
		frame.pack();
	}

	/**
	 * Converts the associated buffered image of this reader into a 2D array of
	 * pixel values
	 * 
	 * @return The image as a 2D array of pixel values
	 */
	private double[][] convertImageTo2DArray()
	{
		double[][] imagePixels = new double[img.getHeight()][img.getWidth()];

		for (int x = 0; x < img.getHeight(); x++)
		{
			for (int y = 0; y < img.getWidth(); y++)
			{
				/*
				 * Get the current colour of the pixel
				 */
				Color currentColor = new Color(img.getRGB(y, x));

				/*
				 * Get the pixel's RGB and set it within the array
				 */
				imagePixels[x][y] = currentColor.getRGB();
			}
		}

		return imagePixels;
	}

	/**
	 * Converts the associated buffered image of this reader into a flat array
	 * of pixel values
	 * 
	 * @return A flat array of pixel values
	 */
	private double[] convertToFlatArray()
	{
		double[] pixelsFlatArray = new double[img.getHeight() * img.getWidth()];

		int flatArrayPosition = 0;

		/*
		 * Loop through the whole of the image and get the colour of each pixel
		 */
		for (int x = 0; x < img.getWidth(); x++)
		{
			for (int y = 0; y < img.getHeight(); y++)
			{
				/*
				 * Get the current colour of the pixel
				 */
				Color currentColor = new Color(img.getRGB(x, y));
				/*
				 * Convert the pixel into a grey scale pixel value and set it
				 * within the new flat array.
				 */
				pixelsFlatArray[flatArrayPosition] = currentColor.getBlue() + currentColor.getGreen()
						+ currentColor.getRed() / 3.0D;

				/*
				 * Move the pointer by 1
				 */
				flatArrayPosition++;
			}
		}

		return pixelsFlatArray;
	}

}
