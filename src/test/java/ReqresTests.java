import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

public class ReqresTests {

    public static String BASE_URI = "https://reqres.in/";

    @Test
    @DisplayName("GET")
    void singleUserTest() {
        given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .when()
                .get("/api/users/2")
                .then()
                .assertThat()
                .statusCode(200)
                .body("data.id", is(2))
                .body("support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"))
                .log().all();
    }

    @Test
    @DisplayName("GET")
    void listResource() {
        given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .when()
                .get("/api/unknown")
                .then()
                .assertThat()
                .statusCode(200)
                .body("total", is(12))
                .body("data[1].name", is("fuchsia rose"))
                .body("support.url", is("https://reqres.in/#support-heading"))
                .log().all();
    }

    @Test
    @DisplayName("POST")
    void create() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"leader\"}";
        given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .body(data)
                .when()
                .post("/api/users")
                .then()
                .assertThat()
                .statusCode(201)
                .body("name", is("morpheus"))
                .body("job", is("leader"))
                .log().all();
    }

    @Test
    @DisplayName("POST")
    void registerSuccessful() {
        String data = "{ \"email\": \"eve.holt@reqres.in\", \"password\": \"pistol\"}";
        given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .body(data)
                .when()
                .post("/api/register")
                .then()
                .assertThat()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"))
                .log().all();
    }

    @Test
    @DisplayName("PATCH")
    void update() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"zion resident\"}";
        given()
                .contentType(ContentType.JSON)
                .baseUri(BASE_URI)
                .body(data)
                .when()
                .patch("/api/users/2")
                .then()
                .assertThat()
                .statusCode(200)
                .body("updatedAt", notNullValue())
                .log().all();
    }
}
