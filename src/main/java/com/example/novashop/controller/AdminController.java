package com.example.novashop.controller;

import com.example.novashop.model.*;
import com.example.novashop.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final VarianteProductoService varianteService;
    private final ImagenProductoService imagenService;
    private final String UPLOAD_DIR = "uploads/productos/";

    /**
     * Dashboard Principal con Estadísticas
     */
    @GetMapping
    public String dashboard(Model model) {
        log.info("Accediendo al dashboard admin");

        // ========== ESTADÍSTICAS GENERALES ==========

        // Total de ventas (suma de pedidos confirmados/entregados)
        BigDecimal totalVentas = pedidoService.calcularTotalVentas();

        // Total de pedidos
        Pageable pageablePedidos = PageRequest.of(0, 1);
        long totalPedidos = pedidoService.obtenerPorEstado(null, pageablePedidos).getTotalElements();

        // Total de productos activos
        long totalProductos = productoService.obtenerProductosActivos(PageRequest.of(0, 1)).getTotalElements();

        // Total de usuarios
        long totalUsuarios = usuarioService.obtenerTodos().size();

        // Pedidos pendientes
        long pedidosPendientes = pedidoService.obtenerPorEstado(
                Pedido.EstadoPedido.PENDIENTE, PageRequest.of(0, 1)
        ).getTotalElements();

        model.addAttribute("totalVentas", totalVentas != null ? totalVentas : BigDecimal.ZERO);
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("totalUsuarios", totalUsuarios);
        model.addAttribute("pedidosPendientes", pedidosPendientes);

        // ========== ÚLTIMOS PEDIDOS ==========
        Pageable pageableUltimosPedidos = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "fechaPedido"));
        List<Pedido> ultimosPedidos = pedidoService.obtenerPorEstado(null, pageableUltimosPedidos).getContent();
        model.addAttribute("ultimosPedidos", ultimosPedidos);

        // ========== PRODUCTOS CON STOCK BAJO ==========
        List<Producto> productosStockBajo = obtenerProductosStockBajo();
        model.addAttribute("productosStockBajo", productosStockBajo);

        // ========== PRODUCTOS MÁS VENDIDOS (Mock - implementar después) ==========
        List<Map<String, Object>> productosMasVendidos = new ArrayList<>();
        // TODO: Implementar query real de productos más vendidos
        model.addAttribute("productosMasVendidos", productosMasVendidos);

        // ========== DATOS PARA GRÁFICAS ==========

        // Ventas por mes (últimos 6 meses)
        Map<String, BigDecimal> ventasPorMes = calcularVentasPorMes();
        model.addAttribute("ventasMensuales", ventasPorMes);

        // Pedidos por estado
        Map<String, Long> pedidosPorEstado = calcularPedidosPorEstado();
        model.addAttribute("pedidosPorEstado", pedidosPorEstado);

        // ========== METADATA ==========
        model.addAttribute("titulo", "Dashboard");
        model.addAttribute("activePage", "dashboard");

        return "admin/dashboard";
    }

    /**
     * Gestión de Productos
     */
    @GetMapping("/productos")
    public String gestionProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String estado,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "fechaCreacion"));
        Page<Producto> productos;

        // Filtros
        if (buscar != null && !buscar.trim().isEmpty()) {
            productos = productoService.buscarProductos(buscar, pageable);
        } else if (categoriaId != null) {
            productos = productoService.obtenerPorCategoria(categoriaId, pageable);
        } else if (estado != null && !estado.isEmpty()) {
            Producto.EstadoProducto estadoEnum = Producto.EstadoProducto.valueOf(estado);
            productos = productoService.obtenerPorEstado(estadoEnum, pageable);
        } else {
            productos = productoService.obtenerTodosPaginados(pageable);
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.obtenerActivas());
        model.addAttribute("titulo", "Gestión de Productos");
        model.addAttribute("activePage", "productos");

        return "admin/productos";
    }

    @GetMapping("/productos/nuevo")
    public String nuevoProducto(Model model) {
        Producto producto = new Producto();
        producto.setEstado(Producto.EstadoProducto.ACTIVO);
        producto.setDestacado(false);

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.obtenerActivas());
        model.addAttribute("titulo", "Nuevo Producto");
        model.addAttribute("activePage", "productos");

        return "admin/producto-form";
    }

    @GetMapping("/productos/editar/{id}")
    public String editarProducto(@PathVariable Long id, Model model) {
        Producto producto = productoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.obtenerActivas());
        model.addAttribute("titulo", "Editar Producto");
        model.addAttribute("activePage", "productos");

        return "admin/producto-form";
    }

    @PostMapping("/productos/guardar")
    public String guardarProducto(
            @ModelAttribute Producto producto,
            @RequestParam(value = "nuevasImagenes", required = false) MultipartFile[] nuevasImagenes,
            @RequestParam(value = "imagenesAEliminar", required = false) String imagenesAEliminar,
            RedirectAttributes redirectAttributes) {

        try {
            boolean esNuevo = (producto.getIdProducto() == null);

            // 1. Guardar el producto base
            // (Quitamos las variantes para guardarlas por separado)
            List<VarianteProducto> variantes = producto.getVariantes();
            producto.setVariantes(null);

            Producto productoGuardado = productoService.guardar(producto);

            // 2. Procesar variantes
            if (variantes != null && !variantes.isEmpty()) {
                for (VarianteProducto variante : variantes) {
                    variante.setProducto(productoGuardado); // Asignar el producto guardado
                    varianteService.guardar(variante);
                }
            }

            // 3. Eliminar imágenes marcadas
            if (imagenesAEliminar != null && !imagenesAEliminar.isEmpty()) {
                String[] idsAEliminar = imagenesAEliminar.split(",");
                for (String idStr : idsAEliminar) {
                    try {
                        Long idImagen = Long.parseLong(idStr.trim());
                        imagenService.eliminarImagen(idImagen);
                    } catch (NumberFormatException e) {
                        log.warn("ID de imagen inválido para eliminar: {}", idStr);
                    }
                }
            }

            // 4. Procesar nuevas imágenes (usando el método auxiliar)
            if (nuevasImagenes != null && nuevasImagenes.length > 0) {
                guardarImagenes(productoGuardado, nuevasImagenes);
            }

            String mensaje = esNuevo ? "Producto creado exitosamente" : "Producto actualizado exitosamente";
            redirectAttributes.addFlashAttribute("mensaje", mensaje);
            return "redirect:/admin/productos";

        } catch (Exception e) {
            log.error("Error al guardar producto: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Error al guardar el producto: " + e.getMessage());
            // Si es nuevo, redirige a 'nuevo', si está editando, redirige a 'editar'
            if (producto.getIdProducto() == null) {
                return "redirect:/admin/productos/nuevo";
            } else {
                return "redirect:/admin/productos/editar/" + producto.getIdProducto();
            }
        }
    }

    // --- MÉTODO eliminarProducto() ACTUALIZADO ---
    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            // Antes de eliminar el producto, eliminamos sus dependencias
            imagenService.eliminarPorProductoId(id);
            varianteService.eliminarPorProductoId(id);

            // Ahora sí eliminamos el producto
            productoService.eliminar(id);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Producto eliminado exitosamente");
        } catch (Exception e) {
            log.error("Error al eliminar producto: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo eliminar el producto: " + e.getMessage());
        }

        return "redirect:/admin/productos";
    }
    private void guardarImagenes(Producto producto, MultipartFile[] imagenes) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Obtenemos el último orden para continuar la secuencia
        int orden = imagenService.obtenerUltimoOrden(producto.getIdProducto()) + 1;

        for (MultipartFile imagen : imagenes) {
            if (imagen.isEmpty()) {
                continue;
            }

            // Validaciones (puedes añadir más)
            String contentType = imagen.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Solo se permiten archivos de imagen");
            }
            if (imagen.getSize() > 5 * 1024 * 1024) { // 5MB
                throw new IllegalArgumentException("La imagen no debe superar los 5MB");
            }

            String nombreOriginal = imagen.getOriginalFilename();
            String extension = "";
            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }
            String nombreArchivo = UUID.randomUUID().toString() + extension;

            Path rutaArchivo = uploadPath.resolve(nombreArchivo);
            Files.copy(imagen.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Crear registro en base de datos
            ImagenProducto productoImagen = new ImagenProducto ();
            productoImagen.setProducto(producto);
            productoImagen.setUrlImagen("/uploads/productos/" + nombreArchivo); // URL relativa
            productoImagen.setOrden(orden++);

            // Marcar como principal si es la primera imagen (y no hay otras)
            if (orden == 1) {
                productoImagen.setEsPrincipal(true);
            }

            imagenService.guardar(productoImagen);
        }
    }

    /**
     * Gestión de Pedidos
     */
    @GetMapping("/pedidos")
    public String gestionPedidos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String estado,
            Model model) {

        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Direction.DESC, "fechaPedido"));
        Page<Pedido> pedidos;

        if (estado != null && !estado.isEmpty()) {
            Pedido.EstadoPedido estadoPedido = Pedido.EstadoPedido.valueOf(estado);
            pedidos = pedidoService.obtenerPorEstado(estadoPedido, pageable);
            model.addAttribute("filtroEstado", estado);
        } else {
            pedidos = pedidoService.obtenerPorEstado(null, pageable);
        }

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("titulo", "Gestión de Pedidos");
        model.addAttribute("activePage", "pedidos");

        return "admin/pedidos";
    }

    @GetMapping("/pedidos/{id}")
    public String detallePedido(@PathVariable Long id, Model model) {
        Pedido pedido = pedidoService.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        model.addAttribute("pedido", pedido);
        model.addAttribute("titulo", "Detalle Pedido #" + pedido.getNumeroPedido());
        model.addAttribute("activePage", "pedidos");

        return "admin/pedido-detalle";
    }

    /**
     * Gestión de Usuarios
     */
    @GetMapping("/usuarios")
    public String gestionUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.obtenerTodos();

        model.addAttribute("usuarios", usuarios);
        model.addAttribute("titulo", "Gestión de Usuarios");
        model.addAttribute("activePage", "usuarios");

        return "admin/usuarios";
    }

    /**
     * Gestión de Categorías
     */
    @GetMapping("/categorias")
    public String gestionCategorias(Model model) {
        List<Categoria> categorias = categoriaService.obtenerTodas();

        model.addAttribute("categorias", categorias);
        model.addAttribute("titulo", "Gestión de Categorías");
        model.addAttribute("activePage", "categorias");

        return "admin/categorias";
    }

    /**
     * Reportes
     */
    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("titulo", "Reportes y Análisis");
        model.addAttribute("activePage", "reportes");

        return "admin/reportes";
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtener productos con stock bajo (menos de 10 unidades)
     */
    private List<Producto> obtenerProductosStockBajo() {
        List<Producto> todosLosProductos = productoService.obtenerTodos();

        return todosLosProductos.stream()
                .filter(p -> {
                    int stockTotal = p.getVariantes().stream()
                            .mapToInt(v -> v.getStock() != null ? v.getStock() : 0)
                            .sum();
                    return stockTotal > 0 && stockTotal < 10;
                })
                .limit(5)
                .collect(Collectors.toList());
    }

    /**
     * Calcular ventas por mes (últimos 6 meses)
     */
    private Map<String, BigDecimal> calcularVentasPorMes() {
        Map<String, BigDecimal> ventasPorMes = new LinkedHashMap<>();

        // Nombres de meses
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        LocalDateTime ahora = LocalDateTime.now();
        int mesActual = ahora.getMonthValue() - 1; // 0-indexed

        // Últimos 6 meses
        for (int i = 5; i >= 0; i--) {
            int mesIndex = (mesActual - i + 12) % 12;
            String nombreMes = meses[mesIndex];

            // TODO: Implementar query real para obtener ventas del mes
            // Por ahora valores de ejemplo
            BigDecimal ventasMes = BigDecimal.valueOf(Math.random() * 10000 + 5000);
            ventasPorMes.put(nombreMes, ventasMes);
        }

        return ventasPorMes;
    }

    /**
     * Calcular cantidad de pedidos por estado
     */
    private Map<String, Long> calcularPedidosPorEstado() {
        Map<String, Long> pedidosPorEstado = new LinkedHashMap<>();

        for (Pedido.EstadoPedido estado : Pedido.EstadoPedido.values()) {
            long cantidad = pedidoService.obtenerPorEstado(estado, PageRequest.of(0, 1))
                    .getTotalElements();
            pedidosPorEstado.put(estado.name(), cantidad);
        }

        return pedidosPorEstado;
    }
}