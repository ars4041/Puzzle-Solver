package puzzles.chess.gui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import puzzles.chess.model.ChessModel;
import puzzles.common.Observer;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * A GUI for a game of solitaire chess.
 *
 * @author Aidan Sanderson
 */
public class ChessGUI extends Application implements Observer<ChessModel, String> {

    /** The resources directory is located directly underneath the gui package */
    private final static String RESOURCES_DIR = "resources/";

    /** pawn image. */
    private final Image pawn = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"pawn.png"));

    /** rook image. */
    private final Image rook = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"rook.png"));

    /** bishop image. */
    private final Image bishop = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"bishop.png"));

    /**  knight image. */
    private final Image knight = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"knight.png"));

    /** king image. */
    private final Image king = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"king.png"));

    /** queen image. */
    private final Image queen = new Image(getClass().getResourceAsStream(RESOURCES_DIR+"queen.png"));

    /** The model for the solitaire chess game. */
    private ChessModel model;

    /** The file for the game. */
    private File file;

    /** The borderpane as the main root of the scene. */
    private BorderPane borderPane;

    /** The grid for the current model configuration. */
    private String[][] grid;

    /** The label containing the message at the top of the scene. */
    private Label message;

    /** The main stage. */
    private Stage stage;

    /** Has the from piece been selected? */
    private boolean fromSelection;

    /** Has the to piece been selected? */
    private boolean toSelection;

    /** The row of the from piece. */
    private int fromRow;

    /** The column of the from piece. */
    private int fromCol;

    /** The row of the to piece. */
    private int toRow;

    /** The column of the to piece. */
    private int toCol;


    /**
     * Creates the borderpane that is used to organize each component of the solitaire chess game, and initializes all the pieces on it.
     */
    public void createBorderPane() {
        this.fromSelection = false;
        this.toSelection = false;
        //init border pane
        this.borderPane = new BorderPane();
        // create the board of buttons
        setBoard();

        //font for text
        Font font = new Font("Ariel", 20);

        //load button
        Button loadButton = new Button("Load");
        loadButton.setPrefSize(100,50);
        loadButton.setFont(font);
        loadButton.setStyle("-fx-animated: false;");
        loadButton.setOnAction((event) -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load a new game");
            String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
            currentPath += File.separator + "data" + File.separator + "chess";
            fileChooser.setInitialDirectory(new File(currentPath));
            File loadFile = fileChooser.showOpenDialog(new Stage());
            this.model.load(loadFile.getPath());
        });

        //reset button
        Button resetButton = new Button("Reset");
        resetButton.setPrefSize(100,50);
        resetButton.setFont(font);
        resetButton.setStyle("-fx-animated: false;");
        resetButton.setOnAction((event) -> {
            this.model.reset();
        });

        //hint button
        Button hintButton = new Button("Hint");
        hintButton.setPrefSize(100,50);
        hintButton.setFont(font);
        hintButton.setStyle("-fx-animated: false;");
        hintButton.setOnAction((event) -> {
            this.model.hint();
        });

        //alignment HBox for load, reset, and hint buttons
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(loadButton);
        buttonBox.getChildren().add(resetButton);
        buttonBox.getChildren().add(hintButton);
        this.borderPane.setBottom(buttonBox);

        //Message label
        this.message = new Label("Loaded: " + this.file.getName());
        this.message.setFont(font);
        this.message.setStyle("-fx-font-weight: bold");
        HBox labelBox = new HBox();
        labelBox.setAlignment(Pos.CENTER);
        labelBox.getChildren().add(this.message);
        this.borderPane.setTop(labelBox);
    }

    /**
     * Sets the current state of the chess board. Each spot is a button and can be used for the selection and capturing process of the model.
     */
    public void setBoard() {
        GridPane board = new GridPane();
        this.grid = this.model.getConfig().getGrid();
        String color;
        color = "white";
        for (int row = 0; row < this.grid.length; row++) {
            for (int col = 0; col < this.grid[0].length; col++) {
                Button button = new Button();
                button.setPrefSize(100,100);
                button.setStyle("-fx-color: " + color );
                Image graphic = getGraphic(row,col);
                Node graphicNode = new ImageView(graphic);
                button.setGraphic(graphicNode);
                int finalRow = row;
                int finalCol = col;
                button.setOnAction((event) -> {
                    //if the first selection has not been made, does that
                    if (!fromSelection) {
                        this.fromSelection = true;
                        this.fromRow = finalRow;
                        this.fromCol = finalCol;
                    }
                    //does the second selection if the first has been made
                    else {
                        this.toSelection = true;
                        this.toRow = finalRow;
                        this.toCol = finalCol;
                    }
                    //makes sure the selections are valid
                    boolean isValid = this.model.isValidSelection(finalRow, finalCol);
                    //If both selections have been made and are valid, the model attempts to make the move.
                    if (isValid && this.toSelection) {
                        this.model.select(this.fromRow,this.fromCol,this.toRow,this.toCol);
                    }

                });
                board.add(button, col, row);
                if (color.equals("white")) {
                    color = "blue";
                }
                else {
                    color = "white";
                }
            }
            if (row % 2 == 0) {
                color = "blue";
            }
            else {
                color = "white";
            }
        }
        board.setGridLinesVisible(true);
        board.setAlignment(Pos.CENTER);
        this.borderPane.setCenter(board);
    }

    /**
     * Gets the appropriate image for a given spot on the board based on what type of piece is there.
     *
     * @param row The row of the spot on the board.
     * @param col The column of the spot on the board.
     * @return The image for the spot based on what piece is there (if there is one at all.)
     */
    public Image getGraphic(int row, int col) {
        String piece = this.grid[row][col];
        switch (piece) {
            case "P":
                return pawn;
            case "R":
                return rook;
            case "B":
                return bishop;
            case "N":
                return knight;
            case "K":
                return king;
            case "Q":
                return queen;
            default:
                return null;
        }
    }

    /**
     * Initializes the GUI by creating the initial file from the given file name in the command line argument.
     * If the file is valid, the model is created, the borderpane is created, and the gui is added as an observer of the model.
     */
    public void init() {
        String fileName = getParameters().getRaw().get(0);
        this.file = new File(fileName);
        try {
            this.model = new ChessModel(this.file);
        }
        catch (IOException e) {
            System.out.println("Can't read file: " + this.file.getName());
            System.exit(1);
        }
        createBorderPane();
        this.model.addObserver(this);
    }

    /**
     * Sets up and displays the gui once all the components for it are initialized.
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception An exception for when something goes wrong with the application.
     *
     * @precondition The components for the scene have already been initialized.
     */
    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Scene scene = new Scene(this.borderPane);
        this.stage.setScene(scene);
        this.stage.setTitle("Chess GUI");
        stage.show();
    }

    /**
     * Updates the view using the most recent version of the models logic. The message can decide what the gui should do next.
     *
     * @param chessModel the object that wishes to inform this object
     *                about something that has happened.
     * @param message optional data the server.model can send to the observer
     */
    @Override
    public void update(ChessModel chessModel, String message) {
        //resets the selection proccess if a selection spot is invalid
        if (message.startsWith("In")) {
            this.message.setText(message);
            this.fromSelection = false;
            this.toSelection = false;
            this.model.setFirstSelection(false);
        }
        else if (message.startsWith("Sel")) {
            this.message.setText(message);
        }
        //sets the board to the current version of the models logic.
        else {
            this.fromSelection = false;
            this.toSelection = false;
            this.model.setFirstSelection(false);
            this.message.setText(message);
            setBoard();
            this.stage.sizeToScene();
        }
    }

    /**
     * Starts the program by starting the javafx application while supplying the command line arguments for the file creation for the model.
     *
     * @param args The command line arguments which should contain the file name for the models ChessConfiguration.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java ChessGUI filename");
            System.exit(0);
        } else {
            Application.launch(args);
        }
    }
}
