import java.util.Random;
import java.util.ArrayList;

public class Blackjack {
    // Represents a card in the deck
    class Card {
        public int rank;   // 1-13 (Ace, 2-10, Jack - 11, Queen - 12, King - 13)
        public int suit;   // 0-3 (Clubs, Diamonds, Hearts, Spades)
    
        public Card(int rank, int suit) {
            this.rank = rank;
            this.suit = suit;
        }

        // Get the value of the card
        public int getValue() {
            if (rank >= 10) {
                return 10; // Face cards (Jack, Queen, King)
            } else if (rank == 1) {
                return 1; 
            } else {
                return rank; // Number cards
            }
        }
    }
    
    // Represents a deck of cards
    class Deck {
        private final Card[] cards;         // Array to hold the cards
        private int currentCard;            // Index of the next card to be dealt
        private final Random random = new Random();

        public Deck() {
            cards = new Card[52];
            currentCard = 0;
            initializeDeck();
        }

        // Initialize the deck with 52 cards
        private void initializeDeck() {
            int index = 0;
            for (int suit = 0; suit < 4; suit++) {
                for (int rank = 1; rank <= 13; rank++) {
                    cards[index++] = new Card(rank, suit);
                }
            }
        }

        // Proper Fisher-Yates Shuffle
        public void shuffle() {
            for (int i = cards.length - 1; i > 0; i--) {
                int j = random.nextInt(i + 1);
                Card temp = cards[i];
                cards[i] = cards[j];
                cards[j] = temp;
            }
            currentCard = 0;  // Reset the deck position
        }

        // Deal the next card
        public Card dealCard() {
            // Check if there are cards left to deal
            if (currentCard < cards.length) {
                return cards[currentCard++];
            }
            return null;  // No more cards in the deck
        }
    }

    //Data members of the Blackjack class
    private Deck deck;
    ArrayList<Card> playerHand, dealerHand;
    int playerScore, dealerScore;
    boolean playerStood;
    boolean playerUsableAce, dealerUsableAce;
    int shownCardValue;
    
    // Constructor to set up the game
    public Blackjack() {
        deck = new Deck();
        deck.shuffle();             // Shuffle the deck at the start
    
        playerHand = new ArrayList<>();  // Player's hand
        dealerHand = new ArrayList<>();  // Dealer's hand

        dealerHand.add(deck.dealCard()); // Deal second card to dealer 
        playerHand.add(deck.dealCard()); // Deal third card to player
        dealerHand.add(deck.dealCard()); // Deal fourth card to dealer
        playerHand.add(deck.dealCard()); // Deal first card to player

        playerScore = calculateScore(playerHand, true); // Calculate player's score
        dealerScore = calculateScore(dealerHand, false); // Calculate dealer's score

        // The first card of the dealer is shown to the player
        shownCardValue = dealerHand.get(0).getValue();
    }

    // Calculate the score of a hand
    // It counts the number of aces and adjusts the score accordingly
    // Aces can be counted as 1 or 11, depending on the total score
    private int calculateScore(ArrayList<Card> hand, boolean isPlayer) {
        int score = 0;
        int aces = 0;
        boolean usableAce = false;
    
        for (Card card : hand) {
            if (card.getValue() == 1) {
                aces++; // Count the number of aces
            } else {
                score += card.getValue(); // Add the value of the card to the score

            }
        }
    
        // Adjust for aces
        while (aces > 0) {
            // Try to count the ace as 11 if it doesn't bust the score
            if (score + 11 <= 21) {
                score += 11;
                usableAce = true; // Ace is usable as 11
            } else {
                score += 1; // Count ace as 1
            }
            aces--;
        }

        // Update the global usableAce for the player or dealer
        if (isPlayer) {
            playerUsableAce = usableAce; // Track the player's usable ace
        } else {
            dealerUsableAce = usableAce; // Track the dealer's usable ace
        }

        return score;
    }

    public void step(int action){
        //Hit
        if(action == 0){
            playerHand.add(deck.dealCard()); // Player hits
            playerScore = calculateScore(playerHand, true); // Update player's scor
        }
        //Stand
        else if(action == 1){
            playerStood = true; // Player stands
            
            // Dealer's turn
            // Dealer hits until score is 17 or higher
            while (dealerScore < 17 || (dealerScore == 17 && dealerUsableAce)) {
                dealerHand.add(deck.dealCard()); // Dealer hits
                dealerScore = calculateScore(dealerHand, false); // Update dealer's score
            }
            
        }
    }

