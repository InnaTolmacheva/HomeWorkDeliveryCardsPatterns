package ru.netology;

import com.codeborne.selenide.Condition;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Selenide.*;

public class DeliveryCardTest {
    static RegistrationInfo info = new RegistrationInfo();

    @BeforeAll
    public static void generateData() {
        Faker faker = new Faker(new Locale("ru"));
        info.setCity(faker.address().cityName());
        info.setName(faker.name().fullName());
        info.setPhone(faker.phoneNumber().phoneNumber());
    }

    @BeforeEach
    void setUp2() {
        open("http://localhost:9999");
    }

    public String meetingDate(int shift) {
        LocalDate newDate = LocalDate.now().plusDays(shift);
        return newDate.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    @Test
    public void successfulAppointmentBooking() {
        String meetingDate = meetingDate(4);
        String meetingOtherDate = meetingDate(10);
        $("[placeholder=\"Город\"]").setValue(info.getCity());// заполнить поле город
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);// очистить дату
        $("[placeholder=\"Дата встречи\"]").setValue(meetingDate);// заполнить поле дата
        $("[name = \"name\"]").setValue(info.getName());//заполнить ФИО
        $("[name = \"phone\"]").setValue(info.getPhone());//заполнить телефон
        $("[data-test-id = \"agreement\"]").click();//нажать согласие
        $x("//*[text()=\"Запланировать\"]").click();//нажать забронировать
        $(".notification__content").shouldHave(Condition.text("Встреча успешно запланирована на " + meetingDate), Duration.ofSeconds(15));
        $("[data-test-id='date'] input").sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.BACK_SPACE);// очистить дату
        $("[placeholder=\"Дата встречи\"]").setValue(meetingOtherDate);// заполнить поле другой датой
        $x("//*[text()=\"Запланировать\"]").click();//нажать забронировать
        $("[data-test-id = \"replan-notification\"]").shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));
        $x("//*[text()=\"Перепланировать\"]").click();//нажать перепланировать
        $(".notification_status_ok").shouldHave(Condition.text("Встреча успешно запланирована на " + meetingOtherDate), Duration.ofSeconds(15));
    }
}
