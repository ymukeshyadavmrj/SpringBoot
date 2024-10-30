package com.example.demo.dtos;

import com.example.demo.models.Product;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

public interface ResponseDTOs {
    enum Status {
        SUCCESS,
        FAILURE
    }

    @Jacksonized
    @Builder
    @Getter
    class AuthenticationResponse {
        Status status;
        String message;
        String token;
    }

    @Jacksonized
    @Builder
    @Getter
    class StatusResponse {
        Status status;
        String message;
    }

    @Jacksonized
    @Builder
    @Getter
    class ProductResponse {
        String message;
        Product product;
    }

    @Jacksonized
    @Builder
    @Getter
    class AddProductResponse {
        Status status;
        Product product;
        String message;
    }
}
