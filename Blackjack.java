// import java.util.HashMap;

// public class Blackjack{
//     // This file uses Q learning to create an optimal Blackjack player
//     //First, we need to create a class that represents the state of the game
    
//     public class State{
//         // The state of the game is represented by the player's current sum, the dealer's face up card, and whether or not the player has a usable ace
//         int playerSum;
//         int dealerCard;
//         boolean usableAce;

//         public State(int playerSum, int dealerCard, boolean usableAce){
//             this.playerSum = playerSum;
//             this.dealerCard = dealerCard;
//             this.usableAce = usableAce;
//         }

//         public boolean equals(Object obj){
//             if(obj instanceof State){
//                 State s = (State) obj;
//                 return s.playerSum == playerSum && s.dealerCard == dealerCard && s.usableAce == usableAce;
//             }
//             return false;
//         }

//         public int hashCode(){
//             return playerSum + dealerCard + (usableAce ? 1 : 0);
//         }
//     }

//     // Next, we need to create a class that represents the Q values for each state-action pair
//     public class QValue {
//         State state;
//         int action;
//         double value;
    
//         public QValue(State state, int action, double value) {
//             this.state = state;
//             this.action = action;
//             this.value = value;
//         }
    
//         @Override
//         public boolean equals(Object obj) {
//             if (obj instanceof QValue) {
//                 QValue q = (QValue) obj;
//                 return q.state.equals(state) && q.action == action;
//             }
//             return false;
//         }
    
//         @Override
//         public int hashCode() {
//             return state.hashCode() + action;
//         }
//     }

//     // Now, we need to create a class that represents the Blackjack environment
//     public class BlackjackEnv{
//         // The environment is represented by the player's current sum, the dealer's face up card, and whether or not the player has a usable ace
//         int playerInitCard1, playerInitCard2, playerSum;
//         int dealerCard;
//         boolean usableAce, dealerUsableAce;

//         public BlackjackEnv(){
//             // Initialize the environment by dealing two cards to the player and one card to the dealer
//             // Each card is between 1 and 13. if the card is 10, 11, 12, or 13, it is a 10
//             // If the card is 1, it is an ace
//             // An ace can either be 1 or 11
//             // If the player has <11 total, the ace is 11
//             // If the player has >11 total, the ace is 1
//             playerInitCard1 = (int) (Math.random() * 13) + 1;
//             playerInitCard2 = (int) (Math.random() * 13) + 1;
//             if(playerInitCard1 > 10){
//                 playerInitCard1 = 10;
//             }
//             if(playerInitCard2 > 10){
//                 playerInitCard2 = 10;
//             }
//             if(playerInitCard1 == 1 && playerInitCard2 == 1){
//                 playerInitCard2 = 11;
//             }
//             if(playerInitCard1 == 1 && playerInitCard2 > 10){
//                 playerInitCard1 = 11;
//             }
//             if(playerInitCard2 == 1 && playerInitCard1 > 10){
//                 playerInitCard2 = 11;
//             }
//             if(playerInitCard1 == 1 && playerInitCard2 < 11){
//                 playerInitCard1 = 11;
//             }
//             if(playerInitCard2 == 1 && playerInitCard1 < 11){
//                 playerInitCard2 = 11;
//             }
//             dealerCard = (int) (Math.random() * 13) + 1;
//             if(dealerCard > 10){
//                 dealerCard = 10;
//             }
//             usableAce = playerInitCard1 == 11 || playerInitCard2 == 11;
//             playerSum = playerInitCard1 + playerInitCard2;
//         }

//         public void reset(){
//             // Reset the environment by dealing two cards to the player and one card to the dealer
//             playerInitCard1 = (int) (Math.random() * 13) + 1;
//             playerInitCard2 = (int) (Math.random() * 13) + 1;
//             if(playerInitCard1 > 10){
//                 playerInitCard1 = 10;
//             }
//             if(playerInitCard2 > 10){
//                 playerInitCard2 = 10;
//             }
//             if(playerInitCard1 == 1 && playerInitCard2 == 1){
//                 playerInitCard2 = 11;
//             }
//             else if(playerInitCard1 == 1 && playerInitCard2 > 10){
//                 playerInitCard1 = 11;
//             }
//             else if(playerInitCard2 == 1 && playerInitCard1 > 10){
//                 playerInitCard2 = 11;
//             }
//             else if(playerInitCard1 == 1 && playerInitCard2 < 11){
//                 playerInitCard1 = 11;
//             }
//             else if(playerInitCard2 == 1 && playerInitCard1 < 11){
//                 playerInitCard2 = 11;
//             }
//             dealerCard = (int) (Math.random() * 13) + 1;
//             if(dealerCard > 10){
//                 dealerCard = 10;
//             }
//             usableAce = playerInitCard1 == 11 || playerInitCard2 == 11;
//             playerSum = playerInitCard1 + playerInitCard2;
//         }

