package controllers.items;

import controllers.BaseTestController;
import controllers.GrammarTestController;
import models.test.results.BaseResult;
import models.test.results.GrammarResult;

public abstract class ItemController {

    public abstract void setResult(BaseResult result);
    public abstract void setOnAfterTest(BaseTestController controller);

}
