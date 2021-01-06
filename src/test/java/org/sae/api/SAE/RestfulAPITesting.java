package org.sae.api.SAE;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.*;

public class RestfulAPITesting {
	
	private static String id = "3fe5a362-6980-4266-a819-aa6882839292";
	
	@BeforeClass
	public void setUp() {
		baseURI = "https://f2245895-d4fa-45a4-a6d2-9873c2792d6e.mock.pstmn.io";
		basePath = "/api/v1/resources";
	}
	
/* 
 * 1) "GET single resource" (view a single entity)	
 * a) Submit the GET request
 * b) Assert within JSON response that value of statusCode field = 200 
 * 
*/
	@Test
	public void verifyStatusCode1() {
		given()
		.when()
			.get(id)
		.then()
			.statusCode(200); 
	}
/*
 * c) Assert within JSON response that value of id field equals the id provided in the request route
*/
	@Test
	public void verifyId() {	
		Response rs = 
				given()
				.pathParam("id", id)
				.get("/{id}");
		
		JsonPath jp = rs.jsonPath();
		assertEquals(jp.getString("results.id"), id);
	}
	
/*
 * 2) "GET resource list" (view multiple entities)
 * a) Submit the GET request
 * b) Assert within JSON response that value of statusCode field = 200
 * c) Assert within JSON response that value of pagination.total field = 3
*/
	@Test
	public void verifyStatusCode2() {
		given()
		.when()
			.queryParam("sortBy", "modificationDate")
			.queryParam("direction", "desc")
			.queryParam("limit", 50)
			.queryParam("offset", 0)
			.get("/resources")			
		.then()
			.statusCode(200)
		.and().assertThat().body("pagination.total", equalTo(3));	
	}

/*
 * 3) "POST resource" (create a new entity)
 * a) Submit the POST request
 * b) Assert within JSON response that value of statusCode field = 201
 * c) Assert within JSON response that value of results.name field = "r-RM resource 20200928121933264 - name"
*/
	@Test
	public void verifyPostRequest() {
		given()
		.headers("Content-Type", ContentType.JSON, "Accept", ContentType.JSON)
		.when()
			.body("{\n" + 
					"	\"deleted\": false,\n" + 
					"	\"description\": \"9-RM resource 20200928121933264 - description\",\n" + 
					"	\"metadata\": null,\n" + 
					"	\"name\": \"r-RM resource 20200928121933264 - name\"\n" + 
					"}")
			.post("/resources")
		.then()
			.assertThat().statusCode(201)
			.assertThat().contentType("text/html; charset=utf-8")
			.assertThat().body(containsString("\"name\": \"r-RM resource 20200928121933264 - name\""));
//		.and().assertThat().body("statusMessage", equalTo("Operation Completed Successfully"));
			
	}
	
/*
 * 4) "DELETE resource" (delete an entity)
 * a) Submit the DELETE request
 * b) Assert within response header that correct status code (204) is returned
*/
	@Test
	public void verifyStatusCode204ForDeleteRequest() {
		given()
		.when()
			.delete("/resources/17bb4ca1-f0e9-4f05-b606-70aab69b78b1")
		.then()
			.assertThat().statusCode(204);
	}
	
/* 
 * 5) Bonus - Negative Test (invalid entity: 3fe5a362-6980-4266-a819-aa688283929x does not exist)
 * a) Submit the GET request
 * b) Assert within response header that correct status code (404) is returned
 * c) Assert within JSON response that value of error.name field = "mockRequestNotFoundError"
*/
	@Test
	public void bonus() {
		given()
		.when()
			.get("/3fe5a362-6980-4266-a819-aa688283929x")
		.then()
			.statusCode(404)
			.body("error.name", equalTo("mockRequestNotFoundError"));
	}
}
