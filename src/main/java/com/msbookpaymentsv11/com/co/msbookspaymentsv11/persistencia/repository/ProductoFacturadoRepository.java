//DECLARACIÓN DE PAQUETES:
package com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.repository;

//IMPORTACIÓN DE LIBRERIAS:
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.entity.ProductoFacturado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
* @Autor MARIA GABRIELA ZAPATA DIAZ.
* @Since 19/02/2026.
* Repositorio JPA para la entidad ProductoFacturado.
* Proporciona consultas personalizadas para el manejo de productos facturados.
*/
public interface ProductoFacturadoRepository extends JpaRepository<ProductoFacturado, Long> {
  
  //CONSULTA PARA CONTAR CUÁNTOS LIBROS FACTURADOS HAY POR ID DE LIBRO:
  @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM tbl_productos_facturados WHERE id_libro = :idLibro")
  Integer cuantosLibrosFacturadosXIdLibro(@Param("idLibro") Long idLibro);
  
  //CONSULTA PARA OBTENER LOS PRODUCTOS FACTURADOS POR ID DE VENTA:
  @Query(nativeQuery = true, value = "SELECT * FROM tbl_productos_facturados WHERE id_venta = :idVenta")
  List<ProductoFacturado> productosPorIdVenta(@Param("idVenta") Long idVenta);
  
  //CONSULTA PARA VERIFICAR SI YA EXISTE UN PRODUCTO FACTURADO EN UNA VENTA PARA UN LIBRO:
  @Query("""
            SELECT p FROM ProductoFacturado p
            WHERE p.venta.idVenta = :idVenta
            AND p.idLibro = :idLibro
        """)
  Optional<ProductoFacturado> buscarExistente(
      @Param("idVenta") Long idVenta,
      @Param("idLibro") Long idLibro);
}
