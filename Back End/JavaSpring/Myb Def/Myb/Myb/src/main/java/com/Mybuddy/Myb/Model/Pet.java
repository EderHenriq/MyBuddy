package com.Mybuddy.Myb.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private Integer idade; // Idade em anos, como visto no card de detalhes

    @Enumerated(EnumType.STRING)
    @Column(length = 40, nullable = false)
    private Especie especie;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Porte porte;

    @Column(length = 30, nullable = false)
    private String cor;

    @Column(length = 60, nullable = true)
    private String pelagem; // Ex: "CURTA", "MEDIA", "LONGA" (usar enums no futuro pode ser melhor)

    @Column(length = 10, nullable = false)
    private String sexo; // Ex: "MACHO", "FEMEA" (usar enums no futuro pode ser melhor)

    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference // Lado "pai" do relacionamento com FotoPet
    private Set<FotoPet> fotos = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false) // Mantenha o length do Enum StatusAdocao
    private StatusAdocao statusAdocao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizacao_id", nullable = false)
    @JsonBackReference // Lado "filho" do relacionamento com Organizacao
    private Organizacao organizacao;

    // --- CAMPOS ADICIONAIS ---
    @Column(nullable = false)
    private boolean microchipado; // true/false

    @Column(nullable = false)
    private boolean vacinado;     // true/false

    @Column(nullable = false)
    private boolean castrado;     // true/false

    // Campo 'temperamento' removido conforme sua solicitação

    @Column(length = 100, nullable = true)
    private String cidade;

    @Column(length = 100, nullable = true)
    private String estado;


    // --- Construtores ---
    public Pet() {
        this.statusAdocao = StatusAdocao.DISPONIVEL;
    }

    public Pet(String nome, String raca, Integer idade, Especie especie, Porte porte, String cor, String pelagem, String sexo, Organizacao organizacao, boolean microchipado, boolean vacinado, boolean castrado, String cidade, String estado) {
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
        this.especie = especie;
        this.porte = porte;
        this.cor = cor;
        this.pelagem = pelagem;
        this.sexo = sexo;
        this.organizacao = organizacao;
        this.microchipado = microchipado;
        this.vacinado = vacinado;
        this.castrado = castrado;
        this.cidade = cidade;
        this.estado = estado;
        this.statusAdocao = StatusAdocao.DISPONIVEL;
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

    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }

    public Porte getPorte() { return porte; }
    public void setPorte(Porte porte) { this.porte = porte; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getPelagem() { return pelagem; }
    public void setPelagem(String pelagem) { this.pelagem = pelagem; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public Set<FotoPet> getFotos() { return fotos; }
    public void setFotos(Set<FotoPet> fotos) { this.fotos = fotos; }

    public void addFoto(FotoPet foto) {
        if (foto != null && !this.fotos.contains(foto)) {
            this.fotos.add(foto);
            foto.setPet(this);
        }
    }
    public void removeFoto(FotoPet foto) {
        if (foto != null && this.fotos.contains(foto)) {
            this.fotos.remove(foto);
            foto.setPet(null);
        }
    }
    public void clearFotos() {
        this.fotos.forEach(foto -> foto.setPet(null)); // Desvincula o pet das fotos
        this.fotos.clear(); // Limpa o set
    }


    public StatusAdocao getStatusAdocao() { return statusAdocao; }
    public void setStatusAdocao(StatusAdocao statusAdocao) { this.statusAdocao = statusAdocao; }

    public Organizacao getOrganizacao() { return organizacao; }
    public void setOrganizacao(Organizacao organizacao) { this.organizacao = organizacao; }

    public boolean isMicrochipado() { return microchipado; }
    public void setMicrochipado(boolean microchipado) { this.microchipado = microchipado; }

    public boolean isVacinado() { return vacinado; }
    public void setVacinado(boolean vacinado) { this.vacinado = vacinado; }

    public boolean isCastrado() { return castrado; }
    public void setCastrado(boolean castrado) { this.castrado = castrado; }

    // Getter e Setter para temperamento removidos

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    // --- Sobrescrita de equals() e hashCode() ---
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

    // --- Sobrescrita de toString() ---
    @Override
    public String toString() {
        return "Pet{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", especie=" + especie +
                ", statusAdocao=" + statusAdocao +
                ", organizacaoId=" + (organizacao != null ? organizacao.getId() : "N/A") +
                ", microchipado=" + microchipado +
                ", vacinado=" + vacinado +
                ", castrado=" + castrado +
                // temperamento removido
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
    