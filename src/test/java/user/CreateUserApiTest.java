package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.config.BaseURL;
import steps.UserSteps;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class CreateUserApiTest {

    private final UserSteps steps = new UserSteps();
    private String accessToken;

    @Before
    public void setup() {
        RestAssured.baseURI = BaseURL.BASE_URL;
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            steps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешная регистрация нового пользователя")
    @Description("Регистрация с уникальным email. Ожидается 200 и accessToken в ответе.")
    public void createUniqueUser() {
        String email = steps.generateUniqueEmail();
        String payload = String.format("{\"email\": \"%s\", \"password\": \"123456\", \"name\": \"Test\"}", email);

        Response response = steps.registerUser(payload);
        response.then().statusCode(200)
                .body("success", equalTo(true))
                .body("accessToken", notNullValue());

        accessToken = steps.extractAccessToken(response);
    }

    @Test
    @DisplayName("Регистрация уже существующего пользователя")
    @Description("Регистрация с повторным email. Ожидается 403 и сообщение.")
    public void createAlreadyRegisteredUser() {
        String email = steps.generateUniqueEmail();
        String payload = String.format("{\"email\": \"%s\", \"password\": \"123456\", \"name\": \"Test\"}", email);

        steps.registerUser(payload);

        Response second = steps.registerUser(payload);
        second.then().statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Ошибка при регистрации без имени")
    @Description("Регистрация без поля name. Ожидается 403 и сообщение об ошибке.")
    public void createUserWithMissingField() {
        String payload = "{\"email\": \"missing@test.com\", \"password\": \"123456\"}";

        Response response = steps.registerUser(payload);
        response.then().statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
