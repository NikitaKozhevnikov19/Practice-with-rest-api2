package tests;

import extensions.LoginExtension;
import models.ApiRequests;
import models.WithLogin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import pages.UserPage;

import static io.qameta.allure.Allure.step;

@Tag("ProfileTests")
public class ProfileTests extends TestBase {

    private final UserPage userPage = new UserPage();
    private final ApiRequests api = new ApiRequests();
    private final LoginExtension loginExtension = new LoginExtension();

    private static final String ISBN_1 = "9781449325862"; // Git Pocket Guide
    private static final String TITLE_1 = "Git Pocket Guide";

    private static final String ISBN_2 = "9781449331818"; // Learning JavaScript Design Patterns
    private static final String TITLE_2 = "Learning JavaScript Design Patterns";

    @Test
    @WithLogin
    @DisplayName("Удаление выбранной книги из коллекции пользователя")
    public void deleteSpecificBookFromCollectionTest() {

        step("Подготовка данных: Очистка коллекции и добавление новых книг", () -> {
            api.clearUserInventory();
            api.addBooksToCollection(ISBN_1, ISBN_2);
        });


        step("Переход в профиль пользователя", userPage::openBrowserAuthorized);

        step("Проверка корректности авторизации (логин: " + loginExtension.getLogin() + ")", () -> {
            userPage.verifyUserName(loginExtension.getLogin());
        });

        step("Удаление книги: " + TITLE_2, () -> {
            userPage.removeBookByIndex(1);
        });

        step("Верификация отсутствия книги '" + TITLE_2 + "' в списке", () -> {
            userPage.verifyBookIsMissing(TITLE_2);
        });

        step("Верификация сохранения книги '" + TITLE_1 + "' в списке", () -> {
            userPage.isBookVisible(TITLE_1);
        });
    }
}
