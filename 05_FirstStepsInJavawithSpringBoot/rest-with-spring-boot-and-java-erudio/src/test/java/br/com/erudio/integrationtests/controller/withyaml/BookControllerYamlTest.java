package br.com.erudio.integrationtests.controller.withyaml;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.controller.withyaml.mapper.YMLMapper;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelBook;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.EncoderConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerYamlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static YMLMapper objectMapper;
	private static BookVO book;
	
	@BeforeAll
	public static void setUp() {
		objectMapper = new YMLMapper();
		
		book = new BookVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = 
				given()
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_YML)
					.accept(TestConfigs.CONTENT_TYPE_YML)
				.body(user, objectMapper)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class, objectMapper)
								.getAccessToken();
		
		specification = new RequestSpecBuilder()
				.addHeader(TestConfigs.HEADER_PARAM_AUTHORIZATION, "Bearer " + accessToken)
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();
	}
	
	@Test
	@Order(1)
	public void testCreate() throws JsonMappingException, JsonProcessingException {
		MockBook();
		
		var persistedBook = 
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(book, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.as(BookVO.class, objectMapper);
				
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());
		
		assertTrue(persistedBook.getId() > 0);
		
		assertEquals("Michael C. Feathers", persistedBook.getAuthor());
		assertEquals(25D, persistedBook.getPrice());
		assertEquals("Working effectively with legacy code", persistedBook.getTitle());
		assertNotNull(persistedBook.getLaunchDate());
	}
	
	@Test
	@Order(2)
	public void testUpdate() throws JsonMappingException, JsonProcessingException {
		book.setAuthor("C. Feathers");
		
		var persistedBook = 
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.body(book, objectMapper)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.as(BookVO.class, objectMapper);
		
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());
		
		assertEquals(book.getId(), persistedBook.getId());
		
		assertEquals("C. Feathers", persistedBook.getAuthor());
		assertEquals(25D, persistedBook.getPrice());
		assertEquals("Working effectively with legacy code", persistedBook.getTitle());
		assertNotNull(persistedBook.getLaunchDate());
	}
	
	@Test
	@Order(3)
	public void testFindById() throws JsonMappingException, JsonProcessingException {
		MockBook();
		
		var persistedBook = 
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.pathParam("id", book.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
							.body()
								.as(BookVO.class, objectMapper);
		
		book = persistedBook;
		
		assertNotNull(persistedBook);
		assertNotNull(persistedBook.getId());
		assertNotNull(persistedBook.getAuthor());
		assertNotNull(persistedBook.getLaunchDate());
		assertNotNull(persistedBook.getPrice());
		assertNotNull(persistedBook.getTitle());
		
		assertEquals(book.getId(), persistedBook.getId());
		
		assertEquals("C. Feathers", persistedBook.getAuthor());
		assertEquals(25D, persistedBook.getPrice());
		assertEquals("Working effectively with legacy code", persistedBook.getTitle());
		assertNotNull(persistedBook.getLaunchDate());
	}
	
	@Test
	@Order(4)
	public void testDelete() throws JsonMappingException, JsonProcessingException {
		
		given().spec(specification)
			.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
			.contentType(TestConfigs.CONTENT_TYPE_YML)
			.accept(TestConfigs.CONTENT_TYPE_YML)
				.pathParam("id", book.getId())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var wrapper = 
				given().spec(specification)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.queryParams("page", 0, "size", 5 , "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.as(PagedModelBook.class, objectMapper);
		
		var books = wrapper.getContent();
		
		BookVO foundBookTwo = books.get(2);
		
		assertNotNull(foundBookTwo.getId());
		assertNotNull(foundBookTwo.getAuthor());
		assertNotNull(foundBookTwo.getLaunchDate());
		assertNotNull(foundBookTwo.getPrice());
		assertNotNull(foundBookTwo.getTitle());
		
		assertEquals(5, foundBookTwo.getId());
		
		assertEquals("Steve McConnell", foundBookTwo.getAuthor());
		assertEquals(58D, foundBookTwo.getPrice());
		assertEquals("Code complete", foundBookTwo.getTitle());
		assertNotNull(foundBookTwo.getLaunchDate());
		
		BookVO foundPersonFour = books.get(4);
		
		assertNotNull(foundPersonFour.getId());
		assertNotNull(foundPersonFour.getAuthor());
		assertNotNull(foundPersonFour.getLaunchDate());
		assertNotNull(foundPersonFour.getPrice());
		assertNotNull(foundPersonFour.getTitle());
		
		assertEquals(8, foundPersonFour.getId());
		
		assertEquals("Eric Evans", foundPersonFour.getAuthor());
		assertEquals(92D, foundPersonFour.getPrice());
		assertEquals("Domain Driven Design", foundPersonFour.getTitle());
		assertNotNull(foundPersonFour.getLaunchDate());
	}
	
	@Test
	@Order(6)
	public void testFindAllWhithoutToken() throws JsonMappingException, JsonProcessingException {
		
		RequestSpecification specificationWhithoutToken = new RequestSpecBuilder()
				.setBasePath("/api/book/v1")
				.setPort(TestConfigs.SERVER_PORT)
				.addFilter(new RequestLoggingFilter(LogDetail.ALL))
				.addFilter(new ResponseLoggingFilter(LogDetail.ALL))
				.build();;
				
				given().spec(specificationWhithoutToken)
				.config(RestAssuredConfig.config().encoderConfig(EncoderConfig.encoderConfig().encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
				.contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
				.when()
					.get()
				.then()
					.statusCode(403);
	}
	
	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var unthreatedContent = given()
                    .config(
                        RestAssuredConfig
                            .config()
                            .encoderConfig(EncoderConfig.encoderConfig()
                                    .encodeContentTypeAs(TestConfigs.CONTENT_TYPE_YML, ContentType.TEXT)))
                    .spec(specification)
                .contentType(TestConfigs.CONTENT_TYPE_YML)
				.accept(TestConfigs.CONTENT_TYPE_YML)
            	.queryParams("page", 0 , "size", 12, "direction", "asc")
                    .when()
                    .get()
                .then()
                    .statusCode(200)
                .extract()
                    .body()
						.asString();
		
		var content = unthreatedContent.replace("\n", "").replace("\r", "");
		
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/3\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/5\""));
		assertTrue(content.contains("rel: \"self\"    href: \"http://localhost:8888/api/book/v1/7\""));
		
		assertTrue(content.contains("rel: \"first\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=0&size=12&sort=title,asc\""));
		assertTrue(content.contains("rel: \"self\"  href: \"http://localhost:8888/api/book/v1?page=0&size=12&direction=asc\""));
		assertTrue(content.contains("rel: \"next\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\""));
		assertTrue(content.contains("rel: \"last\"  href: \"http://localhost:8888/api/book/v1?direction=asc&page=1&size=12&sort=title,asc\""));
		
		assertTrue(content.contains("page:  size: 12  totalElements: 15  totalPages: 2  number: 0"));
	}

	private void MockBook() {
		book.setAuthor("Michael C. Feathers");
		book.setLaunchDate(new Date());
		book.setPrice(25D);
		book.setTitle("Working effectively with legacy code");
		
	}

}
