package ru.netology.selenide;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
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
        $("[data-test-id=date] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);
    }

    // ТЕСТ 1: Успешная отправка формы
    @Test
    void shouldSubmitFormWithValidData() {
        String deliveryDate = generateDate(3);

        $("[data-test-id=city] input").setValue("Москва");
        clearDateField();
        $("[data-test-id=date] input").setValue(deliveryDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79123456789");
        $("[data-test-id=agreement]").click();
        $("button[type='button']").click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно!"));
    }

    // ТЕСТ 2: Проверка с другим городом
    @Test
    void shouldSubmitFormWithDifferentCity() {
        String deliveryDate = generateDate(3);

        $("[data-test-id=city] input").setValue("Санкт-Петербург");
        clearDateField();
        $("[data-test-id=date] input").setValue(deliveryDate);
        $("[data-test-id=name] input").setValue("Петрова Мария");
        $("[data-test-id=phone] input").setValue("+79219876543");
        $("[data-test-id=agreement]").click();
        $("button[type='button']").click();

        $("[data-test-id=notification]")
                .shouldBe(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("Успешно!"));
    }

    // ТЕСТ 3: Неверный город
    @Test
    void shouldNotSubmitWithInvalidCity() {
        String deliveryDate = generateDate(3);

        $("[data-test-id=city] input").setValue("Нью-Йорк");
        clearDateField();
        $("[data-test-id=date] input").setValue(deliveryDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79123456789");
        $("[data-test-id=agreement]").click();
        $("button[type='button']").click();

        $("[data-test-id=city].input_invalid .input__sub")
                .shouldHave(Condition.text("Доставка в выбранный город недоступна"));
    }

    // ТЕСТ 4: Прошедшая дата
    @Test
    void shouldNotSubmitWithPastDate() {
        String pastDate = generateDate(-1);

        $("[data-test-id=city] input").setValue("Москва");
        clearDateField();
        $("[data-test-id=date] input").setValue(pastDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79123456789");
        $("[data-test-id=agreement]").click();
        $("button[type='button']").click();

        $("[data-test-id=date] .input__sub")
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"));
    }

    // ТЕСТ 5: Дата менее 3 дней
    @Test
    void shouldNotSubmitWithDateLessThan3Days() {
        String invalidDate = generateDate(2);

        $("[data-test-id=city] input").setValue("Москва");
        clearDateField();
        $("[data-test-id=date] input").setValue(invalidDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79123456789");
        $("[data-test-id=agreement]").click();
        $("button[type='button']").click();

        $("[data-test-id=date] .input__sub")
                .shouldHave(Condition.text("Заказ на выбранную дату невозможен"));
    }

    // ТЕСТ 6: Неверное имя (латинские буквы)
    @Test
    void shouldNotSubmitWithInvalidName() {
        String deliveryDate = generateDate(3);

        $("[data-test-id=city] input").setValue("Москва");
        clearDateField();
        $("[data-test-id=date] input").setValue(deliveryDate);
        $("[data-test-id=name] input").setValue("Ivanov Ivan");
        $("[data-test-id=phone] input").setValue("+79123456789");
        $("[data-test-id=agreement]").click();
        $("button[type='button']").click();

        $("[data-test-id=name].input_invalid .input__sub")
                .shouldHave(Condition.text("Имя и Фамилия указаные неверно"));
    }

    // ТЕСТ 7: Неверный телефон (без +)
    @Test
    void shouldNotSubmitWithInvalidPhone() {
        String deliveryDate = generateDate(3);

        $("[data-test-id=city] input").setValue("Москва");
        clearDateField();
        $("[data-test-id=date] input").setValue(deliveryDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("89123456789");
        $("[data-test-id=agreement]").click();
        $("button[type='button']").click();

        $("[data-test-id=phone].input_invalid .input__sub")
                .shouldHave(Condition.text("Телефон указан неверно"));
    }

    // ТЕСТ 8: Без согласия
    @Test
    void shouldNotSubmitWithoutAgreement() {
        String deliveryDate = generateDate(3);

        $("[data-test-id=city] input").setValue("Москва");
        clearDateField();
        $("[data-test-id=date] input").setValue(deliveryDate);
        $("[data-test-id=name] input").setValue("Иванов Иван");
        $("[data-test-id=phone] input").setValue("+79123456789");
        // Чекбокс не ставим

        $("button[type='button']").click();

        $("[data-test-id=agreement].input_invalid")
                .shouldBe(Condition.visible);
    }

    // ТЕСТ 9: Пустая форма
    @Test
    void shouldNotSubmitEmptyForm() {
        $("button[type='button']").click();

        $("[data-test-id=city].input_invalid").shouldBe(Condition.visible);
        $("[data-test-id=name].input_invalid").shouldBe(Condition.visible);
        $("[data-test-id=phone].input_invalid").shouldBe(Condition.visible);
        $("[data-test-id=agreement].input_invalid").shouldBe(Condition.visible);
    }
}