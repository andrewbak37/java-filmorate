package ru.yandex.practicum.filmorate.controller;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static io.restassured.RestAssured.get;
import org.hamcrest.Matchers;
import static org.hamcrest.CoreMatchers.is;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class UserControllerTest {
    @Test
    public void testUser() {
        get("http://localhost:8080/users")
                .then()
                .assertThat()
                .statusCode(200)
                .body("size()", is(2));
        get("http://localhost:8080/user")
                .then()
                .assertThat()
                .statusCode(200)
                .body("name", Matchers.equalTo("name"));
    }
}