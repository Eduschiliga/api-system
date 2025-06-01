package com.eduardo.apisystem.service.auth;

import com.eduardo.apisystem.entity.usuario.Usuario;
import com.eduardo.apisystem.mapper.usuario.UsuarioMapper;
import com.eduardo.apisystem.model.dto.jwt.RefreshTokenDTO;
import com.eduardo.apisystem.model.dto.jwt.TokenDTO;
import com.eduardo.apisystem.model.dto.usuario.UsuarioResponseDTO;
import com.eduardo.apisystem.model.request.LoginRequest;
import com.eduardo.apisystem.repository.usuario.UsuarioRepository;
import com.eduardo.apisystem.exception.customizadas.usuario.UsuarioException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements UserDetailsService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final TokenService tokenService;
    private final ApplicationContext applicationContext;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserDetails user = usuarioRepository.findByLogin(login);
        if (user == null) {
            throw new UsernameNotFoundException("Usuário não encontrado com login: " + login);
        }
        return user;
    }


    public Usuario findUsuarioEntityByToken(String token) {
        token = token.replace("Bearer ", "");
        String subject = tokenService.getSubject(token);

        return usuarioRepository.findByLogin(subject);
    }

    public UsuarioResponseDTO findUsuarioByToken(String token) {
        token = token.replace("Bearer ", "");
        String subject = tokenService.getSubject(token);
        Usuario usuario = usuarioRepository.findByLogin(subject);

        return usuarioMapper.usuarioToUsuarioResponseDTO(usuario);
    }

    public TokenDTO refreshToken(RefreshTokenDTO refreshTokenDTO) {
        Long subject = Long.valueOf(tokenService.getSubject(refreshTokenDTO.getRefreshToken()));

        Usuario usuario = usuarioRepository.findById(subject)
                .orElseThrow(() -> new UsuarioException("Não foi possível encontrar o usuário de Id: " + subject, HttpStatus.NOT_FOUND));

        return gerarToken(usuario);
    }

    private TokenDTO gerarToken(Usuario usuario) {
        String token = tokenService.gerarToken(usuario);
        String refreshToken = tokenService.gerarRefreshToken(usuario);

        return TokenDTO.builder()
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenDTO authLogin(LoginRequest loginRequest) {
        UsernamePasswordAuthenticationToken usernamePassAuthToken = new UsernamePasswordAuthenticationToken(
                loginRequest.getLogin(),
                loginRequest.getSenha()
        );

        AuthenticationManager authenticationManager = applicationContext.getBean(AuthenticationManager.class);
        Authentication authentication = authenticationManager.authenticate(usernamePassAuthToken);

        return gerarToken((Usuario) authentication.getPrincipal());
    }
}


