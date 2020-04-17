package models.test.results;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import controllers.GrammarTestController;
import controllers.items.GrammarSummaryController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class GrammarStructure extends RecursiveTreeObject<GrammarStructure> {
	public String name;
	public int score;

	public GrammarStructure(String name, int score) {
		this.name = name;
		this.score = score;
	}

	public GrammarStructure(String name) {
		this.name = name;
	}

	public StringProperty getNameProperty() {
		return new SimpleStringProperty(name);
	}

	public StringProperty getScoreProperty() {
		if (score == -1)
			return new SimpleStringProperty("");
		else
			return new SimpleStringProperty(String.valueOf(score));
	}

	public StringProperty getEvaluationProperty() {
		if (score == -1)
			return new SimpleStringProperty("");
		else
			return new SimpleStringProperty(GrammarSummaryController.scoreTexts[score]);
	}

}
