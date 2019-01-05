package scl10.uk.ac.aber.users.neuralnetwork;

import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.util.data.norm.Normalizer;

import lombok.NonNull;
import scl10.uk.ac.aber.users.registration.RegistrationApplication;

/**
 * Method for both normalising and un-normalising data into the artificial
 * neural network
 * 
 * @author Scott Lockett
 */
public class ImageWarpNormalizer implements Normalizer
{

	/*
	 * ADDITIONAL INFORMATION AS TO THE NORMALISATION METHOD
	 * 
	 * For the images just make sure the pixel values are in the range 0 to 1
	 * e.g. if they are currently in the range 0-255 divide by 255.0.
	 * 
	 * For the warps it is a bit less clear, the warp vectors can be signed and
	 * we don’t really know the max range. Dividing by the size of the image
	 * feels like overkill, maybe assume they can be at most from -50 to +50
	 * pixels in x or y at the original image size? So you would normalize by
	 * adding 50 and dividing the result by 100 i.e. normalized[i] =
	 * (original[i]+50.0)/100.0. Of course if you have halved the image size N
	 * times you would divide those values by 2^N e.g. if N=5 normalized[i] =
	 * (original[i]+50.0/32.0)/(100.0/32.0).
	 */

	/**
	 * Provides a method of un-normalising the output from an artificial neural
	 * network that was normalised using the class.
	 * 
	 * @param inputWarp
	 *            The warp to be unnormalised. This warp must have been
	 *            normalised with this class's normalize method. Can not be
	 *            null.
	 * @return An unnormalised version of the input warp
	 */
	public static double[][] unnormalize(@NonNull final double[][] inputWarp)
	{
		/*
		 * Un-normalise the output from the artificial neural network
		 */
		for (int i = 0; i < inputWarp.length; i++)
		{
			for (int j = 0; j < inputWarp[0].length; j++)
			{
				inputWarp[i][j] = ((inputWarp[i][j] * 100.0D) - 50.0D)
						/ Math.pow(2, RegistrationApplication.PYRAMID_LEVELS);
			}
		}
		return inputWarp;
	}

	/**
	 * Normalises the input pixels values of the images and normalises the
	 * output of the warp between the two images.
	 */
	@Override
	public void normalize(final DataSet dataSet)
	{
		for (DataSetRow row : dataSet.getRows())
		{
			double[] rowInput = row.getInput();
			double[] rowOutput = row.getDesiredOutput();

			/*
			 * Divide the image pixel values by 255
			 */
			for (int i = 0; i < rowInput.length; i++)
			{
				double currentInput = rowInput[i];
				double normalized = currentInput / 255.0D;
				rowInput[i] = normalized;
			}

			/*
			 * Assume the warp has a maximum shift of +/- 50 so add 50 to the
			 * output and divide by 100.
			 */
			for (int j = 0; j < rowOutput.length; j++)
			{
				double desiredOutput = rowOutput[j];
				double factor = Math.pow(2, RegistrationApplication.PYRAMID_LEVELS);
				double normalized = (desiredOutput + 50.0D / factor) / (100.0D / factor);
				rowOutput[j] = normalized;
			}
		}
	}
}
