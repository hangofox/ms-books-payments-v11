//DECLARACIÓN DE PAQUETES:
package com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.repository;

//IMPORTACIÓN DE LIBRERIAS:
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.Constantes.EstadoVenta;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

/**
* @Autor MARIA GABRIELA ZAPATA DIAZ.
* @Since 19/02/2026.
* Repositorio JPA para la entidad Venta.
* Proporciona consultas personalizadas para el manejo de ventas por usuario.
*/
public interface VentaRepository extends JpaRepository <Venta, Long> {
  
  //CONSULTA PARA LISTAR LAS VENTAS DE UN USUARIO POR SU ID:
  List<Venta> findByIdUsuario(Long idUsuario);
  
  //CONSULTA PARA OBTENER LA VENTA DE UN USUARIO CON UN ESTADO ESPECÍFICO:
  List<Venta> findByIdUsuarioAndEstadoVenta(Long idUsuario, EstadoVenta estadoVenta);
  
  //CONSULTA PARA CONTAR CUÁNTOS ÍTEMS TIENE LA VENTA INGRESADA DE UN USUARIO:
  @Query(nativeQuery = true, value = "SELECT IFNULL(SUM(p.cantidad_item),0)" +
                                     "  FROM tbl_ventas v, tbl_productos_facturados p" +
                                     " WHERE v.id_venta = p.id_venta" +
                                     "   AND v.estado_venta = 'INGRESADA'" +
                                     "   AND v.id_usuario = :idUsuario")
  Integer cuantosItemsXVentaIngresadaXUsuario(@Param("idUsuario") Long idUsuario);
}
