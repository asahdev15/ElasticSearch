package repo;

import com.google.common.collect.Lists;
import domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService{

    @Autowired
    private UserRepository userRepository;

    public User save(User user) {
        System.out.println("Saving User : "+user);
        return userRepository.save(user);
    }

    public List<User> saveAll(List<User> users) {
        System.out.println("Saving User size : " + users.size());
        return Lists.newArrayList(userRepository.saveAll(users));
    }

    public void delete(User user) {
        userRepository.delete(user);
    }

    public User findById(String id) {
        Optional<User> result = userRepository.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }

    public Iterable<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findByLastName(String lastName, PageRequest pageRequest) {
        return userRepository.findByLastName(lastName, pageRequest);
    }

    public List<User> findByAddress(String address) {
        return userRepository.findByAddress(address);
    }

}