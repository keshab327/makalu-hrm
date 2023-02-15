package com.makalu.hrm.converter;

import com.makalu.hrm.domain.PersistentAttendanceEntity;
import com.makalu.hrm.model.AttendanceDto;
import com.makalu.hrm.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttendanceConverter extends Convertable<PersistentAttendanceEntity, AttendanceDto> {

    private final UserConverter userConverter;

    @Override
    public PersistentAttendanceEntity convertToEntity(AttendanceDto dto) {
        return null;
    }

    @Override
    public PersistentAttendanceEntity copyConvertToEntity(AttendanceDto dto, PersistentAttendanceEntity entity) {
        return null;
    }

    @Override
    public AttendanceDto convertToDto(PersistentAttendanceEntity entity) {
        if (entity == null) return null;

        AttendanceDto dto = new AttendanceDto();
        dto.setId(entity.getId());
        dto.setPunchInDate(entity.getPunchInDate());
        dto.setPunchInIp(entity.getPunchInIp());
        dto.setPunchOutDate(entity.getPunchOutDate());
        dto.setPunchOutIp(entity.getPunchOutIp());
        dto.setTotalWorkedHours(DateUtils.convertTimeToString(entity.getTotalWorkedHours()));
        dto.setUser(userConverter.convertToDto(entity.getUser()));
        return dto;
    }
}
