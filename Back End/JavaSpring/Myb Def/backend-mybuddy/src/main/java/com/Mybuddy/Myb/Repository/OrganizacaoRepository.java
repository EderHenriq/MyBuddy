package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.Organizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {

    // --- Métodos para validação e busca por CNPJ ---
    // Usado no OrganizacaoService para verificar unicidade ao criar/atualizar
    boolean existsByCnpj(String cnpj);

    // Usado no AuthController e no OrganizacaoService para buscar uma ONG pelo CNPJ
    Optional<Organizacao> findByCnpj(String cnpj);

    // --- Métodos para validação e busca por E-mail de Contato ---
    // Usado no OrganizacaoService para verificar unicidade ao criar/atualizar
    boolean existsByEmailContato(String emailContato);

    // Usado no OrganizacaoService para buscar uma ONG pelo E-mail de Contato
    Optional<Organizacao> findByEmailContato(String emailContato);

    // --- Outros métodos úteis que você pode querer adicionar (se necessário) ---
    // Exemplo: buscar por nome fantasia
    // Optional<Organizacao> findByNomeFantasia(String nomeFantasia);
}