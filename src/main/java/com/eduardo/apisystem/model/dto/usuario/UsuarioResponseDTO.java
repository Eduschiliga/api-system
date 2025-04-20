package com.eduardo.apisystem.model.dto.usuario;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioResponseDTO {
  private Long usuarioId;
  private String email;
  private String login;
  private String nomeCompleto;
}
