package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import java.util.HashSet; // Preferível a List para OneToMany, se a ordem não importa
import java.util.Set;    // Preferível a List para OneToMany, se a ordem não importa
import java.util.Objects; // Para a implementação de equals e hashCode

@Entity
@Table(name = "organizacoes") // Nome da tabela no banco
public class Organizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeFantasia; // Nome fantasia da ONG

    @Column(nullable = false, unique = true)
    private String emailContato; // E-mail de contato principal da ONG

    @Column(nullable = false, unique = true) // CNPJ deve ser único
    private String cnpj;

    private String telefoneContato; // Telefone principal da ONG

    @Column(nullable = false)
    private String endereco; // Endereço completo da sede da ONG

    @Column(columnDefinition = "TEXT") // Permite textos longos para a descrição
    private String descricao; // Breve descrição da ONG

    private String website; // URL do site da ONG (pode ser nullable)

    // Relacionamento One-to-Many com Pets
    // Uma Organização pode ter muitos Pets.
    // 'mappedBy' aponta para o nome do campo na classe Pet que possui o relacionamento ManyToOne com Organizacao.
    // 'cascade = CascadeType.ALL' significa que operações em Organizacao (salvar, deletar) serão aplicadas aos Pets associados.
    // 'orphanRemoval = true' significa que se um Pet for removido da coleção 'pets' da Organizacao, ele será deletado do banco.
    @OneToMany(mappedBy = "organizacao", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Pet> pets = new HashSet<>(); // Inicializa para evitar NullPointerException

    // NOVO: Relacionamento One-to-Many com Usuários
    // Uma Organização pode ter muitos Usuários (funcionários ou admins da ONG).
    // Opcionalmente, pode ser LAZY para não carregar todos os usuários por padrão.
    // orphanRemoval = false, pois usuários podem existir sem uma ONG (e.g., adotantes, admins globais)
    // ou podem ser associados a outra ONG.
    @OneToMany(mappedBy = "organizacao", cascade = CascadeType.MERGE, orphanRemoval = false, fetch = FetchType.LAZY)
    private Set<Usuario> usuarios = new HashSet<>(); // Inicializa para evitar NullPointerException

    // --- Construtores ---
    // Construtor vazio (obrigatório para JPA)
    public Organizacao() {
    }

    // Construtor para facilitar a criação de objetos (sem ID, que é gerado)
    public Organizacao(String nomeFantasia, String emailContato, String cnpj, String telefoneContato, String endereco, String descricao, String website) {
        this.nomeFantasia = nomeFantasia;
        this.emailContato = emailContato;
        this.cnpj = cnpj;
        this.telefoneContato = telefoneContato;
        this.endereco = endereco;
        this.descricao = descricao;
        this.website = website;
    }

    // --- Getters e Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getEmailContato() {
        return emailContato;
    }

    public void setEmailContato(String emailContato) {
        this.emailContato = emailContato;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public Set<Pet> getPets() { // Mudado para Set
        return pets;
    }

    public void setPets(Set<Pet> pets) { // Mudado para Set
        this.pets = pets;
    }

    public Set<Usuario> getUsuarios() { // NOVO: Getter para Usuarios
        return usuarios;
    }

    public void setUsuarios(Set<Usuario> usuarios) { // NOVO: Setter para Usuarios
        this.usuarios = usuarios;
    }

    // --- Métodos Utilitários para gerenciar os relacionamentos bidirecionais ---
    // IMPORTANTE: Eles garantem que ambos os lados do relacionamento (OneToMany e ManyToOne)
    // sejam atualizados corretamente.

    public void addPet(Pet pet) {
        this.pets.add(pet);
        pet.setOrganizacao(this); // Garante que o pet aponte de volta para esta organização
    }

    public void removePet(Pet pet) {
        this.pets.remove(pet);
        pet.setOrganizacao(null); // Desvincula o pet desta organização
    }

    public void addUsuario(Usuario usuario) {
        this.usuarios.add(usuario);
        usuario.setOrganizacao(this); // Garante que o usuário aponte de volta para esta organização
    }

    public void removeUsuario(Usuario usuario) {
        this.usuarios.remove(usuario);
        usuario.setOrganizacao(null); // Desvincula o usuário desta organização
    }


    // --- Sobrescrita de equals() e hashCode() ---
    // Essencial para o bom funcionamento de coleções (Set) e para comparar entidades.
    // Geralmente, usamos o ID para unicidade, mas cuidado com entidades novas que ainda não têm ID.
    // Para entidades persistentes, o ID é o mais seguro.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Organizacao organizacao = (Organizacao) o;
        // Usa o ID para comparação. Se o ID for null (entidade nova), compara pelos campos de negócio.
        return id != null && Objects.equals(id, organizacao.id);
    }

    @Override
    public int hashCode() {
        // Usa o ID para hashCode. Se o ID for null, usa um hash constante ou campos de negócio.
        // Cuidado: hashCode de entidade nova pode mudar após a persistência se usar o ID.
        // Uma abordagem segura para JPA é usar Objects.hash(id).
        return id != null ? Objects.hash(id) : 0; // Se id for null, retorne 0 ou outro valor fixo.
    }


    // --- Sobrescrita de toString() ---
    // Útil para debugging, mostra uma representação significativa do objeto.
    @Override
    public String toString() {
        return "Organizacao{" +
                "id=" + id +
                ", nomeFantasia='" + nomeFantasia + '\'' +
                ", cnpj='" + cnpj + '\'' +
                ", emailContato='" + emailContato + '\'' +
                ", endereco='" + endereco + '\'' +
                '}';
    }
}