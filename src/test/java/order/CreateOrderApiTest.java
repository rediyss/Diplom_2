package order;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import praktikum.config.BaseURL;
import steps.OrderSteps;
import steps.UserSteps;
import steps.UserDto;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class CreateOrderApiTest {

    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = BaseURL.BASE_URL;
        String email = userSteps.generateUniqueEmail();
        UserDto user = new UserDto(email, "123456", "Test");
        Response response = userSteps.registerUser(user);
        accessToken = userSteps.extractAccessToken(response);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken)
                    .then()
                    .statusCode(anyOf(is(202), is(401)));
        }
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и валидными ингредиентами")
    @Description("Ожидается успешное создание заказа и возвращение номера заказа")
    public void testCreateOrderWithAuth() {
        List<String> ingredientIds = orderSteps.getIngredientIds();
        Response response = orderSteps.createOrder(
                ingredientIds.subList(0, 2),
                "Bearer " + accessToken
        );
        response.then().statusCode(200)
                .body("success", is(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка возможности создания заказа без авторизации")
    public void createOrderWithoutAuth() {
        List<String> ingredientIds = orderSteps.getIngredientIds();
        Response response = orderSteps.createOrder(
                ingredientIds.subList(0, 1),
                null
        );
        response.then().statusCode(200)
                .body("success", is(true))
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с невалидным ингредиентом")
    @Description("Передаётся неверный id ингредиента. Ожидается 400 Bad Request")
    public void createOrderWithInvalidHash() {
        List<String> ingredients = Collections.singletonList("invalid_hash");
        Response response = orderSteps.createOrder(ingredients, "Bearer " + accessToken);
        response.then().statusCode(500);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Ожидается 400 и сообщение Ingredient ids must be provided")
    public void createOrderWithoutIngredients() {
        List<String> ingredients = Collections.emptyList();
        Response response = orderSteps.createOrder(ingredients, "Bearer " + accessToken);
        response.then().statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }
}
