package com.example.novelcharacter.dto.Equipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EquipmentDTO {
    private long novelNum;
    private long equipmentNum;
    @NotBlank
    @Size(max=40)
    private String equipmentName;
    private String inform;
}
