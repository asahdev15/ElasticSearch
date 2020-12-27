package repo;

import asahdev.models.User;
import com.google.common.collect.Lists;
import infra.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public void save(User customer) {
        System.out.println("Saving Customer : "+ customer);
        customerRepository.save(customer);
    }

    public User getById(String id) {
        Optional<User> result = customerRepository.searchById(id);
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }

    public List<User> getAll() {
        return customerRepository.searchAll(1000, 0).getContent();
    }

    public void delete(String id) {
        customerRepository.deleteByIds(Lists.newArrayList(id));
    }

}