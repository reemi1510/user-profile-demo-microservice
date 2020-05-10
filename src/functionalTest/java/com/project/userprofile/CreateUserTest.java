package com.project.userprofile;

import com.project.userprofile.controller.request.AppointmentCreationRequest;
import com.project.userprofile.controller.request.UserCreationRequest;
import com.project.userprofile.util.FunctionalTestsContainer;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ProjectApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CreateUserTest {

    @ClassRule
    public static PostgreSQLContainer container = FunctionalTestsContainer.getInstance();

    @Value("http://localhost:${local.server.port}")
    String baseUrl;

    private List<AppointmentCreationRequest> appointments = new ArrayList<>();
    private AppointmentCreationRequest appointment = new AppointmentCreationRequest(1, "Dev", "Test");

    //GIVEN a user provides profile details
    //AND the email address has not been used before
    //WHEN the user sends these details
    //THEN they receive a success response
    @Test
    public void can_create_new_user_profile() {

        appointments.add(appointment);

        UserCreationRequest request = UserCreationRequest.builder()
                .email("example@test.com")
                .firstName("Testy")
                .lastName("McTestface")
                .appointments(appointments)
                .build();

        Response response = givenHeaders()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post(baseUrl + "/api/v1/users")
                .andReturn();

        response.then()
                .assertThat()
                .statusCode(201);

        ArrayList appointmentResponse = response.then()
                .body("email", equalTo("example@test.com"))
                .extract()
                .path("appointments");

        assertThat(appointmentResponse.size()).isEqualTo(1);
        assertThat(appointmentResponse.get(0)).hasFieldOrPropertyWithValue("roleDescription", "Dev");
    }

    private RequestSpecification givenHeaders() {
        return RestAssured.given()
                .relaxedHTTPSValidation()
                .baseUri(baseUrl)
                .header("Content-Type", APPLICATION_JSON_VALUE)
                .header("Accepts", APPLICATION_JSON_VALUE);
    }
}