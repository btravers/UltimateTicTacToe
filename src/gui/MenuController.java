package gui;

import game.Game;
import javafx.fxml.FXML;

public class MenuController {
	
	private MainApp mainApp;
		
	public MenuController() {
		
	}
	
	@FXML
    private void initialize() {
		
	}
	
	public void createPlayerVSComputerAlphaBeta() {
		this.mainApp.game = new Game();
		this.mainApp.algrithm = Algorithm.ALPHA_BETA;
		this.mainApp.player = 0;
		this.mainApp.showGame();
	}
	
	public void createComputerVSPlayerAlphaBeta() {
		this.mainApp.game = new Game();
		this.mainApp.algrithm = Algorithm.ALPHA_BETA;
		this.mainApp.player = 1;
		this.mainApp.showGame();
	}
	
	public void createPlayerVSComputerMonteCarlo() {
		this.mainApp.game = new Game();
		this.mainApp.algrithm = Algorithm.MONTE_CARLO_TREE_SEARCH;
		this.mainApp.player = 0;
		this.mainApp.showGame();
	}
	
	public void createComputerVSPlayerMonteCarlo() {
		this.mainApp.game = new Game();
		this.mainApp.algrithm = Algorithm.MONTE_CARLO_TREE_SEARCH;
		this.mainApp.player = 1;
		this.mainApp.showGame();
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
    }
}
