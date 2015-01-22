package gui;

import algorithms.AlphaBeta;
import algorithms.MonteCarloTreeSearch;
import javafx.fxml.FXML;

public class GameController {

	private MainApp mainApp;

	public GameController() {

	}

	@FXML
	private void initialize() {

	}

	public void callAlgorithm() {
		if (this.mainApp.tour%2 != this.mainApp.player && !this.mainApp.isFinished) {
			algorithms.Algorithm algo;
			switch (this.mainApp.algrithm) {
			case ALPHA_BETA:
				algo = new AlphaBeta(this.mainApp.game);
				break;
			default:
				algo = new MonteCarloTreeSearch(this.mainApp.game);
				break;
			}
			this.mainApp.game.play(algo.run(MainApp.TIMEOUT));
			this.mainApp.isFinished = this.mainApp.game.isEndOfGame() != 0;
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
}
