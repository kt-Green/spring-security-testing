package com.kstech.warroom.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kstech.warroom.entity.ERole;
import com.kstech.warroom.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Integer>{
	Optional<Role> findByName(ERole name);
}
