package com.example;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;
import javafx.stage.Stage;

import static org.junit.Assert.assertEquals;

public class MainAppTest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }

    private void clickButton() {
        Button btn = (Button) lookup("#clickButton").query();
        interact(btn::fire);
    }

    private void clickReset() {
        Button btn = (Button) lookup("#resetButton").query();
        interact(btn::fire);
    }

    private String labelText() {
        return ((Label) lookup("#countLabel").query()).getText();
    }

    @Test
    public void initialLabelShowsZero() {
        assertEquals("Clicks: 0", labelText());
    }

    @Test
    public void clickIncrements() {
        clickButton();
        assertEquals("Clicks: 1", labelText());
    }

    @Test
    public void multipleClicksAccumulate() {
        clickButton();
        clickButton();
        clickButton();
        assertEquals("Clicks: 3", labelText());
    }

    @Test
    public void resetRestoresZero() {
        clickButton();
        clickButton();
        clickReset();
        assertEquals("Clicks: 0", labelText());
    }

    @Test
    public void clickAfterResetStartsFromOne() {
        clickButton();
        clickReset();
        clickButton();
        assertEquals("Clicks: 1", labelText());
    }
}
