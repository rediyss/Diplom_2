package user;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import praktikum.config.BaseURL;
import steps.UserDto;
import steps.UserSteps;

import static io.restassured.RestAssured.baseURI;
import static org.hamcrest.Matchers.*;

public class CreateUserApiTest {

    private final UserSteps steps = new UserSteps();
    private String email;
    private String accessToken;

    @Before
    public void setUp() {
        baseURI = BaseURL.BASE_URL;
        email = steps.generateUniqueEmail();
        accessToken = null;
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
    @DisplayName("Создание уникального пользователя")
    @Description("Ожидается успешное создание пользователя и получение accessToken")
    public void createUniqueUser() {
        UserDto user = new UserDto(email, "123456", "Test");
        Response response = steps.registerUser(user);

        accessToken = steps.extractAccessToken(response);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Создание уже зарегистрированного пользователя")
    @Description("Ожидается 403 и сообщение 'User already exists'")
    public void createAlreadyRegisteredUser() {
        UserDto user = new UserDto(email, "123456", "Test");
        Response first = steps.registerUser(user);
        accessToken = steps.extractAccessToken(first);

        Response response = steps.registerUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Ожидается 403 и сообщение об обязательных полях")
    public void createUserWithoutEmail() {
        UserDto user = new UserDto(null, "123456", "Test");
        Response response = steps.registerUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля")
    @Description("Ожидается 403 и сообщение об обязательных полях")
    public void createUserWithoutPassword() {
        UserDto user = new UserDto(email, null, "Test");
        Response response = steps.registerUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без имени")
    @Description("Ожидается 403 и сообщение об обязательных полях")
    public void createUserWithoutName() {
        UserDto user = new UserDto(email, "123456", null);
        Response response = steps.registerUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без email и пароля")
    @Description("Ожидается 403 и сообщение об обязательных полях")
    public void createUserWithoutEmailAndPassword() {
        UserDto user = new UserDto(null, null, "Test");
        Response response = steps.registerUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без email и имени")
    @Description("Ожидается 403 и сообщение об обязательных полях")
    public void createUserWithoutEmailAndName() {
        UserDto user = new UserDto(null, "123456", null);
        Response response = steps.registerUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без пароля и имени")
    @Description("Ожидается 403 и сообщение об обязательных полях")
    public void createUserWithoutPasswordAndName() {
        UserDto user = new UserDto(email, null, null);
        Response response = steps.registerUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без всех полей")
    @Description("Ожидается 403 и сообщение об обязательных полях")
    public void createUserWithoutAnyFields() {
        UserDto user = new UserDto(null, null, null);
        Response response = steps.registerUser(user);

        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}
