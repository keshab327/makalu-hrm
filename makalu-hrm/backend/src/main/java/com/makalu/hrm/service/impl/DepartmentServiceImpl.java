package com.makalu.hrm.service.impl;

import com.makalu.hrm.converter.DepartmentConverter;
import com.makalu.hrm.domain.PersistentDepartmentEntity;
import com.makalu.hrm.model.DepartmentDTO;
import com.makalu.hrm.model.RestResponseDto;
import com.makalu.hrm.repository.DepartmentRepository;
import com.makalu.hrm.service.DepartmentService;
import com.makalu.hrm.validation.DepartmentValidation;
import com.makalu.hrm.validation.error.DepartmentError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import javax.validation.constraints.NotNull;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    private final DepartmentConverter departmentConverter;

    private final DepartmentValidation departmentValidation;

    private final ServletContext context;

    @Override
    public List<DepartmentDTO> list() {
        return departmentConverter.convertToDtoList(departmentRepository.findAll());
    }

    @Override
    @Transactional
    public RestResponseDto save(@NotNull DepartmentDTO departmentDTO) {
        try {
            DepartmentError error = departmentValidation.validateOnSave(departmentDTO);

            if (!error.isValid()) {
                return RestResponseDto.INSTANCE().validationError().detail(Map.of("error", error, "data", departmentDTO));
            }

            return RestResponseDto
                    .INSTANCE()
                    .success()
                    .detail(departmentConverter.convertToDto(
                            departmentRepository.saveAndFlush(departmentConverter.convertToEntity(departmentDTO))
                    ));
        } catch (Exception ex) {
            log.error("Error while creating department", ex);
            return RestResponseDto
                    .INSTANCE()
                    .internalServerError()
                    .detail(departmentDTO);
        }
    }

    @Override
    public RestResponseDto getResponseById(@NotNull UUID departmentId) {
        PersistentDepartmentEntity departmentEntity = departmentRepository.findById(departmentId).orElse(null);
        if (departmentEntity == null) {
            return RestResponseDto.INSTANCE().notFound().message("Department not found");
        }
        return RestResponseDto.INSTANCE().success().detail(departmentConverter.convertToDto(departmentEntity));
    }

    @Override
    @Transactional
    public RestResponseDto update(@NotNull DepartmentDTO departmentDTO) {
        try {
            DepartmentError error = departmentValidation.validateOnUpdate(departmentDTO);

            if (!error.isValid()) {
                return RestResponseDto.INSTANCE().validationError().detail(Map.of("error", error, "data", departmentDTO));
            }

            PersistentDepartmentEntity departmentEntity = departmentRepository.findById(departmentDTO.getId()).orElse(null);

            if (departmentEntity == null) {
                return RestResponseDto.INSTANCE().validationError().message("Department not found").detail(departmentDTO);
            }

            return RestResponseDto
                    .INSTANCE()
                    .success()
                    .detail(departmentConverter.convertToDto(
                            departmentRepository.saveAndFlush(departmentConverter.copyConvertToEntity(departmentDTO, departmentEntity))
                    ));
        } catch (Exception ex) {
            log.error("Error while creating department", ex);
            return RestResponseDto
                    .INSTANCE()
                    .internalServerError()
                    .detail(departmentDTO);
        }
    }

    @Override
    @Transactional
    public RestResponseDto delete(UUID departmentId) {
        try {
            PersistentDepartmentEntity departmentEntity = departmentRepository.findById(departmentId).orElse(null);

            if (departmentEntity == null) {
                return RestResponseDto.INSTANCE().notFound().message("Department not found");
            }
            departmentRepository.delete(departmentEntity);
            return RestResponseDto.INSTANCE().success();
        } catch (Exception ex) {
            log.error("Error while creating department", ex);
            return RestResponseDto.INSTANCE().internalServerError();
        }
    }

}