//         public void step(int action){
//             // Take a step in the environment based on the action
//             if(action == 0){
//                 // Player hits
//                 int card = (int) (Math.random() * 13) + 1;
//                 if(card > 10){
//                     card = 10;
//                 }
//                 if(card == 1 && playerSum + 11 <= 21){
//                     card = 11;
//                 }
//                 playerSum += card;
//                 if(playerSum > 21 && usableAce){
//                     playerSum -= 10;
//                     usableAce = false;
//                 }
//             }else{
//                 // Player stands
//                 dealerUsableAce = dealerCard == 11;
//                 while(dealerCard < 17 || (dealerCard == 17 && dealerUsableAce)){
//                     int card = (int) (Math.random() * 13) + 1;
//                     if(card > 10){
//                         card = 10;
//                     }
//                     if(card == 1 && dealerCard + 11 <= 21){
//                         card = 11;
//                     }
//                     dealerCard += card;
//                     if(dealerCard > 21 && dealerUsableAce){
//                         dealerCard -= 10;
//                         dealerUsableAce = false;
//                     }
//                 }
//             }
//         }

//         public boolean isTerminal(){
//             // Check if the game is over
//             return playerSum > 21 || dealerCard > 21;
//         }

//         public int getReward(){
//             // Get the reward for the current state
//             if(playerSum > 21){
//                 // Player bust
//                 return -1;
//             }else if(dealerCard > 21){
//                 // Dealer bust
//                 return 1;
//             }else if(playerSum > dealerCard){
//                 // Player wins
//                 return 1;
//             }else if(playerSum < dealerCard){
//                 // Dealer wins
//                 return -1;
//             }else{
//                 // Tie
//                 return 0;
//             }   
//         }
//     }

//     // Now, we need to create a class that represents the Q learning agent
//     public class QLearningAgent{
//         // The agent is represented by the Q values for each state-action pair
//         HashMap<QValue, Double> qValues;
//         double alpha;
//         double gamma;
//         double epsilon;

//         public QLearningAgent(double alpha, double gamma, double epsilon){
//             qValues = new HashMap<QValue, Double>();
//             this.alpha = alpha;
//             this.gamma = gamma;
//             this.epsilon = epsilon;
//         }

//         //Get action for state
//         public int getAction(State state){

//             // Exploration
//             if (Math.random() < epsilon){
//                 return (int) (Math.random() * 2); // Random action: 0 = hit, 1 = stand
//             }

//             // Exploitation
//             int bestAction = 0;
//             double maxQValue = Double.NEGATIVE_INFINITY;

//             for (int action = 0; action < 2; action++) {
//                 QValue q = new QValue(state, action, 0.0);
//                 double value = qValues.getOrDefault(q, 0.0);
//                 if (value > maxQValue) {
//                     maxQValue = value;
//                     bestAction = action;
//                 }
//             }

//             //if the 21 then stand
//             if (state.playerSum == 21){
//                 return 1;
//             }

//             return bestAction;
//         }

//         public void updateQValue(State state, int action, double reward, State nextState){
//             // Update the Q value for the given state-action pair
//             // QValue qValue = new QValue(state, action, 0);
//             QValue currentQ = new QValue(state, action, 0.0);

//             double maxQNext = Double.NEGATIVE_INFINITY;
       
//             // Find the maximum Q value for the next state
//             for (int i = 0; i < 2; i++) {
//                 QValue nextQ = new QValue(nextState, i, 0.0);
//                 // check if the next state is in the map and update the maxQNext
//                 if (qValues.containsKey(nextQ)) {
//                     maxQNext = Math.max(maxQNext, qValues.get(nextQ));
//                 } else {
//                     maxQNext = Math.max(maxQNext, 0.0); // Assume 0 if not in map
//                 }
//             }
       
//             // Update the Q value using the Q-learning update rule
//             double oldQ = qValues.getOrDefault(currentQ, 0.0);
//             double newQ = oldQ + alpha * (reward + gamma * maxQNext - oldQ);
//             qValues.put(currentQ, newQ);
//         }

//         public void train(int episodes){
//             // Train the agent using Q learning
//             for(int i = 0; i < episodes; i++){
//                 BlackjackEnv env = new BlackjackEnv();
//                 State state = new State(env.playerSum, env.dealerCard, env.usableAce);
//                 while(!env.isTerminal()){
//                     int action = getAction(state);
//                     env.step(action);
//                     State nextState = new State(env.playerSum, env.dealerCard, env.usableAce);
//                     int reward = env.getReward();
//                     updateQValue(state, action, reward, nextState);
//                     state = nextState;  
//                 }
//             }
//         }
//     }

