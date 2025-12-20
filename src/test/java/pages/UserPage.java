package pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Selenide.*;

public class UserPage {

    public UserPage openBrowser() {
        open("/images/Toolsqa.jpg");
        return this;
    }

    public UserPage openBrowserAuthorized() {
        open("/profile");
        return this;
    }

    public UserPage verifyUserName(String expectedName) {
        $("#userName-value").shouldHave(Condition.text(expectedName));
        return this;
    }

    public UserPage isBookVisible(String title) {
        $$(".action-buttons").findBy(Condition.textCaseSensitive(title)).should(exist);
        return this;
    }

    public UserPage removeBookByIndex(int index) {
        ElementsCollection trashIcons = $$("span[title='Delete']");
        trashIcons.get(index).click();
        $("#closeSmallModal-ok").click();
        return this;
    }

    public UserPage verifyBookIsMissing(String title) {
        $$(".action-buttons").findBy(Condition.textCaseSensitive(title)).shouldNot(exist);
        return this;
    }
}
