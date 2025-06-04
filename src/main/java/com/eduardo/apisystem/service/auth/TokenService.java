package com.eduardo.apisystem.service.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.eduardo.apisystem.entity.usuario.Usuario;
import com.eduardo.apisystem.exception.customizadas.jwt.TokenJWTException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {
  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.expiration}")
  private String expiration;

  public String gerarRefreshToken(Usuario usuario) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      return JWT.create()
              .withIssuer("Api System")
              .withSubject(usuario.getUsuarioId().toString())
              .withExpiresAt(dataExpiracao("1800"))
              .sign(algorithm);

    } catch (JWTCreationException e) {
      throw new TokenJWTException("Erro ao criar token: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public String gerarToken(Usuario usuario) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      return JWT.create()
          .withIssuer("Api System")
          .withSubject(usuario.getEmail())
          .withExpiresAt(dataExpiracao(expiration))
          .sign(algorithm);

    } catch (JWTCreationException e) {
      throw new TokenJWTException("Erro ao criar token: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public String getSubject(String tokenJWT) {
    try {
      return JWT.require(Algorithm.HMAC256(secret))
          .withIssuer("Api System")
          .build()
          .verify(tokenJWT)
          .getSubject();

    } catch (JWTVerificationException e) {
      throw new TokenJWTException("Erro ao verificar token: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
    }
  }

  public boolean isTokenExpirado(String tokenJWT) {
    DecodedJWT decodedJWT = JWT.decode(tokenJWT);

    Date dataExpiracao = decodedJWT.getExpiresAt();

    return dataExpiracao.before(new Date());
  }

  private Instant dataExpiracao(String time) {
    long segundos = Long.parseLong(time);
    return LocalDateTime.now().plusSeconds(segundos).toInstant(ZoneOffset.of("-03:00"));
  }
}
