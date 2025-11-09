package com.example.novashop.controller;

import com.example.novashop.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CategoriaViewController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/categorias")
    public String verTodasLasCategorias(Model model) {

        // --- INICIO DE LA CORRECCIÓN ---
        // Se llama a obtenerActivas() en lugar de obtenerTodas()
        // para mostrar solo las categorías activas a los clientes.
        model.addAttribute("categorias", categoriaService.obtenerActivas());
        // --- FIN DE LA CORRECCIÓN ---

        model.addAttribute("titulo", "Nuestras Categorías"); // (Añadí un título para consistencia)

        return "tienda/categorias";
    }
}