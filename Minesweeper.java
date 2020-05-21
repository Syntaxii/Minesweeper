import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Random;

/**
 * This class represents a Minesweeper game.
 *
 * @author Jeffrey Neal <jn02788@georgiasouthern.edu>
 */

public class Minesweeper {
	private String[][] boardArray;
	private boolean[][] mineArray;
	private int rounds = 0;
	private int mines = 0;
	private final double ratio = ((Math.random()*15)/100)+ .15; //ratio for # of mines, between 15~30%
	
	 /**
     * Constructs an object instance of the {@link Minesweeper} class using the
     * information provided in <code>seedFile</code>. Documentation about the
     * format of seed files can be found in the <code>project1.pdf</code>
     * file.
     *
     * @param seedFile the seed file used to construct the game
     */
	public Minesweeper(File seedFile) {
    	Scanner input;
    	try { 
    		input = new Scanner(seedFile);
    		String rowString = input.nextLine(); //temp String to read lines in seed file
    		String colString = input.nextLine();
    		int rows = Integer.parseInt(rowString); //parse and pass string values into rows & cols
    		int cols = Integer.parseInt(colString);
    		if (rows > 10 || rows < 1 || cols > 10 || cols < 1) { // check if board is 1x1 ~ 10x10
    			System.out.println("Board dimensions must be 1x1 ~ 10x10");
    			System.exit(0);
    		}
    		this.boardArray = new String[rows][cols];
    		String mineString = input.nextLine(); //same string parsing and passing as above
    		this.mines = Integer.parseInt(mineString);
    		for (int a = 0; a < rows; a++) { //for loop for board
    			for (int b = 0; b < cols; b++) {
    			this.boardArray[a][b] = "   ";
    			}
    		}
    		Random xCoord = new Random();
    		Random yCoord = new Random();
    		mineArray = new boolean[rows][cols];
    		int row1 = 0, col1 = 0;
    		for(int a = 0; a < mines; a++) { //put mines into board
    	    	int row = xCoord.nextInt(rows);
    	    	int col = yCoord.nextInt(cols);
    	    	if(row == row1 && col == col1 && a > 0) {
    	    		a--;
    	    	}
    	    	row1 = row;
    	    	col1 = col;
    	    	this.mineArray[row][col] = true;
    	    }
    		input.close();
    	} catch (FileNotFoundException error) {
    		System.out.println("Error finding file");
    		System.exit(0);
    	} catch (InputMismatchException error2) {
    		System.out.println("Cannot create game with " + seedFile.getName() + " because it is not formatted properly.");
    	}
    }
	
     // Minesweeper
    /**
     * Constructs an object instance of the {@link Minesweeper} class using the
     * <code>rows</code> and <code>cols</code> values as the game grid's number
     * of rows and columns respectively. Additionally, One quarter (rounded up)
     * of the squares in the grid will will be assigned mines, randomly.
     *
     * @param rows the number of rows in the game grid
     * @param cols the number of cols in the game grid
     */
    public Minesweeper(int rows, int cols) {
    	Random xCoord = new Random();
    	Random yCoord = new Random();
    	if (rows <= 10 && rows > 0 && cols <= 10 && cols > 0) {//loop for user input board
    		mineArray = new boolean[rows][cols];
    		boardArray = new String[rows][cols];
    		for (int a = 0; a < rows; a++) {
    			for (int b = 0; b < cols; b++) {
    				mineArray[a][b] = false;
    				boardArray[a][b] = "   ";
    			}
    		}
    	} else {
    		System.out.println("  Cannot create a mine field with that many rows and/or columns!");
    	}
    	
	    int row1 = 0, col1 = 0;
		double ratio = ((Math.random()*15)/100)+ .15; //ratio for # of mines, between 15~30%
	    mines = (int)Math.ceil(rows*cols*ratio); //# of mines
	    //For loop for mine grid
	    for(int a = 0; a < mines; a++) { //loop for user input mines
	    	int row = xCoord.nextInt(rows);
	    	int col = yCoord.nextInt(cols);
	    	if(row == row1 && col == col1 && a > 0) {
	    		a--;
	    	}
	    	row1 = row;
	    	col1 = col;
	    	mineArray[row][col] = true;
	    }
    } // Minesweeper
    
