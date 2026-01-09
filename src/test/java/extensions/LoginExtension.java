package extensions;

import annotations.WithLogin;
import api.AuthApiRequests;
import io.qameta.allure.Allure;
import io.restassured.response.Response;
import lombok.Getter;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.Cookie;
import pages.UserPage;

import static com.codeborne.selenide.WebDriverRunner.getWebDriver;
import static io.qameta.allure.Allure.step;

public class LoginExtension implements BeforeEachCallback {

    @Getter
    private static String userId;
    @Getter
    private static String expires;
    @Getter
    private static String token;

    private final AuthApiRequests authApi = new AuthApiRequests();
    private final UserPage userPage = new UserPage();

    @Getter
    private final String login = System.getProperty("login");
    @Getter
    private final String defaultPassword = System.getProperty("password");

    @Override
    public void beforeEach(ExtensionContext extensionContext) {

        WithLogin annotation = getWithLoginAnnotation(extensionContext);

        String username = (annotation != null && !annotation.username().isEmpty())
                ? annotation.username()
                : login;

        String password = (annotation != null && !annotation.password().isEmpty())
                ? annotation.password()
                : defaultPassword;

        if (username == null || password == null) {
            throw new IllegalStateException("Логин или пароль не заданы. Проверьте системные свойства или аннотацию @WithLogin");
        }


        Response response = authApi.login(username, password);


        step("UI: Инициализация сессии в браузере", () -> {
            userId = response.path("userId");
            expires = response.path("expires");
            token = response.path("token");

            userPage.openBrowser(); // Открываем домен для возможности установки Cookie

            getWebDriver().manage().addCookie(new Cookie("userID", userId));
            getWebDriver().manage().addCookie(new Cookie("expires", expires));
            getWebDriver().manage().addCookie(new Cookie("token", token));
        });

        step("Allure: Логирование сессии", () -> Allure.addAttachment("Authorized User", username));

        step("UI: Переход на страницу профиля", userPage::openBrowserAuthorized);
    }

    private WithLogin getWithLoginAnnotation(ExtensionContext context) {
        WithLogin annotation = context.getRequiredTestMethod().getAnnotation(WithLogin.class);
        if (annotation == null) {
            annotation = context.getRequiredTestClass().getAnnotation(WithLogin.class);
        }
        return annotation;
    }
}
