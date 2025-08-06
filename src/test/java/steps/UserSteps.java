package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserSteps {

    @Step("Генерация уникального email")
    public String generateUniqueEmail() {
        return "user" + System.currentTimeMillis() + "@test.com";
    }

    @Step("Регистрация пользователя: {payload}")
    public Response registerUser(String payload) {
        return given()
                .header("Content-type", "application/json")
                .body(payload)
                .when()
                .post("/api/auth/register");
    }

    @Step("Удаление пользователя по accessToken")
    public void deleteUser(String accessToken) {
        given()
                .header("Authorization", "Bearer " + accessToken)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202); // см. документацию — может быть другой код
    }

    @Step("Извлечение accessToken из ответа")
    public String extractAccessToken(Response response) {
        return response.jsonPath().getString("accessToken").replace("Bearer ", "");
    }
    @Step("Логин пользователя: {payload}")
    public Response loginUser(String payload) {
        return given()
                .header("Content-type", "application/json")
                .body(payload)
                .when()
                .post("/api/auth/login");
    }
}
