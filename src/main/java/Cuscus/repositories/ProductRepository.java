package Cuscus.repositories;

//import Cuscus.configs.JsonUtil;
import Cuscus.configs.JsonUtil;
import Cuscus.models.Product;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Repository
public class ProductRepository {

    @Autowired
    private RestHighLevelClient client;


    public ResponseEntity<String> saveProducto(Product producto) throws IOException {

        if (producto.getId() == null) {
            producto.setId(UUID.randomUUID().toString());
        }

        // Construimos la solicitud de indexación con el ID proporcionado en el objeto Producto
        IndexRequest request = new IndexRequest("productos")
                .id(producto.getId()) // Establecemos el ID
                .source(JsonUtil.toJson(producto), XContentType.JSON);

        try {
            IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            // Manejar excepciones de IO
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error de IO al comunicarse con Elasticsearch: " + e.getMessage());
        } catch (Exception e) {
            // Capturar cualquier otra excepción y depurar
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado al indexar el documento en Elasticsearch: " + e.getMessage());
        }
        // Devolvemos el ID del producto indexado
        return ResponseEntity.status(HttpStatus.OK).body("Producto id" + producto.getId());
    }


    public List<Product> getAllProductos() throws IOException {
        SearchRequest searchRequest = new SearchRequest("productos");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(new MatchAllQueryBuilder());
        searchRequest.source(searchSourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        List<Product> productos = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            String sourceAsString = hit.getSourceAsString();
            Product producto = Product.fromJson(sourceAsString);
            productos.add(producto);
        }

        return productos;
    }


    public GetResponse findById(String id) throws IOException {
        GetRequest request = new GetRequest("productos", id);
        return client.get(request, RequestOptions.DEFAULT);
    }

    public SearchHits search(SearchSourceBuilder sourceBuilder) throws IOException {
        SearchRequest searchRequest = new SearchRequest("productos");
        searchRequest.source(sourceBuilder);

        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        return response.getHits();
    }


}