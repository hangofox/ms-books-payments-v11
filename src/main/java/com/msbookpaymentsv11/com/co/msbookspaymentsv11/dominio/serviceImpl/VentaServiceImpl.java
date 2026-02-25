//DECLARACIÓN DE PAQUETES:
package com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.serviceImpl;

//IMPORTACIÓN DE LIBRERIAS:
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.Constantes.EstadoVenta;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.dto.MessageResponseDTO;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.exception.BusinessException;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.Constantes.MensajeRespuesta;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.dto.VentaDTO;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.dominio.service.VentaService;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.dao.VentaDAO;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.entity.Venta;
import com.msbookpaymentsv11.com.co.msbookspaymentsv11.persistencia.repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
* @Autor MARIA GABRIELA ZAPATA DIAZ.
* @Since 19/02/2026.
* Esta es la declaración de la implementación del servicio de ventas.
* Gestiona la creación, listado, actualización y consulta de ventas por usuario.
*/
@Service//DECLARACIÓN DEL COMPONENTE DE SERVICIO PARA INYECTAR EN EL CONTENEDOR DE SPRING.
public class VentaServiceImpl implements VentaService {
  
  @Autowired//INYECTAMOS EL REPOSITORIO.
  private VentaRepository ventaRepository;
  
  @Autowired//INYECTAMOS EL DAO.
  private VentaDAO ventaDAO;
  
  //LEER LISTA DE VENTAS POR ID DE USUARIO:
  @Override
  public List<VentaDTO> listarVentasPorIdUsuario(Long idUsuario) {
    List<Venta> ventas = ventaRepository.findByIdUsuario(idUsuario);
    
    if (ventas.isEmpty()){
       throw new BusinessException(MensajeRespuesta.ERROR_NO_EXISTEN_REGISTROS);
    }
    
    return ventas.stream().map(ventaDAO::ventaDTO).toList();
  }
  
  //CREAR REGISTRO:
  @Override
  @Transactional
  public MessageResponseDTO crearVenta(VentaDTO ventaDTO) {
    Long idNuevaVenta = null;
    List<Venta> ventasCreadas = ventaRepository.findByIdUsuarioAndEstadoVenta(ventaDTO.getIdUsuario(), EstadoVenta.INGRESADA);

    if(ventasCreadas == null || ventasCreadas.isEmpty()){
      Venta nuevaVenta = ventaRepository.save(ventaDAO.venta(ventaDTO));
      idNuevaVenta = nuevaVenta.getIdVenta();
    } else {
      idNuevaVenta = ventasCreadas.get(ventasCreadas.size() - 1).getIdVenta();
    }
    
    return MessageResponseDTO.builder()
        .status(MensajeRespuesta.EXITO_REGISTRO_CREADO.getStatus())
        .message(MensajeRespuesta.EXITO_REGISTRO_CREADO.getMensaje())
        .idCreated(idNuevaVenta)
        .build();
  }
  
  //MODIFICAR REGISTRO:
  @Override
  public MessageResponseDTO actualizarVenta(Long idVenta, VentaDTO ventaDTO) {
    Optional<Venta> ventaId = ventaRepository.findById(idVenta);
    
    if(ventaId.isEmpty()){
       throw new BusinessException(MensajeRespuesta.ERROR_REGISTRO_NO_ENCONTRADO);
    }
    
    Venta venta = ventaId.get();
    
    if (ventaDTO.getEstadoVenta() != null){
       venta.setEstadoVenta(EstadoVenta.valueOf(ventaDTO.getEstadoVenta()));
    }
    
    if (ventaDTO.getCostoEnvio() != venta.getCostoEnvio()){
       venta.setCostoEnvio(ventaDTO.getCostoEnvio());
    }
    
    if (ventaDTO.getPorcentajeDescuento() != venta.getPorcentajeDescuento()){
       venta.setPorcentajeDescuento(ventaDTO.getPorcentajeDescuento());
    }
    
    if (ventaDTO.getNumeroOrden() != null){
       venta.setNumeroOrden(ventaDTO.getNumeroOrden());
    }
    
    ventaRepository.save(venta);
    
    return MessageResponseDTO.builder()
        .status(MensajeRespuesta.EXITO_REGISTRO_ACTUALIZADO.getStatus())
        .message(MensajeRespuesta.EXITO_REGISTRO_ACTUALIZADO.getMensaje())
        .idCreated(idVenta)
        .build();
  }
  
  //LEER CONSULTA DE LA VENTA EN ESTADO INGRESADA POR ID DE USUARIO:
  @Override
  public VentaDTO obtenerVentaIngresada(Long idUsuario) {
    List<Venta> ventas = ventaRepository.findByIdUsuarioAndEstadoVenta(idUsuario, EstadoVenta.INGRESADA);
    if (ventas == null || ventas.isEmpty()) {
      throw new BusinessException(MensajeRespuesta.ERROR_REGISTRO_NO_ENCONTRADO);
    }
    return ventaDAO.ventaDTO(ventas.get(ventas.size() - 1));
  }
  
  //LEER CONSULTA: CUÁNTOS ÍTEMS TIENE LA VENTA ACTIVA DEL USUARIO:
  @Override
  public Integer cuantosItemsVentaUsuario(Long idUsuario) {
    return ventaRepository.cuantosItemsXVentaIngresadaXUsuario(idUsuario);
  }
}
