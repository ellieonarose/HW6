import java.util.ArrayList;
import java.util.Random;

public class Blackjack {
    // Represents a deck of cards
    class Deck {
        private final int[] cards;  // Array to hold the cards 
        private int currentCard;    // Index of the next card to be dealt
        private final Random random = new Random();

        public Deck() {
            cards = new int[52];
            currentCard = 0;
            
            for (int i = 0; i < 52; i++) {
                cards[i] = (i % 13) + 1;  // Card ranks: 1–13 ( Ace 2-10, Jack, Queen, King )           
            }
        }

        // Shuffle the deck using Fisher-Yates Shuffle
        public void shuffle() {
            for (int i = cards.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                int temp = cards[i];
                cards[i] = cards[j];
                cards[j] = temp;
            }
            currentCard = 0;  // Reset the deck position
        }

        // Deal the next card
        public int dealCard() {
            if (currentCard < cards.length) {
                return cards[currentCard++];
            }
            return -1;  // No more cards in the deck
        }
    }

    // Data members of the Blackjack class
    private Deck deck;
    ArrayList<Integer> playerHand, dealerHand;
    int playerScore, dealerScore;
    boolean playerStood;
    boolean playerUsableAce, dealerUsableAce;
    int shownCardValue;

    // Constructor to set up the game
    public Blackjack() {
        deck = new Deck();
        deck.shuffle();  // Shuffle the deck at the start

        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();

        // Deal initial cards
        dealerHand.add(deck.dealCard());
        playerHand.add(deck.dealCard());
        dealerHand.add(deck.dealCard());
        playerHand.add(deck.dealCard());

        // Calculate scores
        playerScore = calculateScore(playerHand, true);
        dealerScore = calculateScore(dealerHand, false);

        // The first card of the dealer is shown to the player
        shownCardValue = getCardValue(dealerHand.get(0));
    }

    // Calculate the value of a card
    private int getCardValue(int card) {
        return card > 10 ? 10 : card;  // Face cards (11, 12, 13) are worth 10
    }

    // Calculate the score of a hand
    private int calculateScore(ArrayList<Integer> hand, boolean isPlayer) {
        int score = 0;
        int aces = 0;
        boolean usableAce = false;

        for (int card : hand) {
            if (card == 1) {
                aces++;  // Count the number of aces
            } else {
                score += getCardValue(card);  // Add the value of the card to the score
            }
        }

        // Adjust for aces
        while (aces > 0) {
            if (score + 11 <= 21) {
                score += 11;
                usableAce = true;  // Ace is usable as 11
            } else {
                score += 1;  // Count ace as 1
            }
            aces--;
        }

        // Update the global usableAce for the player or dealer
        if (isPlayer) {
            playerUsableAce = usableAce;
        } else {
            dealerUsableAce = usableAce;
        }

        return score;
    }

    public void step(int action) {
        // Hit
        if (action == 0) {
            playerHand.add(deck.dealCard());  // Player hits
            playerScore = calculateScore(playerHand, true);  // Update player's score
        }
        // Stand
        else if (action == 1) {
            playerStood = true;  // Player stands

            // Dealer's turn
            // Dealer hits until score is 17 or higher
            while (dealerScore < 17 || (dealerScore == 17 && dealerUsableAce)) {
                dealerHand.add(deck.dealCard());  // Dealer hits
                dealerScore = calculateScore(dealerHand, false);  // Update dealer's score
            }
        }
    }

    // Check if the game is over
    public boolean isTerminal() {
        return playerStood || playerScore > 21 || dealerScore > 21;
    }

    // Get the reward for the game
    public int getReward() {
        if (playerScore > 21) {
            return -1;  // Player loses
        } else if (dealerScore > 21) {
            return 1;  // Player wins
        } else if (playerScore == dealerScore) {
            return 0;  // Tie
        } else if (playerScore > dealerScore) {
            return 1;  // Player wins
        }
        return -1;  // Dealer wins
    }

    class BJQlearning {
        public static final int HIT = 0; // Action: Hit
        public static final int STAND = 1; // Action: Stand

        Random random = new Random();
        double[][][][] qTable; // Q-table for state-action values
        double alpha;   // Learning rate
        double gamma; // Discount factor for future rewards
        double epsilon; // Exploration rate for epsilon-greedy policy

        // Constructor to initialize the Q-learning parameters
        public BJQlearning(double alpha, double gamma, double epsilon) {
            // Initialize Q-table with dimensions for player score, dealer score, usable ace, and action
            // 30 possible player scores (2-31), 10 dealer scores (1-10) - treat 1 as 11,
            // 2 usable ace states (0 or 1), and 2 actions (hit or stick)
            qTable = new double[32][12][2][2]; 
            this.alpha = alpha; // Set learning rate
            this.gamma = gamma; // Set discount factor
            this.epsilon = epsilon; // Set exploration rate


        }

        public int chooseAction(int playerScore, int dealerShownCard, boolean usableAce) {
            // Epsilon-greedy policy for action selection
            if (random.nextDouble() < epsilon) {
                return random.nextInt(2); // Random action (0 or 1)
            } else {
                // Choose the action with the highest Q-value
                int usableAceIndex = usableAce ? 1 : 0; // Convert boolean to index

               
                return qTable[playerScore][dealerShownCard][usableAceIndex][HIT] >
                        qTable[playerScore][dealerShownCard][usableAceIndex][STAND] ? HIT : STAND;
            }
        }


