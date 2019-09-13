package com.effectivesoft.bookservice.rest.service;

import com.effectivesoft.bookservice.rest.filter.ThreadLocalData;
import org.springframework.stereotype.Service;


@Service
public class UserProfileService {

    public String getUserId() {
        return ThreadLocalData.getData().get().getId();
    }

    public String getUsername() {
        return ThreadLocalData.getData().get().getUsername();
    }
}
