package scl10.uk.ac.aber.users.manipulators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.NonNull;
import scl10.uk.ac.aber.users.readers.ImageReader;

/**
 * A class used applying pixel manipulation functions to bufferedImages.
 * 
 * @author Scott Lockett
 */
public final class ImageManipulator
{
	/**
	 * Logger for logging user information
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageManipulatorTest.class);

	/**
	 * Loads openCV
	 */
	static
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * Private constructor to hide the implicit one ensure the class can not be
	 * instantiated.
	 */
	private ImageManipulator()
	{

	}

	/**
	 * Downsamples a given image by half in both the width and height
	 * 
	 * @param img
	 *            The image that is to be downsampled. Cannot be null.
	 * @return A downsampled version of the image.
	 */
	public static BufferedImage downsample(@NonNull final BufferedImage img)
	{
		/*
		 * Convert the image into an OpenCV matrix
		 */
		Mat matrix = convertImageToMatrix(img);

		/*
		 * Create the new smaller size which is half both the width and height
		 * of the original
		 */
		Size smallerSize = new Size(Math.floor(matrix.width() / 2.0D), Math.floor(matrix.height() / 2.0D));

		/*
		 * Create a destination where the downsampled matrix will be stored
		 */
		Mat destination = new Mat((int) smallerSize.height, (int) smallerSize.width, matrix.type());

		/*
		 * Perform the downsampling
		 */
		Imgproc.pyrDown(matrix, destination, smallerSize);

		return convertMatrixToImage(destination);
	}

	/**
	 * Upsamples a given image by doubling the image in both the width and
	 * height.
	 * 
	 * @param img
	 *            The image that is to be upsampled. cannot be null.
	 * @param newSize
	 *            The desired size of the image. cannot be null.
	 *            <p>
	 *            <strong> The new size MUST be the image's (width * 2) +/- 1
	 *            and the image's (height * 2) +/- 1. </strong>
	 * 
	 * @return An upsampled version of the given image.
	 */
	public static BufferedImage upsample(@NonNull final BufferedImage img, @NonNull final Size newSize)
	{
		/*
		 * Convert the image into an OpenCV matrix
		 */
		Mat matrix = convertImageToMatrix(img);

		/*
		 * Create a destination where the upsampled matrix will be stored
		 */
		Mat destination = new Mat((int) newSize.height, (int) newSize.width, matrix.type());

		/*
		 * Perform the upsampling
		 */
		Imgproc.pyrUp(matrix, destination, newSize);

		return convertMatrixToImage(destination);
	}

	/**
	 * Flips a given image across a given axis.
	 * 
	 * @param imageToFlip
	 *            The image to be flipped. cannot be null.
	 * @param flipDirection
	 *            The axis of the image that is going to be flipped on, either
	 *            -1, 0 or 1.
	 * @return The flipped version of the image across the given axis.
	 */
	public static BufferedImage flipImage(@NonNull final BufferedImage imageToFlip, final int flipDirection)
	{
		/*
		 * Convert the input image into a matrix
		 */
		Mat source = convertImageToMatrix(imageToFlip);

		/*
		 * The result of the flip will be sorted into a destination matrix so
		 * for now just set it as the source to ensure that the size and type is
		 * correct.
		 */
		Mat destination = source;

		/*
		 * Perform the flip
		 */
		Core.flip(source, destination, flipDirection);

		/*
		 * Convert back to an image
		 */
		return convertMatrixToImage(destination);
	}

	/**
	 * Converts a colour image to a greyscale image. Should the image already be
	 * greyscale then it is simply returned. Code was modified from <a href=
	 * "http://stackoverflow.com/questions/20076290/determine-whether-an-image-is-grayscale-in-java">
	 * here. </a>
	 * 
	 * @param imageToConvert
	 *            The colour image that is to become a greysacle image. Cannot
	 *            be null.
	 * @return A greyscale equivalent of the input colour image.
	 */
	public static BufferedImage convertColorImageToGreyscale(@NonNull final BufferedImage imageToConvert)
	{
		/*
		 * Start by checking if the image is already greyscale. If so, then
		 * return it.
		 */
		Raster raster = imageToConvert.getRaster();

		/*
		 * Determine the number of elements within the raster of the image. 1
		 * element means greyscale [0-255] and 3 elements means colour [RGB].
		 */
		int elem = raster.getNumDataElements();
		if (elem == 1)
		{
			return imageToConvert;
		}

		/*
		 * Create a new image in a greyscale format the same size as the image
		 * to convert.
		 */
		BufferedImage greyImage = new BufferedImage(imageToConvert.getWidth(), imageToConvert.getHeight(),
				BufferedImage.TYPE_BYTE_GRAY);

		/*
		 * get the graphics from the
		 */
		Graphics g = greyImage.getGraphics();

		/*
		 * Draw the original image into the greyscale image's graphic
		 */
		g.drawImage(imageToConvert, 0, 0, null);

		/*
		 * Tells the GIGO collector that this graphics object can be disposed of
		 * now.
		 */
		g.dispose();

		return greyImage;
	}

