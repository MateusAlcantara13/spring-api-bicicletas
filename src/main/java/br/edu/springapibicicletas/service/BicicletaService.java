package br.edu.springapibicicletas.service;

import br.edu.springapibicicletas.model.Bicicleta;
import br.edu.springapibicicletas.model.TipoBicicleta;
import br.edu.springapibicicletas.repository.BicicletaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BicicletaService {

    private final BicicletaRepository repository;

    public BicicletaService(BicicletaRepository repository) {
        this.repository = repository;
    }

    public List<Bicicleta> listarTodos() {
        return repository.findAll();
    }

    public Bicicleta buscarPorId(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Bicicleta salvar(Bicicleta bicicleta) {
        return repository.save(bicicleta);
    }

    public boolean existe(Long id) {
        return repository.existsById(id);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

    public List<Bicicleta> buscarPorModelo(String modelo) {
        return repository.findByModeloContainingIgnoreCase(modelo);
    }

    public List<Bicicleta> buscarPorTipo(TipoBicicleta tipo) {
        return repository.findByTipo(tipo);
    }

    // operações

    public Bicicleta alugar(Long id) throws Exception {
        Bicicleta bicicleta = repository.findById(id).orElseThrow(() -> new Exception("Bicicleta não encontrada"));

        if (!bicicleta.temEstoque()) {
            throw new Exception("Bicicleta sem estoque disponível");
        }

        bicicleta.alugar();
        return repository.save(bicicleta);
    }

    public Bicicleta devolver(Long id) throws Exception {
        Bicicleta bicicleta = repository.findById(id).orElseThrow(() -> new Exception("Bicicleta não encontrada"));

        bicicleta.devolver();
        return repository.save(bicicleta);
    }
}
