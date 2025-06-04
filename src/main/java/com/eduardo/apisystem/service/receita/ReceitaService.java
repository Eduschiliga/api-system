package com.eduardo.apisystem.service.receita;


import com.eduardo.apisystem.entity.receita.Receita;
import com.eduardo.apisystem.entity.usuario.Usuario;
import com.eduardo.apisystem.exception.customizadas.receita.ReceitaException;
import com.eduardo.apisystem.mapper.receita.ReceitaMapper;
import com.eduardo.apisystem.model.dto.receita.ReceitaDTO;
import com.eduardo.apisystem.model.enums.receita.TipoCategoria;
import com.eduardo.apisystem.model.enums.receita.TipoCusto;
import com.eduardo.apisystem.repository.receita.ReceitaRepository;
import com.eduardo.apisystem.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReceitaService {
    private final ReceitaRepository receitaRepository;
    private final ReceitaMapper receitaMapper;
    private final AuthService authService;

    public ReceitaDTO criar(ReceitaDTO dto, String token) {
        Usuario usuario = authService.findUsuarioEntityByToken(token);
        Receita receita = setarAtributos(dto, usuario);

        return receitaMapper.toDto(receitaRepository.save(receita));
    }

    public ReceitaDTO buscarPorIdDto(Long receitaId) {
        return receitaMapper.toDto(buscarPorIdEntity(receitaId));
    }

    private Receita buscarPorIdEntity(Long receitaId) {
        return receitaRepository.findById(receitaId)
                .orElseThrow(() -> new ReceitaException("Receita #" + receitaId + " não encnotrado"));
    }

    public List<ReceitaDTO> buscarTodos() {
        return receitaMapper.toListDto(receitaRepository.findAll());
    }

    private Receita setarAtributos(ReceitaDTO dto, Usuario usuario) {
        Receita receita = receitaMapper.toEntity(dto);
        receita.setReceitaId(null);
        receita.getIngredienteSet().forEach(ingrediente -> {
            ingrediente.setIngredienteId(null);
            ingrediente.setReceita(receita);
        });
        receita.setAutor(usuario.getEmail());
        calcularTipoCusto(receita);

        return receita;
    }

    private void calcularTipoCusto(Receita receita) {
        if (receita.getCusto() == null) {
            receita.setCusto(BigDecimal.ZERO);
        }

        double custo = receita.getCusto().doubleValue();

        if (custo >= 0 && custo <= 20) {
            receita.setTipoCusto(TipoCusto.ECONOMICO);
            return;
        }

        if (custo >= 20 && custo <= 40) {
            receita.setTipoCusto(TipoCusto.BARATO);
            return;
        }

        if (custo >= 40 && custo <= 60) {
            receita.setTipoCusto(TipoCusto.RAZOAVEL);
            return;
        }

        if (custo >= 60) {
            receita.setTipoCusto(TipoCusto.CARO);
        }
    }

    public ReceitaDTO atualizar(Long receitaId, ReceitaDTO receitaDTO, String token) {
        Usuario usuario = authService.findUsuarioEntityByToken(token);
        Receita receitaExistente = buscarPorIdEntity(receitaId);

        if (!receitaExistente.getAutor().equals(usuario.getEmail())) {
            throw new ReceitaException("Usuário não autorizado a atualizar esta receita");
        }

        Receita receitaAtualizada = receitaMapper.toEntity(receitaDTO);
        receitaAtualizada.setReceitaId(receitaExistente.getReceitaId());
        receitaAtualizada.setAutor(receitaExistente.getAutor());
        receitaAtualizada.setDataHoraCriacao(receitaExistente.getDataHoraCriacao());

        receitaAtualizada.getIngredienteSet().forEach(ingrediente -> ingrediente.setReceita(receitaAtualizada));

        return receitaMapper.toDto(receitaRepository.save(receitaAtualizada));
    }

    public void deletar(Long receitaId, String token) {
        Usuario usuario = authService.findUsuarioEntityByToken(token);
        Receita receita = buscarPorIdEntity(receitaId);

        if (!receita.getAutor().equals(usuario.getEmail())) {
            throw new ReceitaException("Usuário não autorizado a deletar esta receita");
        }

        receitaRepository.delete(receita);
    }

    public List<ReceitaDTO> buscarTodosPorUsuario(String token) {
        Usuario usuario = authService.findUsuarioEntityByToken(token);

        return receitaMapper.toListDto(receitaRepository.buscarReceitaPorUsuario(usuario.getEmail()));
    }

    public List<ReceitaDTO> buscarComFiltro(String nome, TipoCategoria tipoCategoria, TipoCusto tipoCusto) {
        String nomePatternParaBusca = null;
        if (nome != null && !nome.trim().isEmpty()) {
            nomePatternParaBusca = "%" + nome.trim() + "%";
        }

        return receitaMapper.toListDto(
                receitaRepository.buscarComFiltroNomeCategoriaCusto(nomePatternParaBusca, tipoCategoria, tipoCusto)
        );
    }
}
