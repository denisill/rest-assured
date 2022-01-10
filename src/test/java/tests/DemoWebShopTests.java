package tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Cookie;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class DemoWebShopTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "http://demowebshop.tricentis.com/";
        Configuration.baseUrl = "http://demowebshop.tricentis.com/";
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @Test
    @DisplayName("Добавляем без авторизации на сайте книгу в корзину сначала через UI, затем через API " +
            "и проверяем что общее количество товара в корзине стало 2")
    public void testDemoWebShop() {

        String authorizationCookie =
                given()
                        .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                        .when()
                        .post("/login")
                        .then()
                        .statusCode(200)
                        .extract()
                        .cookie("Nop.customer");

        step("Open minimal content, because cookie can be set when site is opened", () ->
                open("/Themes/DefaultClean/Content/images/logo.png"));

        step("Set cookie to to browser", () -> {
            getWebDriver().manage().addCookie(new Cookie("Nop.customer", authorizationCookie));
        });

        step("Open main page", () -> {
            open("http://demowebshop.tricentis.com");
        });
        step("Go to the section Books", () -> {
            $(byText("Books")).click();
        });
        step("Add to cart book 'Fiction'", () -> {
            $("[onclick=\"AjaxCart.addproducttocart_catalog('/addproducttocart/catalog/45/1/1    ');return false;\"]").click();
        });

        step("Add to cart book 'Fiction' with API and checking the total quantity in the cart", () -> {
            given()
                    .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                    .cookie("Nop.customer", authorizationCookie)
                    .when()
                    .post("addproducttocart/catalog/45/1/1")
                    .then()
                    .log().all()
                    .statusCode(200)
                    .body("updatetopcartsectionhtml", is("(2)"));
        });
    }
}
