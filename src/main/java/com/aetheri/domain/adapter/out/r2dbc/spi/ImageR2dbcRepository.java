package com.aetheri.domain.adapter.out.r2dbc.spi;

import com.aetheri.infrastructure.persistence.Image;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface ImageR2dbcRepository extends R2dbcRepository<Image, Long> {
}
