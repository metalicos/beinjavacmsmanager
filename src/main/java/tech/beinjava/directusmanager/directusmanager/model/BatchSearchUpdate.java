package tech.beinjava.directusmanager.directusmanager.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchSearchUpdate {
    private List<Integer> ids;
}
