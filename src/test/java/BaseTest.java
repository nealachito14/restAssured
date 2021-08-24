import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseTest {
    private static final Logger logger = LogManager.getLogger(ReqResTests.class);

    @BeforeEach
    public void setup() throws FileNotFoundException {
        logger.info("Iniciando la configuración de log");
        System.out.println("El log no sirve pero esto sí");
        RestAssured.requestSpecification = defaultSpecification();
        logger.info("Configuración de log Exitosa");
    }

    public static RequestSpecification defaultSpecification() throws FileNotFoundException {
        List<Filter> filters = new ArrayList<>();
        filters.add(new ResponseLoggingFilter());
        filters.add(new RequestLoggingFilter());
        filters.add(new AllureRestAssured());

        return new RequestSpecBuilder().setBaseUri(ConfVariables.getHost())
                .setBasePath(ConfVariables.getPath())
                .addFilters(filters)
                .setContentType(ContentType.JSON).build();
    }

    public ResponseSpecification defaultResponseSpecification(){
        return new ResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .expectContentType(ContentType.JSON)
                .build();
    }
}
