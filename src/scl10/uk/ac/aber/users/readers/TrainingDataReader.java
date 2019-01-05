package scl10.uk.ac.aber.users.readers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.NonNull;

/**
 * The paths of any training data such as warp files or images files used for
 * the artificial neural networks are stored in a text file. This reader object
 * is used for reading the path of that file and returning the paths of all of
 * the training data within.
 * 
 * @author Scott Lockett
 *
 */
public final class TrainingDataReader
{

	/**
	 * Logger for logging information
	 */
	private static Logger LOGGER = LoggerFactory.getLogger(TrainingDataReader.class);

	/**
	 * A String literal for preventing null pointers during String comparisons
	 */
	private static final String EMPTY_STRING = "";

	/**
	 * The path of the file which contains the path of the training data
	 */
	private final File fileContainingTrainingDataPaths;

	/**
	 * The paths of the training data (values from the read file)
	 */
	@Getter
	private final File[] filesOfTrainingData;

	/**
	 * Creates a new reader which reads in the paths of training data held in a
	 * text file.
	 * 
	 * @param pathOfTrainingDataFile
	 *            The path of the text file that contains the training data
	 *            paths. cannot be null.
	 */
	public TrainingDataReader(@NonNull final File pathOfTrainingDataFile)
	{

		/*
		 * Set the path of the training data
		 */
		fileContainingTrainingDataPaths = pathOfTrainingDataFile;

		/*
		 * Set the paths of the training data
		 */
		filesOfTrainingData = readPaths();
	}

	/**
	 * Given the path of the file
	 * {@link TrainingDataReader#fileContainingTrainingDataPaths} associated
	 * with this object, read the values held in that file which are paths to
	 * any training data and return the training data paths.
	 * 
	 * @return The paths of the training data held in the
	 *         {@link TrainingDataReader#fileContainingTrainingDataPaths} file.
	 */
	private File[] readPaths()
	{
		/*
		 * Use an arraylist to add each of the new files onto.
		 */
		List<File> filesArrayList = new ArrayList<>();

		/*
		 * Try and read the training data paths contained within the file. If
		 * something goes wrong then an IOExcpetion will be thrown and caught
		 */
		try
		{

			/*
			 * Create a reader for the file containing the data paths.
			 */
			BufferedReader trainingDataReader = new BufferedReader(new FileReader(fileContainingTrainingDataPaths));

			/*
			 * The first line will be the path of where the folder where the
			 * training data is held
			 */
			File topLevelFolder = new File(trainingDataReader.readLine());

			/*
			 * The current read line will be read into this String
			 */
			String currentLine;

			do
			{
				/*
				 * Get the current line in the file
				 */
				currentLine = trainingDataReader.readLine();

				/*
				 * If its not null the trim it to remove whitespace
				 */
				if (currentLine != null)
				{
					currentLine = currentLine.trim();
				}

				/*
				 * Making sure the line is not a blank line or null then add it
				 * to the arrayList.
				 */
				if (!EMPTY_STRING.equals(currentLine) && currentLine != null)
				{
					filesArrayList.add(new File(topLevelFolder.getAbsolutePath() + "\\" + currentLine));
				}
			} while (currentLine != null);

			/*
			 * Close the reader
			 */
			trainingDataReader.close();

		} catch (IOException e)
		{
			LOGGER.warn(fileContainingTrainingDataPaths + " has failed to be read. ", e);
		}

		/*
		 * return the ArrayList<File> as a converted File []
		 */
		return filesArrayList.toArray(new File[filesArrayList.size()]);
	}

}
