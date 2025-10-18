package com.example.novashop.controller;
import com.example.novashop.model.Categoria;
import com.example.novashop.model.Producto;
import com.example.novashop.service.CategoriaService;
import com.example.novashop.service.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;



import java.util.List;
@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Slf4j
public class CategoriasViewController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    @GetMapping
    public String listarCategorias(Model model) {
        List<Categoria> categorias = categoriaService.obtenerPrincipales();

        model.addAttribute("categorias", categorias);
        model.addAttribute("titulo", "Categorías");

        return "categorias/lista";
    }

    @GetMapping("/{id}")
    public String verCategoria(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Categoria categoria = categoriaService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));

        Pageable pageable = PageRequest.of(page, 12);
        Page<Producto> productos = productoService.obtenerPorCategoria(id, pageable);

        // Obtener subcategorías si las tiene
        List<Categoria> subcategorias = categoriaService.obtenerSubcategorias(id);

        model.addAttribute("categoria", categoria);
        model.addAttribute("subcategorias", subcategorias);
        model.addAttribute("productos", productos);
        model.addAttribute("titulo", categoria.getNombre());

        return "categorias/detalle";
    }
}