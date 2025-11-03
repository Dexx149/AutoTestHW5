import com.codeborne.selenide.Condition;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;
import utils.DataGenerator;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class DeliveryCardTest {

    private DataGenerator.UserInfo userInfo;


    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setUp() {
        userInfo = DataGenerator.Registration.generateUser("ru");
    }

    @Test
    void shouldSuccessBookDelivery() {
        String planDate = DataGenerator.generateDate(4);

        open("http://localhost:9999");

        fillForm(userInfo, planDate);

        $("[data-test-id='success-notification']")
                .should(Condition.visible, Duration.ofSeconds(15))
                .should(Condition.text("Встреча успешно запланирована на " + planDate));
    }

    @Test
    void shouldRescheduleDelivery() {
        String firstDate = DataGenerator.generateDate(4);
        String secondDate = DataGenerator.generateDate(7);

        open("http://localhost:9999");

        fillForm(userInfo, firstDate);

        $("[data-test-id='success-notification']")
                .should(Condition.visible, Duration.ofSeconds(15))
                .should(Condition.text("Встреча успешно запланирована на " + firstDate));

        $("[data-test-id='date'] input")
                .sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME, Keys.BACK_SPACE));
        $("[data-test-id='date'] input").setValue(secondDate);
        $$("button").find(Condition.text("Запланировать")).click();

        $("[data-test-id='replan-notification']")
                .should(Condition.visible, Duration.ofSeconds(15))
                .shouldHave(Condition.text("У вас уже запланирована встреча на другую дату. Перепланировать?"));

        $$("button").find(Condition.text("Перепланировать")).click();
        $("[data-test-id='success-notification']")
                .should(Condition.visible, Duration.ofSeconds(15))
                .should(Condition.text("Встреча успешно запланирована на " + secondDate));
    }

    private void fillForm(DataGenerator.UserInfo info, String date) {
        $("[data-test-id='city'] input").setValue(info.getCity());

        $("[data-test-id='date'] input")
                .sendKeys(Keys.chord(Keys.SHIFT, Keys.HOME, Keys.BACK_SPACE));
        $("[data-test-id='date'] input").setValue(date);

        $("[data-test-id='name'] input").setValue(info.getName());
        $("[data-test-id='phone'] input").setValue(info.getPhone());
        $("[data-test-id='agreement']").click();
        $$("button").find(Condition.text("Запланировать")).click();
    }
}