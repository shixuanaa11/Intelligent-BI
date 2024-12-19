package com.example.intelligentbibackend.model.request.chart;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class ChartDeleteRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = -8961212201997878616L;


    @JsonSerialize(using = ToStringSerializer.class)
    private Long id;
    
}