package com.Mybuddy.Myb.Model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Table(name = "Interesses_adoacao")
@Entity

public class InteresseAdoacao {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id

    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Enumerated (EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusInteresse status; //Ex.: PENDENTE, APROVADO, REJEITADO

    @Column(name = "mensagem", length = 500)
    private String mensagem;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "Atualizado_em")
    private LocalDateTime AtuaziladoEm;

    public InteresseAdoacao() {}


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public StatusInteresse getStatus() {
        return status;
    }

    public void setStatus(StatusInteresse status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtuaziladoEm() {
        return AtuaziladoEm;
    }

    public void setAtuazilado_em(LocalDateTime atuaziladoEm) {
        AtuaziladoEm = atuaziladoEm;
    }
}
