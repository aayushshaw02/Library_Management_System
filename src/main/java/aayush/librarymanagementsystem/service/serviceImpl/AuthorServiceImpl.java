package aayush.librarymanagementsystem.service.serviceImpl;

import aayush.librarymanagementsystem.dto.Author;
import aayush.librarymanagementsystem.entity.AuthorEntity;
import aayush.librarymanagementsystem.repository.AuthorRepository;
import aayush.librarymanagementsystem.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private final AuthorRepository authorRepository;

    @Override
    public List<Author> getAllAuthors() {
        return authorRepository.findAll().stream().map(AuthorEntity::toDto).toList();
    }

    @Override
    public Optional<Author> getAuthorById(long id) {
        return authorRepository.findById(id).map(AuthorEntity::toDto);
    }
}
