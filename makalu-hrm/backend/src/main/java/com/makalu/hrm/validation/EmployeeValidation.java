package com.makalu.hrm.validation;

import com.makalu.hrm.domain.PersistentEmployeeEntity;
import com.makalu.hrm.model.DepartmentDTO;
import com.makalu.hrm.model.EmployeeDTO;
import com.makalu.hrm.model.PositionDTO;
import com.makalu.hrm.model.WorkExperienceDTO;
import com.makalu.hrm.repository.EmployeeRepository;
import com.makalu.hrm.validation.error.EmployeeError;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeeValidation {
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    private static final int MAX_FILE_SIZE = 3145728;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("png", "jpg", "jpeg");


    private final EmployeeRepository employeeRepository;
    private static EmployeeError error;

    public EmployeeError validateOnSave(EmployeeDTO dto) {
        error = new EmployeeError();
        boolean isValid = validateFullName(dto.getFullname());
        isValid = isValid & validateEmail(dto.getEmail());
        isValid = isValid & validateImage(dto.getEmpImage());
        isValid = isValid & validatePosition(dto.getPositionId());
        isValid = isValid & validateDepartment(dto.getDepartmentId());
        isValid = isValid & validateJoinDate(dto.getJoinDate().toString());
        isValid = isValid & validateContactPhone(dto.getContactDetailDTO().getContactPhone());
        isValid = isValid & validateContactEmail(dto.getContactDetailDTO().getContactEmail());
        isValid = isValid & validateContactAddress(dto.getContactDetailDTO().getContactAddress());
        try {
            isValid = isValid & validateDob(dto.getPersonalDetailDTO().getDob());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        isValid = isValid & validateGender(dto.getPersonalDetailDTO().getGender());
        isValid = isValid & validateMaritalStatus(dto.getPersonalDetailDTO().getMaritalStatus());
        isValid = isValid && validateUniqueEmail(dto.getEmail());
        error.setValid(isValid);
        return error;
    }


    public EmployeeError validateOnUpdate(EmployeeDTO dto) {
        error = new EmployeeError();
        boolean isValid = validateFullName(dto.getFullname());
        isValid = isValid & validateEmail(dto.getEmail());
        isValid = isValid & validatePosition(dto.getPositionId());
        isValid = isValid & validateDepartment(dto.getDepartmentId());
        isValid = isValid & validateJoinDate(dto.getJoinDate().toString());
        isValid = isValid & validateImage(dto.getEmpImage());
        isValid = isValid & validateContactPhone(dto.getContactDetailDTO().getContactPhone());
        isValid = isValid & validateContactEmail(dto.getContactDetailDTO().getContactEmail());
        isValid = isValid & validateContactAddress(dto.getContactDetailDTO().getContactAddress());
        try {
            isValid = isValid & validateDob(dto.getPersonalDetailDTO().getDob());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        isValid = isValid & validateGender(dto.getPersonalDetailDTO().getGender());
        isValid = isValid & validateMaritalStatus(dto.getPersonalDetailDTO().getMaritalStatus());
        isValid = isValid && validateUniqueOnUpdate(dto);
        error.setValid(isValid);
        return error;
    }

    @Transactional
    boolean validateUniqueEmail(String email) {
        PersistentEmployeeEntity employeeEntity = employeeRepository.findByEmail(email);

        if (employeeEntity == null) {
            return true;
        }

        if (employeeEntity.getEmail().equals(email)) {
            error.setEmail("Please provide a unique email address");
        }

        return false;
    }

    @Transactional
    boolean validateUniqueOnUpdate(EmployeeDTO employeeDTO) {
        PersistentEmployeeEntity employeeEntity = employeeRepository.findByEmail(employeeDTO.getEmail());
        if (employeeEntity != null && !employeeEntity.getId().equals(employeeDTO.getId())) {
            error.setEmail("Email already used");
            return false;
        }
        return true;
    }

    private boolean validateFullName(String name) {
        if (name == null || name.trim().isEmpty()) {
            error.setFullname("Name is required");
            return false;
        }

        if (name.length() > 50) {
            error.setFullname("Name must be less than 50 characters");
            return false;
        }
        return true;
    }

    private boolean validateEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            error.setEmail("Email is required");
            return false;
        } else {
            Matcher matcher = EMAIL_PATTERN.matcher(email);
            if (!matcher.matches()) {
                error.setEmail("Incorrect email format");
                return false;
            }
        }
        return true;
    }


    private boolean validateImage(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            String fileName = file.getOriginalFilename();
            String ext = FilenameUtils.getExtension(fileName);
            if (file.getSize() > MAX_FILE_SIZE) {
                error.setEmpImage("Image must be less than 3MB");
                return false;
            }

            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                error.setEmpImage("Invalid file. Supports: jpg,jpeg,png only");
                return false;
            }
        }
        return true;
    }

    private boolean validatePosition(UUID positionId) {
        if (positionId == null) {
            error.setPosition("Position is required");
            return false;
        }
        return true;
    }

    private boolean validateDepartment(UUID departmentId) {
        if (departmentId == null) {
            error.setDepartment("Department is required");
            return false;
        }
        return true;
    }

    private boolean validateContactEmail(String email) {

        if (email == null || email.trim().isEmpty()) {
            error.setContactEmail("Email is required");
            return false;
        } else {
            Matcher matcher = EMAIL_PATTERN.matcher(email);
            if (!matcher.matches()) {
                error.setContactEmail("Incorrect email format");
                return false;
            }
        }
        return true;
    }

    private boolean validateContactPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            error.setContactPhone("Phone number is required");
            return false;
        }
        if (!phone.chars().allMatch(Character::isDigit)) {
            error.setContactPhone("Phone number can only have numerical value");
            return false;
        }
        if (phone.length() != 10) {
            error.setContactPhone("Phone number must have 10 digit");
            return false;
        }
        return true;
    }

    private boolean validateContactAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            error.setContactAddress("Address is required");
            return false;
        }
        if (address.length() > 50) {
            error.setContactAddress("Address can't have more than 50 characters");
            return false;
        }
        return true;
    }

    private boolean validateDob(String dob) throws ParseException {
        if (dob == null || dob.trim().isEmpty()) {
            error.setDob("Date of birth is required");
            return false;
        } else {
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date birthDate = dateFormat.parse(dob);
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();
            int compare = birthDate.compareTo(currentDate);

            if (compare > 0) {
                error.setDob("Date of birth must be less than current date");
                return false;
            }
        }
        return true;
    }


    private boolean validateGender(String gender) {
        if (gender == null || gender.trim().isEmpty()) {
            error.setGender("Gender is required");
            return false;
        }
        return true;
    }

    private boolean validateMaritalStatus(String maritalStatus) {
        if (maritalStatus == null || maritalStatus.trim().isEmpty()) {
            error.setMaritalStatus("Marital status is required");
            return false;
        }
        return true;
    }

    private boolean validateJoinDate(String joinDate) {
        if (joinDate == null || joinDate.trim().isEmpty()) {
            error.setJoinDate("Join date is required");
            return false;
        }
        return true;
    }


}