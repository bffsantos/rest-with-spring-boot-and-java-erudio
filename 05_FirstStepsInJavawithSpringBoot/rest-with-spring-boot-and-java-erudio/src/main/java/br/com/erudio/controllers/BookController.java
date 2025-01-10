package br.com.erudio.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.services.BookServices;
import br.com.erudio.util.MediaType;

@RestController
@RequestMapping("/api/book/v1")
public class BookController {
	
	@Autowired
	private BookServices services;

	@GetMapping(produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
	public List<BookVO> findAll() {
		return services.findAll();
	}
	
	@GetMapping(value ="/{id}", produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
	public BookVO findById(Long id) {
		return services.findById(id);
	}
	
	@PostMapping(consumes = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML }, produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
	public BookVO create(BookVO book) {
		return services.create(book);
	}
	
	@PutMapping(consumes = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML }, produces = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML, MediaType.APPLICATION_YML })
	public BookVO update(BookVO book) {
		return services.update(book);
	}
	
	@DeleteMapping(value ="/{id}")
	public ResponseEntity<?> delete(Long id) {
		services.delete(id);
		return ResponseEntity.noContent().build();
	}
}
