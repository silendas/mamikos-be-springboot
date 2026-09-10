package com.mamikos.backend.service.impl;

import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.KostRequest;
import com.mamikos.backend.dto.KostResponse;
import com.mamikos.backend.dto.PageResponse;
import com.mamikos.backend.exception.ResourceNotFoundException;
import com.mamikos.backend.exception.UnauthorizedException;
import com.mamikos.backend.mapper.KostMapper;
import com.mamikos.backend.model.Kost;
import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.KostRepository;
import com.mamikos.backend.repository.UserRepository;
import com.mamikos.backend.service.KostService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KostServiceImpl implements KostService {

    private final KostRepository kostRepository;
    private final UserRepository userRepository;
    private final KostMapper kostMapper;

    @Override
    @Transactional
    public KostResponse createKost(String username, KostRequest request) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.USER_NOT_FOUND));

        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can add kosts");
        }

        Kost kost = kostMapper.toEntity(request, owner);
        return kostMapper.toResponse(kostRepository.save(kost));
    }

    @Override
    @Transactional
    public KostResponse updateKost(String username, Long kostId, KostRequest request) {
        Kost kost = kostRepository.findById(kostId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.KOST_NOT_FOUND));

        if (!kost.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not the owner of this kost");
        }

        kost.setName(request.getName());
        kost.setLocation(request.getLocation());
        kost.setPrice(request.getPrice());
        kost.setDescription(request.getDescription());
        kost.setRoomCount(request.getRoomCount());

        return kostMapper.toResponse(kostRepository.save(kost));
    }

    @Override
    @Transactional
    public void deleteKost(String username, Long kostId) {
        Kost kost = kostRepository.findById(kostId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.KOST_NOT_FOUND));

        if (!kost.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not the owner of this kost");
        }

        kostRepository.delete(kost);
    }

    @Override
    public KostResponse getKostById(Long kostId) {
        Kost kost = kostRepository.findById(kostId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.KOST_NOT_FOUND));
        return kostMapper.toResponse(kost);
    }

    @Override
    public List<KostResponse> getOwnerKosts(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.USER_NOT_FOUND));

        return kostRepository.findByOwnerId(owner.getId()).stream()
                .map(kostMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PageResponse<KostResponse> searchKosts(String name, String location, Double minPrice, Double maxPrice, String sortDirection, int page, int size) {
        Specification<Kost> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (location != null && !location.trim().isEmpty()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + location.toLowerCase() + "%"));
            }
            if (minPrice != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.unsorted();
        if (sortDirection != null) {
            if (sortDirection.equalsIgnoreCase("asc")) {
                sort = Sort.by(Sort.Direction.ASC, "price");
            } else if (sortDirection.equalsIgnoreCase("desc")) {
                sort = Sort.by(Sort.Direction.DESC, "price");
            }
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Kost> kostPage = kostRepository.findAll(spec, pageable);

        List<KostResponse> content = kostPage.getContent().stream()
                .map(kostMapper::toResponse)
                .collect(Collectors.toList());

        return PageResponse.<KostResponse>builder()
                .content(content)
                .pageNumber(kostPage.getNumber())
                .pageSize(kostPage.getSize())
                .totalElements(kostPage.getTotalElements())
                .totalPages(kostPage.getTotalPages())
                .last(kostPage.isLast())
                .build();
    }
}