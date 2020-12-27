package rest;

import asahdev.models.User;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import repo.CustomerService;

import java.util.List;

@RestController
@AllArgsConstructor
public class CustomerRest
{

   @Autowired
   private CustomerService customerService;

   @PostMapping(value = "/customer", consumes = { "application/json" })
   public void save(@RequestBody User customer)
   {
      customerService.save(customer);
   }

   @GetMapping(value = "/customers/", produces = { "application/json" } )
   public ResponseEntity<List<User>> getAllCustomers()
   {
      return new ResponseEntity<>(Lists.newArrayList(customerService.getAll()), HttpStatus.OK);
   }

   @GetMapping(value = "/customer/", produces = { "application/json" } )
   public ResponseEntity<User> getCustomerById(@RequestParam (required = true) String id)
   {
      return new ResponseEntity<>(customerService.getById(id), HttpStatus.OK);
   }

   @DeleteMapping(value = "/customer/", produces = { "application/json" } )
   public void deleteCustomerById(@RequestParam (required = true) String id)
   {
      customerService.delete(id);
   }

}
