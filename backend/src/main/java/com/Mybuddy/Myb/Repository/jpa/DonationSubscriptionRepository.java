package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.DonationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DonationSubscriptionRepository extends JpaRepository<DonationSubscription, Long> {
    Optional<DonationSubscription> findByMpPreapprovalId(String mpPreapprovalId);

    java.util.List<DonationSubscription> findByUsuarioId(Long usuarioId);

    boolean existsByOrganizacaoIdAndStatusNot(Long organizacaoId, String status);
}
