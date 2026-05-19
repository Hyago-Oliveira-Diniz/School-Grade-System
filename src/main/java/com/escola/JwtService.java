package com.escola;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Gera o token JWT com o username e o tipo do usuário
    public String gerarToken(Usuario usuario) {
        return Jwts.builder()
                .subject(usuario.getUsername())
                .claim("tipo", usuario.getTipo().name())
                .claim("nome", usuario.getNome())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getKey())
                .compact();
    }

    // Extrai o username do token
    public String extrairUsername(String token) {
        return getClaims(token).getSubject();
    }

    // Extrai o tipo do usuário do token
    public String extrairTipo(String token) {
        return getClaims(token).get("tipo", String.class);
    }

    // Valida se o token pertence ao usuário e não está expirado
    public boolean validarToken(String token, Usuario usuario) {
        String username = extrairUsername(token);
        return username.equals(usuario.getUsername()) && !isExpirado(token);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private boolean isExpirado(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
