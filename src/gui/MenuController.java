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
		this.mainApp.game = new Game();
		this.mainApp.algrithm = Algorithm.ALPHA_BETA;
		this.mainApp.player = this.first.isSelected() ? 0 : 1;
		this.mainApp.showGame();
	}
	
	public void createMonteCarloTreeSearch() {
		this.mainApp.game = new Game();
		this.mainApp.algrithm = Algorithm.MONTE_CARLO_TREE_SEARCH;
		this.mainApp.player = this.first.isSelected() ? 0 : 1;
		this.mainApp.showGame();
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
    }
}
