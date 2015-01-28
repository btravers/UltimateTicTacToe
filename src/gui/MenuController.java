package gui;

import game.Game;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class MenuController {
	
	private MainApp mainApp;
	
	@FXML
	private CheckBox first;
		
	public MenuController() {
		
	}
	
	@FXML
    private void initialize() {
		
	}
	
	public void createAlphaBeta() {
		this.createGame(Algorithm.ALPHA_BETA);
	}
	
	public void createMonteCarloTreeSearch() {
		this.createGame(Algorithm.MONTE_CARLO_TREE_SEARCH);
	}
	
	private void createGame(Algorithm algoritm) {
		
		if (this.mainApp.gameThread != null) {
			this.mainApp.gameThread.interrupt();
		}
		if (this.mainApp.updaterThread != null) {
			this.mainApp.updaterThread.interrupt();
		}
		
		this.mainApp.turn = 0;
		this.mainApp.isFinished = false;
		
		this.mainApp.game = new Game();
		this.mainApp.algorithm = algoritm;
		this.mainApp.player = this.first.isSelected() ? 0 : 1;
		this.mainApp.showGame();
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
    }
}
