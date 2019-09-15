package com.effectivesoft.bookservice.rest.controller;

import com.effectivesoft.bookservice.common.dto.AnnualUserStatsDto;
import com.effectivesoft.bookservice.common.dto.MonthlyUserStatsDto;
import com.effectivesoft.bookservice.core.model.MonthlyUserStats;
import com.effectivesoft.bookservice.core.model.UserBook;
import com.effectivesoft.bookservice.core.model.AnnualUserStats;
import com.effectivesoft.bookservice.rest.exception.InternalServerErrorException;
import com.effectivesoft.bookservice.rest.service.RequestParser;
import com.effectivesoft.bookservice.rest.service.UserBookService;
import com.effectivesoft.bookservice.rest.service.UserProfileService;
import com.effectivesoft.bookservice.common.dto.UserBookDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/user_books", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserBookController {

    private final UserProfileService userProfileService;
    private final UserBookService userBookService;
    private final RequestParser requestParser;
    private final Mapper mapper;

    UserBookController(@Autowired UserProfileService userProfileService,
                       @Autowired UserBookService userBookService,
                       @Autowired RequestParser requestParser,
                       @Autowired Mapper mapper) {
        this.userProfileService = userProfileService;
        this.userBookService = userBookService;
        this.requestParser = requestParser;
        this.mapper = mapper;
    }


    @PostMapping
    public ResponseEntity createUserBook(@RequestBody UserBookDto userBookDto) {
        Optional<UserBook> userBook = userBookService.createUserBook(userBookDto.getBookDto().getId(),
                userProfileService.getUserId(),
                mapper.map(userBookDto, UserBook.class));
        if (userBook.isPresent()) {
            return new ResponseEntity(HttpStatus.CREATED);
        } else {
            throw new InternalServerErrorException();
        }
    }

    @GetMapping
    public ResponseEntity readUserBooks(@RequestParam("limit") Integer limit,
                                        @RequestParam("offset") Integer offset,
                                        @RequestParam(value = "sort", required = false) String[] sort) {
        List<UserBook> userBooks = userBookService.readUserBooks(userProfileService.getUserId(),
                limit, offset, requestParser.parseSortParam(sort));
        List<UserBookDto> userBooksDto = new ArrayList<>();
        for (UserBook userBook : userBooks) {
            userBooksDto.add(mapper.map(userBook, UserBookDto.class));
        }
        return ResponseEntity.ok(userBooksDto);
    }

    @GetMapping("/count")
    public ResponseEntity readUserBooksCount() {
        return ResponseEntity.ok(userBookService.readUserBooksCount(userProfileService.getUserId()));
    }

    @GetMapping("/stats")
    public ResponseEntity readUserStats(@RequestParam(value = "from", required = false) Integer from,
                                        @RequestParam(value = "to", required = false) Integer to,
                                        @RequestParam(value = "year", required = false) Integer year) {
        List<AnnualUserStats> annualUserStats;
        List<MonthlyUserStats> monthlyUserStats;
        List<AnnualUserStatsDto> annualUserStatsDto = new ArrayList<>();
        List<MonthlyUserStatsDto> monthlyUserStatsDto = new ArrayList<>();

        if (from != null && to != null && year == null) {
            annualUserStats = userBookService.readUserStats(userProfileService.getUserId(), from, to);

            for (AnnualUserStats userStat : annualUserStats) {
                annualUserStatsDto.add(mapper.map(userStat, AnnualUserStatsDto.class));
            }

            return ResponseEntity.ok(annualUserStatsDto);
        } else {
            if (from == null && to == null && year != null) {
                monthlyUserStats = userBookService.readUserStats(userProfileService.getUserId(), year);

                for (MonthlyUserStats monthlyUserStat : monthlyUserStats) {
                    monthlyUserStatsDto.add(mapper.map(monthlyUserStat, MonthlyUserStatsDto.class));
                }

                return ResponseEntity.ok(monthlyUserStatsDto);
            } else {
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
        }
    }

    @PutMapping
    public ResponseEntity updateUserBook(@Valid @RequestBody UserBookDto userBookDto) {
        Optional<UserBook> userBook = userBookService.updateUserBook(userBookDto, userProfileService.getUserId());
        return ResponseEntity.ok(mapper.map(userBook.orElseThrow(InternalServerErrorException::new), UserBookDto.class));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteUserBook(@PathVariable String id) {
        userBookService.deleteUserBook(id, userProfileService.getUserId());
        return new ResponseEntity<>(200, HttpStatus.OK);
    }
}
