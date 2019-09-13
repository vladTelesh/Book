package com.effectivesoft.bookservice.datagenerator;

import com.effectivesoft.bookservice.core.model.ImportBookDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class BookCsvGenerator {
    private static final char DEFAULT_SEPARATOR = ',';
    @Value("${directory}") String directory;
    @Value("${file.name}") String fileName;
    @Value("${books.count}") int booksCount;

    private static final Logger logger = LoggerFactory.getLogger(BookCsvGenerator.class);

    private static List<String> authors = new ArrayList<>(Arrays.asList("Leo Tolstoy", "Jane Austen", "Franz Kafka", "Nikolai Gogol",
            "Fyodor Dostoyevsky", "Aleksandr Pushkin", "Anton Chekhov", "Mikhail Lermontov"));

    private static List<String> titles = new ArrayList<>(Arrays.asList("Crime and Punishment", "Anna Karenina", "War and Peace", "The Death of Ivan Ilyich",
            "Dead Souls", "The Master and Margarita", "Fathers and Sons", "Eugene Onegin"));

    public void generateBooks(String fileName) {
        List<ImportBookDto> bookDTOS = new ArrayList<>();
        for (int i = 0; i < booksCount; i++) {
            bookDTOS.add(setDefaultValues(new ImportBookDto()));
        }

        Path path = Paths.get(directory);
        if (!Files.exists(path)) {
            throw new IllegalArgumentException();
        }

        try (FileWriter writer = new FileWriter(directory + "/" + fileName)){
            writeLine(writer, Arrays.asList("BookDto id", "Title", "Author", "Additional Authors",
                    "ISBN", "ISBN13", "My Rating", "Average Rating", "Publisher",
                    "Binding", "Number of Pages", "Year Published", "Original Publication Year",
                    "Date Read", "Date Added", "Read Count"));
            for (ImportBookDto bookDTO : bookDTOS) {
                writeLine(writer, Arrays.asList(bookDTO.getId(), bookDTO.getTitle(), bookDTO.getAuthor(),
                        bookDTO.getAdditionalAuthors(), bookDTO.getISBN(), bookDTO.getISBN13(), bookDTO.getMyRating().toString(),
                        bookDTO.getAverageRating().toString(), bookDTO.getPublisher(), bookDTO.getBinding(),
                        bookDTO.getPagesNumber().toString(), bookDTO.getPublicationYear().toString(),
                        bookDTO.getOriginalPublicationYear().toString(), bookDTO.getDateRead(),
                        bookDTO.getDateAdded(), bookDTO.getReadCount().toString()));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private ImportBookDto setDefaultValues(ImportBookDto importBookDto) {
        Random random = new Random();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Date date = new Date();

        importBookDto.setId(UUID.randomUUID().toString());
        importBookDto.setTitle(titles.get(Math.abs(random.nextInt()) % 8));
        importBookDto.setAuthor(authors.get(Math.abs(random.nextInt()) % 8));
        importBookDto.setAdditionalAuthors(authors.get(Math.abs(random.nextInt()) % 8));
        importBookDto.setISBN(String.valueOf(Math.abs(random.nextLong())).substring(0, 10));
        importBookDto.setISBN13(String.valueOf(Math.abs(random.nextLong())).substring(0, 13));
        importBookDto.setMyRating((double) (Math.abs(random.nextInt()) % 6));
        importBookDto.setAverageRating((double) Math.abs(random.nextInt()) % 6);
        importBookDto.setPublisher("Penguin Random House");
        importBookDto.setBinding("Paperback");
        importBookDto.setPagesNumber(0);
        importBookDto.setPublicationYear(1990);
        importBookDto.setOriginalPublicationYear(1978);
        importBookDto.setDateRead(dateFormat.format(date));
        importBookDto.setDateAdded(dateFormat.format(date));
        importBookDto.setReadCount(Math.abs(random.nextInt()) % 8);
        importBookDto.setDescription("");
        return importBookDto;
    }

    private void writeLine(Writer w, List<String> values) throws IOException {
        writeLine(w, values, DEFAULT_SEPARATOR, ' ');
    }

    private String followCSVFormat(String value) {
        String result = Objects.requireNonNullElse(value, "");

        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;
    }

    private void writeLine(Writer w, List<String> values, char separators, char customQuote) throws IOException {
        boolean first = true;

        if (separators == ' ') {
            separators = DEFAULT_SEPARATOR;
        }

        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCSVFormat(value));
            } else {
                sb.append(customQuote).append(followCSVFormat(value)).append(customQuote);
            }
            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
}

