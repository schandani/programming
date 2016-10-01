package scrabble.model;

/**
 * 9/29/16 10:27 PM
 */
public class Player {

    private final ScrabbleGame game;
    private final String name;
    private final LetterRack letterRack;

    public Player(String name, ScrabbleGame game) {
        this.name = name;
        this.game = game;
        letterRack = game.getLetterBag().drawInitialSet();
    }

    void playLetterAtSquare(LetterModel letterModel, int row, int col) {
        if (!letterRack.contains(letterModel)) {
            System.err.println("you don't have the letter " + letterModel + ". cancelling");
            return;
        }
        letterRack.remove(letterModel);
        game.addLetterToBoard(letterModel, row, col);
    }

    public LetterRack getLetterRack() {
        return letterRack;
    }
}