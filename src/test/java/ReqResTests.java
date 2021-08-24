import io.restassured.http.Headers;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.from;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class ReqResTests extends BaseTest{

    @Test
    public void loginTests(){

                given()
                .body("{\n" +
                        "    \"email\": \"eve.holt@reqres.in\",\n" +
                        "    \"password\": \"cityslicka\"\n" +
                        "}")
                .post("login")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token",notNullValue());
                //.extract()
                //.asString();
        //System.out.println(response);
    }
    @Test
    public void getSingleUserTest(){
        given()
                .get("users/3")
                .then()
                .statusCode(200)
                .body("data.id", equalTo(3));


    }
    @Test
    public void deleteUserTest(){
        given()
                .delete("users/2")
                .then()
                .statusCode(HttpStatus.SC_NO_CONTENT);
    }
    @Test
    public void patchUserTest(){
        String nameUpdated = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"zion resident\"\n" +
                        "}")
                .patch("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath()
                .getString("name");
        assertThat(nameUpdated,equalTo("morpheus"));
    }

    @Test
    public void putUserTest(){
        String recursoUptdate = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"Nealachito\",\n" +
                        "    \"job\": \"Automatizador\"\n" +
                        "}")
                .put("users/2")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath()
                .getString("job");
        assertThat(recursoUptdate, equalTo("Automatizador"));
    }
    @Test
    public void getAllUsersTest(){
        Response response = given()
                .get("users?page=2");
        Headers headers = response.getHeaders();
        int statusCode = response.getStatusCode();
        String body = response.getBody().asString();
        String contentType = response.getContentType();

        assertThat(statusCode, equalTo(HttpStatus.SC_OK));

        System.out.println("Headers :) :::"+headers);
        System.out.println("Body :) :::"+body);
        System.out.println("CoNtEnT TyPe :) :::"+contentType);
        System.out.println("*******************************");
        System.out.println(headers.get("Date"));
        System.out.println(headers.get("X-Powered-By"));

    }
    @Test
    public void getAllUsersTest2(){
        String response = given()
                .when()
                .get("users?page=2").then().extract().body().asString();
        int page = from(response).get("page");
        int totalPages = from(response).get("total_pages");
        String nameFirstUser = from(response).get("data[0].first_name");
        System.out.println("Pagina #: "+page);
        System.out.println("Total de paginas: "+totalPages);
        System.out.println("Nombre del primer usuario: "+nameFirstUser);

        System.out.println("*************************");
        List<Map> usersWithIdGreaterThan10 = from(response).get("data.findAll {user -> user.id > 10 }");
        String last_name = usersWithIdGreaterThan10.get(0).get("last_name").toString();
        System.out.println("Apellido: "+last_name);

        List<Map> user = from(response).get("data.findAll {user -> user.id > 10 && user.last_name == 'Edwards' }");
        String first_name = user.get(0).get("first_name").toString();
        int id  = (int) user.get(0).get("id");

        System.out.println("Nombre: "+first_name+" and Id: "+id);
    }

    @Test
    public void createUserTest(){
        String response = given()
                .when()
                .body("{\n" +
                        "    \"name\": \"morpheus\",\n" +
                        "    \"job\": \"leader\"\n" +
                        "}")
                .post("users").then().extract().body().asString();
        User user = from(response).getObject("",User.class);
        System.out.println("Id: "+user.getId()+" Nombre: "+user.getName());
    }

    @Test
    public void registerUserTest(){
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setEmail("eve.holt@reqres.in");
        userRequest.setPassword("nealachito14");

        CreateUserResponse userResponse = given()
                .when()
                .body(userRequest)
                .post("register")
                .then()
                .spec(defaultResponseSpecification())
                .contentType(equalTo("application/json; charset=utf-8"))
                .extract()
                .body()
                .as(CreateUserResponse.class);
        assertThat(userResponse.getId(),equalTo(4));
        assertThat(userResponse.getToken(),equalTo("QpwL5tke4Pnpja7X4"));
    }
}
