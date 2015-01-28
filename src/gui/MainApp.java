package gui;

import game.Game;

import java.io.IOException;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private GameController gameController;

	GameTask gameTask;
	Updater updater;

	Thread gameThread;
	Thread updaterThread;

	static final int TIMEOUT = 3000;
	Game game;
	Algorithm algorithm;
	int player;
	int turn;
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
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("Menu.fxml"));
			AnchorPane menu = (AnchorPane) loader.load();

			// Set the menu into the center of root layout.
			Stage stage = new Stage();
			stage.setTitle("Choose turn");
			stage.getIcons().add(new Image("file:resources/images/logo.png"));
			stage.setScene(new Scene(menu));
			stage.setOnCloseRequest(e -> {
				System.exit(0);
			});
			stage.show();
			//rootLayout.setCenter(menu);

			MenuController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void showGame() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("Game.fxml"));
			AnchorPane game = (AnchorPane) loader.load();

			GameController controller = loader.getController();
			controller.setMainApp(this);
			this.gameController = controller;

			// Set the game into the center of root layout.
			primaryStage.setScene(new Scene(game));
			primaryStage.show();
			
			this.gameTask = new GameTask();
			this.gameThread = new Thread(this.gameTask);
			this.gameThread.start();

			this.updater = new Updater();
			this.updaterThread = new Thread(this.updater);
			this.updaterThread.start();

			//rootLayout.setCenter(game);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class GameTask extends Task<Void> {

		@Override
		protected Void call() throws Exception {
			while (!isFinished && !Thread.currentThread().isInterrupted()) {
				if (turn%2 != player) {
					gameController.callAlgorithm();
				} else {
					
				}
			}
			String[] s = {"", "X", "O", "-"};
			System.out.println("Vainqueur : " + s[game.isEndOfGame()]);
			return null;
		}
		
	}

	class Updater implements Runnable {

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				Platform.runLater(new Runnable() {

					@Override
					public void run() {
						gameController.update();
					}

				});

				try {
					Thread.sleep(1000);
				} catch (InterruptedException ex) {
				}
			}
		}

	}

	public void play(int move) {
		this.game.play(move);
		this.turn++;
		this.isFinished = this.game.isEndOfGame() != 0;
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
