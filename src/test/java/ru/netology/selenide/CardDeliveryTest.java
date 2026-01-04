package ru.netology.selenide;

import com.codeborne.selenide.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.*;

public class CardDeliveryTest {

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    private String generateDate(int daysToAdd) {
        return LocalDate.now()
                .plusDays(daysToAdd)
                .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private void clearDateField() {
        $("[data-test-id='date'] input").doubleClick().sendKeys(Keys.BACK_SPACE);
    }

    @Test
    void shouldSubmitFormWithValidData() {
        String deliveryDate = generateDate(3);

        $("[data-test-id='city'] input").setValue("Москва");
        clearDateField();
        $("[data-test-id='date'] input").setValue(deliveryDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79123456789");
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.exactText("Забронировать")).click();

        $("[data-test-id='notification'] .notification__content")
                .shouldBe(Condition.visible, Duration.ofSeconds(20))
                .shouldHave(Condition.text("Успешно"));
    }

    @Test
    void shouldNotSubmitWithInvalidCity() {
        String deliveryDate = generateDate(3);

        $("[data-test-id='city'] input").setValue("Нью-Йорк");
        clearDateField();
        $("[data-test-id='date'] input").setValue(deliveryDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79123456789");
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.exactText("Забронировать")).click();

        $("[data-test-id='city'].input_invalid .input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Доставка в выбранный город недоступна"));
    }

    @Test
    void shouldNotSubmitWithInvalidName() {
        String deliveryDate = generateDate(3);

        $("[data-test-id='city'] input").setValue("Москва");
        clearDateField();
        $("[data-test-id='date'] input").setValue(deliveryDate);
        $("[data-test-id='name'] input").setValue("Ivanov Ivan");
        $("[data-test-id='phone'] input").setValue("+79123456789");
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.exactText("Забронировать")).click();

        $("[data-test-id='name'] .input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Имя и Фамилия указаные неверно"));
    }

    @Test
    void shouldNotSubmitWithInvalidPhone() {
        String deliveryDate = generateDate(3);

        $("[data-test-id='city'] input").setValue("Москва");
        clearDateField();
        $("[data-test-id='date'] input").setValue(deliveryDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("89123456789");
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.exactText("Забронировать")).click();

        $("[data-test-id='phone'] .input__sub")
                .shouldBe(Condition.visible)
                .shouldHave(Condition.text("Телефон указан неверно"));
    }

    @Test
    void shouldNotSubmitWithoutAgreement() {
        String deliveryDate = generateDate(3);

        $("[data-test-id='city'] input").setValue("Москва");
        clearDateField();
        $("[data-test-id='date'] input").setValue(deliveryDate);
        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79123456789");
        // Чекбокс не ставим

        $$("button").find(Condition.exactText("Забронировать")).click();

        $("[data-test-id='agreement'].input_invalid")
                .shouldBe(Condition.visible);
    }
}