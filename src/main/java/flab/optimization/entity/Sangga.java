package flab.optimization.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
public class Sangga {
    @Id
    private Long id;

    private String name;

    //지번 주소
    private String jibun_address;

    //도로명주소
    private String doro_address;

    @ManyToOne(fetch = FetchType.LAZY)
    private BigClassificationName bigClassificationName;

    @ManyToOne(fetch = FetchType.LAZY)
    private MediumClassificationName mediumClassificationName;

    @ManyToOne(fetch = FetchType.LAZY)
    private SmallClassificationName smallClassificationName;

    @ManyToOne(fetch = FetchType.LAZY)
    private StandardIndustrialClassificationName standardIndustrialClassificationName;
}