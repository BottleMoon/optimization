package flab.optimization.repository;

import flab.optimization.entity.Sangga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SanggaRepository extends JpaRepository<Sangga, Long>, SanggaRepositoryCustom {

    @Query("select s" +
            " from Sangga s" +
            " join fetch s.bigClassificationName" +
            " join fetch s.mediumClassificationName" +
            " join fetch s.smallClassificationName" +
            " join fetch s.standardIndustrialClassificationName")
    Page<Sangga> findAllPageV2(Pageable pageable);

    @Override
    Page<Sangga> findAllPageV3(Pageable pageable);

}
