package com.aetheri.application.port.in.sign;

import reactor.core.publisher.Mono;

/**
 * 사용자 회원가입(Sign Up)과 관련된 비즈니스 로직을 정의하는 유즈케이스 인터페이스입니다.
 * 이 유즈케이스는 외부 서비스(예: 카카오)를 통해 얻은 정보를 바탕으로 신규 사용자를 시스템에 등록하는 기능을 제공합니다.
 */
public interface SignUpUseCase {
    /**
     * 카카오 인증 등 외부 서비스로부터 얻은 정보({@code id}, {@code name})를 사용하여 새로운 회원을 시스템에 등록(회원가입)합니다.
     *
     * <p>이 메서드는 다음 단계를 포함할 수 있습니다:</p>
     * <ol>
     * <li>주어진 {@code id}를 사용하여 사용자 데이터베이스에 중복 사용자가 없는지 확인합니다.</li>
     * <li>새로운 사용자의 정보를 데이터베이스에 저장(영속화)합니다.</li>
     * <li>필요한 경우 초기 설정(예: 기본 권한 부여)을 수행합니다.</li>
     * </ol>
     *
     * @param id 카카오 등 외부 서비스에서 제공하는 사용자의 고유 식별자(ID)입니다.
     * @param name 카카오 등 외부 서비스에서 제공하는 사용자의 이름 또는 닉네임입니다.
     * @return 회원가입 처리가 완료되었을 때 종료되는 {@code Mono<Void>} 객체입니다.
     */
    Mono<Void> signUp(Long id, String name);
}