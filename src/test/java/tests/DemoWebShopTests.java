package tests;

import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.openqa.selenium.Cookie;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.authentication;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class DemoWebShopTests {

    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI = "http://demowebshop.tricentis.com/";
        Configuration.baseUrl = "http://demowebshop.tricentis.com/";
    }

    @Test
    @DisplayName("adding a book to cart with UI")
    public void testDemoWebShop() {
        step("Get cookie by api and set it to browser", () -> {
            String authorizationCookie =
                    given()
                            .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                            .formParam("Email", "123@qa.guru")
                            .formParam("Password", "123@qa.guru")
                            .when()
                            .post("/login")
                            .then()
                            .statusCode(302)
                            .extract()
                            .cookie("NOPCOMMERCE.AUTH");

            step("Open minimal content, because cookie can be set when site is opened", () ->
                    open("/Themes/DefaultClean/Content/images/logo.png"));

            step("Set cookie to to browser", () -> {
                getWebDriver().manage().addCookie(
                        new Cookie("NOPCOMMERCE.AUTH", authorizationCookie));
            });
        });


        step("Open main page", () -> {
            open("http://demowebshop.tricentis.com/");
        });
        step("Go to the section Books", () -> {
            $(byText("Books")).click();
        } );
        step("Add to cart book 'Fiction'", () -> {
            $("[onclick=\"AjaxCart.addproducttocart_catalog('/addproducttocart/catalog/45/1/1    ');return false;\"]").click();
        } );

        given()
                .contentType("application/x-www-form-urlencoded; charset=UTF-8")
                .when()
                .post("addproducttocart/catalog/45/1/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("updatetopcartsectionhtml", is("(2)"));
    }
}