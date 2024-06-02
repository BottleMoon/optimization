package flab.optimization.controller;

import flab.optimization.dto.SanggaDto;
import flab.optimization.service.SanggaService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


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
    public Page<SanggaDto> findAllPageV3() {
        return sanggaService.findAllPageV3();
    }

}