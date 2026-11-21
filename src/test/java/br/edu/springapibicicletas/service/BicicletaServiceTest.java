package br.edu.springapibicicletas.service;


import br.edu.springapibicicletas.model.Bicicleta;
import br.edu.springapibicicletas.model.TipoBicicleta;
import br.edu.springapibicicletas.repository.BicicletaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BicicletaServiceTest {

    @Mock
    private BicicletaRepository repository;

    @InjectMocks
    private BicicletaService service;

    @Test
    void deveAlugarBicicletaQuandoTemEstoque() throws Exception {
        Bicicleta bicicleta = novaBicicleta(1L, 3);

        when(repository.findById(1L)).thenReturn(Optional.of(bicicleta));
        when(repository.save(any(Bicicleta.class))).thenReturn(bicicleta);

        Bicicleta resultado = service.alugar(1L);

        assertEquals(2, resultado.getEstoque());
    }

    @Test
    void naoDeveAlugarBicicletaSemEstoque() {
        Bicicleta bicicleta = novaBicicleta(1L, 0);

        when(repository.findById(1L)).thenReturn(Optional.of(bicicleta));

        assertThrows(Exception.class, () -> service.alugar(1L));

        verify(repository, never()).save(any(Bicicleta.class));
    }

    @Test
    void naoDeveAlugarBicicletaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> service.alugar(99L));

        verify(repository, never()).save(any(Bicicleta.class));

    }

    @Test
    void deveDevolverBicicletaAumentandoEstoque() throws Exception {
        Bicicleta bicicleta = novaBicicleta(1L, 2);

        when(repository.findById(1L)).thenReturn(Optional.of(bicicleta));
        when(repository.save(any(Bicicleta.class))).thenReturn(bicicleta);

        Bicicleta resultado = service.devolver(1L);

        assertEquals(3, resultado.getEstoque());
    }

    @Test
    void naoDeveDevolverBicicletaInexistente() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(Exception.class, () -> service.devolver(99L));

        verify(repository, never()).save(any(Bicicleta.class));
    }

    @Test
    void deveChamarRepositoryUmaUnicaVezAoAlugar() throws Exception {
        Bicicleta bicicleta = novaBicicleta(1L, 5);

        when(repository.findById(1L)).thenReturn(Optional.of(bicicleta));
        when(repository.save(any(Bicicleta.class))).thenReturn(bicicleta);

        service.alugar(1L);

        verify(repository,times(1)).findById(1L);
        verify(repository, times(1)).save(any(Bicicleta.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 10, 50})
    void deveDiminuirEstoqueEmUmAoAlugar(int estoqueInicial) throws Exception {
        Bicicleta bicicleta = novaBicicleta(1L, estoqueInicial);

        when(repository.findById(1L)).thenReturn(Optional.of(bicicleta));
        when(repository.save(any(Bicicleta.class))).thenReturn(bicicleta);

        Bicicleta resultado = service.alugar(1L);

        assertEquals(estoqueInicial - 1, resultado.getEstoque());

    }


    private Bicicleta novaBicicleta(Long id, int estoque) {
        Bicicleta bicicleta = new Bicicleta();
        bicicleta.setId(id);
        bicicleta.setModelo("Caloi");
        bicicleta.setTipo(TipoBicicleta.MOUNTAIN);
        bicicleta.setEstoque(estoque);
        bicicleta.setValorBicicleta(new BigDecimal("9.90"));
        return bicicleta;
    }

}
