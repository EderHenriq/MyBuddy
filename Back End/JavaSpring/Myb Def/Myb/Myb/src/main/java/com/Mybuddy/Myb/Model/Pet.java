package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String nome;

    @Column(length = 60)
    private String raca;

    private Integer idade;

    @Column(length = 40)
    private String especie;

    @Column(length = 20)
    private String porte;

    @Column(length = 30)
    private String cor;

    @Column(length = 10)
    private String sexo;

    @Column(length = 255) // Campo para armazenar a URL da imagem
    private String imageUrl;

    // --- NOVAS MODIFICAÇÕES PARA O STATUS DE ADOÇÃO ---
    @Enumerated(EnumType.STRING) // Armazena o nome da enum (EM_ADOCAO, ADOTADO)
    @Column(length = 20, nullable = false)
    private StatusAdocao statusAdocao; // NOVO CAMPO PARA O STATUS
    // --- FIM NOVAS MODIFICAÇÕES ---

    public Pet() {
        this.statusAdocao = StatusAdocao.EM_ADOCAO; // Define um status padrão ao criar
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getPorte() { return porte; }
    public void setPorte(String porte) { this.porte = porte; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // --- NOVOS: Getter e Setter para statusAdocao ---
    public StatusAdocao getStatusAdocao() { return statusAdocao; }
    public void setStatusAdocao(StatusAdocao statusAdocao) { this.statusAdocao = statusAdocao; }
    // --- FIM NOVOS GETTER E SETTER ---
}