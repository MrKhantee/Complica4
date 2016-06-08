package eu.veldsoft.complica4.model.ia;

import java.util.Arrays;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;

/**
 * Bot based on artificial neural networks.
 * 
 * @author Todor Balabanov
 */
public class NeuralNetworkArtificialIntelligence extends
		AbstractArtificialIntelligence {

	/**
	 * 
	 */
	private int min;

	/**
	 * 
	 */
	private int max;

	/**
	 * Three layer artificial neural network.
	 */
	private BasicNetwork net = new BasicNetwork();

	/**
	 * 
	 * @param inputSize
	 * @param hiddenSize
	 * @param outputSize
	 * @param minPiece
	 * @param maxPiece
	 */
	public NeuralNetworkArtificialIntelligence(int inputSize, int hiddenSize,
			int outputSize, int minPiece, int maxPiece) {
		min = minPiece;
		max = maxPiece;

		net.addLayer(new BasicLayer(null, true, inputSize));
		net.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenSize));
		net.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputSize));
		net.getStructure().finalizeStructure();
		net.reset();
	}

	/**
	 * 
	 */
	@Override
	public int move(int[][] state, int player) throws NoValidMoveException {
		super.move(state, player);

		/*
		 * State matrix should be with valid dimensions.
		 */
		int size = 0;
		for (int i = 0; i < state.length; i++) {
			size += state[i].length;
		}

		/*
		 * Check ANN input layer size.
		 */
		if (size != net.getInputCount()) {
			throw new NoValidMoveException();
		}

		/*
		 * Scale input in the range of [0.0-1.0].
		 */
		double input[] = new double[size];
		for (int i = 0, k = 0; i < state.length; i++) {
			for (int j = 0; j < state[i].length; j++, k++) {
				input[k] = (state[i][j] - min) / (max - min);
			}
		}

		/*
		 * Feed the ANN input. ANN calculation.
		 */
		MLData data = new BasicMLData(input);
		data = net.compute(data);

		/*
		 * Obtain the ANN output.
		 */
		double output[] = data.getData();
		
		/*
		 * Suggest move.
		 */
		int index = 0;
		for (int i = 0; i < output.length; i++) {
			if (output[i] > output[index]) {
				index = i;
			}
		}

		return index;
	}
}