package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByUserPhone(String userPhone);

    boolean existsByLoginId(String loginId);

}
