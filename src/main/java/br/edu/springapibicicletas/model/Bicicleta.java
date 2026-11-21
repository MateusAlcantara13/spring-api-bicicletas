package br.edu.springapibicicletas.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "bicicletas")
public class Bicicleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o modelo")
    @Size(min = 2, max = 80, message = "O modelo deve conter de 2 a 80 caracteres")
    @Column(nullable = false, length = 80)
    private String modelo;

    @NotNull(message = "Informe o tipo de bicicleta")
    @Enumerated(EnumType.STRING)
    private TipoBicicleta tipo;

    @NotNull(message = "Informe o valor da hora")
    @DecimalMin(value = "0.01", message = "O valor deve ser maior que zero")
    private BigDecimal valorBicicleta;

    @NotNull(message = "A quantidade em estoque é obrigatória")
    @PositiveOrZero(message = "A quantidade não pode ser negativa")
    private Integer estoque;

    public boolean temEstoque() {
        return estoque != null && estoque > 0;
    }

    public void alugar() {
        estoque--;
    }

    public void devolver() {
        estoque++;
    }

}
