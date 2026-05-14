package cl.duoc.lmcartms.clients;


import cl.duoc.lmcartms.dtos.ClienteOrderResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// APIName = LMCustomerMS;
// APIURL original sin Eureka y Gateway = "http://localhost:8082/clientes";

@FeignClient(name = "LMCustomerMS", path = "/api/v1/clientes")
public interface ToAPICustomerFeign {

    @GetMapping("/{id}")
    ClienteOrderResponseDTO findById(@PathVariable Long id);

    @GetMapping("/exists-by-id/{id}")
    Boolean existsById(@PathVariable Long id);
}


