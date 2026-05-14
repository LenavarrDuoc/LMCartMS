package cl.duoc.lmcartms.clients;

import cl.duoc.lmcartms.dtos.InventarioResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "LMStockMS", path = "/api/v1/inventarios")
public interface ToAPIStockFeign {

    @GetMapping("/{id}")
    InventarioResponseDTO findById(@PathVariable Long id);
}
