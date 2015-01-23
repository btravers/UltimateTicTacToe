package gui;

import game.Game;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
    private BorderPane rootLayout;
    
    static final int TIMEOUT = 100;
    Game game;
    Algorithm algorithm;
    int player;
    int tour = 0;
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
            primaryStage.show();
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
            rootLayout.setCenter(menu);
            
            this.game = null;
            this.algorithm = null;
            this.player = -1;
            this.tour = 0;
            this.isFinished = false;
            
            MenuController controller = loader.getController();
            controller.setMainApp(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private GameController c;
    
    public void showGame() {
    	try {
            // Load person overview.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("Game.fxml"));
            AnchorPane game = (AnchorPane) loader.load();

            // Set the menu into the center of root layout.
            rootLayout.setCenter(game);
            
            GameController controller = loader.getController();
            controller.setMainApp(this);
            c = controller;
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*public void autorun() {
        c.autorun();    	
    }*/
    
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
