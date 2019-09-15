package com.effectivesoft.bookservice.rest.controller;

import com.effectivesoft.bookservice.common.dto.UserGoalDto;
import com.effectivesoft.bookservice.common.dto.UserGoalProgressDto;
import com.effectivesoft.bookservice.core.model.MonthlyUserStats;
import com.effectivesoft.bookservice.core.model.UserGoal;
import com.effectivesoft.bookservice.rest.exception.InternalServerErrorException;
import com.effectivesoft.bookservice.rest.service.GoalService;
import com.effectivesoft.bookservice.rest.service.UserBookService;
import com.effectivesoft.bookservice.rest.service.UserProfileService;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/goal", consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE)
public class UserGoalController {
    private final Mapper mapper;
    private final GoalService goalService;
    private final UserBookService userBookService;
    private final UserProfileService userProfileService;

    public UserGoalController(@Autowired Mapper mapper,
                              @Autowired GoalService goalService,
                              @Autowired UserBookService userBookService,
                              @Autowired UserProfileService userProfileService) {
        this.mapper = mapper;
        this.goalService = goalService;
        this.userBookService = userBookService;
        this.userProfileService = userProfileService;
    }

    @PostMapping
    public ResponseEntity createUserGoal(@RequestBody UserGoalDto userGoalDto) {
        Optional<UserGoal> userGoal = goalService.createUserGoal(mapper.map(userGoalDto, UserGoal.class),
                userProfileService.getUserId());

        return new ResponseEntity<>(mapper.map(userGoal.orElseThrow(InternalServerErrorException::new),
                UserGoalDto.class), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity readUserGoal() {
        Optional<UserGoal> userGoal = goalService.readUserGoal(userProfileService.getUserId());

        return userGoal.map(goal -> ResponseEntity.ok(mapper.map(goal, UserGoalDto.class))).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping
    public ResponseEntity updateUserGoal(@RequestBody UserGoalDto userGoalDto) {
        Optional<UserGoal> userGoal = goalService.updateUserGoal(mapper.map(userGoalDto, UserGoal.class));

        return ResponseEntity.ok(userGoal.orElseThrow(InternalServerErrorException::new));
    }

    @GetMapping("/progress")
    public ResponseEntity readUserProgress() {
        Optional<UserGoal> userGoal = goalService.readUserGoal(userProfileService.getUserId());
        List<MonthlyUserStats> monthlyUserStats = userBookService.readUserStats(userProfileService.getUserId(),
                LocalDate.now().getYear());

        UserGoalProgressDto userGoalProgress = new UserGoalProgressDto();

        if (userGoal.isPresent()) {
            userGoalProgress.setGoal(userGoal.get().getBookCount());
        } else {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        userGoalProgress.setRead(0);

        for (MonthlyUserStats stat : monthlyUserStats) {
            userGoalProgress.setRead(userGoalProgress.getRead() + stat.getCount());
        }

        userGoalProgress.setYear(LocalDate.now().getYear());

        return ResponseEntity.ok(userGoalProgress);
    }

}
