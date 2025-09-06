package com.aetheri.interfaces.dto.kakao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoAccount(

        // 프로필 정보 제공 동의 여부
        @JsonProperty("profile_needs_agreement")
        Boolean isProfileAgree,

        // 닉네임 제공 동의 여부
        @JsonProperty("profile_nickname_needs_agreement")
        Boolean isNickNameAgree,

        // 프로필 사진 제공 동의 여부
        @JsonProperty("profile_image_needs_agreement")
        Boolean isProfileImageAgree,

        // 사용자 프로필 정보
        @JsonProperty("profile")
        Profile profile,

        // 이름 제공 동의 여부
        @JsonProperty("name_needs_agreement")
        Boolean isNameAgree,

        // 카카오계정 이름
        @JsonProperty("name")
        String name,

        // 이메일 제공 동의 여부
        @JsonProperty("email_needs_agreement")
        Boolean isEmailAgree,

        // 이메일이 유효 여부
        @JsonProperty("is_email_valid")
        Boolean isEmailValid,

        // 이메일이 인증 여부
        @JsonProperty("is_email_verified")
        Boolean isEmailVerified,

        // 카카오계정 대표 이메일
        @JsonProperty("email")
        String email,

        // 연령대 제공 동의 여부
        @JsonProperty("age_range_needs_agreement")
        Boolean isAgeAgree,

        // 연령대
        @JsonProperty("age_range")
        String ageRange,

        // 출생 연도 제공 동의 여부
        @JsonProperty("birthyear_needs_agreement")
        Boolean isBirthYearAgree,

        // 출생 연도 (YYYY 형식)
        @JsonProperty("birthyear")
        String birthYear,

        // 생일 제공 동의 여부
        @JsonProperty("birthday_needs_agreement")
        Boolean isBirthDayAgree,

        // 생일 (MMDD 형식)
        @JsonProperty("birthday")
        String birthDay,

        // 생일 타입 (SOLAR, LUNAR)
        @JsonProperty("birthday_type")
        String birthDayType,

        // 성별 제공 동의 여부
        @JsonProperty("gender_needs_agreement")
        Boolean isGenderAgree,

        // 성별
        @JsonProperty("gender")
        String gender,

        // 전화번호 제공 동의 여부
        @JsonProperty("phone_number_needs_agreement")
        Boolean isPhoneNumberAgree,

        // 전화번호 (+82 00-0000-0000 형식)
        @JsonProperty("phone_number")
        String phoneNumber,

        // CI 동의 여부
        @JsonProperty("ci_needs_agreement")
        Boolean isCIAgree,

        // CI, 연계 정보
        @JsonProperty("ci")
        String ci,

        // CI 발급 시각, UTC
        @JsonProperty("ci_authenticated_at")
        Date ciCreatedAt
) {}