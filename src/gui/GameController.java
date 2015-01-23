package gui;

import game.Game;
import algorithms.AlphaBeta;
import algorithms.MonteCarloTreeSearch;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class GameController {

	private MainApp mainApp;
	
	@FXML private GridPane grid0;
	@FXML private GridPane grid1;
	@FXML private GridPane grid2;
	@FXML private GridPane grid3;
	@FXML private GridPane grid4;
	@FXML private GridPane grid5;
	@FXML private GridPane grid6;
	@FXML private GridPane grid7;
	@FXML private GridPane grid8;
	
	private GridPane[] gridPanes;

	public GameController() {

	}

	@FXML
	private void initialize() {
		this.gridPanes = new GridPane[] {grid0, grid1, grid2, grid3, grid4, grid5, grid6, grid7, grid8};
	}

	public void callAlgorithm() {
		if (this.mainApp.tour%2 != this.mainApp.player && !this.mainApp.isFinished) {
			algorithms.Algorithm algo;
			switch (this.mainApp.algorithm) {
			case ALPHA_BETA:
				algo = new AlphaBeta(this.mainApp.game);
				break;
			default:
				algo = new MonteCarloTreeSearch(this.mainApp.game);
				break;
			}
			
			int move = algo.run(MainApp.TIMEOUT);
			this.updateDisplay(move);
			this.mainApp.game.play(move);
			this.mainApp.isFinished = this.mainApp.game.isEndOfGame() != 0;
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	/*public void autorun() {
		while (!this.mainApp.isFinished) {
			this.callAlgorithm();
		}
	}*/
	
	private void updateDisplay(int move) {		
		String token = "file:resources/images/" + (this.mainApp.game.getCurrentPlayer() == Game.CROSS ? "cross.png" : "circle.png");
		Image image = new Image(token);
		
		int babySquare = move / 9;
		
		int square = move - babySquare * 9;
		
		GridPane gp = this.gridPanes[babySquare]; 
		gp.add(new ImageView(image), square%3, square/3);
	}
}
