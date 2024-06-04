package flab.optimization.controller;

import flab.optimization.aop.Timed;
import flab.optimization.config.RestPage;
import flab.optimization.dto.SanggaDto;
import flab.optimization.service.SanggaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/sangga")
public class SanggaController {

    private final SanggaService sanggaService;

    public SanggaController(SanggaService sanggaService) {
        this.sanggaService = sanggaService;
    }

    @GetMapping("/V1")
    public Page<SanggaDto> findAllPage() {
        return sanggaService.findAllPageV1();
    }

    @GetMapping("/V2")
    public Page<SanggaDto> findAllPageV2() {
        return sanggaService.findAllPageV2();
    }

    @GetMapping("/V3")
    public Page<SanggaDto> findAllPageV3(Pageable pageable) {
        return sanggaService.findAllPageV3(pageable);
    }

    @Timed
    @GetMapping("/caching")
    public RestPage<SanggaDto> findAllPageV3Caching(Pageable pageable) {
        log.info("paging(1000건) Caching");
        return sanggaService.findAllPageV3Caching(pageable);
    }

}