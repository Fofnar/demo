package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AISalesRequest {
    // Liste des ventes envoyées au moteur IA
    private List<AISaleDTO> data;
}