//     //Method for finding the optimal policy
//     public void findOptimalPolicy(QLearningAgent agent){
//         // Find the optimal policy for the agent
//         for(int playerSum = 12; playerSum <= 21; playerSum++){
//             for(int dealerCard = 1; dealerCard <= 10; dealerCard++){
//                 // usable ace switches between true and false
//                 for(boolean usableAce : new boolean[]{true, false}){
//                 State state = new State(playerSum, dealerCard, usableAce);
//                     int action = agent.getAction(state);
//                     System.out.println("Player Sum: " + playerSum + ", Dealer Card: " + dealerCard + ", Usable Ace: " + usableAce + ", Action: " + (action == 0 ? "Hit" : "Stand"));
//                 }
//             }
//         }
//     }

//     public static void main(String[] args){
//         // Create a Q learning agent with alpha = 0.1, gamma = 0.9, and epsilon = 0.1
//         Blackjack blackjack = new Blackjack();
//         QLearningAgent agent = blackjack.new QLearningAgent(.1, 1, .05);
//         // Train the agent for 1000 episodes
//         agent.train(10000);

//         // Find the optimal policy for the agent
//         blackjack.findOptimalPolicy(agent);

//         // Test the agent by playing 100 games and print the results while playing
//         int wins = 0;
//         int losses = 0;
//         int ties = 0;
//         for (int i = 0; i < 100; i++){
//             BlackjackEnv env = blackjack.new BlackjackEnv();
//             State state = blackjack.new State(env.playerSum, env.dealerCard, env.usableAce);
//             while (!env.isTerminal()){
//                 int action = agent.getAction(state);
//                 env.step(action);
//                 State nextState = blackjack.new State(env.playerSum, env.dealerCard, env.usableAce);
//                 state = nextState;
//             }
//             int reward = env.getReward();
//             if (reward == 1){
//                 wins++;
//             } else if (reward == -1){
//                 losses++;
//             } else {
//                 ties++;
//             }
       
//          //   System.out.println("Game " + (i + 1) + ": " + (reward == 1 ? "Win" : reward == -1 ? "Loss" : "Tie"));
//         }
//         System.out.println("Wins: " + wins);
//         System.out.println("Losses: " + losses);
//         System.out.println("Ties: " + ties);
//     }
// }


import java.util.HashMap;

public class Blackjack {
    private static final int MAX_CARD_VALUE = 13;
    private static final int FACE_CARD_VALUE = 10;
    private static final int BLACKJACK = 21;
    private static final int DEALER_HIT_THRESHOLD = 17;

    public class State {
        int playerSum;
        int dealerCard;
        boolean usableAce;

        public State(int playerSum, int dealerCard, boolean usableAce) {
            this.playerSum = playerSum;
            this.dealerCard = dealerCard;
            this.usableAce = usableAce;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof State) {
                State s = (State) obj;
                return s.playerSum == playerSum && s.dealerCard == dealerCard && s.usableAce == usableAce;
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = 17;
            result = 31 * result + playerSum;
            result = 31 * result + dealerCard;
            result = 31 * result + (usableAce ? 1 : 0);
            return result;
        }
    }

    public class QValue {
        State state;
        int action;
        double value;

        public QValue(State state, int action, double value) {
            this.state = state;
            this.action = action;
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof QValue) {
                QValue q = (QValue) obj;
                return q.state.equals(state) && q.action == action;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return state.hashCode() + action;
        }
    }

    public class BlackjackEnv {
        int playerSum, dealerSum, dealerCard;
        boolean usableAce, dealerUsableAce;
        boolean playerStood, dealerStood;

        public BlackjackEnv() {
            reset();
        }

        public void reset() {
            playerSum = 0;
            dealerSum = 0;
            usableAce = false;
            dealerUsableAce = false;
            playerStood = false;
            dealerStood = false;

            int card1 = drawCard();
            int card2 = drawCard();
            dealerCard = drawCard();
            dealerSum = dealerCard;

            playerSum = card1 + card2;
            usableAce = (card1 == 11 || card2 == 11);
            adjustForAces();
        }

        public void step(int action) {
            if (action == 0) {  // Hit
                int card = drawCard();
                playerSum += card;
                adjustForAces();
                if (playerSum > BLACKJACK) playerStood = true;
            } else {  // Stand
                playerStood = true;
            }

            // Dealer logic
            if (playerStood && !dealerStood) {
                dealerUsableAce = dealerCard == 11;
                while (dealerSum < DEALER_HIT_THRESHOLD || 
                       (dealerSum == DEALER_HIT_THRESHOLD && dealerUsableAce)) {
                    int card = drawCard();
                    dealerSum += card;
                    adjustForDealerAces();
                }
                dealerStood = true;
            }
        }

        public void adjustForAces() {
            while (playerSum > BLACKJACK && usableAce) {
                playerSum -= 10;
                usableAce = false;
            }
        }

