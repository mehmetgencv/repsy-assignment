package io.repsy.repsyapi.repository;

import io.repsy.repsyapi.entity.PackageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PackageRepository extends JpaRepository<PackageEntity, Long> {
}