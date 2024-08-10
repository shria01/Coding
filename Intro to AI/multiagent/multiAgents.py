# multiAgents.py
# --------------
# Licensing Information:  You are free to use or extend these projects for
# educational purposes provided that (1) you do not distribute or publish
# solutions, (2) you retain this notice, and (3) you provide clear
# attribution to UC Berkeley, including a link to http://ai.berkeley.edu.
# 
# Attribution Information: The Pacman AI projects were developed at UC Berkeley.
# The core projects and autograders were primarily created by John DeNero
# (denero@cs.berkeley.edu) and Dan Klein (klein@cs.berkeley.edu).
# Student side autograding was added by Brad Miller, Nick Hay, and
# Pieter Abbeel (pabbeel@cs.berkeley.edu).


from util import manhattanDistance
from game import Directions
import random, util

from game import Agent
from pacman import GameState

class ReflexAgent(Agent):
    """
    A reflex agent chooses an action at each choice point by examining
    its alternatives via a state evaluation function.

    The code below is provided as a guide.  You are welcome to change
    it in any way you see fit, so long as you don't touch our method
    headers.
    """


    def getAction(self, gameState: GameState):
        """
        You do not need to change this method, but you're welcome to.

        getAction chooses among the best options according to the evaluation function.

        Just like in the previous project, getAction takes a GameState and returns
        some Directions.X for some X in the set {NORTH, SOUTH, WEST, EAST, STOP}
        """
        # Collect legal moves and successor states
        legalMoves = gameState.getLegalActions()

        # Choose one of the best actions
        scores = [self.evaluationFunction(gameState, action) for action in legalMoves]
        bestScore = max(scores)
        bestIndices = [index for index in range(len(scores)) if scores[index] == bestScore]
        chosenIndex = random.choice(bestIndices) # Pick randomly among the best

        "Add more of your code here if you want to"

        return legalMoves[chosenIndex]

    def evaluationFunction(self, currentGameState: GameState, action):
        """
        Design a better evaluation function here.

        The evaluation function takes in the current and proposed successor
        GameStates (pacman.py) and returns a number, where higher numbers are better.

        The code below extracts some useful information from the state, like the
        remaining food (newFood) and Pacman position after moving (newPos).
        newScaredTimes holds the number of moves that each ghost will remain
        scared because of Pacman having eaten a power pellet.

        Print out these variables to see what you're getting, then combine them
        to create a masterful evaluation function.
        """
        # Useful information you can extract from a GameState (pacman.py)
        successorGameState = currentGameState.generatePacmanSuccessor(action)
        newPos = successorGameState.getPacmanPosition()
        newFood = successorGameState.getFood()
        newGhostStates = successorGameState.getGhostStates()
        newScaredTimes = [ghostState.scaredTimer for ghostState in newGhostStates]

        "*** YOUR CODE HERE ***"
        dist = float('inf')
        foodList = currentGameState.getFood().asList()

        for food in foodList:
            dist = min(dist, manhattanDistance(food, newPos))

        for ghost in newGhostStates:
            gloc = ghost.getPosition()
            if gloc == newPos:
                return float('-inf')

        score = 1 / (1 + dist)

        return score

def scoreEvaluationFunction(currentGameState: GameState):
    """
    This default evaluation function just returns the score of the state.
    The score is the same one displayed in the Pacman GUI.

    This evaluation function is meant for use with adversarial search agents
    (not reflex agents).
    """
    return currentGameState.getScore()

class MultiAgentSearchAgent(Agent):
    """
    This class provides some common elements to all of your
    multi-agent searchers.  Any methods defined here will be available
    to the MinimaxPacmanAgent, AlphaBetaPacmanAgent & ExpectimaxPacmanAgent.

    You *do not* need to make any changes here, but you can if you want to
    add functionality to all your adversarial search agents.  Please do not
    remove anything, however.

    Note: this is an abstract class: one that should not be instantiated.  It's
    only partially specified, and designed to be extended.  Agent (game.py)
    is another abstract class.
    """

    def __init__(self, evalFn = 'scoreEvaluationFunction', depth = '2'):
        self.index = 0 # Pacman is always agent index 0
        self.evaluationFunction = util.lookup(evalFn, globals())
        self.depth = int(depth)

