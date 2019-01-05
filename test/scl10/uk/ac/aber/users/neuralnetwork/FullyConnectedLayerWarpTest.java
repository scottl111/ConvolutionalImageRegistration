package scl10.uk.ac.aber.users.neuralnetwork;

import java.io.File;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import scl10.uk.ac.aber.users.neuralnetwork.FullyConnectedLayerWarp;

/**
 * A class for testing the functionality of the {@link FullyConnectedLayerWarp}
 * class.
 * 
 * @author Scott Lockett
 */
public class FullyConnectedLayerWarpTest
{
	/**
	 * A resources folder where the training images and warps are stored.
	 */
	private static final File testResourcesFolder = new File(".\\resources\\test-network\\");

	/**
	 * The file that contains the paths for the images used in the artificial
	 * neural network training
	 */
	private static final File imagePathsFile = new File(testResourcesFolder + "\\trainingImagePaths.txt");

	/**
	 * The file that contains the path for the warps used in the artificial
	 * neural network training
	 */
	private static final File warpPathsFile = new File(testResourcesFolder + "\\trainingWarpPaths.txt");

	/**
	 * The fully connected layer that is to be tested
	 */
	private FullyConnectedLayerWarp warpNeuralNetwork;

	/**
	 * Set up method for setting the values
	 */
	@Before
	public void initialise()
	{

		final int trainingImagesHeight = 18;
		final int trainingImagesWidth = 25;

		/*
		 * Size of the input to the neural network is equal to the lowest level
		 * of the pyramids * 2.
		 */
		final int nnInputSize = (trainingImagesHeight * trainingImagesWidth) * 2;

		/*
		 * Create the fully connected layered warp neural network. The input,
		 * hidden layer, and output size are all the same so simply set the them
		 * all as the input size.
		 */
		warpNeuralNetwork = new FullyConnectedLayerWarp(nnInputSize, nnInputSize, nnInputSize, imagePathsFile,
				warpPathsFile);
	}

	/**
	 * Tests the training of the artificial neural network by ensuring that the
	 * error rate reduces after each iteration. This test takes a minute or two
	 * to train the network.
	 * 
	 */
	@Test
	public void testTrainingNetworkAndEnsureNetworkErorrIsReducing()
	{
		/*
		 * Set the network to run 10 times indefinitely by setting a hugely low
		 * max error rate. This ensure that the back propergation doesn't happen
		 * just a couple of times, and the error rates can be properly tested.
		 */
		warpNeuralNetwork.setUpNeuralNetwork(0.000001, 0.7, 10);

		/*
		 * Train the network
		 */
		warpNeuralNetwork.trainNetwork();

		/*
		 * Get the error rates at each iteration of training
		 */
		ArrayList<Double> networkErrors = warpNeuralNetwork.getEventHandler().getNetworkErrorList();

		/*
		 * Remove the first value as something to compare to.
		 */
		Double previousError = networkErrors.remove(0);

		/*
		 * Loop through each error value and compare again the previous value
		 * ensuring that each iteration value has reduced.
		 */
		for (Double error : networkErrors)
		{
			if (previousError < error)
			{
				Assert.fail("Network error did not reduce. Test failed. ");
			}
			previousError = error;
		}
	}
}