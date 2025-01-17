package aayush.librarymanagementsystem.controller;

import aayush.librarymanagementsystem.dto.Account;
import aayush.librarymanagementsystem.dto.Book;
import aayush.librarymanagementsystem.dto.BookStatus;
import aayush.librarymanagementsystem.property.ConfigurationProperty;
import aayush.librarymanagementsystem.service.AuthorService;
import aayush.librarymanagementsystem.service.LibraryService;
import aayush.librarymanagementsystem.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;
    private final UserService userService;
    private final AuthorService authorService;
    private final int pageSize;

    public LibraryController(LibraryService libraryService, UserService userService, AuthorService authorService, ConfigurationProperty props) {
        this.libraryService = libraryService;
        this.userService = userService;
        this.authorService = authorService;
        pageSize = props.getBooksPageSize();
    }

    @GetMapping(path = {"/books"})
    public String libraryBooks(Model model, @PageableDefault(sort = {"title"}, size = 5) Pageable pageable,
                               @RequestParam(value = "keyword", required = false) String keyword, Principal principal) {
        Pageable pageProps = pageable;

        if (pageProps.getPageSize() <= 5) {
            pageProps = PageRequest.of(pageable.getPageNumber(), pageSize, pageable.getSort());
        }

        Page<Book> bookList;

        if (keyword == null || keyword.isEmpty()) {
            bookList = libraryService.getBookList(pageProps);
        } else {
            bookList = libraryService.searchBooksByKeyword(keyword, pageProps);
        }

        model.addAttribute("books", bookList);
        model.addAttribute("currentPage", pageProps.getPageNumber());
        model.addAttribute("totalPages", bookList.getTotalPages());
        model.addAttribute("totalItems", bookList.getTotalElements());

        // find the user by email
        if (principal != null) {
            Account account = userService.getAccountByEmail(principal.getName()).orElse(null);
            model.addAttribute("account", account);
        }

        return "books";
    }

    @GetMapping("/books/{id}")
    public String libraryBook(Model model, @PathVariable("id") long id, Principal principal, @RequestParam(value = "error", required = false) String error) {
        Book book = libraryService.getBookById(id).orElse(null);

        if (book == null) {
            return "error";
        }

        if (error != null) {
            model.addAttribute("bookIsReservedError", error);
        }

        model.addAttribute("updatedBook", book); // updatedBook is used for updating book info
        model.addAttribute("libraryBook", book);
        model.addAttribute("statusList", Arrays.stream(BookStatus.values()).map(BookStatus::name).toList());

        model.addAttribute("authors", authorService.getAllAuthors());

        if (principal != null) {
            Account account = userService.getAccountByEmail(principal.getName()).orElse(null);
            model.addAttribute("account", account);
        }

        return "book";
    }

    @Secured("ROLE_LIBRARIAN")
    @PatchMapping("/books/{id}")
    public String libraryBookUpdate(@PathVariable("id") long id, @Valid @ModelAttribute("updatedBook") Book updatedBook, Errors errors, Principal principal, Model model) {
        if (principal != null) {
            Account account = userService.getAccountByEmail(principal.getName()).orElse(null);
            model.addAttribute("account", account);
        }

        if (errors.hasErrors()) {
            Book book = libraryService.getBookById(id).orElse(null);
            model.addAttribute("libraryBook", book);
            model.addAttribute("statusList", Arrays.stream(BookStatus.values()).map(BookStatus::name).toList());

            return "book";
        }

        Optional<Book> bookEntity = libraryService.updateBookInfo(id, updatedBook);

        if (bookEntity.isPresent()) {
            return "redirect:/library/books/" + id;
        }

        return "error";
    }

    @Secured("ROLE_LIBRARIAN")
    @GetMapping("/books/add")
    public String libraryBookAddView(Model model, Principal principal) {
        if (principal != null) {
            Account account = userService.getAccountByEmail(principal.getName()).orElse(null);
            model.addAttribute("account", account);
        }

        model.addAttribute("book", new Book());
        model.addAttribute("authors", authorService.getAllAuthors());

        return "bookAdd";
    }

    @Secured("ROLE_LIBRARIAN")
    @PostMapping("/books/add")
    public String libraryBookAdd(@Valid @ModelAttribute("book") Book book, Errors errors, Principal principal, Model model) {
        if (principal != null) {
            Account account = userService.getAccountByEmail(principal.getName()).orElse(null);
            model.addAttribute("account", account);
        }

        if (errors.hasErrors()) {
            return "bookAdd";
        }

        libraryService.addBook(book);
        return "redirect:/library/books";
    }

    @Secured("ROLE_LIBRARIAN")
    @PutMapping("/books/restore/{id}")
    public String libraryBookRestore(@PathVariable("id") long id) {
        Optional<Book> book = libraryService.restoreBook(id);
        if (book.isEmpty()) {
            return "error";
        }
        return "redirect:/library/books/" + id;
    }

    @Secured("ROLE_LIBRARIAN")
    @DeleteMapping("/books/archive/{id}")
    public String libraryBookArchive(@PathVariable("id") long id) {
        Optional<Book> book = libraryService.archiveBook(id);
        if (book.isEmpty()) {
            return "error";
        }
        return "redirect:/library/books/" + id;
    }
}