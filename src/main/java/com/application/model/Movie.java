package com.application.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private String titleId;
    private String titleName;
    private Integer year;
    private List<Cast> topBilledCasts;
}
