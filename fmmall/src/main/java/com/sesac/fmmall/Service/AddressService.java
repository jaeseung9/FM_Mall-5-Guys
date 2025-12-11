package com.sesac.fmmall.Service;

import com.sesac.fmmall.DTO.Address.AddressResponseDto;
import com.sesac.fmmall.DTO.Address.AddressSaveRequestDto;
import com.sesac.fmmall.Entity.Address;
import com.sesac.fmmall.Entity.User;
import com.sesac.fmmall.Repository.AddressRepository;
import com.sesac.fmmall.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressResponseDto addAddress(Integer userId, AddressSaveRequestDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        Address address = dto.toEntity(user);
        return new AddressResponseDto(addressRepository.save(address));
    }

    @Transactional(readOnly = true)
    public List<AddressResponseDto> getAddressesByUserId(Integer userId) {
        List<Address> addresses = addressRepository.findByUser_UserId(userId);
        return addresses.stream()
                .map(AddressResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AddressResponseDto getAddressById(Integer addressId, Integer userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주소입니다."));

        if (address.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주소만 조회할 수 있습니다.");
        }

        return new AddressResponseDto(address);
    }

    public AddressResponseDto updateAddress(Integer addressId, Integer userId, AddressSaveRequestDto dto) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주소입니다."));

        if (address.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주소만 수정할 수 있습니다.");
        }

        Address updatedAddress = Address.builder()
                .addressId(address.getAddressId())
                .receiverName(dto.getReceiverName())
                .receiverPhone(dto.getReceiverPhone())
                .zipcode(dto.getZipcode())
                .address1(dto.getAddress1())
                .address2(dto.getAddress2())
                .isDefault(dto.getIsDefault())
                .createdAt(address.getCreatedAt())
                .user(address.getUser())
                .build();

        return new AddressResponseDto(addressRepository.save(updatedAddress));
    }

    public void deleteAddress(Integer addressId, Integer userId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 주소입니다."));

        if (address.getUser().getUserId() != userId) {
            throw new IllegalArgumentException("본인의 주소만 삭제할 수 있습니다.");
        }

        addressRepository.delete(address);
    }
}