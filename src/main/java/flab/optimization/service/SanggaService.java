package flab.optimization.service;

import flab.optimization.aop.Timed;
import flab.optimization.config.RestPage;
import flab.optimization.dto.SanggaDto;
import flab.optimization.entity.Sangga;
import flab.optimization.repository.SanggaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SanggaService {
    private final SanggaRepository sanggaRepository;

    public SanggaService(SanggaRepository sanggaRepository) {
        this.sanggaRepository = sanggaRepository;
    }

    @Timed
    public Page<SanggaDto> findAllPageV1() {
        Page<SanggaDto> sanggaByNameContaining =
                sanggaRepository.findAll(getPageable()).map(this::SanggaToDto);
        log.info("paging(3000건) V1");
        return sanggaByNameContaining;
    }

    @Timed
    public Page<SanggaDto> findAllPageV2() {
        Page<SanggaDto> sanggaByNameContaining =
                sanggaRepository.findAllPageV2(getPageable()).map(this::SanggaToDto);
        log.info("paging(3000건) V2");
        return sanggaByNameContaining;
    }

    @Timed
    public Page<SanggaDto> findAllPageV3(Pageable pageable) {
        Page<SanggaDto> sanggaByNameContaining =
                sanggaRepository.findAllPageV3(pageable).map(this::SanggaToDto);
        return sanggaByNameContaining;
    }

    @Cacheable(value = "Sangga_Page",cacheManager = "CacheManager")
    public RestPage<SanggaDto> findAllPageV3Caching(Pageable pageable) {
        log.info("SanggaService.java findAllPageV3 실행");
        Page<SanggaDto> sanggaByNameContaining =
                sanggaRepository.findAllPageV3(pageable).map(this::SanggaToDto);
        return new RestPage<SanggaDto>(sanggaByNameContaining);
    }

    private static PageRequest getPageable() {
        return PageRequest.of(0, 3000);
    }

    private SanggaDto SanggaToDto(Sangga sangga) {
        SanggaDto sanggaDto = new SanggaDto();
        sanggaDto.setName(sangga.getName());
        sanggaDto.setJibun_address(sangga.getJibun_address());
        sanggaDto.setDoro_address(sangga.getDoro_address());
        sanggaDto.setBigClassificationName(sangga.getBigClassificationName().getName());
        sanggaDto.setMediumClassificationName(sangga.getMediumClassificationName().getName());
        sanggaDto.setSmallClassificationName(sangga.getSmallClassificationName().getName());
        sanggaDto.setStandardIndustrialClassificationName(sangga.getStandardIndustrialClassificationName().getName());
        return sanggaDto;
    }
}