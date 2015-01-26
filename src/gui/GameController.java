package gui;

import java.util.List;

import game.Game;
import algorithms.AlphaBeta;
import algorithms.MonteCarloTreeSearch;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
		if (this.mainApp.turn%2 != this.mainApp.player && !this.mainApp.isFinished) {
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
			this.mainApp.play(move);
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}
	
	public void autorun() {
		//while (!this.mainApp.isFinished) {
		//	this.callAlgorithm();
		//}
		
		System.out.println(grid0.getChildren());
	}
	
	public void highlightPossibleMoves() {
		List<Integer> possibleMoves = this.mainApp.game.getSuccessors();
		Color higlight = Color.GREENYELLOW; 
		
		int counter = 0;
		for (int i = 0; i < gridPanes.length; i++) {
			for (Node pane : gridPanes[i].getChildren()) {
				if (pane instanceof StackPane) {
					if (possibleMoves.contains(counter)) {
						Rectangle r = new Rectangle();
						r.setFill(higlight);
						r.setHeight(60.0);
						r.setWidth(60.0);
						((StackPane) pane).getChildren().add(r);
					}
					else {
						((StackPane) pane).getChildren().removeIf((n) -> 
							n instanceof Rectangle && ((Rectangle) n).getFill() == higlight);
					}
					
					counter++;
				}
			}
		}
	}
	
	public void onMouseClick(Event event) {
		String id = ((Node) event.getSource()).getId();
		int move = Integer.parseInt(id.substring(id.length() - 2));
		
		List<Integer> possibleMoves = this.mainApp.game.getSuccessors();
		
		if (possibleMoves.contains(move)) {
			this.updateDisplay(move);
			this.mainApp.play(move);
		}
	}
	
	private void updateDisplay(int move) {
		Image Xtoken = new Image("file:resources/images/cross.png");
		Image Otoken = new Image("file:resources/images/circle.png");
		
		int daddySquare = move / 9;
		int babySquare = move - daddySquare * 9;

		// Attention, ceci est très sale. Ne pas reproduire à la maison !
		// updateDisplay est appelé avant que le coup ne soit joué, donc daddyTable n'est pas à jour.
		// J'ai cloné le jeu pour pouvoir joué le coup et avoir la bonne valeur...
		Game g = this.mainApp.game.clone();
		g.play(move);
		byte value = g.getDaddySquareValue(daddySquare);
		
		if (value == Game.EMPTY) {
			final StackPane p = (StackPane) this.gridPanes[daddySquare].getChildren().get(babySquare);
			
			if (this.mainApp.game.getCurrentPlayer() == Game.CROSS) {
				p.getChildren().add(new ImageView(Xtoken));
			}
			else {
				p.getChildren().add(new ImageView(Otoken));
			}
		}
		else if (value == Game.CROSS) {
			for (Node p : this.gridPanes[daddySquare].getChildren()) {
				if (p instanceof StackPane) {
					((StackPane) p).getChildren().clear();
					((StackPane) p).getChildren().add(new ImageView(Xtoken));
				}
			}
		}
		else if (value == Game.CIRCLE) {
			for (Node p : this.gridPanes[daddySquare].getChildren()) {
				if (p instanceof StackPane) {
					((StackPane) p).getChildren().clear();
					((StackPane) p).getChildren().add(new ImageView(Otoken));
				}
			}
		}
		else {
			for (Node p : this.gridPanes[daddySquare].getChildren()) {
				if (p instanceof StackPane) {
					((StackPane) p).getChildren().clear();
					
					Rectangle r = new Rectangle();
					r.setFill(Color.GREY);
					r.setHeight(60.0);
					r.setWidth(60.0);
					((StackPane) p).getChildren().add(r);
				}
			}
		}
	}
}
