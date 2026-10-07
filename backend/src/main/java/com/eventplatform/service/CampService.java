package com.eventplatform.service;

import com.eventplatform.dto.camp.CampCreateRequest;
import com.eventplatform.dto.camp.CampResponse;
import com.eventplatform.dto.camp.CampUpdateRequest;
import com.eventplatform.dto.common.PageResponse;
import com.eventplatform.exception.ApiException;
import com.eventplatform.model.Camp;
import com.eventplatform.model.User;
import com.eventplatform.model.enums.CampType;
import com.eventplatform.repository.CampRepository;
import com.eventplatform.security.CustomUserDetails;
import com.eventplatform.security.SecurityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampService {

    private final CampRepository campRepository;
    private final SecurityService securityService;

    public CampResponse create(CampCreateRequest request, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User organizer = userDetails.getUser();

        Camp camp = Camp.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .ageGroup(request.getAgeGroup())
                .capacity(request.getCapacity())
                .price(request.getPrice())
                .organizer(organizer)
                .published(request.getPublished() != null && request.getPublished())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        camp = campRepository.save(camp);
        log.info("Camp created: {} by user: {}", camp.getId(), organizer.getEmail());
        return CampResponse.from(camp);
    }

    public PageResponse<CampResponse> list(int page, int size, String sortBy, String sortOrder,
                                             CampType type, String search, Boolean published) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                sortBy
        ));

        Page<Camp> campsPage;

        if (type != null && published != null && published) {
            campsPage = campRepository.findByTypeAndPublishedTrue(type, pageable);
        } else if (published != null && published) {
            if (search != null && !search.isBlank()) {
                campsPage = campRepository.findByPublishedTrueAndTitleContainingIgnoreCase(search, pageable);
            } else {
                campsPage = campRepository.findByPublishedTrue(pageable);
            }
        } else if (type != null) {
            campsPage = campRepository.findByTypeAndPublishedTrue(type, pageable);
        } else {
            campsPage = campRepository.findAll(pageable);
        }

        List<CampResponse> content = campsPage.getContent().stream()
                .map(CampResponse::from)
                .toList();

        return PageResponse.<CampResponse>builder()
                .content(content)
                .page(campsPage.getNumber())
                .size(campsPage.getSize())
                .totalElements(campsPage.getTotalElements())
                .totalPages(campsPage.getTotalPages())
                .first(campsPage.isFirst())
                .last(campsPage.isLast())
                .build();
    }

    public CampResponse getById(String id) {
        Camp camp = campRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Camp not found"));
        return CampResponse.from(camp);
    }

    public CampResponse update(String id, CampUpdateRequest request, Authentication authentication) {
        if (!securityService.canModifyCamp(id, authentication)) {
            throw ApiException.forbidden("You do not have permission to update this camp");
        }

        Camp camp = campRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Camp not found"));

        if (request.getTitle() != null) camp.setTitle(request.getTitle());
        if (request.getDescription() != null) camp.setDescription(request.getDescription());
        if (request.getType() != null) camp.setType(request.getType());
        if (request.getStartDate() != null) camp.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) camp.setEndDate(request.getEndDate());
        if (request.getLocation() != null) camp.setLocation(request.getLocation());
        if (request.getAgeGroup() != null) camp.setAgeGroup(request.getAgeGroup());
        if (request.getCapacity() != null) camp.setCapacity(request.getCapacity());
        if (request.getPrice() != null) camp.setPrice(request.getPrice());
        if (request.getPublished() != null) camp.setPublished(request.getPublished());

        camp.setUpdatedAt(Instant.now());
        camp = campRepository.save(camp);

        log.info("Camp updated: {} by user: {}", id, ((CustomUserDetails) authentication.getPrincipal()).getUser().getEmail());
        return CampResponse.from(camp);
    }

    public void delete(String id, Authentication authentication) {
        if (!securityService.canModifyCamp(id, authentication)) {
            throw ApiException.forbidden("You do not have permission to delete this camp");
        }

        if (!campRepository.existsById(id)) {
            throw ApiException.notFound("Camp not found");
        }

        campRepository.deleteById(id);
        log.info("Camp deleted: {} by user: {}", id, ((CustomUserDetails) authentication.getPrincipal()).getUser().getEmail());
    }

    public PageResponse<CampResponse> getMyCamps(int page, int size, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        User user = userDetails.getUser();

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Camp> campsPage = campRepository.findByOrganizer(user, pageable);

        List<CampResponse> content = campsPage.getContent().stream()
                .map(CampResponse::from)
                .toList();

        return PageResponse.<CampResponse>builder()
                .content(content)
                .page(campsPage.getNumber())
                .size(campsPage.getSize())
                .totalElements(campsPage.getTotalElements())
                .totalPages(campsPage.getTotalPages())
                .first(campsPage.isFirst())
                .last(campsPage.isLast())
                .build();
    }

}