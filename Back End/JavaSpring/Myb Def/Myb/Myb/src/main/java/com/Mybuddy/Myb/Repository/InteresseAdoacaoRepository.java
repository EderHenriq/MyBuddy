package com.Mybuddy.Myb.Repository;

import com.Mybuddy.Myb.Model.InteresseAdoacao;
import com.Mybuddy.Myb.Model.Usuario;
import com.Mybuddy.Myb.Model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InteresseAdoacaoRepository extends JpaRepository<InteresseAdoacao, Long> {
    List<InteresseAdoacao> findByUsuario(Usuario usuario);
    List<InteresseAdoacao> findByPet(Pet pet);
}

