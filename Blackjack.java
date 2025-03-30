import java.util.HashMap;

public class Blackjack{
    // This file uses Q learning to create an optimal Blackjack player
    // create variable for 4d array with state of the game

    //First, we need to create a class that represents the state of the game
    public class State{
        // The state of the game is represented by the player's current sum, the dealer's face up card, and whether or not the player has a usable ace
        int playerSum;
        int dealerCard;
        boolean usableAce;

        public State(int playerSum, int dealerCard, boolean usableAce){
            this.playerSum = playerSum;
            this.dealerCard = dealerCard;
            this.usableAce = usableAce;
        }

        public boolean equals(Object obj){
            if(obj instanceof State){
                State s = (State) obj;
                return s.playerSum == playerSum && s.dealerCard == dealerCard && s.usableAce == usableAce;
            }
            return false;
        }

        public int hashCode(){
            return playerSum + dealerCard + (usableAce ? 1 : 0);
        }
    }

    // // Next, we need to create a class that represents the Q values for each state-action pair
    // public class QValue {
    //     State state;
    //     int action;
    //     double value;
    
    //     public QValue(State state, int action, double value) {
    //         this.state = state;
    //         this.action = action;
    //         this.value = value;
    //     }
    
    //     @Override
    //     public boolean equals(Object obj) {
    //         if (obj instanceof QValue) {
    //             QValue q = (QValue) obj;
    //             return q.state.equals(state) && q.action == action;
    //         }
    //         return false;
    //     }
    
    //     @Override
    //     public int hashCode() {
    //         return state.hashCode() + action;
    //     }
    // }

    // Now, we need to create a class that represents the Blackjack environment
    public class BlackjackEnv{
        // The environment is represented by the player's current sum, the dealer's face up card, and whether or not the player has a usable ace
        int playerInitCard1, playerInitCard2, playerSum;
        int dealerCard, dealerSum;
        boolean usableAce, dealerUsableAce, playerStood;

        public BlackjackEnv(){
            // Initialize the environment by dealing two cards to the player and one card to the dealer
            // Each card is between 1 and 13. if the card is 10, 11, 12, or 13, it is a 10
            // If the card is 1, it is an ace
            // An ace can either be 1 or 11
            // If the player has <11 total, the ace is 11
            // If the player has >11 total, the ace is 1
            playerInitCard1 = (int) (Math.random() * 13) + 1;
            playerInitCard2 = (int) (Math.random() * 13) + 1;
            playerStood = false;
            if(playerInitCard1 > 10){
                playerInitCard1 = 10;
            }
            if(playerInitCard2 > 10){
                playerInitCard2 = 10;
            }
            if(playerInitCard1 == 1 && playerInitCard2 == 1){
                playerInitCard2 = 11;
            }
            if(playerInitCard1 == 1 && playerInitCard2 > 10){
                playerInitCard1 = 11;
            }
            if(playerInitCard2 == 1 && playerInitCard1 > 10){
                playerInitCard2 = 11;
            }
            if(playerInitCard1 == 1 && playerInitCard2 < 11){
                playerInitCard1 = 11;
            }
            if(playerInitCard2 == 1 && playerInitCard1 < 11){
                playerInitCard2 = 11;
            }
            dealerCard = (int) (Math.random() * 13) + 1;
            if(dealerCard > 10){
                dealerCard = 10;
            }
            usableAce = playerInitCard1 == 11 || playerInitCard2 == 11;
            dealerSum = dealerCard;
            playerSum = playerInitCard1 + playerInitCard2;
        }

