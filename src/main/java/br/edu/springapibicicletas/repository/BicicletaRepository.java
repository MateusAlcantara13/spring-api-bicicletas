package br.edu.springapibicicletas.repository;

import br.edu.springapibicicletas.model.Bicicleta;
import br.edu.springapibicicletas.model.TipoBicicleta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BicicletaRepository extends JpaRepository<Bicicleta, Long> {

    List<Bicicleta> findByModeloContainingIgnoreCase(String modelo);

    List<Bicicleta> findByTipo(TipoBicicleta tipo);

    String modelo(String modelo);
}
