package com.Mybuddy.Myb.Repository; // Declara o pacote onde esta interface de repositório está localizada.

import com.Mybuddy.Myb.Model.Usuario; // Importa a entidade Usuario, que é o tipo de objeto que este repositório gerenciará.
import org.springframework.data.jpa.repository.JpaRepository; // Importa a interface JpaRepository do Spring Data JPA.
import org.springframework.stereotype.Repository; // Importa a anotação @Repository do Spring.

import java.util.Optional; // Importa a classe Optional, usada para indicar que um resultado pode ou não estar presente.

// Anotação do Spring que indica que esta interface é um componente de repositório.
// Embora não seja estritamente necessária para interfaces que estendem JpaRepository (pois o Spring já as detecta),
// é uma boa prática para clareza e para permitir a detecção de exceções específicas de persistência.
@Repository
public interface UsuarioRepository extends JpaRepository <Usuario, Long> {
    // Declara a interface UsuarioRepository.

    // Ao estender JpaRepository<Usuario, Long>, esta interface herda automaticamente uma série de métodos CRUD
    // (Create, Read, Update, Delete) para a entidade Usuario, onde Long é o tipo da chave primária (ID) da entidade Usuario.
    // O Spring Data JPA implementará esses métodos em tempo de execução.

    // Declara um método de consulta personalizado.
    // O Spring Data JPA irá automaticamente gerar a implementação deste método
    // para buscar um usuário no banco de dados com base no seu endereço de e-mail.
    // O método retorna um Optional<Usuario> para indicar que o usuário pode ser encontrado ou não.
    // O nome 'findByEmail' segue a convenção de nomeação de métodos do Spring Data JPA para criar consultas.
    Optional<Usuario> findByEmail(String email);
}