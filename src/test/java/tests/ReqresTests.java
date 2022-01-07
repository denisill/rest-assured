package tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ReqresTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    @DisplayName("GET")
    void singleUserTest() {
        given()
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
    @DisplayName("GET")
    void listResource() {
        given()
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
    @DisplayName("POST")
    void create() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"leader\"}";
        given()
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
    @DisplayName("POST")
    void registerSuccessful() {
        String data = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";
        given()
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
    @DisplayName("PATCH")
    void update() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"zion resident\"}";
        given()
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
