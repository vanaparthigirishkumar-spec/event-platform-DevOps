package com.eventplatform.security;

import com.eventplatform.model.Event;
import com.eventplatform.model.Camp;
import com.eventplatform.model.User;
import com.eventplatform.repository.EventRepository;
import com.eventplatform.repository.CampRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final EventRepository eventRepository;
    private final CampRepository campRepository;

    public boolean isEventOwner(String eventId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return eventRepository.existsByIdAndOrganizer(eventId, userDetails.getUser());
    }

    public boolean isCampOwner(String campId, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return campRepository.existsByIdAndOrganizer(campId, userDetails.getUser());
    }

    public boolean isAdmin(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return userDetails.getUser().getRole() == com.eventplatform.model.enums.UserRole.ADMIN;
    }

    public boolean canModifyEvent(String eventId, Authentication authentication) {
        return isAdmin(authentication) || isEventOwner(eventId, authentication);
    }

    public boolean canModifyCamp(String campId, Authentication authentication) {
        return isAdmin(authentication) || isCampOwner(campId, authentication);
    }

}