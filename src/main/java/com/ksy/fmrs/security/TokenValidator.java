package com.ksy.fmrs.security;

import com.ksy.fmrs.domain.enums.TokenType;
import com.ksy.fmrs.exception.UnauthorizedException;
import com.ksy.fmrs.repository.BlackListRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TokenValidator{

    private final BlackListRepository blackListRepository;

    public void validateTokenInBlacklist(Claims claims){
        if(blackListRepository.existsBlackListsByRefreshJti(claims.getId())){
            throw new UnauthorizedException("이미 로그아웃되었거나 무효화된 리프레시 토큰입니다.");
        }
    }

    public void validateUserId(Claims claims, Long userId){
        if(!claims.getSubject().equals(userId.toString())){
            throw new UnauthorizedException("로그인한 사용자의 토큰이 아닙니다.");
        }
    }

    public void validateTokenType(Claims claims, TokenType tokenType){
        if(!claims.get("type", String.class).equals(tokenType.name())){
            throw new UnauthorizedException("토큰 타입이 다릅니다.");
        }
    }
}