	/**
	 * Writes a buffered image out to a given file that is saved as a JPG.
	 * 
	 * @param fileToWriteTo
	 *            The file which is going to be written to. cannot be null.
	 * @param image
	 *            The image that is going to be written out. cannot be null.
	 * @return true is the file was successfully written to, or false if an
	 *         exception was thrown during the writing.
	 */
	public static boolean writeImageFile(@NonNull final File fileToWriteTo, @NonNull final BufferedImage image)
	{
		try
		{
			ImageIO.write(image, "jpg", fileToWriteTo);
		} catch (IOException e)
		{
			LOGGER.error(fileToWriteTo.getAbsolutePath() + " can not be written to.", e);
			return false;
		}
		return true;
	}

	/**
	 * Converts a BufferedImage into an open CV matrix object. Modified from
	 * <a href=
	 * "http://stackoverflow.com/questions/14958643/converting-bufferedimage-to-mat-in-opencv/15339157">
	 * here. </a>
	 * 
	 * @param img
	 *            The image to be converted into an open CV matrix. Can not be
	 *            null.
	 * @return The matrix equivalent of the input buffered image.
	 */
	private static Mat convertImageToMatrix(@NonNull final BufferedImage img)
	{
		/*
		 * Get the pixel from the image
		 */
		byte[] pixels = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();

		/*
		 * Create the matrix object the height and width of the matrix
		 */
		Mat m = new Mat(img.getHeight(), img.getWidth(), CvType.CV_8UC3);

		/*
		 * put the values of the pixels into the matrix starting at position
		 * [0,0]
		 */
		m.put(0, 0, pixels);

		/*
		 * return the matrix once the pixel values has been assigned to position
		 * within the matrix.
		 */
		return m;
	}

	/**
	 * Determines the average colour of an image and creates a new buffered
	 * image the same size as the input image that is wholly the colour of the
	 * average pixel colour.
	 * 
	 * @param img
	 *            The image thats average pixel colour is required. Can not be
	 *            null.
	 * @return An image the same size as the input image that is entirely the
	 *         average colour of the input image.
	 */
	public static BufferedImage createAveragedColouredImageFromImage(@NonNull final BufferedImage img)
	{
		/*
		 * Set up some values to accumulate the rgb of the image
		 */
		int red = 0;
		int green = 0;
		int blue = 0;

		/*
		 * Determine the average RGB of the image.
		 */
		for (int i = 0; i < img.getHeight(); i++)
		{
			for (int j = 0; j < img.getWidth(); j++)
			{
				Color currentPixelColor = new Color(img.getRGB(j, i));
				red += currentPixelColor.getRed();
				blue += currentPixelColor.getBlue();
				green += currentPixelColor.getGreen();
			}
		}

		/*
		 * Determine the size of the image so we can divide each colour channel
		 * to get an average
		 */
		int imageSize = img.getHeight() * img.getWidth();

		/*
		 * Work out the average colour
		 */
		Color averageColor = new Color(red / imageSize, green / imageSize, blue / imageSize);

		/*
		 * Create a new image that will be an average colour of the original
		 * image
		 */
		BufferedImage averageColoredImage = new BufferedImage(img.getWidth(), img.getHeight(),
				BufferedImage.TYPE_3BYTE_BGR);

		/*
		 * Set the average colour within the average coloured image.
		 */
		for (int i = 0; i < img.getHeight(); i++)
		{
			for (int j = 0; j < img.getWidth(); j++)
			{
				averageColoredImage.setRGB(j, i, averageColor.getRGB());
			}
		}

		return averageColoredImage;
	}

