package com.eduardo.apisystem.controller;

import com.eduardo.apisystem.model.dto.receita.ReceitaDTO;
import com.eduardo.apisystem.model.enums.receita.TipoCategoria;
import com.eduardo.apisystem.model.enums.receita.TipoCusto;
import com.eduardo.apisystem.service.receita.ReceitaService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/system/receitas")
public class ReceitaController {
    private final ReceitaService receitaService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    @Operation(summary = "Cria uma receita")
    public ResponseEntity<ReceitaDTO> criarUsuario(@RequestBody ReceitaDTO receitaDTO, UriComponentsBuilder uriBuilder, @RequestHeader("Authorization") String token) {
        ReceitaDTO receitaSalva = receitaService.criar(receitaDTO, token);

        URI uri = uriBuilder.path("/api/system/receitas/{id}").buildAndExpand(receitaSalva.getReceitaId()).toUri();

        return ResponseEntity.created(uri).body(receitaSalva);
    }

    @GetMapping("{receitaId}")
    @Operation(summary = "Busca uma receita pelo id")
    public ResponseEntity<ReceitaDTO> handleBuscarPorId(@PathVariable @NotNull Long receitaId) {
        return ResponseEntity.ok().body(receitaService.buscarPorIdDto(receitaId));
    }

    @GetMapping("")
    @Operation(summary = "Busca todas as receitas")
    public ResponseEntity<List<ReceitaDTO>> handleBuscarTodos() {
        return ResponseEntity.ok().body(receitaService.buscarTodos());
    }

    @GetMapping("usuario")
    @Operation(summary = "Busca todas as receitas de um usuario")
    public ResponseEntity<List<ReceitaDTO>> handleBuscarPorUsuario(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok().body(receitaService.buscarTodosPorUsuario(token));
    }

    @PutMapping("{receitaId}")
    @Operation(summary = "Atualiza uma receita pelo id")
    public ResponseEntity<ReceitaDTO> atualizarReceita(@PathVariable @NotNull Long receitaId, @RequestBody ReceitaDTO receitaDTO, @RequestHeader("Authorization") String token) {
        ReceitaDTO receitaAtualizada = receitaService.atualizar(receitaId, receitaDTO, token);
        return ResponseEntity.ok(receitaAtualizada);
    }

    @DeleteMapping("{receitaId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deleta uma receita pelo id")
    public void deletarReceita(@PathVariable @NotNull Long receitaId, @RequestHeader("Authorization") String token) {
        receitaService.deletar(receitaId, token);
    }

    @GetMapping("filtro")
    public ResponseEntity<List<ReceitaDTO>> buscarComFiltro(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) TipoCategoria tipoCategoria,
            @RequestParam(required = false) TipoCusto tipoCusto
    ) {
        return ResponseEntity.ok().body(receitaService.buscarComFiltro(nome, tipoCategoria, tipoCusto));
    }
}
