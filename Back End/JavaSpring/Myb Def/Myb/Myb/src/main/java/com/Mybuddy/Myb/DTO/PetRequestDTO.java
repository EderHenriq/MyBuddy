package com.Mybuddy.Myb.DTO;

import com.Mybuddy.Myb.Model.StatusAdocao; // Importe se você usa StatusAdocao no DTO

public class PetRequestDTO {
    private String nome;
    private String especie;
    private String raca;
    private Integer idade;
    private String cor;
    private String porte;
    private String sexo;
    private String imageUrl; // Pode ser nulo
    private StatusAdocao statusAdocao; // Pode ser nulo, o serviço define padrão

    // Este campo receberá o ID da organização do frontend
    private Long organizacaoId;

    // Opcional: Adicione campos para microchipado, vacinado, castrado, cidade, estado
    // que você parece ter no frontend, mas não no Pet.java atual.
    private boolean microchipado;
    private boolean vacinado;
    private boolean castrado;
    private String cidade;
    private String estado;

    // --- Getters e Setters ---
    // Você pode gerar com o IDE (Alt+Insert no IntelliJ, Source -> Generate Getters and Setters no Eclipse)
    // ou usar Lombok (@Data)

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }

    public Integer getIdade() { return idade; }
    public void setIdade(Integer idade) { this.idade = idade; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public String getPorte() { return porte; }
    public void setPorte(String porte) { this.porte = porte; }

    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

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

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}