package com.aetheri.domain.adapter.out.r2dbc;

import com.aetheri.application.port.out.image.ImageRepositoryPort;
import com.aetheri.domain.adapter.out.r2dbc.spi.ImageR2dbcRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryR2dbcAdapter implements ImageRepositoryPort {
    private final ImageR2dbcRepository imageR2dbcRepository;
}