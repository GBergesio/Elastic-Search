package Cuscus.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.IOException;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "productos")
public class Product {

    @Id
    private String id;
    private String nombre;
    private String descripcion;
    private double precio;

    // Método estático para deserializar un documento JSON a un objeto Producto
    public static Product fromJson(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, Product.class);
    }
}
