package flab.optimization.service;

import flab.optimization.aop.Timed;
import flab.optimization.dto.SanggaDto;
import flab.optimization.entity.Sangga;
import flab.optimization.repository.SanggaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class SanggaService {
    private final SanggaRepository sanggaRepository;
    private final StopWatch stopWatch = new StopWatch();

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
    public Page<SanggaDto> findAllPageV3() {
        Page<SanggaDto> sanggaByNameContaining =
                sanggaRepository.findAllPageV3(getPageable()).map(this::SanggaToDto);
        log.info("paging(3000건) V3");
        return sanggaByNameContaining;
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