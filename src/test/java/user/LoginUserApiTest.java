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

import static org.hamcrest.Matchers.*;

public class LoginUserApiTest {

    private final UserSteps steps = new UserSteps();
    private String accessToken;
    private String email = steps.generateUniqueEmail();
    private final String password = "123456";
    private final String name = "Test";

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURL.BASE_URL;

        String payload = String.format("{\"email\": \"%s\", \"password\": \"%s\", \"name\": \"%s\"}", email, password, name);
        Response response = steps.registerUser(payload);
        accessToken = steps.extractAccessToken(response);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            steps.deleteUser(accessToken);
        }
    }

    @Test
    @DisplayName("Успешный логин с существующим пользователем")
    @Description("Проверка успешной авторизации с корректными логином и паролем")
    public void loginWithValidUser() {
        String payload = String.format("{\"email\": \"%s\", \"password\": \"%s\"}", email, password);

        Response response = steps.loginUser(payload);
        response.then().statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Ошибка при логине с неверным логином и паролем")
    @Description("Ожидается ошибка 401 Unauthorized и сообщение о некорректных данных")
    public void loginWithInvalidCredentials() {
        String payload = "{\"email\": \"wrong@email.com\", \"password\": \"wrongpass\"}";

        Response response = steps.loginUser(payload);
        response.then().statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}