class MinimaxAgent(MultiAgentSearchAgent):
    """
    Your minimax agent (question 2)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the minimax action from the current gameState using self.depth
        and self.evaluationFunction.

        Here are some method calls that might be useful when implementing minimax.

        gameState.getLegalActions(agentIndex):
        Returns a list of legal actions for an agent
        agentIndex=0 means Pacman, ghosts are >= 1

        gameState.generateSuccessor(agentIndex, action):
        Returns the successor game state after an agent takes an action

        gameState.getNumAgents():
        Returns the total number of agents in the game

        gameState.isWin():
        Returns whether or not the game state is a winning state

        gameState.isLose():
        Returns whether or not the game state is a losing state
        """
        "*** YOUR CODE HERE ***"
        moves = gameState.getLegalActions(0)
        sucs = [gameState.generateSuccessor(0, action) for action in moves]
        index = 0
        maximum = float('-inf')
        for x in range(len(sucs)):
            if sucs[x].isWin() or sucs[x].isLose():
                val = self.evaluationFunction(sucs[x])
            else:
                val = self.minVal(sucs[x], 0, 1)
            if val > maximum:
                index = x
                maximum = val
        return moves[index]

    def minVal(self, gameState, depth, aCount):
        moves = gameState.getLegalActions(aCount)
        score = float('inf')
        sucs = [gameState.generateSuccessor(aCount, action) for action in moves]
        for s in sucs:
            if aCount + 1 == gameState.getNumAgents():
                if s.isWin() or s.isLose() or depth + 1 == self.depth:
                    score = min(score, self.evaluationFunction(s))
                else:
                    score = min(score, self.maxVal(s, depth + 1, 0))
            else:
                if s.isWin() or s.isLose() or depth == self.depth:
                    score = min(score, self.evaluationFunction(s))
                else:
                    score = min(score, self.minVal(s, depth, aCount + 1))
        return score

    def maxVal(self, gameState, depth, aCount):
        moves = gameState.getLegalActions(aCount)
        score = float('-inf')
        sucs = [gameState.generateSuccessor(aCount, action) for action in moves]
        for s in sucs:
            if s.isWin() or s.isLose() or depth == self.depth:
                score = max(score, self.evaluationFunction(s))
            else:
                score = max(score, self.minVal(s, depth, 1))
        return score


class AlphaBetaAgent(MultiAgentSearchAgent):
    """
    Your minimax agent with alpha-beta pruning (question 3)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the minimax action using self.depth and self.evaluationFunction
        """
        "*** YOUR CODE HERE ***"
        moves = gameState.getLegalActions(0)
        sucs = [gameState.generateSuccessor(0, action) for action in moves]
        index = 0
        maximum = float('-inf')
        alpha = float('-inf')
        beta = float('inf')
        for x in range(len(sucs)):
            if sucs[x].isWin() or sucs[x].isLose() or self.depth == 0:
                val = self.evaluationFunction(sucs[x])
            else:
                val = self.minVal(sucs[x], 0, 1, alpha, beta)
            if val > maximum:
                index = x
                maximum = val
                alpha = val
        return moves[index]

    def minVal(self, gameState, depth, aCount, alpha, beta):
        moves = gameState.getLegalActions(aCount)
        score = float('inf')
        for a in moves:
            if aCount + 1 == gameState.getNumAgents():
                if gameState.generateSuccessor(aCount, a).isWin() or gameState.generateSuccessor(aCount, a).isLose() or depth + 1 == self.depth:
                    score = min(score, self.evaluationFunction(gameState.generateSuccessor(aCount, a)))
                else:
                    score = min(score, self.maxVal(gameState.generateSuccessor(aCount, a), depth + 1, 0, alpha, beta))
            else:
                if gameState.generateSuccessor(aCount, a).isWin() or gameState.generateSuccessor(aCount, a).isLose() or depth == self.depth:
                    score = min(score, self.evaluationFunction(gameState.generateSuccessor(aCount, a)))
                else:
                    score = min(score, self.minVal(gameState.generateSuccessor(aCount, a), depth, aCount + 1, alpha, beta))
            if score < alpha:
                return score
            beta = min(beta, score)
        return score
    def maxVal(self, gameState, depth, aCount, alpha, beta):
        moves = gameState.getLegalActions(aCount)
        score = float('-inf')
        for a in moves:
            if gameState.generateSuccessor(aCount, a).isWin() or gameState.generateSuccessor(aCount, a).isLose() or depth == self.depth:
                score = max(score, self.evaluationFunction(gameState.generateSuccessor(aCount, a)))
            else:
                score = max(score, self.minVal(gameState.generateSuccessor(aCount, a), depth, 1, alpha, beta))
            if score > beta:
                return score
            alpha = max(alpha, score)
        return score

