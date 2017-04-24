# TD-Gammon
Implements the famous TD-Gammon algorithm for Backgammon-playing built on top of this java project: http://modelai.gettysburg.edu/2013/tdgammon/index.html

Most of the code is taken from here:
http://modelai.gettysburg.edu/2013/tdgammon/index.html
http://modelai.gettysburg.edu/2013/tdgammon/pa2.pdf
http://modelai.gettysburg.edu/2013/tdgammon/pa4.pdf

But the following classes have been added:
1. To package player - BackPropPlayer2,Utility
2. To package driver - SimulationDriver,TestStrengthDriver

BackPropPlayer2 : A NN player, that implements TD learning, takes parameters Lambda(TD-lambda), Alpha(NN learning-rate), trainingmode(true/false)
Utility: Implements primarily "BoardtoVec" to change a board to a NN representation
SimulationDriver: To run simulations
TestStrengthDriver: To test any two players against each other (the first one is always black)

Useful things to know:
1. SavedNN is a trained NN, the one provided with the code was generated after 1000000 games
of selfplay (specified in the while loop of SimulationDriver) and took 886 minutes to train on a very fast laptop.
Comparing this to another Tensorflow based (Python-simulated) implmentation: 
https://github.com/fomorians/td-gammon
https://medium.com/jim-fleming/before-alphago-there-was-td-gammon-13deff866197
The author mentions that it takes him 1 hr for 1000 games training, while for this implementation, it takes only about a minute.

2. NeuralNetworkVisualizer class can be used to visualize the Neural Nets thus generated

Important observation:
The quality of network trained depends highly on the startegy adopted to choose next moves. I trained two different networks as follows:
1. Next move is the one, where the next board-position has smallest utility for the opponent. Similar to Minimax strategy.
2. Next move is simply where there is greatest ulitilty. 

The network trained using 1st strategy has much better playing capacity than the 2nd.
