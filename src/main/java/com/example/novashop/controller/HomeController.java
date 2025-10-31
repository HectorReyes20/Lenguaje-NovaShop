package com.example.novashop.controller;
import com.example.novashop.model.*;
import com.example.novashop.security.SecurityUtils;
import com.example.novashop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final SecurityUtils securityUtils;

    @GetMapping({"/", "/home", "/index"})
    public String home(Model model) {
        // Obtener productos destacados
        Pageable pageable = PageRequest.of(0, 8);
        Page<Producto> destacados = productoService.obtenerProductosDestacados(pageable);

        // Obtener productos en oferta
        Page<Producto> ofertas = productoService.obtenerConOfertas(PageRequest.of(0, 4));

        // Obtener categorías principales
        List<Categoria> categorias = categoriaService.obtenerPrincipales();

        // Usuario actual
        securityUtils.getCurrentUser().ifPresent(usuario ->
                model.addAttribute("usuarioActual", usuario)
        );

        model.addAttribute("productosDestacados", destacados.getContent());
        model.addAttribute("productosOfertas", ofertas.getContent());
        model.addAttribute("categorias", categorias);
        model.addAttribute("titulo", "Bienvenido a NovaShop");

        return "index";
    }

    //@GetMapping("/productos")
    public String listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) Long categoria,
            @RequestParam(required = false) String genero,
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) String orden,
            Model model) {

        Pageable pageable = PageRequest.of(page, 12);
        Page<Producto> productos;
        String tituloSeccion = "Todos los Productos";

        if (buscar != null && !buscar.isEmpty()) {
            productos = productoService.buscarProductos(buscar, pageable);
            tituloSeccion = "Resultados para: " + buscar;
            model.addAttribute("buscar", buscar);
        } else if (categoria != null) {
            productos = productoService.obtenerPorCategoria(categoria, pageable);
            categoriaService.obtenerPorId(categoria).ifPresent(cat ->
                    model.addAttribute("categoriaActual", cat)
            );
            tituloSeccion = "Productos por categoría";
            model.addAttribute("categoriaId", categoria);
        } else if (genero != null) {
            Producto.GeneroProducto generoEnum = Producto.GeneroProducto.valueOf(genero.toUpperCase());
            productos = productoService.obtenerPorGenero(generoEnum, pageable);
            tituloSeccion = "Productos para " + genero;
            model.addAttribute("genero", genero);
        } else {
            productos = productoService.obtenerProductosActivos(pageable);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.obtenerActivas());
        model.addAttribute("titulo", tituloSeccion);

        return "productos/lista";
    }

    @GetMapping("/productos/{id}")
    public String detalleProducto(@PathVariable Long id, Model model) {
        log.info("Mostrando detalle del producto ID: {}", id);

        try {

            Producto producto = productoService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

            if (producto.getEstado() != Producto.EstadoProducto.ACTIVO) {
                throw new RuntimeException("Este producto no está disponible");
            }

            Pageable pageable = PageRequest.of(0, 4);
            List<Producto> relacionados = productoService.obtenerRelacionados(
                    id,
                    producto.getCategoria().getIdCategoria(),
                    pageable
            );

            Double calificacion = productoService.obtenerCalificacionPromedio(id);

            boolean esFavorito = false;
            if (securityUtils.isAuthenticated()) {
                Long idUsuario = securityUtils.getCurrentUserId().orElse(null);
                if (idUsuario != null) {
                    // TODO: Implementar cuando tengas FavoritoService
                    // esFavorito = favoritoService.esFavorito(idUsuario, id);
                }
            }

            model.addAttribute("producto", producto);
            model.addAttribute("relacionados", relacionados);
            model.addAttribute("calificacion", calificacion != null ? calificacion : 0.0);
            model.addAttribute("esFavorito", esFavorito);
            model.addAttribute("titulo", producto.getNombre());

            return "productos/detalle";

        } catch (RuntimeException e) {
            log.error("Error al mostrar detalle del producto: {}", e.getMessage());

            return "redirect:/productos?error=" + e.getMessage();
        }
    }

    //@GetMapping("/ofertas")
    public String ofertas(
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 12);
        Page<Producto> productos = productoService.obtenerConOfertas(pageable);

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Ofertas Especiales");

        return "productos/ofertas";
    }
}
