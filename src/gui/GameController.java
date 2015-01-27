package gui;

import java.util.List;

import game.Game;
import algorithms.AlphaBeta;
import algorithms.MonteCarloTreeSearch;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
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
			this.mainApp.play(move);
		}
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
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
		if (this.mainApp.turn%2 == this.mainApp.player) {
			String id = ((Node) event.getSource()).getId();
			int move = Integer.parseInt(id.substring(id.length() - 2));

			List<Integer> possibleMoves = this.mainApp.game.getSuccessors();

			if (possibleMoves.contains(move)) {
				this.mainApp.play(move);
			}
		}
	}

	public void update() {
		Image Xtoken = new Image("file:resources/images/cross.png");
		Image Otoken = new Image("file:resources/images/circle.png");

		for (int i=0; i<9; i++) {
			for (int j=0; j<9; j++) {
				final StackPane p = (StackPane) this.gridPanes[i].getChildren().get(j);
				p.getChildren().clear();

				if (this.mainApp.game.getDaddySquareValue(i) == Game.CROSS) {
					p.getChildren().add(new ImageView(Xtoken));
				} else if (this.mainApp.game.getDaddySquareValue(i) == Game.CIRCLE) {
					p.getChildren().add(new ImageView(Otoken));
				} else if (this.mainApp.game.getDaddySquareValue(i) == Game.DRAW) {
					Rectangle r = new Rectangle();
					r.setFill(Color.GREY);
					r.setHeight(60.0);
					r.setWidth(60.0);
					p.getChildren().add(r);
				} else {
					if (this.mainApp.game.getBabySquareValue(i*9+j) == Game.CROSS) {
						p.getChildren().add(new ImageView(Xtoken));
					} else if (this.mainApp.game.getBabySquareValue(i*9+j) == Game.CIRCLE) {
						p.getChildren().add(new ImageView(Otoken));
					}
				}
			}
		}

		if (this.mainApp.turn%2 == this.mainApp.player) {
			this.highlightPossibleMoves();
		}
	}
}