        public void reset(){
            // Reset the environment by dealing two cards to the player and one card to the dealer
            playerInitCard1 = (int) (Math.random() * 13) + 1;
            playerInitCard2 = (int) (Math.random() * 13) + 1;
            if(playerInitCard1 > 10){
                playerInitCard1 = 10;
            }
            if(playerInitCard2 > 10){
                playerInitCard2 = 10;
            }
            if(playerInitCard1 == 1 && playerInitCard2 == 1){
                playerInitCard2 = 11;
            }
            else if(playerInitCard1 == 1 && playerInitCard2 > 10){
                playerInitCard1 = 11;
            }
            else if(playerInitCard2 == 1 && playerInitCard1 > 10){
                playerInitCard2 = 11;
            }
            else if(playerInitCard1 == 1 && playerInitCard2 < 11){
                playerInitCard1 = 11;
            }
            else if(playerInitCard2 == 1 && playerInitCard1 < 11){
                playerInitCard2 = 11;
            }
            dealerCard = (int) (Math.random() * 13) + 1;
            if(dealerCard > 10){
                dealerCard = 10;
            }
            usableAce = playerInitCard1 == 11 || playerInitCard2 == 11;
            dealerSum = dealerCard;
            playerStood = false;
            playerSum = playerInitCard1 + playerInitCard2;
        }

        public void step(int action){
            if(action == 0){
                // Player hits
                int card = (int) (Math.random() * 13) + 1;
                if(card > 10){
                    card = 10;
                }
                if(card == 1 && playerSum + 11 <= 21){
                    card = 11;
                }
                playerSum += card;
                if(playerSum > 21 && usableAce){
                    playerSum -= 10;
                    usableAce = false;
                }
            } else {
                // Player stands
                playerStood = true;
        
                dealerUsableAce = dealerCard == 11;
                while (dealerSum < 17 || (dealerSum == 17 && dealerUsableAce)) {
                    int card = (int) (Math.random() * 13) + 1;
                    if (card > 10) card = 10;
                    if (card == 1) {
                        if (dealerSum + 11 <= 21 && !dealerUsableAce) {
                            card = 11;
                            dealerUsableAce = true;
                        } else {
                            card = 1;
                        }
                    }
                    dealerSum += card;
                    if (dealerSum > 21 && dealerUsableAce) {
                        dealerSum -= 10;
                        dealerUsableAce = false;
                    }
                }
            }
        }
        

        public boolean isTerminal(){
            // Check if the game is over
            // The dealer card is only one card and therefore cannot be over 21. 
            // The dealer should bust if their total is over 21\
            return playerSum > 21 || dealerSum > 21 || playerStood;
        }

        public int getReward(){
            // Get the reward for the current state
            // player bust
            if(playerSum > 21){
                return -1;
            }
            // dealer bust
            else if(dealerSum > 21){
                return 1;
            }
            // player sum is greater than dealer sum
            else if(playerSum > dealerSum){
                return 1;
            }
            // player sum is less than dealer sum
            else if(playerSum < dealerSum){
                return -1;
            }
            // player sum is equal to dealer sum
            else{
                return 0;
            }  
        }
    }

    // Now, we need to create a class that represents the Q learning agent
    public class QLearningAgent{
        // The agent is represented by the Q values for each state-action pair
        double alpha;
        double gamma;
        double epsilon;
        double[][][][] q; // indexed by player sum, dealer card, usable ace, and action

        public QLearningAgent(double alpha, double gamma, double epsilon){
            q = new double[32][11][2][2]; 
            this.alpha = alpha;
            this.gamma = gamma;
            this.epsilon = epsilon;
            for (int ps = 0; ps < 32; ps++) {
                for (int dc = 1; dc < 11; dc++) {
                    for (int ua = 0; ua < 2; ua++) {
                        for (int a = 0; a < 2; a++) {
                            q[ps][dc][ua][a] = 0; // optimistic guess
                        }
                    }
                }
            }
        }

        //Get action for state
        public int getAction(State state){
            int ps = Math.min(Math.max(state.playerSum, 0), 31);
            int dc = Math.min(Math.max(state.dealerCard, 1), 10);
            int ua = state.usableAce ? 1 : 0;
        
            if (Math.random() < epsilon) {
                return Math.random() < 0.5 ? 0 : 1; // Random: Hit or Stand
            }
        
            int bestAction = 0;
            double maxQ = Double.NEGATIVE_INFINITY;
        
            for (int action = 0; action < 2; action++) {
                double value = q[ps][dc][ua][action];
                if (value > maxQ) {
                    maxQ = value;
                    bestAction = action;
                }
            }
        
            return bestAction;
        }

