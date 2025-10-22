package com.Mybuddy.Myb.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Mybuddy.Myb.Model.Organizacao;

@Repository
public interface OrganizacaoRepository extends JpaRepository<Organizacao, Long> {
   
}
