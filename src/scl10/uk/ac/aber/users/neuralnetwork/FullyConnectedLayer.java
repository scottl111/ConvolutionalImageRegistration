package scl10.uk.ac.aber.users.neuralnetwork;

import org.neuroph.core.data.DataSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;

import lombok.Getter;

/**
 * A class representing the basic shell of a fully connected layer.
 * 
 * @author Scott Lockett
 */
class FullyConnectedLayer
{

	/**
	 * Package level empty constructor
	 */
	protected FullyConnectedLayer()
	{

	}

	/**
	 * The multi layer perceptron that will be trained and tested with.
	 * 
	 * No modifier so that extending class within the package can access the
	 * neural network.
	 */
	protected MultiLayerPerceptron mlp;

	/**
	 * The training data data set which will be used to train the multi layer
	 * perceptron.
	 * 
	 * No modifier so that extending class within the package can access the
	 * training data set.
	 */
	protected DataSet trainingData;

	/**
	 * An event handler will be required to ensure that the network error is
	 * reducing after each training iteration.
	 */
	@Getter
	public final ArtificialNeuralNetworkEventHandler eventHandler = new ArtificialNeuralNetworkEventHandler();

	/**
	 * Sets up the basics parameters for the neural networks and adds an event
	 * action listener.
	 * 
	 * @param maxError
	 *            The maximum level of error the network is allowed in order to
	 *            stop back propagating.
	 * @param learningRate
	 *            The learning rate of the neural network. between 0 - 1.
	 *            Preferably around 0.7.
	 * @param iterations
	 *            If the network isn't stopped by the neural network reaching
	 *            the max error that has been set, then how many times should it
	 *            keep trying to reduce the error before it gives up.
	 * @see ArtificialNeuralNetworkEventHandler#handleLearningEvent(org.neuroph.core.events.LearningEvent)
	 */
	public void setUpNeuralNetwork(final double maxError, final double learningRate, final int iterations)
	{
		/*
		 * Randomise the initial weights
		 */
		mlp.randomizeWeights();

		/*
		 * Get the learning rule and set some basic parameters for learning
		 */
		MomentumBackpropagation learningRule = (MomentumBackpropagation) mlp.getLearningRule();
		learningRule.setMaxError(maxError);
		learningRule.setLearningRate(learningRate);
		learningRule.setMaxIterations(iterations);
		learningRule.addListener(eventHandler);
	}

}
