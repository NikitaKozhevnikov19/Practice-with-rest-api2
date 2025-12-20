package models;

import extensions.LoginExtension;
import specs.SpecCustoms;
import java.util.List;
import static helpers.CustomApiListener.withCustomTemplates;
import static io.restassured.RestAssured.given;

public class ApiRequests {

    private static final String BOOKSTORE_ENDPOINT = "/BookStore/v1/Books";

    private String getAuthToken() {
        return LoginExtension.getToken();
    }

    public void clearUserInventory() {
        given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .queryParam("UserId", LoginExtension.getUserId())
                .header("Authorization", "Bearer " + getAuthToken())
                .when()
                .delete(BOOKSTORE_ENDPOINT)
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(204));
    }

    public void addBooksToCollection(String... isbns) {
        List<ISBN> isbnList = List.of(new ISBN(isbns[0]), new ISBN(isbns[1]));
        Book bookPayload = new Book(LoginExtension.getUserId(), isbnList);
        given()
                .filter(withCustomTemplates())
                .spec(SpecCustoms.requestSpecification)
                .header("Authorization", "Bearer " + getAuthToken())
                .body(bookPayload)
                .when()
                .post(BOOKSTORE_ENDPOINT)
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(201));
    }
}
