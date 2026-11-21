package br.edu.springapibicicletas.controller;


import br.edu.springapibicicletas.model.Bicicleta;
import br.edu.springapibicicletas.model.TipoBicicleta;
import br.edu.springapibicicletas.service.BicicletaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/bicicletas")
public class BicicletaController {

    private final BicicletaService service;

    public BicicletaController(BicicletaService service) {
        this.service = service;
    }

    // POST /api/bicicletas
    @PostMapping
    public ResponseEntity<Bicicleta> criar(@Valid @RequestBody Bicicleta bicicleta) {
        Bicicleta salva = service.salvar(bicicleta);
        URI uri = URI.create("/api/bicicletas/" + salva.getId());
        return ResponseEntity.created(uri).body(salva);
    }

    // GET /api/bicicletas
    @GetMapping
    public List<Bicicleta> listar() {
        return service.listarTodos();
    }

    // GET /api/bicicletas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Bicicleta> buscarPorId(@PathVariable Long id) {
        Bicicleta bicicleta = service.buscarPorId(id);

        if (bicicleta == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(bicicleta);
    }

    // PUT /api/bicicletas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Bicicleta> atualizar(@PathVariable Long id, @Valid @RequestBody Bicicleta bicicleta) {
        Bicicleta existente = service.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        existente.setModelo(bicicleta.getModelo());
        existente.setTipo(bicicleta.getTipo());
        existente.setValorBicicleta(bicicleta.getValorBicicleta());
        existente.setEstoque(bicicleta.getEstoque());

        return ResponseEntity.ok(service.salvar(existente));
    }

    // DELETE /api/bicicletas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        if (!service.existe(id)) {
            return ResponseEntity.notFound().build();
        }

        service.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/bicicletas/busca?modelo=caloi
    @GetMapping("/busca")
    public List<Bicicleta> buscaPorModelo(@RequestParam String modelo) {
        return service.buscarPorModelo(modelo);
    }

    // GET /api/bicicletas/tipo/MOUNTAIN
    @GetMapping("/tipo/{tipo}")
    public List<Bicicleta> buscarPorTipo(@PathVariable TipoBicicleta tipo) {
        return service.buscarPorTipo(tipo);
    }
    
    // ---- Alugar e devolver ----
    @PostMapping("/{id}/alugar")
    public ResponseEntity<?> alugar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.alugar(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.devolver(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
