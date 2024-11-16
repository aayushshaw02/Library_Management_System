package aayush.librarymanagementsystem.repository;

import aayush.librarymanagementsystem.entity.AuthorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<AuthorEntity, Long>{
}
