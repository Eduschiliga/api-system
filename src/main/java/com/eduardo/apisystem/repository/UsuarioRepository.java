package com.eduardo.apisystem.repository;

import com.eduardo.apisystem.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
  Usuario findByEmail(String email);

  Usuario findByLogin(String login);
}