	/**
	 * Converts a matrix object into a Buffered Image. Modified from <a href=
	 * "http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/">
	 * here. </a>
	 * 
	 * @param matrix
	 *            The matrix to be converted into a buffered image. Can not be
	 *            null.
	 * @return The buffered image equivalent of the input matrix
	 */
	private static BufferedImage convertMatrixToImage(@NonNull final Mat matrix)
	{
		/*
		 * Set the size of the image buffer to be the matrix's size
		 */
		int bufferSize = matrix.width() * matrix.height() * matrix.channels();

		/*
		 * Create the byte array the size of the buffer
		 */
		byte[] b = new byte[bufferSize];

		/*
		 * Get all of the pixel from the matrix starting from position [0,0] and
		 * write them to the byte array b.
		 */
		matrix.get(0, 0, b);

		/*
		 * Create the new image the size of the matrix
		 */
		BufferedImage image = new BufferedImage(matrix.width(), matrix.height(), BufferedImage.TYPE_3BYTE_BGR);

		/*
		 * get the target pixels from the image as a byte array
		 */
		byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

		/*
		 * Copy the matrix values from b into the buffered image's target pixels
		 */
		System.arraycopy(b, 0, targetPixels, 0, b.length);

		/*
		 * once set return the buffered image
		 */
		return image;
	}

	/**
	 * Applies a given x and y pixel shift to an image. Also converts image to
	 * grey scale in the process.
	 * 
	 * @param image
	 *            2D representation of an image which he pixels values for each
	 *            pixel within the image. cannot be null.
	 * @param xwarp
	 *            2D x warp which is going to be applied to the image. Can not
	 *            be null.
	 * @param ywarp
	 *            2D y warp which is going to be applied to the image. Can not
	 *            be null.
	 * @return A registered image with both of the functions applied to the
	 *         input image
	 */
	public static BufferedImage applyWarpFunctionToImage(@NonNull final BufferedImage image,
			@NonNull final double[][] xwarp, @NonNull final double[][] ywarp)
	{
		/*
		 * Create temporary dimensions for the image, the xwarp and the ywarp
		 * for ease of testing the sizes.
		 */
		Dimension xWarpDim = new Dimension(xwarp[0].length, xwarp.length);
		Dimension yWarpDim = new Dimension(ywarp[0].length, ywarp.length);
		Dimension imageDim = new Dimension(image.getWidth(), image.getHeight());

		/*
		 * Ensure that the image and the warps are all the same size, if not,
		 * something has gone very wrong somewhere.
		 */
		if (!imageDim.equals(xWarpDim) || !yWarpDim.equals(xWarpDim))
		{
			throw new IllegalArgumentException("Image and warp sizes are not the same. Warp can not be applied. ");
		}

		/*
		 * Set the warped image as an average of the current image. This ensures
		 * that if pixels are shifted and there is nothing to show, then at
		 * least the images average pixel colour is shown as opposed to black
		 * which is the bufferedImages default colour. This helps to keep the
		 * image as a (slightly) true representation. Black might never occur in
		 * the original image at all so there is the possibility that large
		 * areas of the warped image might not be an actual representation if
		 * left as the default.
		 */
		BufferedImage warpedImage = createAveragedColouredImageFromImage(image);

		/*
		 * Convert the image into a 2D array of pixel values
		 */
		double[][] array2DofPixelValuesOfImage = new ImageReader(image).getAs2DArray();

		/*
		 * Loop through each pixel and apply the warp
		 */
		for (int i = 0; i < warpedImage.getHeight(); i++)
		{

			for (int j = 0; j < warpedImage.getWidth(); j++)
			{
				/*
				 * Ensure we're still writing to a position within the new image
				 */
				if (((i + xwarp[i][j]) > 0) && (j + ywarp[i][j]) > 0
						&& ((int) (i + xwarp[i][j]) < warpedImage.getHeight())
						&& ((int) (j + ywarp[i][j]) < warpedImage.getWidth()))
				{

					/*
					 * Find the new rgb pixel value for the current i,j position
					 */
					int newRGB = (int) array2DofPixelValuesOfImage[(int) (i + xwarp[i][j])][(int) (j + ywarp[i][j])];

					/*
					 * Create a new color object from the RGB value
					 */
					Color c = new Color(newRGB);

					/*
					 * Set the new pixel at its position within the image.
					 */
					warpedImage.setRGB(j, i, c.getRGB());
				}
			}
		}

		return warpedImage;
	}
}
