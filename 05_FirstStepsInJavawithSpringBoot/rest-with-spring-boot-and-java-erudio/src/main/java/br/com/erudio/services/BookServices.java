package br.com.erudio.services;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.erudio.controllers.BookController;
import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.excpetions.RequiredObjectIsNullException;
import br.com.erudio.excpetions.ResourceNotFoundException;
import br.com.erudio.mapper.ErudioMapper;
import br.com.erudio.model.Book;
import br.com.erudio.repositories.BookRepository;

@Service
public class BookServices {
	
	private Logger logger = Logger.getLogger(BookServices.class.getName());
	
	@Autowired
	BookRepository repository;
	
	public List<BookVO> findAll() {
		
		logger.info("Finding all books.");
		
		var books = ErudioMapper.parseListObjects(repository.findAll(), BookVO.class);
		
		books.stream().forEach(b -> b.add(linkTo(methodOn(BookController.class).findById(b.getKey())).withSelfRel()));
		
		return books;
	}	
	
	public BookVO findById(Long id) {
		
		logger.info("Finding one book.");
		
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
		
		var vo = ErudioMapper.parseObject(entity, BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(id)).withSelfRel());
		
		return vo;
	}

	public BookVO create(BookVO book) {
		
		if(book == null) throw new RequiredObjectIsNullException();
	
		logger.info("Creating one book.");
		
		var entity = ErudioMapper.parseObject(book, Book.class);
		
		var vo = ErudioMapper.parseObject(repository.save(entity), BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
	}
	
	public BookVO update(BookVO book) {
		
		if(book == null) throw new RequiredObjectIsNullException();
		
		logger.info("Updating one book.");
		
		var entity = repository.findById(book.getKey()).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
		
		entity.setAuthor(book.getAuthor());		
		entity.setLaunchDate(book.getLaunchDate());		
		entity.setPrice(book.getPrice());		
		entity.setTitle(book.getTitle());
		
		var vo = ErudioMapper.parseObject(repository.save(entity), BookVO.class);
		
		vo.add(linkTo(methodOn(BookController.class).findById(vo.getKey())).withSelfRel());
		
		return vo;
		
	}
	
	public void delete(Long id) {
		
		logger.info("Deleting one book.");
		
		var entity = repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No records found for this ID."));
		
		repository.delete(entity);
	}
}
