package com.eduardo.apisystem.repository.receita;

import com.eduardo.apisystem.entity.receita.Receita;
import com.eduardo.apisystem.model.enums.receita.TipoCategoria;
import com.eduardo.apisystem.model.enums.receita.TipoCusto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Repository
public interface ReceitaRepository extends JpaRepository<Receita, Long> {
    @Query("FROM Receita WHERE autor = ?1 ORDER BY receitaId DESC")
    List<Receita> buscarReceitaPorUsuario(String email);

    @Query("SELECT r FROM Receita r WHERE " +
            "(:nome IS NULL OR r.nome LIKE :nome) AND " +
            "(:tipoCategoria IS NULL OR r.categoria = :tipoCategoria) AND " +
            "(:tipoCusto IS NULL OR r.tipoCusto = :tipoCusto)")
    List<Receita> buscarComFiltroNomeCategoriaCusto(
            @Param("nome") String nome,
            @Param("tipoCategoria") TipoCategoria tipoCategoria,
            @Param("tipoCusto") TipoCusto tipoCusto
    );

    @Query("SELECT r FROM Receita r WHERE r.nome LIKE CONCAT('%', :nomeParam, '%')")
    List<Receita> apenasTesteNomeLike(@Param("nomeParam") String nomeParam);
}
