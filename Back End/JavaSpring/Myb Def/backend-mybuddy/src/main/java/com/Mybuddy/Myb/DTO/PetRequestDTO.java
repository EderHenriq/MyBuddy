package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.Especie;
import com.Mybuddy.Myb.Model.Porte;
import com.Mybuddy.Myb.Model.StatusAdocao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PetRequestDTO {

    @NotBlank(message = "O nome do pet é obrigatório.")
    @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres.")
    private String nome;

    @NotNull(message = "A espécie do pet é obrigatória.")
    private Especie especie;

    @NotBlank(message = "A raça do pet é obrigatória.")
    @Size(max = 60, message = "A raça deve ter no máximo 60 caracteres.")
    private String raca;

    @NotNull(message = "A idade do pet é obrigatória.")
    @Min(value = 0, message = "A idade não pode ser negativa.")
    private Integer idade; // Idade em anos

    @NotBlank(message = "A cor do pet é obrigatória.")
    @Size(max = 30, message = "A cor deve ter no máximo 30 caracteres.")
    private String cor;

    @NotNull(message = "O porte do pet é obrigatório.")
    private Porte porte;

    @NotBlank(message = "O sexo do pet é obrigatório.")
    @Size(max = 10, message = "O sexo deve ter no máximo 10 caracteres.")
    private String sexo; // M, F

    @Size(max = 3, message = "São permitidas no máximo 3 fotos por pet.")
    private List<String> fotosUrls; // Lista de URLs das fotos

    @NotNull(message = "O status de adoção é obrigatório.")
    private StatusAdocao statusAdocao;

    @NotNull(message = "O ID da organização é obrigatório.")
    private Long organizacaoId;

    @NotNull(message = "A informação de microchip é obrigatória.")
    private boolean microchipado;

    @NotNull(message = "A informação de vacinação é obrigatória.")
    private boolean vacinado;

    @NotNull(message = "A informação de castração é obrigatória.")
    private boolean castrado;

    @Size(max = 60, message = "A pelagem deve ter no máximo 60 caracteres.")
    private String pelagem;

    // Campo 'temperamento' removido

    @Size(max = 100, message = "A cidade deve ter no máximo 100 caracteres.")
    private String cidade;

    @Size(max = 100, message = "O estado deve ter no máximo 100 caracteres.")
    private String estado;

    // --- Getters e Setters ---
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Especie getEspecie() { return especie; }
    public void setEspecie(Especie especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public Porte getPorte() { return porte; }
    public void setPorte(Porte porte) { this.porte = porte; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public List<String> getFotosUrls() { return fotosUrls; }
    public void setFotosUrls(List<String> fotosUrls) { this.fotosUrls = fotosUrls; }

    public StatusAdocao getStatusAdocao() { return statusAdocao; }
    public void setStatusAdocao(StatusAdocao statusAdocao) { this.statusAdocao = statusAdocao; }

    public Long getOrganizacaoId() { return organizacaoId; }
    public void setOrganizacaoId(Long organizacaoId) { this.organizacaoId = organizacaoId; }

    public boolean isMicrochipado() { return microchipado; }
    public void setMicrochipado(boolean microchipado) { this.microchipado = microchipado; }

    public boolean isVacinado() { return vacinado; }
    public void setVacinado(boolean vacinado) { this.vacinado = vacinado; }

    public boolean isCastrado() { return castrado; }
    public void setCastrado(boolean castrado) { this.castrado = castrado; }

    public String getPelagem() { return pelagem; }
    public void setPelagem(String pelagem) { this.pelagem = pelagem; }

    // Getter e Setter para temperamento removidos

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}