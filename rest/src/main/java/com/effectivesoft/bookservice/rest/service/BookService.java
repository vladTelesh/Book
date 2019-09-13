package com.effectivesoft.bookservice.rest.service;

import com.effectivesoft.bookservice.core.dao.BookDao;
import com.effectivesoft.bookservice.core.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class BookService {

    @Value("${image.directory}")
    private String directory;
    @Value("${server.port}")
    private String port;
    @Value("${server.host}")
    private String host;
    @Value("${hosting.images.url}")
    private String imagesHostingUrl;

    private final BookDao bookDao;

    BookService(@Autowired BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Transactional
    public Optional<Book> createBook(Book book) {
        book.setId(UUID.randomUUID().toString());
        book.setAverageRating(0.0);
        return bookDao.create(book);
    }

    public Optional<Book> readBook(String id) {
        return bookDao.read(id);
    }

    public List<Book> readBooks(int limit, int offset, String[] sortingColumns) {
        StringBuilder sort = new StringBuilder();
        if (sortingColumns != null) {
            for (String s : sortingColumns) {
                sort.append(s).append(",");
            }
            sort.deleteCharAt(sort.length() - 1);
        }
        List<Book> books = bookDao.readBooks(limit, offset, sort.toString());
        return Objects.requireNonNullElse(books, Collections.emptyList());
    }

    public long readBooksCount() {
        return bookDao.readCount();
    }

    public long readBooksCount(String title){
        return bookDao.readCount(title);
    }

    public List<Book> readBooksByTitle(String title){
        return bookDao.readBooksByTitle(title);
    }

    @Transactional
    public Optional<Book> updateBook(Book book) {
        return bookDao.update(book);
    }

    @Transactional
    public boolean updateBookImage(MultipartFile imageFile, String bookId) throws IOException {
        String path = directory + "/book/" + bookId;
        File folder = new File(path);
        folder.mkdir();
        String imageName = UUID.randomUUID().toString();
        String photoLink = "http://" + host + ":" + port + imagesHostingUrl + "/book/" + bookId + "/" + imageName + ".jpg";

        if (bookDao.updateImageLink(bookId, photoLink) == 1) {
            imageFile.transferTo(new File(folder.getPath() + "\\" + imageName + ".jpg"));
            return true;
        }

        return false;
    }

    @Transactional
    public void deleteBook(String id) {
        bookDao.delete(id);
    }
}
