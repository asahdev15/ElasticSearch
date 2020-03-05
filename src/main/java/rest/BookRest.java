package rest;

import com.google.common.collect.Lists;
import domain.Book;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repo.BookService;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
public class BookRest
{

   @Autowired
   private BookService bookService;

   // Get
   @GetMapping(value = "/book/all", produces = { "application/json" } )
   public ResponseEntity<List<Book>> getAllBooks()
   {
      return new ResponseEntity<>(Lists.newArrayList(bookService.findAll()), HttpStatus.OK);
   }
   @GetMapping(value = "/book/author/{id}", produces = { "application/json" } )
   public ResponseEntity<Book> getBook(@RequestParam (required = true) String id)
   {
      return new ResponseEntity<>(bookService.findById(id), HttpStatus.OK);
   }
   @GetMapping(value = "/book/title", produces = { "application/json" } )
   public ResponseEntity<List<Book>> getBooksByTitle(@RequestParam (required = true) String title)
   {
      return new ResponseEntity<>(Lists.newArrayList(bookService.findByTitle(title)), HttpStatus.OK);
   }
   @GetMapping(value = "/book/author", produces = { "application/json" } )
   public ResponseEntity<List<Book>> getBooksByAuthor(@RequestParam (required = true) String author)
   {
      return new ResponseEntity<>(Lists.newArrayList(bookService.findByAuthor(author, new PageRequest(0, 10))), HttpStatus.OK);
   }
   // Create
   @PostMapping(value = "/book", consumes = { "application/json" })
   public void addBook(@RequestBody Book book)
   {
      bookService.save(book);
   }
   @PostMapping(value = "/book/saveAll", consumes = { "application/json" })
   public void saveAll(@RequestParam int count)
   {
      List<Book> books = Lists.newArrayList();
      for(int i =0 ; i<count ; i++){
         books.add(new Book(String.valueOf(i),"Title:"+i,"Author:"+i,"Date:"+i));
      }
      List<CompletableFuture> cfs = Lists.newArrayList();
      for(int i =0 ; i<count ; i++){
         cfs.add(CompletableFuture.runAsync(()->bookService.saveAll(books)));
      }
   }
   // Update
   // Delete
   @PostMapping(value = "/book/delete", consumes = { "application/json" })
   public void deleteBook(@RequestBody Book book)
   {
      bookService.delete(book);
   }

}
