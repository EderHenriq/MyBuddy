package com.Mybuddy.Myb.Model; // Declara o pacote onde este modelo (entidade) está localizado.

import org.hibernate.annotations.CreationTimestamp; //  anotação do Hibernate para preencher automaticamente a data de criação.
import org.hibernate.annotations.UpdateTimestamp; // Importa anotação do Hibernate para preencher automaticamente a data de última atualização.

import java.time.LocalDateTime; // Importa a classe LocalDateTime para lidar com datas e horas sem fuso horário.

@Table(name = "Interesses_adoacao") // Anotação JPA que especifica o nome da tabela no banco de dados para esta entidade.
@Entity // Anotação JPA que marca esta classe como uma entidade, ou seja, uma classe que será mapeada para uma tabela no banco de dados.
public class InteresseAdoacao { // Declara a classe que representa um interesse de adoção.

    @GeneratedValue(strategy = GenerationType.IDENTITY) // Anotação JPA que configura a estratégia de geração de valor para a chave primária. IDENTITY usa a auto-incremento do banco de dados.
    @Id // Anotação JPA que marca este campo como a chave primária da entidade.
    private Long id; // Campo para armazenar o identificador único do interesse de adoção.

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Anotação JPA que define um relacionamento Muitos-para-Um.
    // fetch = FetchType.LAZY: O objeto relacionado (Usuário) será carregado do banco de dados apenas quando for acessado.
    // optional = false: Indica que um InteresseAdoacao sempre deve estar associado a um Usuário.
    @JoinColumn(name = "usuario_id", nullable = false) // Anotação JPA que especifica a coluna da chave estrangeira na tabela "Interesses_adoacao" que referencia a tabela "Usuario".
    // nullable = false: Indica que esta coluna não pode ser nula.
    private Usuario usuario; // Campo que representa o usuário que manifestou o interesse.

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Anotação JPA que define outro relacionamento Muitos-para-Um (com Pet).
    // fetch = FetchType.LAZY: O objeto relacionado (Pet) será carregado apenas quando acessado.
    // optional = false: Indica que um InteresseAdoacao sempre deve estar associado a um Pet.
    @JoinColumn(name = "pet_id", nullable = false) // Anotação JPA que especifica a coluna da chave estrangeira que referencia a tabela "Pet".
    // nullable = false: Indica que esta coluna não pode ser nula.
    private Pet pet; // Campo que representa o pet para o qual o interesse foi manifestado.

    @Enumerated (EnumType.STRING) // Anotação JPA que especifica como um tipo enumerado (enum) deve ser persistido no banco de dados.
    // EnumType.STRING: Armazena o nome da enumeração como uma String no banco (ex: "PENDENTE").
    @Column(name = "status", nullable = false, length = 20) // Anotação JPA que mapeia o campo para uma coluna no banco de dados.
    // nullable = false: A coluna não pode ser nula.
    // length = 20: Define o tamanho máximo da string na coluna do banco de dados.
    private StatusInteresse status; // Campo para armazenar o status atual do interesse (ex.: PENDENTE, APROVADO, REJEITADO).

    @Column(name = "mensagem", length = 500) // Anotação JPA que mapeia o campo 'mensagem' para uma coluna no banco de dados.
    // length = 500: Define o tamanho máximo da string.
    private String mensagem; // Campo para armazenar uma mensagem opcional do usuário.

    @CreationTimestamp // Anotação do Hibernate que faz com que este campo seja preenchido automaticamente com a data e hora de criação da entidade.
    @Column(name = "criado_em", updatable = false) // Anotação JPA. updatable = false: Impede que este campo seja alterado após a inserção inicial.
    private LocalDateTime criadoEm; // Campo para armazenar a data e hora de criação do interesse.

    @UpdateTimestamp // Anotação do Hibernate que faz com que este campo seja atualizado automaticamente com a data e hora da última modificação da entidade.
    @Column(name = "Atualizado_em") // Anotação JPA para a coluna no banco.
    private LocalDateTime AtualizadoEm; // Campo para armazenar a data e hora da última atualização do interesse.

    public InteresseAdoacao() {} 


    public Long getId() { 
        return id;
    }

    public void setId(Long id) { 
        this.id = id;
    }

    public Usuario getUsuario() { // Método getter para o usuário.
        return usuario;
    }

    public void setUsuario(Usuario usuario) { // Método setter para o usuário.
        this.usuario = usuario;
    }

    public Pet getPet() { // Método getter para o pet.
        return pet;
    }

    public void setPet(Pet pet) { // Método setter para o pet.
        this.pet = pet;
    }

    public StatusInteresse getStatus() { // Método getter para o status.
        return status;
    }

    public void setStatus(StatusInteresse status) { // Método setter para o status.
        this.status = status;
    }

    public String getMensagem() { // Método getter para a mensagem.
        return mensagem;
    }

    public void setMensagem(String mensagem) { // Método setter para a mensagem.
        this.mensagem = mensagem;
    }

    public LocalDateTime getCriadoEm() { // Método getter para a data de criação.
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) { // Método setter para a data de criação.
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() { 
        return AtualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
    this.AtualizadoEm = atualizadoEm;
    }

}