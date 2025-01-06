package aayush.librarymanagementsystem.job;

import aayush.librarymanagementsystem.dto.Book;
import aayush.librarymanagementsystem.service.LibraryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
@PersistJobDataAfterExecution
public class DeleteBookJob implements Job {
    private final LibraryService libraryService;

    /**
     * Delete books that are archived and have been archived for more than 30 days
     * @param jobExecutionContext
     * @throws JobExecutionException
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
//        JobDataMap dataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        List<Book> booksToDelete = libraryService.getBooksToDelete();

        booksToDelete.forEach(book -> {
            log.info("Deleting book: {}", book.getTitle());
            libraryService.deleteBook(book.getId());
        });
    }
}