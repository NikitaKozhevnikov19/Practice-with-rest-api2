package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;

public class UserPage {

    private final SelenideElement userNameValue = $("#userName-value");
    private final SelenideElement confirmDeleteButton = $("#closeSmallModal-ok");
    private final ElementsCollection actionButtons = $$(".action-buttons");
    private final ElementsCollection trashIcons = $$("span[title='Delete']");

    public UserPage openBrowser() {
        open("/images/Toolsqa.jpg");
        return this;
    }

    public UserPage openBrowserAuthorized() {
        open("/profile");
        return this;
    }

    public UserPage verifyUserName(String expectedName) {
        userNameValue.shouldHave(Condition.text(expectedName));
        return this;
    }

    public UserPage isBookVisible(String title) {
        actionButtons.findBy(Condition.textCaseSensitive(title)).should(exist);
        return this;
    }

    public UserPage removeBookByIndex(int index) {
        trashIcons.get(index).click();
        confirmDeleteButton.shouldBe(Condition.visible).click();
        return this;
    }

    public UserPage verifyBookIsMissing(String title) {
        actionButtons.findBy(Condition.textCaseSensitive(title)).shouldNot(exist);
        return this;
    }
}