        public void update(int ps, int dc, boolean ua, int action, int reward, int nextPs, int nextDc, boolean nextUa, boolean terminal) {
            int actionIdx = action; 
            double oldQ = qTable[ps][dc][ua ? 1 : 0][actionIdx]; // Get current Q-value
            
            double futureMax = 0;
            if (!terminal) {
                futureMax = Math.max(
                    qTable[nextPs][nextDc][nextUa ? 1 : 0][HIT],
                    qTable[nextPs][nextDc][nextUa ? 1 : 0][STAND]
                );
            }
            

            // Update the Q-value using the Q-learning formula
            double newQ = oldQ + alpha * (reward + gamma * futureMax - oldQ);
            qTable[ps][dc][ua ? 1 : 0][actionIdx] = newQ;
        }
        

        public void train(int episodes) {
            for (int i = 0; i < episodes; i++) {
                Blackjack game = new Blackjack(); // Create a new game instance for each episode
        
                // While the game is still going on (not terminal)
                while (!game.isTerminal()) {
                    int playerScore = game.playerScore;
                    int dealerShownCard = game.shownCardValue;
                    boolean usableAce = game.playerUsableAce;
        
                    // Choose an action using the epsilon-greedy policy
                    int action = chooseAction(playerScore, dealerShownCard, usableAce);
        
                    // Take a step in the game (player action)
                    game.step(action);
        
                    // Get the new state after the action
                    int newPlayerScore = game.playerScore;
                    boolean newUsableAce = game.playerUsableAce;
                    int newDealerCard = game.shownCardValue;
        
                    // Update the Q-table

                    boolean terminal = game.isTerminal();
                    int reward = game.getReward();

                    int nextPlayerScore = terminal ? playerScore : newPlayerScore;
                    int nextDealerCard = terminal ? dealerShownCard : newDealerCard;
                    boolean nextUsableAce = terminal ? usableAce : newUsableAce;

                    update(playerScore, dealerShownCard, usableAce, action, reward, nextPlayerScore, nextDealerCard, nextUsableAce, terminal);

                    if (terminal) break;

                    playerScore = nextPlayerScore;
                    dealerShownCard = nextDealerCard;
                    usableAce = nextUsableAce;
                }
        
                // Decay epsilon
                epsilon = Math.max(0.01, epsilon * 0.999);
            }
        }
    }


    public static void main(String[] args) {
        // Create a Blackjack2 instance to test the game
        Blackjack game = new Blackjack();
        
        // Set Q-learning parameters: alpha, gamma, epsilon
        double alpha = .01;   // Learning rate
        double gamma = 0.3;
        double epsilon = 0.05 ; // Exploration rate
        
        // Create an instance of the Q-learning agent
        BJQlearning agent = game.new BJQlearning(alpha, gamma, epsilon);
        
        // Train the agent with a certain number of episodes
        int episodes = 1_000_000;  // Number of training episodes
        agent.train(episodes);
        
        // After training, print the optimal policy for a range of player scores and dealer's shown card
        System.out.println("\nOptimal Policy:");
        printPolicyTable(agent, true);
        System.out.println();
        System.out.println();
        printPolicyTable(agent, false);
        
        // Play 1000 games using the trained Q-learning agent
        int totalWins = 0;
        int totalLosses = 0;
        int totalTies = 0;
        
        int numGames = 100_000;
        for (int i = 0; i < numGames; i++) {
            Blackjack gameInstance = new Blackjack(); // Create a new game instance
            
            // Play the game until it's terminal
            while (!gameInstance.isTerminal()) {
                int playerScore = gameInstance.playerScore;
                int dealerCard = gameInstance.shownCardValue;
                boolean usableAce = gameInstance.playerUsableAce;
                
                // Choose an action using the epsilon-greedy policy
                //int action = agent.chooseAction(playerScore, dealerCard, usableAce);
                int action = agent.chooseAction(playerScore, dealerCard, usableAce);

                
                // Take a step in the game (player action)
                gameInstance.step(action);
            }
            
            // Get the reward for this game
            int reward = gameInstance.getReward();
            
            // Track wins, losses, and ties
            if (reward == 1) {
                totalWins++;
            } else if (reward == -1) {
                totalLosses++;
            } else {
                totalTies++;
            }
        }
        
        // Print the results of the 1000 games
        System.out.println("\nResults after 1_000_000 games:");
        System.out.println("Wins: " + totalWins);
        System.out.println("Losses: " + totalLosses);
        System.out.println("Ties: " + totalTies);
    }
    
    // Helper method to print a single table for either usable or non-usable ace
    private static void printPolicyTable(BJQlearning agent, boolean usableAce) {
        // Print header: A separately, then 2 → 10
        System.out.print("    ");               // Space for row labels  
        for (int dealerCard = 1; dealerCard <= 10; dealerCard++) {
            System.out.printf("%4d", dealerCard);
        }
        System.out.println();

        // Iterate over player scores (12 → 21)
        for (int playerScore = 21; playerScore >= 12 ; playerScore--) {
            System.out.printf("%4d", playerScore); // Print player score

            for (int dealerCard = 1; dealerCard <= 10; dealerCard++) {
                // Get the action: 0 for hit, 1 for stand
                
                int action = agent.chooseAction(playerScore, dealerCard, usableAce);

                // Print "H" for Hit and "S" for Stand
                System.out.printf("%4s", action == 0 ? " " : "S");
            }
            System.out.println();
        }
    }
}
