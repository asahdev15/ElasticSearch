package repo;

import com.google.common.collect.Lists;
import domain.Customer;
import infra.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public void save(Customer customer) {
        System.out.println("Saving Customer : "+ customer);
        customerRepository.save(customer);
    }

    public Customer getById(String id) {
        Optional<Customer> result = customerRepository.searchById(id);
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }

    public List<Customer> getAll() {
        return customerRepository.searchAll(1000, 0).getContent();
    }

    public void delete(String id) {
        customerRepository.deleteByIds(Lists.newArrayList(id));
    }

}