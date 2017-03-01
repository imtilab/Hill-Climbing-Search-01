
import java.util.Random;
import java.util.Stack;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mti
 */

//heuristic function f(h) is lowest possible f (n) from current state

public class Eight_Puzzle {

    //solution state of the 8-puzzle game
    int goal_state[][] = {
        {1, 2, 3},
        {8, 0, 4},
        {7, 6, 5}
    };

    //problem board of 8-puzzle game
    int game_board[][] = {
        {2, 6, 3},
        {1, 0, 4},
        {8, 7, 5}
    };
    /* one local maxima input example input
     {2, 8, 3},
     {1, 6, 4},
     {7, 0, 5}
     */
    /* one solved input example
     {1, 3, 4},
     {8, 2, 5},
     {0, 7, 6}
    
     {2, 0, 6},
     {1, 4, 3},
     {8, 7, 5}
     //nice backtrack not solved in local maxima
     {2, 6, 3},
     {1, 0, 4},
     {8, 7, 5}
     */
    /* one no backtrack local maxima test input example
     {1, 4, 0},
     {8, 3, 2},
     {7, 6, 5}
     */
    /* one impossible local maxima test input example
     {1, 2, 0},
     {8, 3, 4},
     {7, 6, 5}
    
     using best solved
     {8, 2, 3},
     {0, 6, 4},
     {7, 1, 5}
    
     not using best
     {1, 0, 2},
     {8, 3, 6},
     {7, 4, 5}
     */
    //initial empty tile position
    int emptyTile_row = 0;
    int emptyTile_col = 0;
    int stepCounter = 0;

    int min_fn;
    Node min_fn_node;

    Random random = new Random();
    Stack<Node> stack_state = new Stack<>();//for backtracking

    //initializations
    public void initializations() {

        locateEmptyTilePosition();//set empty tile position
        min_fn = get_fn(game_board);//-? min fn

        System.out.println("=========================================");
        printState(game_board, "initial problem state");
        System.out.println("initial empty tile position: " + emptyTile_row + ", " + emptyTile_col);
        System.out.println("initial fn (number of misplaced tiles): " + min_fn);
        System.out.println("=========================================");

        //start hill climbing search
        try {
            hill_climbing_search();
        } catch (Exception e) {
            System.out.println("Goal can not be reached, found closest solution state");
            printState(min_fn_node.state, "---------solution state------with min fn " + min_fn);
        }
    }

