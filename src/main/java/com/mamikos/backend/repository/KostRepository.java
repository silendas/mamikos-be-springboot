package com.mamikos.backend.repository;

import com.mamikos.backend.model.Kost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KostRepository extends JpaRepository<Kost, Long>, JpaSpecificationExecutor<Kost> {
    List<Kost> findByOwnerId(Long ownerId);
}
