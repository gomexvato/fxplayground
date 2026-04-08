package com.example.components.submittables;

import com.example.core.Unit;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class FxSubmittableTextFieldTest extends ApplicationTest {

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setScene(new Scene(new StackPane(), 400, 200));
        stage.show();
    }

    private <T> FxSubmittableTextField<T> show(FxSubmittableTextField<T> field) {
        interact(() -> stage.getScene().setRoot(field));
        return field;
    }

    private static TextField tf(FxSubmittableTextField<?> field) {
        return (TextField) field.textfield;
    }

    // --- Initial state ---

    @Test
    public void initialTextIsEmpty() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        assertEquals("", tf(field).getText());
    }

    @Test
    public void initialStatePropertyIsEmpty() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        assertEquals("", field.stateProperty().get());
    }

    // --- set() ---

    @Test
    public void setUpdatesTextfield() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        interact(() -> field.set("hello"));
        assertEquals("hello", tf(field).getText());
    }

    @Test
    public void setUpdatesStateProperty() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        interact(() -> field.set("hello"));
        assertEquals("hello", field.stateProperty().get());
    }

    @Test
    public void setWithDoubleUnit() {
        FxSubmittableTextField<Double> field = show(FxSubmittableTextField.milliAmps());
        interact(() -> field.set(1.5));
        assertTrue(tf(field).getText().contains("1.5"));
    }

    // --- submit() ---

    @Test
    public void submitCallsConsumer() {
        AtomicReference<String> captured = new AtomicReference<>();
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> captured.set(v)));
        interact(() -> field.submit("hello"));
        assertEquals("hello", captured.get());
    }

    @Test
    public void submitCallsConsumerWithDoubleValue() {
        AtomicReference<Double> captured = new AtomicReference<>();
        FxSubmittableTextField<Double> field = show(
                new FxSubmittableTextField<>("0.0", Unit.MILLI_AMPS, (f, v) -> captured.set(v))
        );
        interact(() -> field.submit(2.5));
        assertEquals(2.5, captured.get(), 0.001);
    }

    @Test
    public void modelResponseAfterSubmitUpdatesTextfield() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        interact(() -> field.submit("hello"));
        interact(() -> field.set("confirmed"));
        assertEquals("confirmed", tf(field).getText());
    }

    @Test
    public void modelResponseAfterSubmitUpdatesStateProperty() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        interact(() -> field.submit("hello"));
        interact(() -> field.set("confirmed"));
        assertEquals("confirmed", field.stateProperty().get());
    }

    // --- Value transformations ---

    @Test
    public void negatedSubmitsNegativeValue() {
        AtomicReference<Double> captured = new AtomicReference<>();
        FxSubmittableTextField<Double> field = show(
                new FxSubmittableTextField<Double>((f, v) -> captured.set(v)).negated()
        );
        interact(() -> field.submit(5.0));
        assertEquals(-5.0, captured.get(), 0.001);
    }

    @Test
    public void limitsClampsTooHighValue() {
        AtomicReference<Double> captured = new AtomicReference<>();
        FxSubmittableTextField<Double> field = show(
                new FxSubmittableTextField<Double>((f, v) -> captured.set(v)).limits(0.0, 10.0)
        );
        interact(() -> field.submit(99.0));
        assertEquals(10.0, captured.get(), 0.001);
    }

    @Test
    public void limitsClampsTooLowValue() {
        AtomicReference<Double> captured = new AtomicReference<>();
        FxSubmittableTextField<Double> field = show(
                new FxSubmittableTextField<Double>((f, v) -> captured.set(v)).limits(0.0, 10.0)
        );
        interact(() -> field.submit(-5.0));
        assertEquals(0.0, captured.get(), 0.001);
    }

    @Test
    public void limitsAllowsValueWithinRange() {
        AtomicReference<Double> captured = new AtomicReference<>();
        FxSubmittableTextField<Double> field = show(
                new FxSubmittableTextField<Double>((f, v) -> captured.set(v)).limits(0.0, 10.0)
        );
        interact(() -> field.submit(7.0));
        assertEquals(7.0, captured.get(), 0.001);
    }

    // --- indeterminate ---

    @Test
    public void indeterminateMakesTextfieldNonEditable() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        interact(() -> field.indeterminate(true));
        assertFalse(tf(field).isEditable());
    }

    @Test
    public void notIndeterminateMakesTextfieldEditable() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        interact(() -> field.indeterminate(true));
        interact(() -> field.indeterminate(false));
        assertTrue(tf(field).isEditable());
    }

    // --- dispose ---

    @Test
    public void disposeDoesNotThrow() {
        FxSubmittableTextField<String> field = show(new FxSubmittableTextField<>((f, v) -> {}));
        interact(field::dispose);
    }
}
