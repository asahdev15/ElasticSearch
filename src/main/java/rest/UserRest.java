package rest;

import com.google.common.collect.Lists;
import domain.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repo.UserService;

import java.util.List;

@RestController
@AllArgsConstructor
public class UserRest
{

   @Autowired
   private UserService userService;

   // Get
   @GetMapping(value = "/user/all", produces = { "application/json" } )
   public ResponseEntity<List<User>> getAllUsers()
   {
      return new ResponseEntity<>(Lists.newArrayList(userService.findAll()), HttpStatus.OK);
   }
   @GetMapping(value = "/user/{id}", produces = { "application/json" } )
   public ResponseEntity<User> getUser(@RequestParam (required = true) String id)
   {
      return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
   }
   @GetMapping(value = "/user/address", produces = { "application/json" } )
   public ResponseEntity<List<User>> getUserByAddresss(@RequestParam (required = true) String address)
   {
      return new ResponseEntity<>(Lists.newArrayList(userService.findByAddress(address)), HttpStatus.OK);
   }
   @GetMapping(value = "/user/lastName", produces = { "application/json" } )
   public ResponseEntity<List<User>> getUserByLastName(@RequestParam (required = true) String lastName)
   {
      return new ResponseEntity<>(Lists.newArrayList(userService.findByLastName(lastName, new PageRequest(0, 10))), HttpStatus.OK);
   }
   // Create
   @PostMapping(value = "/user", consumes = { "application/json" })
   public void saveUser(@RequestBody User user)
   {
      User userNew = User.builder()
              .id(user.getId())
              .firstName(user.getFirstName())
              .lastName(user.getLastName())
              .address(user.getAddress())
              .build();
      userService.save(userNew);
   }


//   @PostMapping(value = "/book/saveAll", consumes = { "application/json" })
//   public void saveAll(@RequestParam int count)
//   {
//      List<Book> books = Lists.newArrayList();
//      for(int i =0 ; i<count ; i++){
//         books.add(new Book(String.valueOf(i),"Title:"+i,"Author:"+i,"Date:"+i));
//      }
//      List<CompletableFuture> cfs = Lists.newArrayList();
//      for(int i =0 ; i<count ; i++){
//         cfs.add(CompletableFuture.runAsync(()-> userService.saveAll(books)));
//      }
//   }
//   // Update
//   // Delete
//   @PostMapping(value = "/user/delete", consumes = { "application/json" })
//   public void deleteBook(@RequestBody Book book)
//   {
//      userService.delete(book);
//   }

}
