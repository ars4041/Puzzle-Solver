package puzzles.tilt.gui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import puzzles.common.Observer;
import puzzles.tilt.TiltTiles;
import puzzles.tilt.model.TiltModel;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import java.io.File;
import java.nio.file.Paths;

import static javafx.scene.layout.Priority.ALWAYS;

/**
 * TiltGUI
 * This class contains the graphical view and controller for the Tilt puzzle within
 * the Model-View-Controller design pattern.
 *
 * @author Ryan O'Malley
 * @github cro5058
 */
public class TiltGUI extends Application
                     implements Observer<TiltModel, String>, TiltTiles {

    /** Constants */
    // Resources directory (located directly underneath the gui package)
    private final static String RESOURCES_DIR = "resources/";
    // Default width for a game piece image
    public static final int PIECE_H_SIZE = 50;
    // Default height for a game piece image
    public static final int PIECE_V_SIZE = 50;

    /** Fields */
    // TiltModel
    private TiltModel model;
    // Game board size
    private int N;
    // Whether a board reload is in progress (pauses game board updates)
    private boolean reloadInProgress;

    /** GUI elements */
    // Label at the top of the GUI
    private Label label = new Label();
    // Game board
    private BorderPane gameBoard;
    // Grid of buttons within the game board
    private GridPane gameGrid;

    // Images of game board pieces
    private Image greenDisk = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "green.png"),
                                        PIECE_H_SIZE, PIECE_V_SIZE, true, true);
    private Image blueDisk  = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "blue.png"),
                                        PIECE_H_SIZE, PIECE_V_SIZE, true, true);
    private Image block     = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "block.png"),
                                        PIECE_H_SIZE, PIECE_V_SIZE, true, true);
    private Image hole      = new Image(getClass().getResourceAsStream(RESOURCES_DIR + "hole.png"),
                                        PIECE_H_SIZE, PIECE_V_SIZE, true, true);


    /**
     * Initializes the model by creating the model, adding this GUI object as an observer,
     * loading the game file into the model, and getting the board size so the grid can be made.
     */
    public void init() {
        // Read in the filename from the input
        String filename = getParameters().getRaw().get(0);

        // Make sure the specified file exists.
        File file = new File(filename);

        // If so...
        if (file.exists()) {
            // Make a TiltModel based on the input filename
            this.model = new TiltModel();

            // Add self as an observer to the model
            this.model.addObserver(this);

            // Load the file
            this.model.loadFile(filename);

            // Remember the size of the board
            this.N = this.model.getBoardSize();

            // No reload in progress
            this.reloadInProgress = false;
        }
        // Otherwise...
        else {
            // Print error message and exit
            System.out.println("Could not load file: " + filename);
            System.exit(1);
        }
    }

    /**
     * Creates and shows the GUI for Tilt.
     * Further documentation from the Application abstract class.
     *
     * @param stage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception if something goes wrong.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Create new empty HBox to hold all the gui elements.
        VBox gui = new VBox();

        // Set up the label at the top of the GUI
        gui.getChildren().add(this.label);

        // Create a new central HBox to hold the game board and sidebar
        HBox center = new HBox();
        VBox.setVgrow(center, ALWAYS);

        // Create the game board and add it to the central HBox
        this.gameBoard = makeGameBoard(stage);

        // Make the gameBoard always grow when the window is resized
        HBox.setHgrow(this.gameBoard, ALWAYS);
        center.getChildren().add(this.gameBoard);

        // Make sidebar and add it to the central HBox
        center.getChildren().add(makeSidebar(stage));

        // Add the central HBox to the gui
        gui.getChildren().add(center);

        // Make a Scene containing the gui
        Scene scene = new Scene(gui);

        // Set the new Scene to be the active Scene in the stage
        stage.setScene(scene);

        // Set up the stage with a title and the gui
        stage.setTitle("Tilt");

        // Show the stage with the gui
        stage.show();
    }

    /**
     * Makes the main game board as a BorderPane,
     * including the grid showing the pieces and the buttons to tilt the board.
     * Returns the game board BorderPane for use in the start() method.
     *
     * @param stage the top-level window in the JavaFX application.
     * @return a BorderPane containing the grid and tilt buttons.
     */
    private BorderPane makeGameBoard(Stage stage) {
        // Create a BorderPane to contain the grid and the buttons
        BorderPane gameBoard = new BorderPane();

        // 1. MAKE TILT BUTTONS
        Button northButton = new Button("^");
        northButton.setMaxWidth(Double.MAX_VALUE);
        northButton.setOnAction(event -> this.model.tilt("N"));
        gameBoard.setTop(northButton);

        Button southButton = new Button("v");
        southButton.setMaxWidth(Double.MAX_VALUE);
        southButton.setOnAction(event -> this.model.tilt("S"));
        gameBoard.setBottom(southButton);

        Button eastButton = new Button(">");
        eastButton.setMaxHeight(Double.MAX_VALUE);
        eastButton.setOnAction(event -> this.model.tilt("E"));
        gameBoard.setRight(eastButton);

        Button westButton = new Button("<");
        westButton.setMaxHeight(Double.MAX_VALUE);
        westButton.setOnAction(event -> this.model.tilt("W"));
        gameBoard.setLeft(westButton);

        // 2. MAKE GAME GRID
        this.gameGrid = makeGrid(stage);

        // Add the game grid to the game board
        gameBoard.setCenter(this.gameGrid);

        // Show initial placement of game pieces from the model
        updateGrid();

        // Return the game board to be added to the gui
        return gameBoard;
    }

    /**
     * Makes the grid of buttons that goes into the game board.
     * This method can also be used to regenerate a new grid upon loading a new file into the model.
     *
     * @param stage the top-level window in the JavaFX application.
     * @return a GridPane that will show the game pieces within a Tilt puzzle.
     */
    private GridPane makeGrid(Stage stage) {
        // Make a GridPane to contain the grid of pieces
        GridPane grid = new GridPane();

        // Set the preferred size of the grid
        //grid.setPrefSize(GRID_H_SIZE, GRID_V_SIZE);

        // Add column and row constraints to make the grid resizable
        for (int i = 0; i < this.N; i++) {
            // Calculate how much space each cell should take up, as a percentage
            double division = 100.0 / this.N;

            ColumnConstraints thisColumnConstraint = new ColumnConstraints();
            RowConstraints thisRowConstraint = new RowConstraints();

            // Set the new constraints to have the appropriate width and height
            thisColumnConstraint.setPercentWidth(division);
            thisRowConstraint.setPercentHeight(division);

            // Add the constraints to the grid
            grid.getColumnConstraints().add(thisColumnConstraint);
            grid.getRowConstraints().add(thisRowConstraint);
        }

        // Add all the buttons to the grid. For each button...
        for (int row = 0; row < this.N; row++) {
            for (int col = 0; col < this.N; col++) {
                // Make the new button
                Button button = new Button();

                // The button will have a preferred size
                button.setPrefSize(PIECE_H_SIZE, PIECE_V_SIZE);

                // But ultimately the button can expand if the user resizes the window
                button.setMaxWidth(Double.MAX_VALUE);
                button.setMaxHeight(Double.MAX_VALUE);

                // Add the button to the grid
                grid.add(button, col, row);
            }
        }

        // Return the newly created grid of buttons
        return grid;
    }

    /**
     * Generates the sidebar of the Tilt GUI, with load, reset, and hint buttons.
     * Returns the sidebar for use inside start().
     *
     * @param stage the top-level window in the JavaFX application.
     * @return a VBox containing the sidebar.
     */
    private VBox makeSidebar(Stage stage) {
        // Create a VBox for load, reset, and hint
        VBox sidebar = new VBox();

        // Add buttons to the sidebar
        Button loadButton = new Button("Load");
        loadButton.setMaxWidth(Double.MAX_VALUE);
        loadButton.setOnAction(event -> chooseFile(stage));
        sidebar.getChildren().add(loadButton);

        Button resetButton = new Button("Reset");
        resetButton.setMaxWidth(Double.MAX_VALUE);
        resetButton.setOnAction(event -> this.model.reset());
        sidebar.getChildren().add(resetButton);

        Button hintButton = new Button("Hint");
        hintButton.setMaxWidth(Double.MAX_VALUE);
        hintButton.setOnAction(event -> this.model.hint());
        sidebar.getChildren().add(hintButton);

        // Center the buttons horizontally and vertically in the sidebar
        sidebar.setAlignment(Pos.CENTER);

        // Return the sidebar to be added to the gui
        return sidebar;
    }

    /**
     * Displays a dialogue box to allow the user to load in a new game board.
     * Also temporarily pauses board updates from update() in case the new board being loaded in
     * is of a different size than the current board.
     *
     * @param stage the top-level window in the JavaFX application.
     */
    private void chooseFile(Stage stage) {
        // Pause board updates while loading in the new board
        // (If the new board is a different size from the current one it causes indexing exceptions)
        this.reloadInProgress = true;

        // Make a new FileChooser to let the user choose a game file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load a game board.");

        // Open up a window for the user to interact with
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Set the directory to the data/tilt folder
        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        currentPath += File.separator + "data" + File.separator + "tilt";
        fileChooser.setInitialDirectory(new File(currentPath));

        // If the file is not null and the board was loaded correctly...
        if (selectedFile != null && this.model.loadFile(selectedFile)) {
            // Get the new board size
            this.N = this.model.getBoardSize();

            // Remove the existing gameGrid from its parent (gameBoard)
            this.gameBoard.setCenter(null);

            // Update the game grid manually because we paused it so it wouldn't happen in update()
            this.gameGrid = makeGrid(stage);
            updateGrid();

            // Add the new gameGrid back to its parent (gameBoard) and resize the window
            this.gameBoard.setCenter(this.gameGrid);
            stage.sizeToScene();
        }

        // Unpause board updates
        this.reloadInProgress = false;
    }

    /**
     * Method to update the GUI based on changes that occur in the model.
     * Updates the status bar to show any messages received,
     * and updates the game board with the current placement of game pieces.
     *
     * @param tiltModel the model that is connected to the GUI.
     * @param message the message to display, as a String.
     */
    @Override
    public void update(TiltModel tiltModel, String message) {
        // Update the message bar to show the new message
        this.label.setText(message);

        // If the board is not currently changing...
        if (!reloadInProgress) {
            // Update the board
            updateGrid();
        }
    }

    /**
     * Helper function for update().
     * Iterates through all tiles in the game grid. For each tile, this method updates
     * the tile's appearance based on the piece that is present at the tile.
     */
    public void updateGrid() {
        for (int row = 0; row < this.N; row++) {
            for (int col = 0; col < this.N; col++) {
                // Grab the current button and the type of tile that should be there
                Button currentButton = (Button) this.gameGrid.getChildren().get(N * row + col);
                char tile = this.model.getGridCell(row, col);

                // Image version - set the graphic of the button to the appropriate image
                switch(tile) {
                    // Empty space
                    case BLANK:
                        currentButton.setGraphic(null);
                        break;
                    // Gray blocker
                    case BLOCKER:
                        currentButton.setGraphic(new ImageView(block));
                        break;
                    // Blue slider
                    case BLUE:
                        currentButton.setGraphic(new ImageView(blueDisk));
                        break;
                    // Exit hole
                    case EXIT:
                        currentButton.setGraphic(new ImageView(hole));
                        break;
                    // Green slider
                    case GREEN:
                        currentButton.setGraphic(new ImageView(greenDisk));
                        break;
                    default:
                        break;
                }

                // Text version - change the letter inside the button
                // currentButton.setText(String.valueOf(tile));
            }
        }
    }

    /**
     * The main method.
     */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TiltGUI filename");
            System.exit(0);
        }
        else {
            Application.launch(args);
        }
    }
}
