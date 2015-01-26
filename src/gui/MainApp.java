package gui;

import game.Game;

import java.io.IOException;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
    private BorderPane rootLayout;
    private GameController gameController;
    
    static final int TIMEOUT = 100;
    Game game;
    Algorithm algorithm;
    int player;
    int turn = 0;
    boolean isFinished;
    
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Ultimate Tic Tac Toe");
		this.primaryStage.getIcons().add(new Image("file:resources/images/logo.png"));

        initRootLayout();

        showMenu();
	}
	
	/**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            //primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     */
    public void showMenu() {
        try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("Menu.fxml"));
            AnchorPane menu = (AnchorPane) loader.load();

            // Set the menu into the center of root layout.
            Stage stage = new Stage();
            stage.setTitle("Choose turn");
            stage.setScene(new Scene(menu));
            stage.setOnCloseRequest(e -> {
                System.exit(0);
            });
            stage.show();
            //rootLayout.setCenter(menu);
            
            this.game = null;
            this.algorithm = null;
            this.player = -1;
            this.turn = 0;
            this.isFinished = false;
            
            MenuController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showGame() {
    	try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("Game.fxml"));
            AnchorPane game = (AnchorPane) loader.load();
            
            GameController controller = loader.getController();
            controller.setMainApp(this);
            this.gameController = controller;

            // Set the game into the center of root layout.
            primaryStage.setScene(new Scene(game));
            primaryStage.show();
            //rootLayout.setCenter(game);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void playTurn() {
    	if (this.turn%2 != this.player) {
    		this.gameController.callAlgorithm();
    	}
    	else {
    		this.gameController.highlightPossibleMoves();
    	}
    }
    
	public void play(int move) {
		this.game.play(move);
		this.turn++;
		this.isFinished = this.game.isEndOfGame() != 0;
		
		System.out.println(game.toString());
		if (!this.isFinished) {
			this.playTurn();
		}
		else {
			String[] s = {"", "X", "O", "-"};
			//this.primaryStage.close();
			System.out.println("Vainqueur : " + s[this.game.isEndOfGame()]);
		}
	}
    
    /**
     * Returns the main stage.
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

	public static void main(String[] args) {
		launch(args);
	}
}
