package com.example.gbsports.repository;

import com.example.gbsports.entity.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RolesRepo extends JpaRepository<Roles,Integer> {
    @Query(value = """
            SELECT * FROM roles r
            WHERE r.ma_roles = :maRoles
            """, nativeQuery = true)
    Optional<Roles> findByMaRoles(String maRoles);
}
