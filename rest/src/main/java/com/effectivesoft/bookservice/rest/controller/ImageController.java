package com.effectivesoft.bookservice.rest.controller;

import com.effectivesoft.bookservice.core.model.Image;
import com.effectivesoft.bookservice.rest.exception.InternalServerErrorException;
import com.effectivesoft.bookservice.rest.service.ImageService;
import com.effectivesoft.bookservice.rest.service.UserProfileService;
import com.effectivesoft.bookservice.rest.service.Validator;
import com.effectivesoft.bookservice.common.dto.ImageDto;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/v1/users/images", produces = MediaType.APPLICATION_JSON_VALUE)
public class ImageController {

    private final UserProfileService userProfileService;
    private final ImageService imageService;
    private final Validator validator;
    private final Mapper mapper;

    ImageController(@Autowired UserProfileService userProfileService,
                    @Autowired ImageService imageService,
                    @Autowired Validator validator,
                    @Autowired Mapper mapper) {
        this.userProfileService = userProfileService;
        this.imageService = imageService;
        this.validator = validator;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity createImage(@RequestParam(value = "image") MultipartFile image,
                                      @RequestParam(value = "is_main", defaultValue = "false") String isMain) throws IOException {
        if (!validator.extensionValidator(Objects.requireNonNull(image.getContentType()))) {
            return new ResponseEntity(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }
        Optional<Image> optionalImage = imageService.createImage(image, userProfileService.getUserId(), isMain);

        return ResponseEntity.ok(optionalImage.orElseThrow(InternalServerErrorException::new));
    }

    @GetMapping(value = "/main")
    public ResponseEntity readMainImage() {
        Optional<Image> image = imageService.readMainImage(userProfileService.getUserId());
        return image.map(value -> ResponseEntity.ok(mapper.map(value, ImageDto.class))).orElseGet(() ->
                new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity readImages() {
        List<Image> images = imageService.readImages(userProfileService.getUserId());
        List<ImageDto> imagesDto = new ArrayList<>();
        for (Image image : images) {
            imagesDto.add(mapper.map(image, ImageDto.class));
        }
        return ResponseEntity.ok(imagesDto);
    }

    @PutMapping
    public ResponseEntity updateMainImage(@RequestBody ImageDto imageDto) {
        imageService.updateMainImage(userProfileService.getUserId(), imageDto.getLink());
        return ResponseEntity.ok(imageDto);
    }

    @DeleteMapping(value = "/{imageId}")
    public ResponseEntity deleteImage(@PathVariable String imageId) {
        imageService.deleteImage(imageId);
        return new ResponseEntity<>(200, HttpStatus.OK);
    }
}
