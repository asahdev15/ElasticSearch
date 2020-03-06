package rest;

import com.google.common.collect.Lists;
import domain.Attribute;
import domain.Customer;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repo.CustomerService;

import java.awt.print.Book;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@AllArgsConstructor
public class CustomerRest
{

   @Autowired
   private CustomerService customerService;

   @PostMapping(value = "/customer", consumes = { "application/json" })
   public void save(@RequestBody Customer customer)
   {
      customerService.save(customer);
   }

   @GetMapping(value = "/customers/", produces = { "application/json" } )
   public ResponseEntity<List<Customer>> getAllCustomers()
   {
      return new ResponseEntity<>(Lists.newArrayList(customerService.getAll()), HttpStatus.OK);
   }

   @GetMapping(value = "/customer/", produces = { "application/json" } )
   public ResponseEntity<Customer> getCustomerById(@RequestParam (required = true) String id)
   {
      return new ResponseEntity<>(customerService.getById(id), HttpStatus.OK);
   }

   @DeleteMapping(value = "/customer/", produces = { "application/json" } )
   public void deleteCustomerById(@RequestParam (required = true) String id)
   {
      customerService.delete(id);
   }

}
