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

    @Column(length = 40, nullable = false)
    private String especie; // Ex: "Cat", "Dog"

    @Column(length = 20, nullable = false)
    private String porte; // Ex: "Small", "Medium", "Large" (posteriormente traduzido no frontend/DTO)

    @Column(length = 30, nullable = false)
    private String cor;

    @Column(length = 60, nullable = true) // Campo adicionado para pelagem
    private String pelagem; // Ex: "Long Hair", "Short Hair", "White", "Black"

    @Column(length = 10, nullable = false)
    private String sexo; // Ex: "Male", "Female" (posteriormente traduzido no frontend/DTO)

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

    @Column(length = 255, nullable = true) // Campo adicionado para 'tutoring'/'temperamento'
    private String temperamento; // Renomeado de 'tutoring' para algo mais descritivo como temperamento

    @Column(length = 100, nullable = true)
    private String cidade;

    @Column(length = 100, nullable = true)
    private String estado;


    // --- Construtores ---
    public Pet() {
        this.statusAdocao = StatusAdocao.EM_ADOCAO; // Definindo o padrão conforme seu enum
    }

    public Pet(String nome, String raca, Integer idade, String especie, String porte, String cor, String pelagem, String sexo,
               StatusAdocao statusAdocao, Organizacao organizacao, boolean microchipado, boolean vacinado,
               boolean castrado, String temperamento, String cidade, String estado) {
        this.nome = nome;
        this.raca = raca;
        this.idade = idade;
        this.especie = especie;
        this.porte = porte;
        this.cor = cor;
        this.pelagem = pelagem;
        this.sexo = sexo;
        this.statusAdocao = (statusAdocao != null) ? statusAdocao : StatusAdocao.EM_ADOCAO; // Padrão conforme seu enum
        this.organizacao = organizacao;
        this.microchipado = microchipado;
        this.vacinado = vacinado;
        this.castrado = castrado;
        this.temperamento = temperamento;
        this.cidade = cidade;
        this.estado = estado;
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

    public String getPelagem() { return pelagem; }
    public void setPelagem(String pelagem) { this.pelagem = pelagem; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public Set<FotoPet> getFotos() { return fotos; }
    public void setFotos(Set<FotoPet> fotos) { this.fotos = fotos; }

    public void addFoto(FotoPet foto) {
        this.fotos.add(foto);
        foto.setPet(this);
    }
    public void removeFoto(FotoPet foto) {
        this.fotos.remove(foto);
        foto.setPet(null);
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

    public String getTemperamento() { return temperamento; }
    public void setTemperamento(String temperamento) { this.temperamento = temperamento; }

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
                ", especie='" + especie + '\'' +
                ", statusAdocao=" + statusAdocao +
                ", organizacaoId=" + (organizacao != null ? organizacao.getId() : "N/A") +
                ", microchipado=" + microchipado +
                ", vacinado=" + vacinado +
                ", castrado=" + castrado +
                ", temperamento='" + temperamento + '\'' +
                ", cidade='" + cidade + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}