    //start hill climbing search for 8-puzzle problem
    public void hill_climbing_search() throws Exception {

        while (true) {
            System.out.println(">========================================<");
            System.out.println("cost/steps: " + (++stepCounter));
            System.out.println("-------------");

            //Priority.preState = game_board;//change pre state
            Node lowestPossible_fn_node = getLowestPossible_fn_node();
            addToStackState(Priority.neighbors_nodeArray);//add neighbors to stack in high to low order fn

            printState(lowestPossible_fn_node.state, "-------new state");
            //print all fn values
//            System.out.print("all sorted fn of current state: ");
//            for (int i = 0; i < Priority.neighbors_nodeArray.length; i++) {
//                System.out.print(Priority.neighbors_nodeArray[i].fn + " ");
//            }
//            System.out.println();

            //check for local maxima
            int fnCounter = 1;
            for (int i = 1; i < Priority.neighbors_nodeArray.length; i++) {
                if (Priority.neighbors_nodeArray[i - 1].fn == Priority.neighbors_nodeArray[i].fn) {//fns are equal
                    fnCounter++;
                }
            }
            if (Priority.neighbors_nodeArray.length != 1 && fnCounter == Priority.neighbors_nodeArray.length) {//all fns are equal, equal chances to choose
                System.out.println("---fn's are equal, found in local maxima---");

                //backtracking
                for (int i = 0; i < Priority.neighbors_nodeArray.length; i++) {
                    if (stack_state != null) {
                        System.out.println("pop " + (i + 1));
                        stack_state.pop();
                    } else {
                        System.out.println("empty stack inside loop");
                    }
                }

                if (stack_state != null) {
                    Node gameNode = stack_state.pop();
                    game_board = gameNode.state;//update game board
                    Priority.preState = gameNode.parent;//update prestate
                    locateEmptyTilePosition();//locate empty tile for updated state

                    printState(game_board, "popped state from all equal fn");
                    System.out.println("empty tile position: " + emptyTile_row + ", " + emptyTile_col);
                } else {
                    System.out.println("stack empty inside first lm check");
                }
            } else {//for backtracking

                System.out.println("lowest fn: " + lowestPossible_fn_node.fn);

                if (lowestPossible_fn_node.fn == 0) {//no misplaced found
                    System.out.println("-------------------------");
                    System.out.println("8-Puzzle has been solved!");
                    System.out.println("-------------------------");
                    System.out.println("Total cost/steps to reach the goal: " + stepCounter);
                    System.out.println("-------------------------------------");
                    break;
                }

                if (lowestPossible_fn_node.fn <= min_fn) {
                    min_fn = lowestPossible_fn_node.fn;
                    min_fn_node = lowestPossible_fn_node;//store lowest fn solution

                    if (stack_state != null) {
                        Node gameNode = stack_state.pop();
                        game_board = gameNode.state;//update game board
                        Priority.preState = gameNode.parent;//update prestate
                        locateEmptyTilePosition();//locate empty tile for updated state

                        printState(game_board, "-------new state as going deeper");
                        System.out.println("empty tile position: " + emptyTile_row + ", " + emptyTile_col);
                    } else {
                        System.out.println("stack empty");
                    }

                } else {
                    System.out.println("---stuck in local maxima---");
                    System.out.println("getting higher, not possible");
                //break;

                    //backtracking
                    for (int i = 0; i < Priority.neighbors_nodeArray.length; i++) {
                        if (stack_state != null) {
                            //System.out.println("pop " + (i + 1));
                            stack_state.pop();
                        } else {
                            System.out.println("empty stack inside loop");
                        }

                    }
                    if (stack_state != null) {

                        Node gameNode = stack_state.pop();
                        game_board = gameNode.state;//update game board
                        Priority.preState = gameNode.parent;//update prestate
                        locateEmptyTilePosition();//locate empty tile for updated state

                        printState(game_board, "popped state from getting higher");
                        System.out.println("empty tile position: " + emptyTile_row + ", " + emptyTile_col);
                    } else {
                        System.out.println("stack empty inside second lm check");
                    }
                }//end of if cond: new fn<=pre min fn 
            }//end of if cond: all fn equal
        }//while end
    }