    private void Commands() { //Commands method
    	Scanner input = new Scanner(System.in);
        String input2 = input.nextLine();
        Scanner userInput = new Scanner(input2);
        String commands = userInput.next();
        //if-else that invokes methods depending on user input
        if (commands.equals("h") || commands.equals("help")) {
        	help();
        } else if (commands.equals("g")|| commands.equals("guess")) {
        	guess(userInput);
        } else if (commands.equals("m") || commands.equals("mark")) {
        	mark(userInput);
        } else if (commands.equals("r") || commands.equals("reveal")) {
        	reveal(userInput);
        } else if (commands.equals("q")|| commands.equals("quit")) {
        	quit();
        } else {
        	System.out.println("Unrecognized command, type [h] or [help] for commands");
        }
    }
    
    private void quit() { //Quit command
    	System.out.println("Thanks for playing!");
    	System.exit(0);
    }

    public void help() { //Help command (prints all commands)
    	System.out.println("Commands available...");
    	System.out.println("-Reveal: r/reveal row col");
    	System.out.println("-Mark: m/mark row col");
    	System.out.println("-Guess: g/guess row col");
    	System.out.println("-Help: h/help");
    	System.out.println("Quit: q/quit");
    }
    
    //Reveal command (prints number of adjacent mines on desired location, gameover if revealing a mine
    //win if all non-mine cells are revealed)
    private void reveal(Scanner userInput) {
    	boolean revealed = false;
    	int revealedRow = 0, revealedCol = 0;
    	revealedRow = userInput.nextInt(); 
    	revealedCol = userInput.nextInt();
    	if(isInBounds(revealedRow, revealedCol) == true) {
    		revealed = true;
    		rounds++;
    		if (!mineArray[revealedRow][revealedCol]) {
    			boardArray[revealedRow][revealedCol] = " " + getNumAdjMines(revealedRow, revealedCol) + " ";
    		} else {
    			gameOver();
    		}
    	}
    }
    
    private void guess(Scanner userInput) { //Guess command
    	boolean guessed = false;
    	int guessedRow = 0, guessedCol = 0;
    	guessedRow = userInput.nextInt();
    	guessedCol = userInput.nextInt();
    	if(isInBounds(guessedRow, guessedCol) == true) {
    		guessed = true;
    		rounds++;
    		boardArray[guessedRow][guessedCol] = " ? ";
    	}
    }
    
    private boolean mark(Scanner userInput) { //Mark command, win game if all mines marked
    	boolean marked = false;
    	int markedRow = 0, markedCol = 0;
    	markedRow = userInput.nextInt();
    	markedCol = userInput.nextInt();
    	if (isInBounds(markedRow, markedCol) == true) {
    		marked = true;
    		rounds++;
    		boardArray[markedRow][markedCol] = " F ";
    	}
    	return marked;
    }
    
    /**
     * Starts the game and execute the game loop.
     */
    private void startGame() {
    	System.out.println("Rounds Played: " + rounds);
    	System.out.println("");
    	printBoard();
    	cmdLine();
    	Commands();
    }
    
    private void run() {
    	startScreen();
    	do { //loop that keeps game running, ends when you meet win conditions or quitting
    		startGame();
    		if(winConditions()) {
    			break;
    		}
    	}
    	while (true);
    	win();
    } // run
    
    private void cmdLine() { // prints "command" line
    	System.out.println("");
    	System.out.print("minesweeper-alpha$ ");
    }
    
    private int getNumAdjMines(int row, int col) {
    	int numAdjMines = 0;
    	for(int a = row - 1; a <= row + 1; a++) {//nested for-loop/if-else that scans adjacent cells and prints # of mines found on reveal location
    		if((a >= 0 && a < boardArray.length) == false) {
    			continue;
    		}
    		for(int b = col-1; b <= col+1; b++) {
    			if(((b >= 0 && b < boardArray[0].length) == false) || (a == row && b == col)) {
    				continue;
    			} else if(mineArray[a][b]) {
    					numAdjMines++;
    			}
    		}
    	}
    	return numAdjMines;
    }	
    
    private boolean isInBounds(int row, int col) { //checks boundaries of input (for commands)
    	boolean inbounds;
    	if (row >= 0 && row < mineArray.length && col >= 0 && col < mineArray.length) {
    		inbounds = true;
    	} else {
    		inbounds = false;
    		System.out.println("Input not in array bounds");
    	}
    	return inbounds;
    }
   
    private void printBoard() { //Prints grid for user to play on
    	for (int a = 0; a < boardArray.length; a++) {
    		System.out.print(a + " |");
    		for (int b = 0; b < boardArray[a].length; b++) {
    			System.out.print(boardArray[a][b]);
    			if(b < boardArray[a].length - 1) {
    				System.out.print("|");
    			}
    		}
			System.out.println("|");
    	}
    	System.out.print("   ");
    	for (int a = 0; a < boardArray[0].length; a++) {
        	System.out.print(a + "   ");
    	}
    	System.out.println();
    }
    
