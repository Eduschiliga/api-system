package com.eduardo.apisystem.controller;

import com.eduardo.apisystem.entity.usuario.Usuario;
import com.eduardo.apisystem.model.dto.perfil.PerfilDTO;
import com.eduardo.apisystem.model.dto.usuario.SenhaDTO;
import com.eduardo.apisystem.model.dto.usuario.UsuarioDTO;
import com.eduardo.apisystem.model.dto.usuario.UsuarioResponseDTO;
import com.eduardo.apisystem.service.auth.AuthService;
import com.eduardo.apisystem.service.usuario.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping("/api/system/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final AuthService authService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Cria um usuário")
    public ResponseEntity<Void> criarUsuario(@RequestBody @Valid UsuarioDTO usuarioDTO, UriComponentsBuilder uriBuilder) {
        Usuario usuario = usuarioService.salvar(usuarioDTO);

        URI uri = uriBuilder.path("/api/system/usuarios/{id}").buildAndExpand(usuario.getUsuarioId()).toUri();

        return ResponseEntity.created(uri).build();
    }

    @GetMapping
    @Operation(summary = "Busca todos os usuários")
    public ResponseEntity<List<UsuarioDTO>> buscarUsuario() {
        return ResponseEntity.ok().body(usuarioService.buscarTodos());
    }

    @GetMapping("/{usuarioId}")
    @Operation(summary = "Busca um usuário pelo ID")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable @NotNull Long usuarioId) {
        return ResponseEntity.ok().body(usuarioService.buscarUsuarioPorId(usuarioId));
    }

    @DeleteMapping("/{usuarioId}")
    @Operation(summary = "Deleta um usuário pelo ID")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable @NotNull Long usuarioId) {
        usuarioService.deletar(usuarioId);
    }

    @PutMapping
    @Operation(summary = "Atualiza um usuário")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@RequestBody @Valid UsuarioDTO usuarioDTO, @RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(usuarioService.atualizar(usuarioDTO, token));
    }

    @PutMapping("senha")
    @Operation(summary = "Atualiza a senha de um usuário")
    public ResponseEntity<Void> handleAtualizarSenha(
            @RequestHeader("Authorization") String token,
            @RequestBody SenhaDTO senhaDTO
            ) {
        usuarioService.atualizarSenha(senhaDTO, token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("token")
    @Operation(summary = "Busca um usuário pelo token")
    public ResponseEntity<UsuarioResponseDTO> loadUserByToken(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(authService.findUsuarioResponseByToken(token));
    }

    @PatchMapping("{usuarioId}/perfil")
    public ResponseEntity<UsuarioResponseDTO> handleAdicionarPerfil(@PathVariable Long usuarioId, @RequestBody PerfilDTO perfilDTO) {
        return ResponseEntity.ok().body(usuarioService.adicionarPerfil(usuarioId, perfilDTO));
    }
}
