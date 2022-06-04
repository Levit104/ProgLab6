package ru.itmo.levit104.lab6.common.data;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Coordinates implements Serializable {
    private static final long serialVersionUID = 3110654373292991507L;
    private Double x;
    private Double y;

    @Override
    public String toString() {
        return String.format("%s,%s", x, y);
    }
}
