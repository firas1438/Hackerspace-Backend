package com.naru.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.naru.entity.RefreshToken;
import com.naru.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	void deleteByUser(User user); // for login

	void deleteByToken(String token); // for logout
}