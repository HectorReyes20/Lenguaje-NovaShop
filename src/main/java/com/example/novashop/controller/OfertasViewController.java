package com.example.novashop.controller;

import com.example.novashop.model.Producto;
import com.example.novashop.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class OfertasViewController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/ofertas")
    public String verPaginaDeOfertas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size, // Mismo tamaño que en productos
            @RequestParam(defaultValue = "nombre,asc") String sort, // Orden por defecto
            Model model) {

        // Configuración de paginación y ordenamiento
        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));


        Page<Producto> paginaOfertas = productoService.obtenerConOfertas(pageable);

        // Pasamos los datos a la vista
        model.addAttribute("paginaProductos", paginaOfertas); // Reutilizamos el nombre de variable
        model.addAttribute("tituloPagina", "🔥 ¡Ofertas Imperdibles!"); // Título llamativo
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginaOfertas.getTotalPages());

        // No necesitamos 'categorias' ni 'filtroActivo' aquí

        return "tienda/ofertas"; // Usaremos esta nueva plantilla
    }
}