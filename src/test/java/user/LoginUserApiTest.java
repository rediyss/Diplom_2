package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
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

    @Before
    public void setUp() {
        baseURI = BaseURL.BASE_URL;
        email = steps.generateUniqueEmail();
        UserDto user = new UserDto(email, password, "Test");
        steps.registerUser(user); // зарегистрировать пользователя
    }

    @Test
    @DisplayName("Логин с валидными данными")
    @Description("Пользователь должен успешно залогиниться")
    public void loginWithValidCredentials() {
        UserDto user = new UserDto(email, password, null); // имя не нужно
        Response response = steps.loginUser(user);

        response.then().statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Логин с невалидными данными")
    @Description("Ожидается 401 и сообщение об ошибке")
    public void loginWithInvalidCredentials() {
        UserDto user = new UserDto("wrong@email.com", "wrongpass", null);
        Response response = steps.loginUser(user);

        response.then().statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}
