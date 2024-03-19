package Cuscus.controllers;

import Cuscus.models.Product;
import Cuscus.repositories.ProductRepository;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/productos")
public class ProductController {

    private final ProductRepository productoRepository;

    @Autowired
    public ProductController(ProductRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @PostMapping("/crear")
    public ResponseEntity<String> saveProducto(@RequestBody Product producto) {
        try {
            productoRepository.saveProducto(producto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Producto creado correctamente " + producto);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al crear el producto: " + e.getMessage());
        }
    }

    @PostMapping("/crear2")
    public ResponseEntity<String> saveProducto2(@RequestBody Product producto) throws IOException {
       return productoRepository.saveProducto(producto);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProductos() {
        try {
            List<Product> productos = productoRepository.getAllProductos();
            return ResponseEntity.ok(productos);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getProductById(@PathVariable String id) {
        try {
            GetResponse response = productoRepository.findById(id);
            if (response.isExists()) {
                return ResponseEntity.ok(response.getSource());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró ningún producto con el ID proporcionado.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al buscar el producto en Elasticsearch: " + e.getMessage());
        }
    }

    @GetMapping("/buscar")
    public ResponseEntity<Object> searchProductos(@RequestParam String descripcion) {
        try {
            SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
            sourceBuilder.query(QueryBuilders.matchQuery("descripcion", descripcion));

            SearchHits hits = productoRepository.search(sourceBuilder);

            // Recorrer los resultados y devolverlos
            return ResponseEntity.ok(hits);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al realizar la búsqueda en Elasticsearch: " + e.getMessage());
        }
    }


}