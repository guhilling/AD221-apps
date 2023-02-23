package com.redhat.training.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.path.json.JsonPath;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.json.Json;

import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.hamcrest.collection.IsCollectionWithSize;

@QuarkusTest
class RestPaymentRouteTest {


	@Test
	void testPayments() {
		JsonPath result = given()
						  .get("/payments/")
						  .then()
						  .statusCode(200)
						  .extract()
						  .body()
						  .jsonPath();
		List<Object> resultList = result.getList(".");
		assertThat(resultList, hasSize(equalTo(4)));
	}

	@Test
	void testPayment() {
		int orderId = given()
								  .get("/payments/12")
								  .then()
								  .statusCode(200)
								  .extract()
								  .body()
								  .jsonPath()
								  .getInt("[0]['orderId']");
		assertEquals(orderId, 8474);
	}
}