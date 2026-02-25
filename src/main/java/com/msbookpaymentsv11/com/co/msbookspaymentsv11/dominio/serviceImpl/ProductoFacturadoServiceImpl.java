//DECLARACIÓN DE PAQUETES:
package com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.serviceImpl;

//IMPORTACIÓN DE LIBRERIAS:
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.dto.MessageResponseDTO;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.exception.BusinessException;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.Constantes.MensajeRespuesta;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.dto.ProductoFacturadoDTO;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.dto.KardexInventarioDTO;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.service.CatalogueClientService;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.service.ProductoFacturadoService;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.dao.ProductoFacturadoDAO;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.entity.ProductoFacturado;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.repository.ProductoFacturadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
* @Autor MARIA GABRIELA ZAPATA DIAZ.
* @Since 19/02/2026.
* Esta es la declaración de la implementación del servicio de productos facturados.
* Gestiona la creación, eliminación, consulta y actualización parcial de productos
* dentro de una venta, integrándose con el microservicio de catálogo para el inventario.
*/
@Service//DECLARACIÓN DEL COMPONENTE DE SERVICIO PARA INYECTAR EN EL CONTENEDOR DE SPRING.
public class ProductoFacturadoServiceImpl implements ProductoFacturadoService {
  
  @Autowired//INYECTAMOS EL DAO.
  private ProductoFacturadoDAO productoFacturadoDAO;
  
  @Autowired//INYECTAMOS EL REPOSITORIO.
  private ProductoFacturadoRepository productoFacturadoRepository;
  
  @Autowired//INYECTAMOS EL SERVICIO CLIENTE DEL CATÁLOGO.
  //private CatalogueClientService catalogueClientService;
  
  //CREAR REGISTRO:
  @Override
  public MessageResponseDTO crearProductoFacturado(ProductoFacturadoDTO productoFacturadoDTO) {
    Long idLibro = productoFacturadoDTO.getIdLibro();
    Long idVenta = productoFacturadoDTO.getIdVenta();
    Integer cantidad = productoFacturadoDTO.getCantidadItem();
    
    //Revisa el catalogue para traer el stock:
    /*Integer stock = catalogueClientService.getStock(idLibro);
    
    if (stock == null || stock < cantidad) {
      throw new BusinessException(MensajeRespuesta.ERROR_STOCK_INSUFICIENTE);
    }*/
    
    Optional<ProductoFacturado> productoExistente = productoFacturadoRepository.buscarExistente(idVenta, idLibro);
    
    ProductoFacturado nuevoProducto = productoExistente
        .map(p -> {
          p.setCantidadItem(p.getCantidadItem() + cantidad);
          return productoFacturadoRepository.save(p);
        })
        .orElseGet(() -> productoFacturadoRepository.save(productoFacturadoDAO.productoFacturado(productoFacturadoDTO)));
    
    //Registra la salida del inventario:
    /*KardexInventarioDTO salidaDTO = new KardexInventarioDTO();
    salidaDTO.setIdLibro(idLibro);
    salidaDTO.setCantidadInventario(cantidad);
    catalogueClientService.registrarSalidaInventario(salidaDTO);*/
    
    return MessageResponseDTO.builder()
        .status(MensajeRespuesta.EXITO_REGISTRO_CREADO.getStatus())
        .message(MensajeRespuesta.EXITO_REGISTRO_CREADO.getMensaje())
        .idCreated(nuevoProducto.getIdProductoFacturado())
        .build();
  }
  
  //ELIMINAR REGISTRO:
  @Override
  public void eliminarProductoFacturado(Long idProductoFacturado) {
   
    Optional<ProductoFacturado> productoFacturado = productoFacturadoRepository.findById(idProductoFacturado);
    
    if (productoFacturado.isEmpty()) {
       throw new BusinessException(MensajeRespuesta.ERROR_REGISTRO_NO_ENCONTRADO);
    }
    
    Long idLibro = productoFacturado.get().getIdLibro();
    Integer cantidad = productoFacturado.get().getCantidadItem();
    
    productoFacturadoRepository.deleteById(idProductoFacturado);
    
    //Registra la entrada del inventario al eliminar el producto facturado:
    /*KardexInventarioDTO entradaDTO = new KardexInventarioDTO();
    entradaDTO.setIdLibro(idLibro);
    entradaDTO.setCantidadInventario(cantidad);
    catalogueClientService.registrarEntradaInventario(entradaDTO);*/
  }
  
  //LEER CONSULTA: CUÁNTOS PRODUCTOS FACTURADOS HAY POR ID DE LIBRO:
  @Override
  public Integer cuantosProductosFacturadosPorIdLibro(Long idLibro) {
    return productoFacturadoRepository.cuantosLibrosFacturadosXIdLibro(idLibro);
  }
  
  //LEER LISTA DE PRODUCTOS FACTURADOS POR ID DE VENTA:
  @Override
  public List<ProductoFacturadoDTO> listaProductosFacturadosXIdVenta(Long idVenta) {
    List<ProductoFacturado> productos = productoFacturadoRepository.productosPorIdVenta(idVenta);
    
    if (productos.isEmpty()) {
      throw new BusinessException(MensajeRespuesta.ERROR_NO_EXISTEN_REGISTROS);
    }
    
    return productos.stream().map(productoFacturadoDAO::productoFacturadoDTO).toList();
  }
  
  //MODIFICAR PARCIALMENTE REGISTRO:
  @Override
  public MessageResponseDTO actualizacionParcialProductoFacturado(Long idProductoFacturado, ProductoFacturadoDTO productoFacturadoDTO) {
    Optional<ProductoFacturado> productoFacturadoExiste = productoFacturadoRepository.findById(idProductoFacturado);
    
    if (productoFacturadoExiste.isEmpty()) {
       throw new BusinessException(MensajeRespuesta.ERROR_REGISTRO_NO_ENCONTRADO);
    }
    
    ProductoFacturado productoFacturado = productoFacturadoExiste.get();
    
    if (productoFacturadoDTO.getCantidadItem() != null) {
       productoFacturado.setCantidadItem(productoFacturadoDTO.getCantidadItem());
    }
    
    if (productoFacturadoDTO.getEstadoProductoFacturado() != null) {
       productoFacturado.setEstadoProductoFacturado(productoFacturadoDTO.getEstadoProductoFacturado());
    }
    
    productoFacturadoRepository.save(productoFacturado);
    
    return MessageResponseDTO.builder()
        .status(MensajeRespuesta.EXITO_REGISTRO_ACTUALIZADO.getStatus())
        .message(MensajeRespuesta.EXITO_REGISTRO_ACTUALIZADO.getMensaje())
        .idCreated(idProductoFacturado)
        .build();
  }
}
