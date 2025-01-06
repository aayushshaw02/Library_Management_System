package aayush.librarymanagementsystem;

import aayush.librarymanagementsystem.controller.LibraryController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class LibraryManagementSystemApplicationTests {

    @Autowired
    private LibraryController libraryController;

    @Test
    void contextLoads() {
        assertThat(libraryController).isNotNull();
    }
}