        public void adjustForDealerAces() {
            while (dealerSum > BLACKJACK && dealerUsableAce) {
                dealerSum -= 10;
                dealerUsableAce = false;
            }
        }

        public boolean isTerminal() {
            return (playerStood && dealerStood) || playerSum > BLACKJACK || dealerSum > BLACKJACK;
        }

        public int getReward() {
            if (playerSum > BLACKJACK) return -1;   // Player bust
            if (dealerSum > BLACKJACK) return 1;    // Dealer bust
            if (playerSum > dealerSum) return 1;    // Player wins
            if (playerSum < dealerSum) return -1;   // Dealer wins
            return 0;                               // Tie
        }

        private int drawCard() {
            int card = (int) (Math.random() * MAX_CARD_VALUE) + 1;
            return card > FACE_CARD_VALUE ? FACE_CARD_VALUE : (card == 1 ? 11 : card);
        }
    }

    public class QLearningAgent {
        HashMap<QValue, Double> qValues;
        double alpha, gamma, epsilon;

        public QLearningAgent(double alpha, double gamma, double epsilon) {
            qValues = new HashMap<>();
            this.alpha = alpha;
            this.gamma = gamma;
            this.epsilon = epsilon;
        }

        public int getAction(State state) {
            // Force the player to stand at 20 or 21
            if (state.playerSum >= 20) {
                return 1;  // Stand
            }
        
            if (Math.random() < epsilon) {
                return (int) (Math.random() * 2);
            }
        
            int bestAction = 0;
            double maxQValue = Double.NEGATIVE_INFINITY;
        
            for (int action = 0; action < 2; action++) {
                QValue q = new QValue(state, action, 0.0);
                double value = qValues.getOrDefault(q, 0.0);
                if (value > maxQValue) {
                    maxQValue = value;
                    bestAction = action;
                }
            }
        
            return bestAction;
        }

        public void updateQValue(State state, int action, double reward, State nextState) {
            QValue currentQ = new QValue(state, action, 0.0);
            double maxQNext = 0.0;

            for (int i = 0; i < 2; i++) {
                QValue nextQ = new QValue(nextState, i, 0.0);
                maxQNext = Math.max(maxQNext, qValues.getOrDefault(nextQ, 0.0));
            }

            double oldQ = qValues.getOrDefault(currentQ, 0.0);
            double newQ = oldQ + alpha * (reward + gamma * maxQNext - oldQ);
            qValues.put(currentQ, newQ);
        }

        public void train(int episodes) {
            for (int i = 0; i < episodes; i++) {
                BlackjackEnv env = new BlackjackEnv();
                State state = new State(env.playerSum, env.dealerCard, env.usableAce);

                while (!env.isTerminal()) {
                    int action = getAction(state);
                    env.step(action);
                    State nextState = new State(env.playerSum, env.dealerCard, env.usableAce);
                    int reward = env.getReward();
                    updateQValue(state, action, reward, nextState);
                    state = nextState;
                }

                epsilon *= 0.999;  // Slow epsilon decay
            }
        }

        public void printPolicy() {
            System.out.println("\nOptimal Policy:");
            for (int playerSum = 12; playerSum <= 21; playerSum++) {
                for (int dealerCard = 1; dealerCard <= 10; dealerCard++) {
                    for (boolean usableAce : new boolean[]{true, false}) {
                        State state = new State(playerSum, dealerCard, usableAce);
                        int action = getAction(state);
                        System.out.printf("Player: %2d, Dealer: %2d, Usable Ace: %-5s -> %s\n",
                                playerSum, dealerCard, usableAce, (action == 0 ? "Hit" : "Stand"));
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Blackjack blackjack = new Blackjack();
        QLearningAgent agent = blackjack.new QLearningAgent(0.1, 1.0, 0.1);

        System.out.println("Training...");
        agent.train(500000);
        System.out.println("Training completed!");

        // Print the optimal policy
        agent.printPolicy();

        // Simulate games
        int wins = 0, losses = 0, ties = 0;
        for (int i = 0; i < 1000; i++) {
            BlackjackEnv env = blackjack.new BlackjackEnv();
            State state = blackjack.new State(env.playerSum, env.dealerCard, env.usableAce);

            while (!env.isTerminal()) {
                int action = agent.getAction(state);
                env.step(action);
                state = blackjack.new State(env.playerSum, env.dealerCard, env.usableAce);
            }

            int reward = env.getReward();
            if (reward == 1) wins++;
            else if (reward == -1) losses++;
            else ties++;
        }

        // Display results
        System.out.println("\nResults:");
        System.out.printf("Wins: %d (%.2f%%)\n", wins, (wins / 10.0));
        System.out.printf("Losses: %d (%.2f%%)\n", losses, (losses / 10.0));
        System.out.printf("Ties: %d (%.2f%%)\n", ties, (ties / 10.0));
    }
}