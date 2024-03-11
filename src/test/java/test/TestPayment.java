package test;

import org.junit.jupiter.api.*;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import data.DataHelper;
import data.SQLHelper;
import page.PageTravel;


import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static data.SQLHelper.*;

public class TestPayment {

    private static SQLHelper.PaymentEntity payment;
    private static SQLHelper.OrderEntity order;
    private static String url = System.getProperty("app.url");

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open(url);
    }

    @AfterEach
    public void cleanData() {
        cleanDatabase();
    }

    @Test
    @DisplayName("Card number with status APPROVED for payment")
    void shouldSuccessfulPayWithAPPROVEDCard() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        int price = page.getPriceInKops();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationOk();

        payment = SQLHelper.getPaymentEntity();
        order = SQLHelper.getOrderEntity();
        assertEquals(status, payment.getStatus());
        assertEquals(price, payment.getAmount());
        assertEquals(payment.getTransaction_id(), order.getPayment_id());
    }

    @Test
    @DisplayName("Card number with status DECLINED for payment")
    void shouldErrorPayWithDECLINEDCard() {

        String status = "DECLINED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationError();

        assertEquals(status, SQLHelper.getPaymentEntity().getStatus());
    }

    @Test
    @DisplayName("Empty form for payment")
    void shouldMessageFilInFieldInPay() {

        PageTravel page = new PageTravel();
        page.buy();
        page.clickContinue();
        page.waitNotificationMessageNumber("Поле обязательно для заполнения");
        page.waitNotificationMessageMonth("Поле обязательно для заполнения");
        page.waitNotificationMessageYear("Поле обязательно для заполнения");
        page.waitNotificationMessageOwner("Поле обязательно для заполнения");
        page.waitNotificationMessageCVC("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Card number with status INVALID for payment")
    void shouldErrorPayWithINVALIDCard() {

        String status = "INVALID";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationError();
        assertNull(SQLHelper.getOrderEntity(), "Таблица order_entity не пустая");
        assertNull(SQLHelper.getPaymentEntity());

    }

    @Test
    @DisplayName("Card number with status ZERO for payment")
    void shouldErrorPayWithZEROCard() {

        String status = "ZERO";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationError();
        assertNull(SQLHelper.getOrderEntity(), "Таблица order_entity не пустая");
        assertNull(SQLHelper.getPaymentEntity());
    }

    @Test
    @DisplayName("Card number with status FIFTEEN for payment")
    void shouldErrorPayWithFIFTEENCard() {

        String status = "FIFTEEN";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageNumber("Неверный формат");

    }

    @Test
    @DisplayName("Month ZERO for payment")
    void shouldErrorZeroMonthForPay() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getZero());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверно указан срок действия карты");

    }

    @Test
    @DisplayName("Month Over for payment")
    void shouldErrorOverMonthForPay() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOver());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверно указан срок действия карты");

    }

    @Test
    @DisplayName("Month One Digit for payment")
    void shouldErrorOneDigitMonthForPay() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOneDig());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверный формат");

    }

    @Test
    @DisplayName("Year ZERO for payment")
    void shouldErrorZeroYearForPay() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.getZero());
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Истёк срок действия карты");

    }

    @Test
    @DisplayName("Year More for payment")
    void shouldErrorMoreYearForPay() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(10));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Неверно указан срок действия карты");

    }

    @Test
    @DisplayName("Year Less for payment")
    void shouldErrorLessYearForPay() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(-1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Истёк срок действия карты");

    }

    @Test
    @DisplayName("Cyrillic Name  for payment")
    void shouldErrorCyrillicNameForPayment() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderCyrillic());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");

    }

    @Test
    @DisplayName("Number Name for payment")
    void shouldErrorNumberNameForPayment() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderNumeric());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");

    }

    @Test
    @DisplayName("One letter Name for payment")
    void shouldErrorOneLetterNameForPayment() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderOneSymbol());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");

    }

    @Test
    @DisplayName("Special characters Name for payment")
    void shouldErrorSpecCharNameForPayment() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderSpecChar(5));
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");

    }

    @Test
    @DisplayName("Two digits CVC for payment")
    void shouldErrorTwoDigCVCForPayment() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(2);
        page.clickContinue();
        page.waitNotificationMessageCVC("Неверный формат");

    }

    @Test
    @DisplayName("One digits CVC for payment")
    void shouldErrorOneDigCVCForPayment() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buy();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(1);
        page.clickContinue();
        page.waitNotificationMessageCVC("Неверный формат");

    }

}