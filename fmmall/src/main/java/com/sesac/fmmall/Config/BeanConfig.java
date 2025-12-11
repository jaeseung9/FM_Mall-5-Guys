package com.sesac.fmmall.Config;

import com.sesac.fmmall.DTO.Order.OrderResponse;
import com.sesac.fmmall.Entity.Order;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

    @Bean
    public ModelMapper modelMapper() {

        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true)
                .setAmbiguityIgnored(true);

        // Order → OrderResponse 변환 시 userId 자동 매핑 금지
        modelMapper.typeMap(Order.class, OrderResponse.class)
                .addMappings(mapper -> mapper.skip(OrderResponse::setUserId));

        return modelMapper;
    }
}
