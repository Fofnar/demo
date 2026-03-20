package com.fof.demo.repository;

import com.fof.demo.entity.SaleEntity;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface SaleRepository extends
        JpaRepository<SaleEntity, Long>,
        JpaSpecificationExecutor<SaleEntity>
{
    //Trouver les ventes apès une date spécifique
    List<SaleEntity> findBySaleDateAfter(LocalDateTime date);

    //Ventes par produit
    List<SaleEntity> findByProduct(String product);

    //Récupérer les ventes d’un user
    List<SaleEntity> findByUserEmail(String email);

}
