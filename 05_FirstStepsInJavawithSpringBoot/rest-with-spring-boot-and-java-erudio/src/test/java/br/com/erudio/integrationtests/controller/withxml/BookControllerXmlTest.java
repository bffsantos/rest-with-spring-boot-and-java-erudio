package br.com.erudio.integrationtests.controller.withxml;

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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import br.com.erudio.configs.TestConfigs;
import br.com.erudio.integrationtests.testcontainers.AbstractIntegrationTest;
import br.com.erudio.integrationtests.vo.AccountCredentialsVO;
import br.com.erudio.integrationtests.vo.BookVO;
import br.com.erudio.integrationtests.vo.TokenVO;
import br.com.erudio.integrationtests.vo.pagedmodels.PagedModelBook;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(OrderAnnotation.class)
public class BookControllerXmlTest extends AbstractIntegrationTest{
	
	private static RequestSpecification specification;
	private static XmlMapper objectMapper;
	private static BookVO book;
	
	@BeforeAll
	public static void setUp() {
		objectMapper = new XmlMapper();
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		
		book = new BookVO();
	}
	
	@Test
	@Order(0)
	public void authorization() throws JsonMappingException, JsonProcessingException {
		AccountCredentialsVO user = new AccountCredentialsVO("leandro", "admin123");
		
		var accessToken = 
				given()
				.basePath("/auth/signin")
					.port(TestConfigs.SERVER_PORT)
					.contentType(TestConfigs.CONTENT_TYPE_XML)
					.accept(TestConfigs.CONTENT_TYPE_XML)
				.body(user)
					.when()
				.post()
					.then()
						.statusCode(200)
							.extract()
							.body()
								.as(TokenVO.class)
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
		
		var content = 
				given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		
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
		
		var content = 
				given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.body(book)
				.when()
					.post()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		
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
		
		var content = 
				given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
					.header(TestConfigs.HEADER_PARAM_ORIGIN, TestConfigs.ORIGIN_ERUDIO)
					.pathParam("id", book.getId())
				.when()
					.get("{id}")
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		BookVO persistedBook = objectMapper.readValue(content, BookVO.class);
		
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
			.contentType(TestConfigs.CONTENT_TYPE_XML)
			.accept(TestConfigs.CONTENT_TYPE_XML)
				.pathParam("id", book.getId())
			.when()
				.delete("{id}")
			.then()
				.statusCode(204);
	}
	
	@Test
	@Order(5)
	public void testFindAll() throws JsonMappingException, JsonProcessingException {
		
		var content = 
				given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.queryParams("page", 0, "size", 5 , "direction", "asc")
				.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
							.body()
								.asString();
		
		PagedModelBook wrapper = objectMapper.readValue(content, PagedModelBook.class);
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
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
				.when()
					.get()
				.then()
					.statusCode(403);
	}
	
	@Test
	@Order(7)
	public void testHATEOAS() throws JsonMappingException, JsonProcessingException {
		
		var content = given().spec(specification)
				.contentType(TestConfigs.CONTENT_TYPE_XML)
				.accept(TestConfigs.CONTENT_TYPE_XML)
            	.queryParams("page", 0 , "size", 12, "direction", "asc")
					.when()
					.get()
				.then()
					.statusCode(200)
						.extract()
						.body()
							.asString();
		
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/3</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/5</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1/7</href></links>"));
		
		assertTrue(content.contains("<links><rel>first</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=0&amp;size=12&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>self</rel><href>http://localhost:8888/api/book/v1?page=0&amp;size=12&amp;direction=asc</href></links>"));
		assertTrue(content.contains("<links><rel>next</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=1&amp;size=12&amp;sort=title,asc</href></links>"));
		assertTrue(content.contains("<links><rel>last</rel><href>http://localhost:8888/api/book/v1?direction=asc&amp;page=1&amp;size=12&amp;sort=title,asc</href></links>"));
		
		assertTrue(content.contains("<page><size>12</size><totalElements>15</totalElements><totalPages>2</totalPages><number>0</number></page>"));
	}

	private void MockBook() {
		book.setAuthor("Michael C. Feathers");
		book.setLaunchDate(new Date());
		book.setPrice(25D);
		book.setTitle("Working effectively with legacy code");
		
	}

}
