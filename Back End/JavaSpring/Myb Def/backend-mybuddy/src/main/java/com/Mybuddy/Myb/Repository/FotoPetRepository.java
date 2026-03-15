package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.FotoPet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FotoPetRepository extends JpaRepository<FotoPet, Long> {
    // Você pode adicionar métodos personalizados aqui se precisar,
    // por exemplo, para buscar a foto principal de um pet:
    // Optional<FotoPet> findByPetIdAndPrincipalTrue(Long petId);

    // Ou buscar todas as fotos de um pet
    // List<FotoPet> findByPetId(Long petId);
}