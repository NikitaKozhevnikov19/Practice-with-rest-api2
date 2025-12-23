package api;

import extensions.LoginExtension;
import models.Book;
import models.ISBN;
import specs.SpecCustoms;

import java.util.Arrays;
import java.util.List;

import static helpers.CustomApiListener.withCustomTemplates;
import static io.restassured.RestAssured.given;

public class BookApiRequests {

    private static final String BOOKSTORE_ENDPOINT = "/BookStore/v1/Books";

    private String getAuthToken() {
        return LoginExtension.getToken();
    }

    public void clearUserInventory() {

        given()
                .filter(withCustomTemplates()) // Оставили как было у тебя
                .spec(SpecCustoms.requestSpecification)
                .queryParam("UserId", LoginExtension.getUserId())
                .header("Authorization", "Bearer " + getAuthToken())
                .when()
                .delete(BOOKSTORE_ENDPOINT)
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(204));
    }

    public void addBooksToCollection(String... isbns) {

        List<ISBN> isbnList = Arrays.stream(isbns).map(ISBN::new).toList();
        Book bookPayload = new Book(LoginExtension.getUserId(), isbnList);

        given()
                .filter(withCustomTemplates()) // Оставили как было у тебя
                .spec(SpecCustoms.requestSpecification)
                .header("Authorization", "Bearer " + getAuthToken())
                .body(bookPayload)
                .when()
                .post(BOOKSTORE_ENDPOINT)
                .then()
                .spec(SpecCustoms.responseSpecificationBuilder(201));
    }
}
