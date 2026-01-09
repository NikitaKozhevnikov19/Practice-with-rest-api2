package api;

import io.restassured.response.Response;
import models.UserLoginData;
import specs.SpecCustoms;

import static helpers.CustomApiListener.withCustomTemplates;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class AuthApiRequests {

    private static final String LOGIN_ENDPOINT = "/Account/v1/Login";

    public Response login(String username, String password) {
        UserLoginData loginData = new UserLoginData(username, password);

        return step("API: Отправка запроса на аутентификацию для пользователя " + username, () ->
                given()
                        .filter(withCustomTemplates())
                        .spec(SpecCustoms.requestSpecification)
                        .body(loginData)
                        .when()
                        .post(LOGIN_ENDPOINT)
                        .then()
                        .spec(SpecCustoms.responseSpecificationBuilder(200))
                        .extract().response()
        );
    }
}
