package com.provapratica.api.controller;

import com.provapratica.api.dto.ProfessorRequest;
import com.provapratica.api.dto.ProfessorResponse;
import com.provapratica.api.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("api/professor")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @GetMapping
    public List<ProfessorResponse> findAll() {
        return professorService.findAll();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public ProfessorResponse save(@RequestBody @Validated ProfessorRequest professorRequest) {
        return professorService.save(professorRequest);
    }

    @PutMapping("{id}/editar")
    public ProfessorResponse update(@PathVariable Integer id,
                                    @Validated @RequestBody ProfessorRequest professorRequest) {
        return professorService.update(id, professorRequest);
    }
}
