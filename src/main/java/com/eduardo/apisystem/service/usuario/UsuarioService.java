package com.eduardo.apisystem.service.usuario;

import com.eduardo.apisystem.entity.usuario.Perfil;
import com.eduardo.apisystem.entity.usuario.Usuario;
import com.eduardo.apisystem.exception.customizadas.usuario.SenhaException;
import com.eduardo.apisystem.exception.customizadas.usuario.UsuarioException;
import com.eduardo.apisystem.mapper.usuario.UsuarioMapper;
import com.eduardo.apisystem.model.dto.perfil.PerfilDTO;
import com.eduardo.apisystem.model.dto.usuario.SenhaDTO;
import com.eduardo.apisystem.model.dto.usuario.UsuarioDTO;
import com.eduardo.apisystem.model.dto.usuario.UsuarioResponseDTO;
import com.eduardo.apisystem.model.enums.usurio.TipoPerfil;
import com.eduardo.apisystem.repository.perfil.PerfilRepository;
import com.eduardo.apisystem.repository.usuario.UsuarioRepository;
import com.eduardo.apisystem.service.auth.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final PerfilRepository perfilRepository;

    public Usuario salvar(UsuarioDTO usuarioDTO) {
        Usuario usuario = usuarioMapper.usuarioDTOtoUsuario(usuarioDTO);
        usuario.setUsuarioId(null);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Perfil perfil = perfilRepository.findByNomePerfil(TipoPerfil.USUARIO);
        usuario.setPerfilList(new LinkedHashSet<>());
        usuario.getPerfilList().add(perfil);

        usuario = usuarioRepository.save(usuario);

        return usuario;
    }

    public List<UsuarioDTO> buscarTodos() {
        return usuarioMapper.usuarioListToUsuarioDTOList(usuarioRepository.findAll());
    }

    public void atualizarSenha(SenhaDTO senhaNova, String token) {
        Usuario usuario = authService.findUsuarioEntityByToken(token);

        if (senhaNova.getSenha() == null || senhaNova.getSenha().length() < 8) {
            throw new SenhaException("A nova senha não tem mais que 8 caracteres", HttpStatus.BAD_REQUEST);
        }

        usuario.setSenha(passwordEncoder.encode(senhaNova.getSenha()));
        usuarioRepository.save(usuario);
    }

    public UsuarioResponseDTO atualizar(UsuarioDTO usuarioDTO, String token) {
        Usuario usuarioSalvo = authService.findUsuarioEntityByToken(token);

        usuarioSalvo.setSenha(passwordEncoder.encode(usuarioDTO.getSenha()));
        usuarioSalvo.setEmail(usuarioDTO.getEmail());
        usuarioSalvo.setNomeCompleto(usuarioDTO.getNomeCompleto());

        return usuarioMapper.usuarioToUsuarioResponseDTO(usuarioRepository.save(usuarioSalvo));
    }

    public void deletar(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioException("Usuario com id:" + usuarioId + "Não encontrado", HttpStatus.NOT_FOUND));

        if (usuario != null) {
            usuarioRepository.delete(usuario);
        }
    }

    public UsuarioResponseDTO buscarUsuarioPorId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioException("Usuário com id:" + usuarioId + "Não encontrado", HttpStatus.NOT_FOUND));
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuario);
    }

    @Transactional
    public UsuarioResponseDTO adicionarPerfil(Long usuarioId, PerfilDTO perfilDTO) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioException("Usuário com id:" + usuarioId + "Não encontrado", HttpStatus.NOT_FOUND));

        Perfil perfil = perfilRepository.findByNomePerfil(perfilDTO.getPerfil().getTipoPerfil());

        usuario.getPerfilList().add(perfil);
        usuarioRepository.save(usuario);
        return usuarioMapper.usuarioToUsuarioResponseDTO(usuario);
    }
}