        public void updateQValue(State state, int action, double reward, State nextState, boolean isTerminal) {
            // Update the Q value for the given state-action pair
            // QValue qValue = new QValue(state, action, 0);
            int ps = Math.min(state.playerSum, 31);
            int dc = Math.min(Math.max(state.dealerCard, 1), 10);
            int ua = state.usableAce ? 1 : 0;

            int nextPs = Math.min(nextState.playerSum, 31);
            int nextDc = Math.min(Math.max(nextState.dealerCard, 1), 10);
            int nextUa = nextState.usableAce ? 1 : 0;

            double oldQ = q[ps][dc][ua][action];
            double maxQNext = isTerminal ? 0 : Math.max(q[nextPs][nextDc][nextUa][0], q[nextPs][nextDc][nextUa][1]);

            double newQ = oldQ + alpha * (reward + gamma * maxQNext - oldQ);
            q[ps][dc][ua][action] = newQ;
    
          
        }

        //Train the agent for a given number of episodes
        public void train(int episodes) {
            double minEpsilon = 0.01;  // Minimum exploration rate
            double decayRate = Math.pow(minEpsilon / epsilon, 1.0 / episodes);
            int decayStart = episodes / 2;

            for (int i = 0; i < episodes; i++) {
                BlackjackEnv env = new BlackjackEnv();
                State state = new State(env.playerSum, env.dealerCard, env.usableAce);
                
                while (!env.isTerminal()) {
                   
                    int action = getAction(state);
                    env.step(action);
                    State nextState = new State(env.playerSum, env.dealerCard, env.usableAce);
                    boolean terminal = env.isTerminal();
                    int reward = terminal ? env.getReward() : 0;
                    
                    updateQValue(state, action, reward, nextState, terminal);

                    if(env.isTerminal()){
                        break;
                    }
                    state = nextState;
                }
        
                // Gradually decay epsilon
                if (i > decayStart) 
                epsilon *= decayRate;
            }
        }
    }

    //Method for finding the optimal policy
    public void findOptimalPolicy(QLearningAgent agent){
        // Find the optimal policy for the agent
        for(int playerSum = 12; playerSum <= 21; playerSum++){
            for(int dealerCard = 1; dealerCard <= 10; dealerCard++){
                // usable ace switches between true and false
                for(boolean usableAce : new boolean[]{true, false}){
                State state = new State(playerSum, dealerCard, usableAce);
                    int action = agent.getAction(state);
                    System.out.println("Player Sum: " + playerSum + ", Dealer Card: " + dealerCard + ", Usable Ace: " + usableAce + ", Action: " + (action == 0 ? "Hit" : "Stand"));
                }
            }
        }
    }

    public static void main(String[] args){
        // Create a Q learning agent with alpha = 0.1, gamma = 0.9, and epsilon = 0.1
        Blackjack blackjack = new Blackjack();
        QLearningAgent agent = blackjack.new QLearningAgent(.1, 1, .05);
        // Train the agent for 1000 episodes
        agent.train(10000000);

        // Find the optimal policy for the agent
        System.out.println("Optimal Policy:");
        blackjack.findOptimalPolicy(agent);

        // Test the agent by playing 100 games and print the results while playing
        int wins = 0;
        int losses = 0;
        int ties = 0;
        for (int i = 0; i < 100; i++){
            BlackjackEnv env = blackjack.new BlackjackEnv();
            State state = blackjack.new State(env.playerSum, env.dealerCard, env.usableAce);
            while (!env.isTerminal()){
                int action = agent.getAction(state);
                env.step(action);
                State nextState = blackjack.new State(env.playerSum, env.dealerCard, env.usableAce);
                state = nextState;
            }

            int reward = env.getReward();
            if (reward == 1){
                wins++;
            } else if (reward == -1){
                losses++;
            } else {
                ties++;
            }
       
         //   System.out.println("Game " + (i + 1) + ": " + (reward == 1 ? "Win" : reward == -1 ? "Loss" : "Tie"));
        }
        System.out.println("Wins: " + wins);
        System.out.println("Losses: " + losses);
        System.out.println("Ties: " + ties);
    }


}

