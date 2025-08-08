package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import praktikum.config.OrderRequest;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderSteps {

    @Step("Создание заказа c токеном: {token} и ингредиентами: {ingredients}")
    public Response createOrder(List<String> ingredients, String token) {
        OrderRequest order = new OrderRequest(ingredients);

        return given()
                .header("Content-type", "application/json")
                .header("Authorization", token != null ? token : "")
                .body(order)
                .when()
                .post("/api/orders")
                .then()
                .extract()
                .response();
    }

    @Step("Получение списка id всех ингредиентов")
    public List<String> getIngredientIds() {
        return given()
                .get("/api/ingredients")
                .then()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("data._id", String.class);
    }
}
