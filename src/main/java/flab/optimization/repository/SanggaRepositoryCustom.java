package flab.optimization.repository;


import flab.optimization.entity.Sangga;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SanggaRepositoryCustom {
    public Page<Sangga> findAllPageV3(Pageable pageable);

}
