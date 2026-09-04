package com.naru.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.naru.entity.RefreshToken;
import com.naru.entity.User;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	Optional<RefreshToken> findByUser(User user);

	void deleteByUser(User user);

	void deleteByToken(String token);
}