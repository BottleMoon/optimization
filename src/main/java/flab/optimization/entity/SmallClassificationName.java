package flab.optimization.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class SmallClassificationName {
    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "smallClassificationName")
    private List<Sangga> sanggaList = new ArrayList<>();
}
