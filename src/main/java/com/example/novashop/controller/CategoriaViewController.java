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
        // CORRECCIÓN AQUÍ:
        model.addAttribute("categorias", categoriaService.obtenerTodas());
        return "tienda/categorias";
    }
}