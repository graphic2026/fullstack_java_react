package com.example.demo.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Customer;

import com.example.demo.repositories.CustomerRepository;

@RestController
@CrossOrigin({"http://localhost:3000"})
public class CustomerRestController {
	
	@Autowired
	private CustomerRepository repo;
	
	@GetMapping("customers")
	public List<Customer> getEmployee(){
		return repo.findAll();
	}

	@PostMapping("/customers")
	public Customer insertEmployee(@RequestBody Customer c) {
		return repo.save(c);
	}
	
	
	@GetMapping("/customers/{accountno}")
	    public ResponseEntity<?> getCustomerAmount(@PathVariable Integer accountno) {
	        return repo.findById(accountno)
	                   .map(customer -> ResponseEntity.ok(customer.getAmount()))
	                   .orElse(ResponseEntity.notFound().build());
	    }
	
	@PutMapping("/customers/{accountno}/{updateAmount}")
	public ResponseEntity<?> addCustomerAmount(@PathVariable Integer accountno, @PathVariable Integer updateAmount) {
	        return repo.findById(accountno).map(customer -> {
	            // Add to the existing amount
	            customer.setAmount(customer.getAmount() + updateAmount);
	            repo.save(customer);
	            return ResponseEntity.ok(customer);
	        }).orElse(ResponseEntity.notFound().build());
	    }
	
	
	@PutMapping("/customers/reduce/{accountno}/{updateAmount}")
	public ResponseEntity<?> reduceCustomerAmount(@PathVariable Integer accountno, @PathVariable Integer updateAmount) {
	        return repo.findById(accountno).map(customer -> {
	            // Add to the existing amount
	            customer.setAmount(customer.getAmount() - updateAmount);
	            repo.save(customer);
	            return ResponseEntity.ok(customer);
	        }).orElse(ResponseEntity.notFound().build());
	    }
	
	
	@PostMapping("/transactions/{fromAccount}/{amount}/{toAccount}")
    public ResponseEntity<String> performTransaction(
            @PathVariable Integer fromAccount,
            @PathVariable Integer amount,
            @PathVariable Integer toAccount) {

        Customer fromCustomer = repo.findById(fromAccount).orElse(null);
        Customer toCustomer = repo.findById(toAccount).orElse(null);

        if (fromCustomer == null || toCustomer == null) {
            return ResponseEntity.notFound().build();
        }

        if (fromCustomer.getAmount() < amount) {
            return ResponseEntity.badRequest().body("Insufficient funds");
        }

        fromCustomer.setAmount(fromCustomer.getAmount() - amount);
        toCustomer.setAmount(toCustomer.getAmount() + amount);

        repo.save(fromCustomer);
        repo.save(toCustomer);

        return ResponseEntity.ok("Transaction successful");
    }
	
	
	
	
	
	

}