class ExpectimaxAgent(MultiAgentSearchAgent):
    """
      Your expectimax agent (question 4)
    """

    def getAction(self, gameState: GameState):
        """
        Returns the expectimax action using self.depth and self.evaluationFunction

        All ghosts should be modeled as choosing uniformly at random from their
        legal moves.
        """
        "*** YOUR CODE HERE ***"
        moves = gameState.getLegalActions(0)
        sucs = [gameState.generateSuccessor(0, action) for action in moves]
        index = 0
        maximum = float('-inf')
        for x in range(len(sucs)):
            if sucs[x].isWin() or sucs[x].isLose():
                val = self.evaluationFunction(sucs[x])
            else:
                val = self.expectiVal(sucs[x], 0, 1)
            if val > maximum:
                index = x
                maximum = val
        return moves[index]

    def maxVal(self, gameState, depth, aCount):
        moves = gameState.getLegalActions(aCount)
        score = float('-inf')
        sucs = [gameState.generateSuccessor(aCount, action) for action in moves]
        for s in sucs:
            if s.isWin() or s.isLose() or depth == self.depth:
                score = max(score, self.evaluationFunction(s))
            else:
                score = max(score, self.expectiVal(s, depth, 1))
        return score
    def expectiVal(self, gameState, depth, aCount):
        moves = gameState.getLegalActions(aCount)
        score = 0.0
        sucs = [gameState.generateSuccessor(aCount, action) for action in moves]
        for s in sucs:
            if aCount + 1 == gameState.getNumAgents():
                if s.isWin() or s.isLose() or depth + 1 == self.depth:
                    score += (self.evaluationFunction(s)/len(sucs))
                else:
                    score += (self.maxVal(s, depth + 1, 0)/len(sucs))
            else:
                if s.isWin() or s.isLose() or depth == self.depth:
                    score += (self.evaluationFunction(s)/len(sucs))
                else:
                    score += (self.expectiVal(s, depth, aCount + 1)/len(sucs))
        return score

def betterEvaluationFunction(currentGameState: GameState):
    """
    Your extreme ghost-hunting, pellet-nabbing, food-gobbling, unstoppable
    evaluation function (question 5).

    DESCRIPTION: <write something here so we know what you did>
    """
    "*** YOUR CODE HERE ***"
    pPos = currentGameState.getPacmanPosition()
    gPos = currentGameState.getGhostStates()
    foodList = currentGameState.getFood().asList()
    score = currentGameState.getScore()
    dist = float('inf')
    for food in foodList:
        dist = min(dist, manhattanDistance(food, pPos))
    if dist < float('inf') :
        score += 10.0 / dist
    else:
        score += 10.0
    for ghost in gPos:
        gloc = ghost.getPosition()
        dist2 = manhattanDistance(gloc, pPos)
        if gloc == pPos:
            return float('-inf')
        if ghost.scaredTimer > 0:
            score += 100.0 / dist2
        else:
            score += -10.0 / dist2

    return score

# Abbreviation
better = betterEvaluationFunction
