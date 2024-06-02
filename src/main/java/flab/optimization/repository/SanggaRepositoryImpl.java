package flab.optimization.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import flab.optimization.entity.Sangga;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static flab.optimization.entity.QSangga.sangga;

public class SanggaRepositoryImpl implements SanggaRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    public SanggaRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public Page<Sangga> findAllPageV3(Pageable pageable) {
        List<Sangga> content = queryFactory
                .select(sangga)
                .from(sangga)
                .join(sangga.bigClassificationName).fetchJoin()
                .join(sangga.mediumClassificationName).fetchJoin()
                .join(sangga.smallClassificationName).fetchJoin()
                .join(sangga.standardIndustrialClassificationName).fetchJoin()
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(sangga.count())
                .from(sangga);

        return PageableExecutionUtils.getPage(content,pageable,countQuery::fetchOne);
    }
}