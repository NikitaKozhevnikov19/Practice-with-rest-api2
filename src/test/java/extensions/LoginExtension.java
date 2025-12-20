package extensions;

import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.Getter;
import models.UserLoginData;
import models.WithLogin;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;
import pages.UserPage;
import specs.SpecCustoms;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static helpers.CustomApiListener.withCustomTemplates;
import static io.qameta.allure.Allure.step;
import static io.restassured.RestAssured.given;

public class LoginExtension implements BeforeEachCallback {

    @Getter
    private static String userId;
    @Getter
    private static String expires;
    @Getter
    private static String token;

    private static final UserPage USER_PAGE = new UserPage();
    private static final String LOGIN_ENDPOINT = "/Account/v1/Login";

    @Getter
    private final String login = System.getProperty("login", "kozherka");
    @Getter
    private final String password = System.getProperty("password", "Kozher1@");

    @Override
    public void beforeEach(ExtensionContext extensionContext) {

        WithLogin withLogin = extensionContext.getRequiredTestMethod().getAnnotation(WithLogin.class);
        if (withLogin == null) {
            withLogin = extensionContext.getRequiredTestClass().getAnnotation(WithLogin.class);
        }

        String username = withLogin != null && !withLogin.username().isEmpty()
                ? withLogin.username()
                : login;

        String userPassword = withLogin != null && !withLogin.password().isEmpty()
                ? withLogin.password()
                : password;

        UserLoginData loginData = new UserLoginData(username, userPassword);

        Response response = step("Выполнение API-запроса на аутентификацию", () -> given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .body(loginData)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(200))
                .extract().response());

        step("Инициализация браузера на базовом домене", () -> {
            USER_PAGE.openBrowser(); // Метод, который открывает /images/Toolsqa.jpg
        });

        step("Передача авторизационных Cookies в WebDriver", () -> {
            userId = response.path("userId");
            expires = response.path("expires");
            token = response.path("token");

            getWebDriver().manage().addCookie(new Cookie("userID", userId));
            getWebDriver().manage().addCookie(new Cookie("expires", expires));
            getWebDriver().manage().addCookie(new Cookie("token", token));
        });

        step("Фиксация данных авторизации в отчете", () -> {
            Allure.addAttachment("Auth Info", String.format("Account: %s", username));
        });

        step("Переход на страницу профиля под авторизованной сессией", USER_PAGE::openBrowserAuthorized);
    }
}
