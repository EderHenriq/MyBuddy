package com.Mybuddy.Myb.Repository.jpa;

import com.Mybuddy.Myb.Model.DonationSubscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DonationSubscriptionRepository extends JpaRepository<DonationSubscription, Long> {
    Optional<DonationSubscription> findByMpPreapprovalId(String mpPreapprovalId);
}
