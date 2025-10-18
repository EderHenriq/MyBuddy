package com.Mybuddy.Myb.Model; // Declara o pacote onde este modelo (entidade) está localizado.

import jakarta.persistence.*; // Importa todas as anotações JPA (Java Persistence API) para mapeamento de objetos para o banco de dados.

@Entity // Anotação JPA que marca esta classe como uma entidade, ou seja, uma classe que será mapeada para uma tabela no banco de dados.
@Table(name = "pets") // Anotação JPA que especifica o nome da tabela no banco de dados para esta entidade.
public class Pet { // Declara a classe que representa um animal de estimação (Pet).

    @Id // Anotação JPA que marca este campo como a chave primária da entidade.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Anotação JPA que configura a estratégia de geração de valor para a chave primária. IDENTITY usa a auto-incremento do banco de dados.
    private Long id; // Campo para armazenar o identificador único do pet.

    @Column(length = 80, nullable = false) // Anotação JPA que mapeia o campo 'nome' para uma coluna no banco de dados.
    // length = 80: Define o tamanho máximo da string na coluna.
    // nullable = false: Indica que esta coluna não pode ser nula.
    private String nome; // Campo para armazenar o nome do pet.

    @Column(length = 60) // Anotação JPA que mapeia o campo 'raca' para uma coluna no banco de dados com tamanho máximo de 60 caracteres.
    private String raca; // Campo para armazenar a raça do pet.

    private Integer idade; // Campo para armazenar a idade do pet. (O JPA inferirá o tipo de coluna padrão para Integer).

    @Column(length = 40) // Anotação JPA que mapeia o campo 'especie' para uma coluna no banco de dados com tamanho máximo de 40 caracteres.
    private String especie; // Campo para armazenar a espécie do pet (ex: Cachorro, Gato).

    @Column(length = 20) // Anotação JPA que mapeia o campo 'porte' para uma coluna no banco de dados com tamanho máximo de 20 caracteres.
    private String porte; // Campo para armazenar o porte do pet (ex: Pequeno, Médio, Grande).

    @Column(length = 30) // Anotação JPA que mapeia o campo 'cor' para uma coluna no banco de dados com tamanho máximo de 30 caracteres.
    private String cor; // Campo para armazenar a cor do pet.

    @Column(length = 10) // Anotação JPA que mapeia o campo 'sexo' para uma coluna no banco de dados com tamanho máximo de 10 caracteres.
    private String sexo; // Campo para armazenar o sexo do pet (ex: Macho, Fêmea).

    @Column(length = 255) // Anotação JPA que mapeia o campo 'imageUrl' para uma coluna no banco de dados com tamanho máximo de 255 caracteres.
    private String imageUrl; // Campo para armazenar a URL da imagem do pet (onde a imagem está hospedada).


    @Enumerated(EnumType.STRING) // Anotação JPA que especifica como um tipo enumerado (enum) deve ser persistido no banco de dados.
    // EnumType.STRING: Armazena o nome da enumeração como uma String no banco (ex: "EM_ADOCAO", "ADOTADO").
    @Column(length = 20, nullable = false) // Mapeia o campo para uma coluna com tamanho máximo de 20 e que não pode ser nula.
    private StatusAdocao statusAdocao; // NOVO CAMPO: Campo para armazenar o status de adoção do pet (ex: EM_ADOCAO, ADOTADO).


    public Pet() { // Construtor padrão (vazio), necessário para o JPA.
        this.statusAdocao = StatusAdocao.EM_ADOCAO; // Ao criar uma nova instância de Pet, define um status padrão inicial como EM_ADOCAO.
    }

    // Métodos Getters e Setters para cada atributo da classe, permitindo acesso e modificação dos valores dos campos.

    public Long getId() { return id; } // Método getter para o ID do pet.
    public void setId(Long id) { this.id = id; } // Método setter para o ID do pet.

    public String getNome() { return nome; } // Método getter para o nome do pet.
    public void setNome(String nome) { this.nome = nome; } // Método setter para o nome do pet.

    public String getRaca() { return raca; } // Método getter para a raça do pet.
    public void setRaca(String raca) { this.raca = raca; } // Método setter para a raça do pet.

    public Integer getIdade() { return idade; } // Método getter para a idade do pet.
    public void setIdade(Integer idade) { this.idade = idade; } // Método setter para a idade do pet.

    public String getEspecie() { return especie; } // Método getter para a espécie do pet.
    public void setEspecie(String especie) { this.especie = especie; } // Método setter para a espécie do pet.

    public String getPorte() { return porte; } // Método getter para o porte do pet.
    public void setPorte(String porte) { this.porte = porte; } // Método setter para o porte do pet.

    public String getCor() { return cor; } // Método getter para a cor do pet.
    public void setCor(String cor) { this.cor = cor; } // Método setter para a cor do pet.

    public String getSexo() { return sexo; } // Método getter para o sexo do pet.
    public void setSexo(String sexo) { this.sexo = sexo; } // Método setter para o sexo do pet.

    public String getImageUrl() { return imageUrl; } // Método getter para a URL da imagem do pet.
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; } // Método setter para a URL da imagem do pet.


    public StatusAdocao getStatusAdocao() { return statusAdocao; } // Método getter para o status de adoção do pet.
    public void setStatusAdocao(StatusAdocao statusAdocao) { this.statusAdocao = statusAdocao; } // Método setter para o status de adoção do pet.

}