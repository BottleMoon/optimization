package flab.optimization.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;

@Entity
@Getter
public class StandardIndustrialClassificationName {
    @Id
    private Long id;

    private String name;
}
