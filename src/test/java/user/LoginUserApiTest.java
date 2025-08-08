package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.config.BaseURL;
import steps.UserSteps;
import steps.UserDto;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.*;

public class LoginUserApiTest {

    private final UserSteps steps = new UserSteps();
    private String email;
    private final String password = "123456";

    private String accessToken;

    @Before
    public void setUp() {
        baseURI = BaseURL.BASE_URL;
        email = steps.generateUniqueEmail();
        UserDto user = new UserDto(email, password, "Test");
        Response response = steps.registerUser(user);
        accessToken = steps.extractAccessToken(response);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            steps.deleteUser(accessToken)
                    .then()
                    .statusCode(anyOf(is(202), is(401)));
        }
    }

    @Test
    @DisplayName("Логин с валидными данными")
    @Description("Пользователь должен успешно залогиниться")
    public void loginWithValidCredentials() {
        UserDto user = new UserDto(email, password, null);
        Response response = steps.loginUser(user);

        response.then().statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Ожидается 401 и сообщение об ошибке при неверном email")
    public void loginWithInvalidEmail() {
        UserDto user = new UserDto("wrong_" + email, password, null);
        Response response = steps.loginUser(user);

        response.then().statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным паролем")
    @Description("Ожидается 401 и сообщение об ошибке при неверном пароле")
    public void loginWithInvalidPassword() {
        UserDto user = new UserDto(email, "wrongpass", null);
        Response response = steps.loginUser(user);

        response.then().statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}
