Image Registration using Convolutional Neural Networks Version 1.0 08/05/2017

INCLUDED:
1) PROJECT STRUCTURE
2) SETUP (for Eclipse)
3) RUNNING OF THE REGISTRATION APPLICATION
4) RUNNING OF THE ARTIFICIAL NEURAL NETWORK DATA GENERATION 


PROJECT STRUCTURE (From CNNProject/)
======================
For a better understand of the packages and classes see the project's Javadoc (CNNProject/doc/).

fullyConnectedLayerTrainingImagesPaths.txt			Contains a series of paths to the image training data for the artifical neural network.

fullyConnectedLayerTrainingWarpsPaths.txt			Contains a series of paths fo the warp training data for the artifical neural network. 

The above two files are VERY CLOSELY RELATED in that:
Each two paths from the imagesPaths.txt are a single piece of input into the artifical neural network for training.
The two paths from the warpsPaths.txt are the related output to the two images that is used as the output  from the artifical neural network.
The two files MUST be correctly related else the network will be trained incorrectly and/ or exceptions will be thrown.

For example
Images			Warps

Training data 1 
female1.jpg   	f1f2Y.txt 
female2.jpg 	f1f2X.txt 

Training data 2
female1.jpg		f1f3X.txt 
female3.jpg		f1f3Y.txt

[...]

Training data N
female7.jpg		f7f12X.txt 
female12.jpg	f7f12Y.txt

[...]

Training data N
female8.jpg		f8f3X.txt 
female3.jpg		f8f3Y.txt

pathsOfWarpsToDownsample.txt						A file containing a series of paths to the warp data that was downsampled in order to create the data within resources/registration-data/ann-training-data that is used as input into the artifical neural network 
		
pathsOfImagesToDownsample.txt 						A file containing a series of paths to the image data that was downsampled in order to create the data within resources/registration-data/ann-training-data that is used as input into the artifical neural network 


src/scl10/uk/ac/aber/users/							Folder for containing any Java source files. 										
│
├───manipulators									Package containing classes that manipulate objects (typically images) in someway.
│       ImageManipulator.java						Used for manipulating image data in a series of ways.
│       PatchedImage.java							Used for creating a patched image from which pacthed data can be derived.
│       WarpManipulator.java						Used for manipulating warp data in a series of ways.
│
├───neuralnetwork									Package containing classes that are associated with aspects of artifical neural networks. 
│       ArtificialNeuralNetworkEventHandler.java	Used for handling learning events within the artifical neural network.
│       ConvolutionLayer.java						Used for representing a convolutional layer as part of an artifical neural network.
│       ConvolutionLayerPyramid.java				Used for repersenting a series of convolution layers.
│       FullyConnectedLayer.java					Used as a basis for a forward feed multilayer perceptron. 
│       FullyConnectedLayerWarp.java				Used for predicting an initial warp between two test images at a very small size
│       ImageWarpNormalizer.java					Used for normalising and unnormalising artifical neural network data
│	
├───pyramids										Package containing classes that represent pyramid data. Or data that has been upsampled or downsampled a series of times. 
│       ImagePyramid.java							Used for representing a series of downsampled image data in a pyramid shape. 
│       WarpPyramid.java							Used for representing a series of upsampled warp data in a pyramid shape.
│
├───readers											Package containing classes that read data from the disk into the application.
│       ImageReader.java							Used for reading images from the disk into the application.  
│       TrainingDataReader.java						Used for reading a file that contians the path to artifical neural network training data.
│       WarpReader.java								Used for paring warp data from the disk into the appliation.
│
├───registration									Package containing the classes that are actually associated with the registration process.
│       Main.java									Contains the main method. The start of the program. Parsers users input. 
│       RegistrationApplication.java				Starts the registration process and outputs the registered image/ images.
│
└───trainingdatageneration							Package that is used for the production of training data for the artifical neurnal network. 
        TrainingDataGenerator.java					Given users input, generates artifical neural network of either images or warp data.
		
		
test/scl10/uk/ac/aber/users/						Folder containing any Java test files. 										
├───manipulators									Package for testing the classes within the manipulators source package.
│       ImageManipulatorTest.java					Used for testing the functionailty of the ImageManipulator class.
│       PatchedImageTest.java						Used for testing the functionailty of the PatchedImage class.
│       WarpManipulatorTest.java					Used for testing the functionailty of the WarpManipulator class. 
│
├───neuralnetwork									Package for testing the classes within the neural network source package. 
│       FullyConnectedLayerWarpTest.java			Used for testing the functionailty of the FullyConnectedLayerWarp class.
│
└───sikuli											Package for the containment of Sikuli. 
        SikuliUtils.java							Used for applying Sikuli based operations. 

resources/grouth-truth-images						Folder containing ground truth image ddata for any image based (Sikuli) unit tests.
					
resources/grouth-truth-warps						Folder containing ground truth warp data for any unit tests.

resources/original									Folder containing any data that is the test data to be used within a test and then compared to the ground truth.

resources/registration-data							Folder containing any of the original registation data. This included a series of registered image and the X and Y warp functions to register them. 

resources/registration-data/ann-training-data		Folder containing the training data for the artifical neural network. This data has been generated with the TrainingDataGenerator.java class
													and pathsOfWarpsToDownsample.txt and pathsOfImagesToDownsample.txt to downsample the data a series of times. 

resources/sikuli-icons								Folder containing any graphical user interface elements Sikuli makes use of. 

resources/test-network								Folder containing training data for the artifical neural network testing.


SET UP (for Eclipse)
======================
Unzip the folder where you choose. 

