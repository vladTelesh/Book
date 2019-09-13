package com.effectivesoft.bookservice.dataimporter;

import com.effectivesoft.bookservice.core.dao.AuthorDao;
import com.effectivesoft.bookservice.core.dao.BookDao;
import com.effectivesoft.bookservice.core.dao.UserBookDao;
import com.effectivesoft.bookservice.core.model.Author;
import com.effectivesoft.bookservice.core.model.Book;
import com.effectivesoft.bookservice.core.model.ImportBookDto;
import com.effectivesoft.bookservice.core.model.UserBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class BookImportService {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

    @Value("${user.id}")
    private String defaultUserId;

    private final BookDao bookDao;
    private final AuthorDao authorDao;
    private final UserBookDao userBookDao;

    private static final Logger logger = LoggerFactory.getLogger(BookImportService.class);

    BookImportService(@Autowired BookDao bookDao,
                      @Autowired AuthorDao authorDao,
                      @Autowired UserBookDao userBookDao){
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.userBookDao = userBookDao;
    }

    @Transactional
    public void importBookFromCsv(File file) {
        List<ImportBookDto> importBooks = new ArrayList<>();
        Path pathToFile = Paths.get(file.getPath());

        try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
            String line;
            line = br.readLine();
            line = br.readLine();

            while (line != null) {
                String[] attributes = line.split(",");

                ImportBookDto importBookDto = new ImportBookDto();
                importBookDto.setId(attributes[0]);
                importBookDto.setTitle(attributes[1]);
                importBookDto.setAuthor(attributes[2]);
                importBookDto.setAdditionalAuthors(attributes[3]);
                importBookDto.setISBN(attributes[4]);
                importBookDto.setISBN13(attributes[5]);
                importBookDto.setMyRating(Double.parseDouble(attributes[6]));
                importBookDto.setAverageRating(Double.parseDouble(attributes[7]));
                importBookDto.setPublisher(attributes[8]);
                importBookDto.setBinding(attributes[9]);
                importBookDto.setPagesNumber(Integer.parseInt(attributes[10]));
                importBookDto.setPublicationYear(Integer.parseInt(attributes[11]));
                importBookDto.setOriginalPublicationYear(Integer.parseInt(attributes[12]));
                importBookDto.setDateRead(attributes[13]);
                importBookDto.setDateAdded(attributes[14]);
                importBookDto.setReadCount(Integer.parseInt(attributes[15]));
                importBookDto.setDescription(attributes[16]);
                importBooks.add(importBookDto);
                line = br.readLine();
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        for (ImportBookDto importBook : importBooks){
            Book book = new Book();
            book.setId(importBook.getId());
            book.setTitle(importBook.getTitle());
            book.setAdditionalAuthors(importBook.getAdditionalAuthors());
            book.setISBN(importBook.getISBN().substring(1, 11));
            book.setISBN13(importBook.getISBN13().substring(1, 14));
            book.setAverageRating(importBook.getAverageRating());
            book.setPublisher(importBook.getPublisher());
            book.setBinding(importBook.getBinding());
            book.setPagesNumber(importBook.getPagesNumber());
            book.setPublicationYear(importBook.getPublicationYear());
            book.setOriginalPublicationYear(importBook.getOriginalPublicationYear());
            book.setDescription(importBook.getDescription());

            Optional<Author> author = authorDao.readAuthorByName(importBook.getAuthor());

            if(author.isPresent()){
                book.setAuthor(author.get());
            } else {
                Author newAuthor = new Author();
                newAuthor.setId(UUID.randomUUID().toString());
                newAuthor.setName(importBook.getAuthor());
                newAuthor.setGenerated(true);
                if(authorDao.create(newAuthor).isPresent()) {
                    book.setAuthor(newAuthor);
                }
            }

            bookDao.create(book);

            UserBook userBook = new UserBook();
            userBook.setBookId(importBook.getId());
            userBook.setUserId(defaultUserId);
            userBook.setMyRating(importBook.getMyRating());

            userBook.setReadCount(importBook.getReadCount());
            userBook.setDateAdded(LocalDate.parse(importBook.getDateAdded(), formatter));
            userBook.setDateRead(LocalDate.parse(importBook.getDateRead(), formatter));
            userBookDao.create(userBook);
        }
    }
}


