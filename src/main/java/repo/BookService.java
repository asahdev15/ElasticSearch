package repo;

import com.google.common.collect.Lists;
import domain.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookService{

    @Autowired
    private BookRepository bookRepository;

    public Book save(Book book) {
        System.out.println("Saving Book : "+book);
        return bookRepository.save(book);
    }

    public List<Book> saveAll(List<Book> books) {
        System.out.println("Saving Book size : " + books.size());
        return Lists.newArrayList(bookRepository.saveAll(books));
    }

    public void delete(Book book) {
        bookRepository.delete(book);
    }

    public Book findById(String id) {
        Optional<Book> result = bookRepository.findById(id);
        if(result.isPresent()){
            return result.get();
        }
        return null;
    }

    public Iterable<Book> findAll() {
        return bookRepository.findAll();
    }

    public Page<Book> findByAuthor(String author, PageRequest pageRequest) {
        return bookRepository.findByAuthor(author, pageRequest);
    }

    public List<Book> findByTitle(String title) {
        return bookRepository.findByTitle(title);
    }

}