Open Eclipse

Go to Import -> General -> File System and selected the unziped folder and click finish

All of the files and packages should be imported into the Eclipse workspace, including src/ and test/
(The project will be rife with error as the libraries need added to the project also.) 
   
   
LIBRARIES
---------

This software makes use of a series of 3rd party libraries. 
Each of these libraries will need including within the project. 

Within the lib folder (CNNProject/lib) will be the following folders: 
-commons-lang3-3.5-bin
-lombok
-neuroph-2.93b
-OpenCV3.2
-sikuli-develop
-slf4j-1.7.23

Each of these are folders contain library JARs that are to be included within the project.

 

COMMONS-LANG
Go to Window -> Preferences -> New -> And set the new library name to be Apache Commons Lang

Click "Add Jars" and navigate to the commons-lang3-3.5-bin folder within CNNProject/lib/commons-lang3-3.5-bin and select
the main JAR file (CNNProject/lib/commons-lang3-3.5-bin/commons-lang3-3.5/commons-lang3-3.5.jar)

Once the main JAR has been included, click on "source attachment" and click "edit". Browse to 
CNNProject/lib/commons-lang3-3.5-bin/commons-lang3-3.5/ and select commons-lang3-3.5.jar-sources.jar

(You can also attach the Javadoc but thats not required for compilation) 



LOMBOK
Setup can be found here https://projectlombok.org/download.html

Alternatively .... 

Go to Windows -> Preference -> New -> And set the new library name to Lombok 

Click "Add Jars" and navigate to the lombok folder within CNNProject/lib/ and select the main
(and only) JAR file (CNNProject/lib/lombok/lombok-1.16.4)




LOGGER
Go to Windows -> Preferences -> New -> And set the new library name to Logger

Click "Add Jars" and navigate to the slf4j-1.7.23 folder within CNNProject/lib/ and select slf4j-api-1.7.23.jar

Once that JAR has been included, click on the "source attachment" and click the "edit". Browse 
to CNNProject/lib/slf4j-1.7.23 and select slf4j-api-1.7.23-sources.jar

Another main JAR needes adding so click on the Logger library that was just created on the first line 
and click "Add JAR" again and this time navigate to the slf4j-1.7.23 folder within CNNProject/lib/ 
and select slf4j-simple-1.7.23.jar

Once that JAR has been included, click on the "source attachment" and click "edit". Browse to CNNProject/lib/ 
and select  slf4j-simple-1.7.23-sources.jar

NOTE:- By default the Logger library will bind with java.util.logging, which is incorrect. We need it to bind
with the simple logger so you might have to remove any java.util.logger imports and choose to import the
slf4j logger if there are compilation errors



OPENCV3
Go to Windows -> Preferences -> New -> And set the new library name to OpenCV3.2

The OpenCV included method is describes below. The only differece is that the library is included so click "Add JAR"
not "Add external JAR"
http://docs.opencv.org/2.4/doc/tutorials/introduction/java_eclipse/java_eclipse.html



NEUROPH 
Neruoph is split into 4 compontents. Core, Adapters, Contrib and Imgrec.

Go to Windows -> Preferences -> New -> And set the new library name to Neuroph

Click "Add Jars" and navigate to the neuroph-2.93b folder within CNNProject/lib/neuroph2.93b and select
the neuroph-core-2.93 JAR (CNNProject/lib/neuroph-2.93b/neuroph-2.93b). 

Once the Core JAR has been included, click on "source attachment" and click "edit". Browse to 
CNNProject/lib/neuroph-2.93b/neuroph-2.93b and select neuroph-core-2.93-sources.jar. 

Repeat the last two steps for the 
Adapters, (neuroph-adapters-2.93.jar and neuroph-adapters-2.93-sources.jar)
Conrib (neuroph-contrib-2.93.jar and neuroph-contrib-2.93-sources.jar)
and Imgrec. (neuroph-imgreg-2.93.jar and neuroph-imgreg-2.93-sources.jar)



SIKULI
Go to Windows -> Pref ... library name to Sikuli 

The Sikulixsetup-1.1.1.jar might have to be ran in order for sikuli to work correctly

Sikuli setup can be found here http://doc.sikuli.org/faq/030-java-dev.html


RUNNING OF THE REGISTRATION APPLICATION
========================================
The registration process is ran from within Main.java class. The parameters for the main method are:

    args[0] - The path of source image 
    args[1] - The path of target image 
    args[2] - The path of the file that holds all of the paths to the images for training the artificial neural network
    args[3] - The path of the file that holds all of the paths to the warps for training the artificial neural network
    args[4] optional - The number of levels within the pyramid. 7 will be used if no preferred value is given.

	For example
	".\\resources\\registration-data\\female1.jpg",
	".\\resources\\registration-data\\female2.jpg",
	".\\fullyConnectedLayerTrainingImagesPaths.txt",
	".\\fullyConnectedLayerTrainingWarpsPaths.txt", 
	"7"

RUNNING OF THE ARTIFICIAL NEURAL NETWORK DATA GENERATION 
=========================================================
The production of the artificial neural network data is ran from within TrainingDataGenerator.java class. The parameters
for the main method are: 

	agrs[0] The path of the file that contains the paths to all of the training data paths. Can be image data or warp data.
	args[1] The folder where your downsampled files are to be written to. If the folder does not exist yet, it wil be created. 
	args[2] The number of times the data is to be downsampled between 1 - 10.
	
	For example
	".\\pathsOfWarpsToDownsample.txt",
	".\\new_downsampled_warps",
	"7"
	