    // Check if the either the player or dealer won
    public boolean isTerminal() {
        return playerStood || playerScore > 21 || dealerScore > 21;
    }

    public int getReward() {
        // Check if player busted
        if (playerScore > 21) {
            return -1; // Player loses
        }
        // Check if dealer busted
        else if (dealerScore > 21) {
            return 1; // Player wins
        }
        // Check for a tie
        else if (playerScore == dealerScore) {
            return 0; // Tie
        }
        // Check if player wins
        else if (playerScore > dealerScore) {
            return 1; // Player wins
        }
        // Dealer wins
        return -1; // Player loses
    }

    class BJQlearning {
        Random random = new Random();
        double[][][][] qTable; // Q-table for state-action values
        double alpha;   // Learning rate
        double gammaUsableAce, gammaNoUsableAce; // Discount factor for future rewards
        double epsilon; // Exploration rate for epsilon-greedy policy

        // Constructor to initialize the Q-learning parameters
        public BJQlearning(double alpha, double gammaUsableAce, double gammaNoUsableAce, double epsilon) {
            // Initialize Q-table with dimensions for player score, dealer score, usable ace, and action
            // 30 possible player scores (2-31), 10 dealer scores (1-10) - treat 1 as 11,
            // 2 usable ace states (0 or 1), and 2 actions (hit or stick)
            qTable = new double[32][12][2][2]; 
            this.alpha = alpha; // Set learning rate
            this.gammaUsableAce = gammaUsableAce; // Set discount factor
            this.gammaNoUsableAce = gammaNoUsableAce; // Set discount factor
            this.epsilon = epsilon; // Set exploration rate


        }

        public int chooseAction(int playerScore, int dealerShownCard, boolean usableAce) {
            // Epsilon-greedy policy for action selection
            if (random.nextDouble() < epsilon) {
                return random.nextInt(2); // Random action (0 or 1)
            } else {
                // Choose the action with the highest Q-value
                int usableAceIndex = usableAce ? 1 : 0; // Convert boolean to index

               
                return qTable[playerScore][dealerShownCard][usableAceIndex][0] >
                        qTable[playerScore][dealerShownCard][usableAceIndex][1] ? 0 : 1;
            }
        }

       

        public void update(int ps, int dc, boolean ua, int action, int reward, int nextPs, int nextDc, boolean nextUa, boolean terminal) {
            int actionIdx = action; // 0 -> hit, 1 -> stay
            double oldQ = qTable[ps][dc][ua ? 1 : 0][actionIdx]; // Get current Q-value
            
            double futureMax = 0;
            if (!terminal) {
                futureMax = Math.max(
                    qTable[nextPs][nextDc][nextUa ? 1 : 0][0],
                    qTable[nextPs][nextDc][nextUa ? 1 : 0][1]
                );
            }
            
            // Get the gamma value to use based on whether the next state has a usable ace or not
            double gammaToUse = ua ? gammaUsableAce : gammaNoUsableAce;

            // Update the Q-value using the Q-learning formula
            double newQ = oldQ + alpha * (reward + gammaToUse * futureMax - oldQ);
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
        
                    // Get the reward for this step
                    int reward = game.getReward();
        
                    // Update the Q-table
                    if (game.isTerminal()) {
                        reward = game.getReward();
                        update(playerScore, dealerShownCard, usableAce, action, reward, playerScore, dealerShownCard, usableAce, true);
                        break;
                    } else {
                        reward = game.getReward();
                        update(playerScore, dealerShownCard, usableAce, action, reward, newPlayerScore, newDealerCard, newUsableAce, false);
                        playerScore = newPlayerScore;
                        dealerShownCard = newDealerCard;
                        usableAce = newUsableAce;
                    }
                    
                }
        
                // Decay epsilon
                epsilon = Math.max(0.025, epsilon * 0.995);
            }
        }
    }


    public static void main(String[] args) {
        // Create a Blackjack2 instance to test the game
        Blackjack game = new Blackjack();
        
        // Set Q-learning parameters: alpha, gamma, epsilon
        double alpha = .055;   // Learning rate
        double gammaUsableAce = .03;     // Discount factor
        double gammaNoUsableAce = .15;   // Discount factor
        double epsilon = 0.09 ; // Exploration rate
        
        // Create an instance of the Q-learning agent
        BJQlearning agent = game.new BJQlearning(alpha, gammaUsableAce, gammaNoUsableAce, epsilon);
        
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
