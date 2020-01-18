package views.items;

import models.test.results.GrammarResult;
import models.test.results.PronunResult;

public class PronunResultItem extends ResultItem {

    public PronunResultItem(GrammarResult grammarResult, PronunResult pronunResult) {
        super(null, pronunResult);
    }
}
