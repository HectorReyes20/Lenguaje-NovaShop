package com.example.novashop.controller;

import com.example.novashop.model.Categoria;
import com.example.novashop.model.Producto;
import com.example.novashop.service.CategoriaService;
import com.example.novashop.service.ProductoService;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@Slf4j
public class ProductoViewController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/productos")
    public String verPaginaDeProductos(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "nombre,asc") String sort,
            Model model, HttpServletRequest request) {
        request.getSession(true);

        String[] sortParams = sort.split(",");
        Sort.Direction direction = sortParams[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<Producto> paginaProductos;
        String tituloPagina = "Todos los Productos";

        if (buscar != null && !buscar.isEmpty()) {
            paginaProductos = productoService.buscarProductos(buscar, pageable);
            tituloPagina = "Resultados para: '" + buscar + "'";

        } else if (genero != null && !genero.isEmpty()) {
            try {
                Producto.GeneroProducto generoEnum = Producto.GeneroProducto.valueOf(genero.toUpperCase());
                paginaProductos = productoService.obtenerPorGenero(generoEnum, pageable);
                tituloPagina = "Ropa para " + genero.substring(0, 1).toUpperCase() + genero.substring(1).toLowerCase();
            } catch (IllegalArgumentException e) {
                log.warn("Género no válido recibido: {}", genero);
                paginaProductos = productoService.obtenerProductosActivos(pageable);
                tituloPagina = "Género no encontrado";
            }

        } else if (categoriaId != null) {
            paginaProductos = productoService.obtenerPorCategoria(categoriaId, pageable);
            Categoria cat = categoriaService.obtenerPorId(categoriaId).orElse(null);
            if (cat != null) {
                tituloPagina = cat.getNombre();
            } else {
                tituloPagina = "Categoría no encontrada";
            }

        } else {
            paginaProductos = productoService.obtenerProductosActivos(pageable);
        }

        // --- INICIO DE LA CORRECCIÓN ---
        // Se llama a obtenerActivas() en lugar de obtenerTodas()
        // para mostrar solo las categorías activas en el filtro.
        List<Categoria> categorias = categoriaService.obtenerActivas();
        // --- FIN DE LA CORRECCIÓN ---

        model.addAttribute("paginaProductos", paginaProductos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("tituloPagina", tituloPagina);
        model.addAttribute("sort", sort);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paginaProductos.getTotalPages());

        return "tienda/productos";
    }
}