package com.sesac.fmmall.Repository;

import com.sesac.fmmall.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByUser_UserId(Integer userId);

    Optional<Address> findByUser_UserIdAndIsDefault(Integer userId, String isDefault);

}
