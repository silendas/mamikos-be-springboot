package com.mamikos.backend.service;

import com.mamikos.backend.common.ResponseMessage;
import com.mamikos.backend.dto.KostRequest;
import com.mamikos.backend.dto.KostResponse;
import com.mamikos.backend.exception.ResourceNotFoundException;
import com.mamikos.backend.exception.UnauthorizedException;
import com.mamikos.backend.model.Kost;
import com.mamikos.backend.model.Role;
import com.mamikos.backend.model.User;
import com.mamikos.backend.repository.KostRepository;
import com.mamikos.backend.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KostService {

    private final KostRepository kostRepository;
    private final UserRepository userRepository;

    @Transactional
    public KostResponse createKost(String username, KostRequest request) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.USER_NOT_FOUND));

        if (owner.getRole() != Role.OWNER) {
            throw new UnauthorizedException("Only owners can add kosts");
        }

        Kost kost = Kost.builder()
                .owner(owner)
                .name(request.getName())
                .location(request.getLocation())
                .price(request.getPrice())
                .description(request.getDescription())
                .roomCount(request.getRoomCount())
                .build();

        Kost savedKost = kostRepository.save(kost);
        return mapToResponse(savedKost);
    }

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

        Kost updatedKost = kostRepository.save(kost);
        return mapToResponse(updatedKost);
    }

    @Transactional
    public void deleteKost(String username, Long kostId) {
        Kost kost = kostRepository.findById(kostId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.KOST_NOT_FOUND));

        if (!kost.getOwner().getUsername().equals(username)) {
            throw new UnauthorizedException("You are not the owner of this kost");
        }

        kostRepository.delete(kost);
    }

    public KostResponse getKostById(Long kostId) {
        Kost kost = kostRepository.findById(kostId)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.KOST_NOT_FOUND));
        return mapToResponse(kost);
    }

    public List<KostResponse> getOwnerKosts(String username) {
        User owner = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(ResponseMessage.USER_NOT_FOUND));

        return kostRepository.findByOwnerId(owner.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<KostResponse> searchKosts(String name, String location, Double minPrice, Double maxPrice, String sortDirection) {
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

        List<Kost> kosts = kostRepository.findAll(spec, sort);
        return kosts.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private KostResponse mapToResponse(Kost kost) {
        return KostResponse.builder()
                .id(kost.getId())
                .name(kost.getName())
                .location(kost.getLocation())
                .price(kost.getPrice())
                .description(kost.getDescription())
                .roomCount(kost.getRoomCount())
                .ownerId(kost.getOwner().getId())
                .ownerName(kost.getOwner().getUsername())
                .createdAt(kost.getCreatedAt())
                .updatedAt(kost.getUpdatedAt())
                .build();
    }
}

