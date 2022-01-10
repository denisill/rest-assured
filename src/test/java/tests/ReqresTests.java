package tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.restassured.AllureRestAssured;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static filters.CustomLogFilter.customLogFilter;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ReqresTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in/";
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @Test
    @DisplayName("Получение информации по пользователю по id")
    void singleUserTest() {
        given()
                .filter(new AllureRestAssured())
                .contentType(ContentType.JSON)
                .when()
                .get("/api/users/2")
                .then()
                .assertThat()
                .log().all()
                .statusCode(200)
                .body("data.id", is(2))
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Test
    @DisplayName("Получение списка данных")
    void listResource() {
        given()
                .filter(customLogFilter().withCustomTemplates())
                .contentType(ContentType.JSON)
                .when()
                .get("/api/unknown")
                .then()
                .assertThat()
                .log().all()
                .statusCode(200)
                .body("total", is(12))
                .body("data[1].name", is("fuchsia rose"))
                .body("support.url", is("https://reqres.in/#support-heading"));
    }

    @Test
    @DisplayName("Создание нового пользователя")
    void create() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"leader\"}";
        given()
                .filter(customLogFilter().withCustomTemplates())
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("/api/users")
                .then()
                .assertThat()
                .log().all()
                .statusCode(201)
                .body("name", is("morpheus"))
                .body("job", is("leader"));
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    void registerSuccessful() {
        String data = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";
        given()
                .filter(customLogFilter().withCustomTemplates())
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .post("/api/register")
                .then()
                .assertThat()
                .log().all()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Обновление данных по пользователю")
    void update() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"zion resident\"}";
        given()
                .filter(customLogFilter().withCustomTemplates())
                .contentType(ContentType.JSON)
                .body(data)
                .when()
                .patch("/api/users/2")
                .then()
                .assertThat()
                .log().all()
                .statusCode(200)
                .body("updatedAt", notNullValue());
    }
}