    private boolean winConditions() {
    	boolean minesRevealed = true;
    	boolean squaresRevealed = true;
    	for(int a = 0; a < boardArray.length; a++) {//Nested for-loop/if-else for the necessary win conditions
    		for(int b = 0; b < boardArray[0].length; b++) {
    			if (mineArray[a][b]) {
    				if(boardArray[a][b] != " F ") {
    					minesRevealed = true;
    				}
    			}else {
    				if(boardArray[a][b] ==  " F " || boardArray[a][b] == " ? " || boardArray[a][b] == "   ") {
    				squaresRevealed = false;
    				}
    			}
    		}
    	}
    	return minesRevealed && squaresRevealed;	
    }
    
    public void startScreen() { //Minesweeper ascii screen
    	System.out.println("");
		System.out.println("    /\\/\\ (_)_ __   ___  _____      _____  ___ _ __   ___ _ __");
    	System.out.println("   /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|");
    	System.out.println("  / /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |");
    	System.out.println("  \\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|");
    	System.out.println("                                       ALPHA |_| EDITION");
		System.out.println("");
    }
    
    public void gameOver() { //Gameover screen
    	System.out.println("");
		System.out.println("    Oh no... You revealed a mine!");
    	System.out.println("");
    	System.out.println("    __ _  __ _ _ __ ___   ___    _____   _____ _ __");
    	System.out.println("   / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|");
    	System.out.println("  | (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |");
    	System.out.println("   \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|");
    	System.out.println("   |___/");
    	System.out.println("");
    	System.out.println("Score: " + (Math.round((mines/this.ratio)) - rounds - mines));
    }
     
    public void win() { //Win screen (also shows up if you choose 1x1, which is no fun)
    	System.out.println("");
    	System.out.println("   ___                            _         _       _   _                    _ ");
    	System.out.println("  / __\\___  _ __   __ _ _ __ __ _| |_ _   _| | __ _| |_(_) ___  _ __  ___   / \\");
    	System.out.println(" / /  / _ \\| '_ \\ / _` | '__/ _` | __| | | | |/ _` | __| |/ _ \\| '_ \\/ __| /  /");
    	System.out.println("/ /__| (_) | | | | (_| | | | (_| | |_| |_| | | (_| | |_| | (_) | | | \\__ \\/\\_/ ");
    	System.out.println("\\____/\\___/|_| |_|\\__, |_|  \\__,_|\\__|\\__,_|_|\\__,_|\\__|_|\\___/|_| |_|___/\\/   ");
    	System.out.println("                  |___/                                                        ");
    	System.out.println("");
    	System.out.println("Score: " + (Math.round((mines/this.ratio)) - rounds - mines));
    }
    
    /**
     * The entry point into the program. This main method does implement some
     * logic for handling command line arguments. If two integers are provided
     * as arguments, then a Minesweeper game is created and started with a
     * grid size corresponding to the integers provided and with 10% (rounded
     * up) of the squares containing mines, placed randomly. If a single word
     * string is provided as an argument then it is treated as a seed file and
     * a Minesweeper game is created and started using the information contained
     * in the seed file. If none of the above applies, then a usage statement
     * is displayed and the program exits gracefully.
     *
     * @param args the shell arguments provided to the program
     */
    public static void main(String[] args) {

	/*
	  The following switch statement has been designed in such a way that if
	  errors occur within the first two cases, the default case still gets
	  executed. This was accomplished by special placement of the break
	  statements.
	*/

	Minesweeper game = null;

	switch (args.length) {

        // random game
	case 2:

	    int rows, cols;

	    // try to parse the arguments and create a game
	    try {
		rows = Integer.parseInt(args[0]);
		cols = Integer.parseInt(args[1]);
		game = new Minesweeper(rows, cols);
		break;
	    } catch (NumberFormatException nfe) {
		// line intentionally left blank
	    } // try

	// seed file game
	case 1:

	    String filename = args[0];
	    File file = new File(filename);

	    if (file.isFile()) {
		game = new Minesweeper(file);
		break;
	    } // if

        // display usage statement
	default:

	    System.out.println("Usage: java Minesweeper [FILE]");
	    System.out.println("Usage: java Minesweeper [ROWS] [COLS]");
	    System.exit(0);

	} // switch

	// if all is good, then run the game
	game.run();

    } // main

} // Minesweeper
