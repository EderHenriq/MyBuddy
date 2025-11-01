package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "pets")
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 80, nullable = false)
    private String nome;

    @Column(length = 60, nullable = false)
    private String raca;

    @Column(nullable = false)
    private Integer idade;

    @Column(length = 40, nullable = false)
    private String especie;

    @Column(length = 20, nullable = false)
    private String porte;

    @Column(length = 30, nullable = false)
    private String cor;

    @Column(length = 10, nullable = false)
    private String sexo;

    @Column(length = 255)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private StatusAdocao statusAdocao;

    // Relacionamento com a organização que cadastrou o pet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organizacao organizacao;

    // NOVO: Relacionamento com o usuário que adotou o pet
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adotado_por_id")
    private Usuario adotadoPor;

    // --- Construtores ---
    public Pet() {
        this.statusAdocao = StatusAdocao.EM_ADOCAO;
    }

    public Pet(String nome, String raca, Integer idade, String especie, String porte, String cor,
               String sexo, String imageUrl, StatusAdocao statusAdocao, Organizacao organizacao) {
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
        this.especie = especie;
        this.porte = porte;
        this.cor = cor;
        this.sexo = sexo;
        this.imageUrl = imageUrl;
        this.statusAdocao = (statusAdocao != null) ? statusAdocao : StatusAdocao.EM_ADOCAO;
        this.organizacao = organizacao;
    }

    // --- Getters e Setters ---
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

    public StatusAdocao getStatusAdocao() { return statusAdocao; }
    public void setStatusAdocao(StatusAdocao statusAdocao) { this.statusAdocao = statusAdocao; }

    public Organizacao getOrganizacao() { return organizacao; }
    public void setOrganizacao(Organizacao organizacao) { this.organizacao = organizacao; }

    public Usuario getAdotadoPor() { return adotadoPor; }
    public void setAdotadoPor(Usuario adotadoPor) { this.adotadoPor = adotadoPor; }

    // --- equals e hashCode ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet pet = (Pet) o;
        return id != null && Objects.equals(id, pet.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hash(id) : 0;
    }

    // --- toString ---
    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especie='" + especie + '\'' +
                ", statusAdocao=" + statusAdocao +
                ", organizacaoId=" + (organizacao != null ? organizacao.getId() : "N/A") +
                ", adotadoPorId=" + (adotadoPor != null ? adotadoPor.getId() : "N/A") +
                '}';
    }
}
