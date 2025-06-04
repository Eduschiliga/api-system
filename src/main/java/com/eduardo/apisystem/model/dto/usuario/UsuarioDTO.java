package com.eduardo.apisystem.model.dto.usuario;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsuarioDTO {
  private String email;
  private String senha;
  private String nomeCompleto;
}
