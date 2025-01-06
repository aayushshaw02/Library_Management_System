package aayush.librarymanagementsystem.service;

import aayush.librarymanagementsystem.dto.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {
    List<Author> getAllAuthors();

    Optional<Author> getAuthorById(long id);
}