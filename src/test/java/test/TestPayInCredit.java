package test;

import com.codeborne.selenide.logevents.SelenideLogger;
import data.DataHelper;
import data.SQLHelper;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import page.PageTravel;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestPayInCredit {

    private static DataHelper.CreditRequestEntity credit;
    private static DataHelper.OrderEntity order;
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
    @Test
    @DisplayName("Card number with status APPROVED for credit")
    void shouldSuccessfulBuyInCreditWithAPPROVEDCard() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationOk();

        credit = SQLHelper.getCreditRequestEntity();
        order = SQLHelper.getOrderEntity();
        assertEquals(status, credit.getStatus());
        assertEquals(credit.getBank_id(), order.getPayment_id());
        assertEquals(credit.getId(), order.getCredit_id());

    }

    @Test
    @DisplayName("Card number with status DECLINED for credit")
    void shouldErrorBuyInCreditWithDECLINEDCard() {

        String status = "DECLINED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationError();

        assertEquals(status, SQLHelper.getCreditRequestEntity().getStatus());

    }

    @Test
    @DisplayName("Empty form for credit")
    void shouldMessageFilInFieldInCredit() {

        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.clickContinue();
        page.waitNotificationMessageNumber("Поле обязательно для заполнения");
        page.waitNotificationMessageMonth("Поле обязательно для заполнения");
        page.waitNotificationMessageYear("Поле обязательно для заполнения");
        page.waitNotificationMessageOwner("Поле обязательно для заполнения");
        page.waitNotificationMessageCVC("Поле обязательно для заполнения");
    }

    @Test
    @DisplayName("Card number with status INVALID for credit")
    void shouldErrorCreditWithINVALIDCard() {

        String status = "INVALID";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationError();
        assertEquals(null, SQLHelper.getOrderEntity());
        assertEquals(null, SQLHelper.getCreditRequestEntity());

    }

    @Test
    @DisplayName("Card number with status ZERO for credit")
    void shouldErrorCreditWithZEROCard() {

        String status = "ZERO";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationError();
        assertEquals(null, SQLHelper.getOrderEntity());
        assertEquals(null, SQLHelper.getCreditRequestEntity());

    }

    @Test
    @DisplayName("Card number with status FIFTEEN for credit")
    void shouldErrorCreditWithFIFTEENCard() {

        String status = "FIFTEEN";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageNumber("Неверный формат");

    }

    @Test
    @DisplayName("Month ZERO for credit")
    void shouldErrorZeroMonthForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getZero());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверно указан срок действия карты");

    }

    @Test
    @DisplayName("Month Over for credit")
    void shouldErrorOverMonthForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOver());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверно указан срок действия карты");

    }

    @Test
    @DisplayName("Month One Digit for credit")
    void shouldErrorOneDigitMonthForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.getMonthOneDig());
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageMonth("Неверный формат");

    }

    @Test
    @DisplayName("Year ZERO for credit")
    void shouldErrorZeroYearForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.getZero());
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Истёк срок действия карты");

    }

    @Test
    @DisplayName("Year More for credit")
    void shouldErrorMoreYearForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(10));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Неверно указан срок действия карты");

    }

    @Test
    @DisplayName("Year Less for credit")
    void shouldErrorLessYearForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(-1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageYear("Истёк срок действия карты");

    }

    @Test
    @DisplayName("Cyrillic Name for credit")
    void shouldErrorCyrillicNameForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderCyrillic());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");

    }

    @Test
    @DisplayName("Number Name for credit")
    void shouldErrorNumberNameForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderNumeric());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");

    }

    @Test
    @DisplayName("One letter Name for credit")
    void shouldErrorOneLetterNameForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderOneSymbol());
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");

    }

    @Test
    @DisplayName("Special characters Name for credit")
    void shouldErrorSpecCharNameForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolderSpecChar(5));
        page.inputCVC(3);
        page.clickContinue();
        page.waitNotificationMessageOwner("Неверный формат");

    }

    @Test
    @DisplayName("Two digits CVC for credit")
    void shouldErrorTwoDigCVCForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(2);
        page.clickContinue();
        page.waitNotificationMessageCVC("Неверный формат");

    }

    @Test
    @DisplayName("One digits CVC for credit")
    void shouldErrorOneDigCVCForCredit() {

        String status = "APPROVED";
        PageTravel page = new PageTravel();
        page.buyInCredit();
        page.inputNumberCard(status);
        page.inputMonth(DataHelper.generateMonthPlus(0));
        page.inputYear(DataHelper.generateYearPlus(1));
        page.inputOwner(DataHelper.generateHolder());
        page.inputCVC(1);
        page.clickContinue();
        page.waitNotificationMessageCVC("Неверный формат");

    }
}