package aayush.librarymanagementsystem.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import aayush.librarymanagementsystem.dto.Author;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "AUTHOR")
public class AuthorEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "author_seq")
    @TableGenerator(
            name = "author_seq",
            table = "id_gen_table",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            initialValue = 1000,
            allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "NAME")
    private String name;

    public Author toDto() {
        return new Author(id, name);
    }

    public static AuthorEntity fromDto(Author author) {
        if (author == null) {
            return null;
        }
        return new AuthorEntity(author.getId(), author.getName());
    }
}
