package aayush.librarymanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import aayush.librarymanagementsystem.dto.Book;
import aayush.librarymanagementsystem.dto.BookStatus;

import java.util.Date;

@Data
@Entity
@Table(name = "BOOK")
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class BookEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "book_seq")
    @TableGenerator(
            name = "book_seq",
            table = "id_gen_table",
            pkColumnName = "gen_name",
            valueColumnName = "gen_val",
            initialValue = 1000,
            allocationSize = 1)
    @Column(name = "ID")
    private Long id;
    @Column(name = "ISBN", unique = true, nullable = false)
    @NonNull
    private String ISBN;
    @Column(name = "TITLE", nullable = false)
    @NonNull
    private String title;
    @Column(name = "LANGUAGE", nullable = false)
    @NonNull
    private String language;
    @Column(name = "NUMBER_OF_PAGES", nullable = false)
    @NonNull
    private Integer numberOfPages;
    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private BookStatus status = BookStatus.ACTIVE;
    @Temporal(TemporalType.DATE)
    @Column(name = "PUBLISH_DATE")
    private Date publishDate = new Date();
    @Temporal(TemporalType.DATE)
    @Column(name = "ARCHIVE_DATE")
    private Date archiveDate;
    @Column(name = "NUMBER_OF_COPIES", nullable = false)
    @NonNull
    private Integer numberOfCopies = 1;

    @ManyToOne
    @JoinColumn(name = "AUTHOR_ID")
    private AuthorEntity author;

    public Book toDto() {
        var authorDto = author == null ? null : author.toDto();
        return new Book(id, ISBN, title, language, numberOfPages, status, publishDate, archiveDate, numberOfCopies, authorDto);
    }

    public static BookEntity fromDto(Book book) {
        if (book == null) {
            return null;
        }

        return new BookEntity(
                book.getId(),
                book.getISBN(),
                book.getTitle(),
                book.getLanguage(),
                book.getNumberOfPages(),
                book.getStatus(),
                book.getPublishDate(),
                book.getArchiveDate(),
                book.getNumberOfCopies(),
                AuthorEntity.fromDto(book.getAuthor())
        );
    }
}