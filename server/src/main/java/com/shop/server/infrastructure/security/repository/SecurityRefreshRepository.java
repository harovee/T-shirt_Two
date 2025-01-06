package com.shop.server.infrastructure.security.repository;

import com.shop.server.entities.main.RefreshToken;
import com.shop.server.repositories.RefreshTokenRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface SecurityRefreshRepository extends RefreshTokenRepository {

    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    Optional<RefreshToken> findByUserId(String userId);

    int deleteByUserId(String userId);

    @Modifying
    @Transactional
    @Query(
            value = """
                    delete from refresh_token
                    where user_id = :userId
                    """,
            nativeQuery = true
    )
    int deleteByUserId(Long userId);

    @Query(
            value = """
                            SELECT revoked_at
                            FROM refresh_token rt
                            WHERE rt.user_id = :userId
                    """,
            nativeQuery = true
    )
    Long isRevoked(String userId);


    @Query(
            value = """
                    SELECT rt
                    FROM RefreshToken rt
                    WHERE rt.userId = :userId
                    """
    )
    RefreshToken getRefreshTokenByUserId(String userId);

}
