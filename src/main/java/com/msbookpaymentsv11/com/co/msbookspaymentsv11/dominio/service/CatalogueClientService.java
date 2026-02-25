//DECLARACIÓN DE PAQUETES:
package com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.service;

//IMPORTACIÓN DE LIBRERIAS:
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.Constantes.MensajeRespuesta;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.dto.KardexInventarioDTO;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.exception.BusinessException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
* @Autor MARIA GABRIELA ZAPATA DIAZ.
* @Since 19/02/2026.
* Declaración del servicio cliente para la comunicación con el microservicio ms-books-catalogue.
* Gestiona las consultas de stock y el registro de movimientos de inventario.
*/
@Service//DECLARACIÓN DEL COMPONENTE DE SERVICIO PARA INYECTAR EN EL CONTENEDOR DE SPRING.
public class CatalogueClientService {
  
  //DECLARACIÓN DEL WEBCLIENT PARA LA COMUNICACIÓN CON EL MICROSERVICIO CATALOGUE:
  private final WebClient webClient;
  
  /**
  * @Autor MARIA GABRIELA ZAPATA DIAZ.
  * @Since 19/02/2026.
  * @param builder Constructor del WebClient con balanceo de carga hacia catalogue-service.
  * Constructor que inicializa el WebClient apuntando al microservicio de catálogo.
  */
  public CatalogueClientService(WebClient.Builder builder) {
    this.webClient = builder.baseUrl("https://ms-books-catalogue-v11-production.up.railway.app").build();
  }
  
  /**
  * @Autor MARIA GABRIELA ZAPATA DIAZ.
  * @Since 19/02/2026.
  * @param idLibro Identificador del libro a consultar en el inventario.
  * @return Integer con la cantidad de stock disponible para el libro.
  * Consulta el stock disponible de un libro en el microservicio de catálogo.
  */
  //MÉTODO PARA OBTENER EL STOCK DE UN LIBRO DESDE EL MICROSERVICIO DE CATÁLOGO:
  public Integer getStock(Long idLibro) {
    return webClient.get()
        .uri("inventarios/stock/{id}", idLibro)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response ->
            Mono.error(new BusinessException(MensajeRespuesta.ERROR_SISTEMA)))
        .bodyToMono(Integer.class)
        .block();
  }
  
  /**
  * @Autor MARIA GABRIELA ZAPATA DIAZ.
  * @Since 19/02/2026.
  * @param dto DTO con el idLibro y la cantidad a descontar del inventario.
  * Registra una salida de inventario en el microservicio de catálogo al realizar una compra.
  */
  //MÉTODO PARA REGISTRAR UNA SALIDA DE INVENTARIO EN EL MICROSERVICIO DE CATÁLOGO:
  public void registrarSalidaInventario(KardexInventarioDTO dto) {
    webClient.post()
        .uri("/inventarios/salida")
        .bodyValue(dto)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response ->
            Mono.error(new BusinessException(MensajeRespuesta.ERROR_SISTEMA)))
        .toBodilessEntity()
        .block();
  }
  
  /**
  * @Autor MARIA GABRIELA ZAPATA DIAZ.
  * @Since 19/02/2026.
  * @param dto DTO con el idLibro y la cantidad a reponer en el inventario.
  * Registra una entrada de inventario en el microservicio de catálogo al eliminar un producto facturado.
  */
  //MÉTODO PARA REGISTRAR UNA ENTRADA DE INVENTARIO EN EL MICROSERVICIO DE CATÁLOGO:
  public void registrarEntradaInventario(KardexInventarioDTO dto) {
    webClient.post()
        .uri("/inventarios/entrada")
        .bodyValue(dto)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, response ->
            Mono.error(new BusinessException(MensajeRespuesta.ERROR_SISTEMA)))
        .toBodilessEntity()
        .block();
  }
}
