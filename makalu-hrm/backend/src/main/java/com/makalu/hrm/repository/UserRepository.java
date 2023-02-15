package com.makalu.hrm.repository;

import com.makalu.hrm.domain.PersistentUserEntity;
import com.makalu.hrm.enumconstant.UserType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<PersistentUserEntity, UUID> {

    Optional<PersistentUserEntity> findById(UUID userId);

    Optional<PersistentUserEntity> findByUsername(String username);

    List<PersistentUserEntity> findByUserType(UserType userType);

    @Override
    List<PersistentUserEntity> findAllById(Iterable<UUID> uuids);

}
