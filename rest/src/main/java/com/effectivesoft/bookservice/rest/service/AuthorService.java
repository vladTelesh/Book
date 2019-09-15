package com.effectivesoft.bookservice.rest.service;

import com.effectivesoft.bookservice.core.dao.AuthorDao;
import com.effectivesoft.bookservice.core.model.Author;
import com.effectivesoft.bookservice.core.model.Book;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class AuthorService {
    @Value("${image.directory}")
    private String directory;
    @Value("${server.port}")
    private String port;
    @Value("${server.host}")
    private String host;
    @Value("${hosting.images.url}")
    private String imagesHostingUrl;

    private final AuthorDao authorDao;

    public AuthorService(@Autowired AuthorDao authorDao) {
        this.authorDao = authorDao;
    }

    @Transactional
    public Optional<Author> createAuthor(Author author) {
        author.setId(UUID.randomUUID().toString());

        return authorDao.create(author);
    }

    public Optional<Author> readAuthor(String id) {
        return authorDao.read(id);
    }

    public List<Author> readAuthors(String name, boolean generated, int limit, int offset, String[] sortingColumns) {
        StringBuilder sort = new StringBuilder();
        if (sortingColumns != null) {
            for (String s : sortingColumns) {
                sort.append(s).append(",");
            }
            sort.deleteCharAt(sort.length() - 1);
        }

        List<Author> authors = authorDao.readAuthors(name, generated, limit, offset, sort.toString());

        return Objects.requireNonNullElse(authors, Collections.emptyList());
    }

    public List<Book> readAuthorsBooks(String id, int limit, int offset) {
        return authorDao.readAuthorsBooks(id, limit, offset);
    }

    public List<Author> readAuthorsByName(String name) {
        return authorDao.readAuthorsByName(name);
    }

    public long readAuthorsCount(String name, boolean generated) {
        return authorDao.readAuthorsCount(name, generated);
    }

    @Transactional
    public boolean updateAuthorPhoto(MultipartFile imageFile, String authorId) throws IOException {
        String path = directory + "/author/" + authorId;
        File folder = new File(path);
        folder.mkdir();
        FileUtils.cleanDirectory(folder);
        String imageName = UUID.randomUUID().toString();
        String photoLink = "http://" + host + ":" + port + imagesHostingUrl + "/author/" + authorId + "/" + imageName + ".jpg";

        authorDao.updatePhotoLink(authorId, photoLink);

        if (authorDao.updatePhotoLink(authorId, photoLink)) {
            imageFile.transferTo(new File(folder.getPath() + "\\" + imageName + ".jpg"));
            return true;
        }

        return false;
    }

    @Transactional
    public Optional<Author> updateAuthor(Author author) {
        return authorDao.update(author);
    }
}