    private Node getLowestPossible_fn_node() {

        if (emptyTile_row == 0 && emptyTile_col == 0) {//0,0 position is empty tile
            //System.out.println("Empty 0,0");
            Node fn_array[] = {get_fn_down(), get_fn_right()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        } else if (emptyTile_row == 0 && emptyTile_col == 1) {//0,1 position is empty tile
            //System.out.println("Empty 0,1");
            Node fn_array[] = {get_fn_left(), get_fn_down(), get_fn_right()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        } else if (emptyTile_row == 0 && emptyTile_col == 2) {//0,2 position is empty tile
            //System.out.println("Empty 0,2");
            Node fn_array[] = {get_fn_left(), get_fn_down()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        } else if (emptyTile_row == 1 && emptyTile_col == 0) {//1,0 position is empty tile
            //System.out.println("Empty 1,0");
            Node fn_array[] = {get_fn_down(), get_fn_right(), get_fn_up()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        } else if (emptyTile_row == 1 && emptyTile_col == 1) {//1,1 position is empty tile
            //System.out.println("Empty 1,1");
            Node fn_array[] = {get_fn_left(), get_fn_down(), get_fn_right(), get_fn_up()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        } else if (emptyTile_row == 1 && emptyTile_col == 2) {//1,2 position is empty tile
            //System.out.println("Empty 1,2");
            Node fn_array[] = {get_fn_left(), get_fn_down(), get_fn_up()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        } else if (emptyTile_row == 2 && emptyTile_col == 0) {//2,0 position is empty tile
            //System.out.println("Empty 2,0");
            Node fn_array[] = {get_fn_right(), get_fn_up()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        } else if (emptyTile_row == 2 && emptyTile_col == 1) {//2,1 position is empty tile
            //System.out.println("Empty 2,1");
            Node fn_array[] = {get_fn_left(), get_fn_right(), get_fn_up()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        } else if (emptyTile_row == 2 && emptyTile_col == 2) {//2,2 position is empty tile
            //System.out.println("Empty 2,2");
            Node fn_array[] = {get_fn_left(), get_fn_up()};
            Node lowest_fn_node = Priority.sort(fn_array);
            return lowest_fn_node;

        }
        return null;
    }

    //----------------------------
    //return number of misplaced tiles for left state
    private Node get_fn_left() {

        int left_state[][] = new int[game_board.length][game_board[0].length];
        for (int i = 0; i < game_board.length; i++) {
            for (int j = 0; j < game_board[0].length; j++) {

                if (i == emptyTile_row && j == emptyTile_col) {//empty tile, swap left
                    left_state[i][j] = game_board[i][j - 1];
                    left_state[i][j - 1] = game_board[i][j];
                } else {//normal copy
                    left_state[i][j] = game_board[i][j];
                }
            }
        }
        printState(left_state, "left state");//print left state
        Node node = new Node(get_fn(left_state), left_state, game_board);
        return node;
    }

    //return number of misplaced tiles for right state
    private Node get_fn_right() {

        int right_state[][] = new int[game_board.length][game_board[0].length];
        for (int i = 0; i < game_board.length; i++) {
            for (int j = 0; j < game_board[0].length; j++) {

                if (i == emptyTile_row && j == emptyTile_col) {//empty tile, swap right
                    right_state[i][j] = game_board[i][j + 1];
                    right_state[i][j + 1] = game_board[i][j];
                    j++;//as j++ position already copied/updated 
                } else {//normal copy
                    right_state[i][j] = game_board[i][j];
                }
            }
        }

        printState(right_state, "right state");//print right state
        Node node = new Node(get_fn(right_state), right_state, game_board);
        return node;
    }

    //return number of misplaced tiles for up state
    private Node get_fn_up() {

        int up_state[][] = new int[game_board.length][game_board[0].length];
        for (int i = 0; i < game_board.length; i++) {
            for (int j = 0; j < game_board[0].length; j++) {

                if (i == emptyTile_row && j == emptyTile_col) {//empty tile, swap up
                    up_state[i][j] = game_board[i - 1][j];
                    up_state[i - 1][j] = game_board[i][j];
                } else {//normal copy
                    up_state[i][j] = game_board[i][j];
                }
            }
        }
        printState(up_state, "up state");//print up state
        Node node = new Node(get_fn(up_state), up_state, game_board);
        return node;
    }

    //return number of misplaced tiles for down state
    private Node get_fn_down() {

        int down_state[][] = new int[game_board.length][game_board[0].length];
        for (int i = 0; i < game_board.length; i++) {
            for (int j = 0; j < game_board[0].length; j++) {

                if ((i - 1) == emptyTile_row && j == emptyTile_col) {//down pos of empty tile, swap down
                    down_state[i][j] = game_board[i - 1][j];
                    down_state[i - 1][j] = game_board[i][j];
                } else {//normal copy
                    down_state[i][j] = game_board[i][j];
                }
            }
        }
        printState(down_state, "down state");//print down state
        Node node = new Node(get_fn(down_state), down_state, game_board);
        return node;
    }

    //takes a game state and returns number of misplaced tiles
    private int get_fn(int[][] game_state) {

        int fn_count = 0;
        for (int i = 0; i < game_state.length; i++) {
            for (int j = 0; j < game_state[0].length; j++) {
                if (game_state[i][j] != goal_state[i][j] && game_state[i][j] != 0) {//found misplaced tiles
                    fn_count++;
                }
            }
        }
        return fn_count;
    }

    //takes parent removed,  sorted node array and add states to stack in high to low order 
    private void addToStackState(Node nodeArray[]) {
        for (int i = nodeArray.length - 1; i >= 0; i--) {
            stack_state.add(nodeArray[i]);//highest fn to lowest fn
        }
    }

    //find out the new empty tile position for current state
    private void locateEmptyTilePosition() {

        nestedloop://to break inner and outer loop
        for (int i = 0; i < game_board.length; i++) {
            for (int j = 0; j < game_board[0].length; j++) {
                if (game_board[i][j] == 0) {
                    emptyTile_row = i;
                    emptyTile_col = j;
                    break nestedloop;
                }
            }
        }
    }

    //print the current state of the game board
    private void printState(int[][] state, String message) {
        System.out.println(message);
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state[0].length; j++) {
                System.out.print(state[i][j] + "  ");
            }
            System.out.println();
        }
        System.out.println("--------");
    }
}
