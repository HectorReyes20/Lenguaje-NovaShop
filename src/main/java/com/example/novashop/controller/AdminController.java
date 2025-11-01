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

            // ========================================
            // VALIDACIÓN 1: Datos básicos del producto
            // ========================================
            if (producto.getNombre() == null || producto.getNombre().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre del producto es obligatorio");
                return redirigirFormulario(esNuevo, producto.getIdProducto());
            }

            if (producto.getPrecioBase() == null || producto.getPrecioBase().compareTo(BigDecimal.ZERO) <= 0) {
                redirectAttributes.addFlashAttribute("error", "El precio base debe ser mayor a 0");
                return redirigirFormulario(esNuevo, producto.getIdProducto());
            }

            if (producto.getCategoria() == null || producto.getCategoria().getIdCategoria() == null) {
                redirectAttributes.addFlashAttribute("error", "Debes seleccionar una categoría");
                return redirigirFormulario(esNuevo, producto.getIdProducto());
            }

            // ========================================
            // VALIDACIÓN 2: Variantes (al menos 1)
            // ========================================
            List<VarianteProducto> variantes = producto.getVariantes();

            if (variantes == null || variantes.isEmpty()) {
                redirectAttributes.addFlashAttribute("error",
                        "Debes agregar al menos una variante (talla, color, stock)");
                return redirigirFormulario(esNuevo, producto.getIdProducto());
            }

            // ========================================
            // VALIDACIÓN 3: Datos completos de variantes
            // ========================================
            for (int i = 0; i < variantes.size(); i++) {
                VarianteProducto variante = variantes.get(i);

                // Validar talla
                if (variante.getTalla() == null || variante.getTalla().trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error",
                            "La variante #" + (i+1) + " debe tener una talla");
                    return redirigirFormulario(esNuevo, producto.getIdProducto());
                }

                // Validar color
                if (variante.getColor() == null || variante.getColor().trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error",
                            "La variante #" + (i+1) + " debe tener un color");
                    return redirigirFormulario(esNuevo, producto.getIdProducto());
                }

                // Validar SKU
                if (variante.getCodigoSku() == null || variante.getCodigoSku().trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error",
                            "La variante #" + (i+1) + " debe tener un código SKU");
                    return redirigirFormulario(esNuevo, producto.getIdProducto());
                }

                // Validar SKU único (no duplicado)
                if (!esNuevo || variante.getIdVariante() != null) {
                    // Al editar, verificar que el SKU no esté en uso por OTRA variante
                    Optional<VarianteProducto> skuExistente = varianteService.buscarPorSku(variante.getCodigoSku());
                    if (skuExistente.isPresent() &&
                            !skuExistente.get().getIdVariante().equals(variante.getIdVariante())) {
                        redirectAttributes.addFlashAttribute("error",
                                "El SKU '" + variante.getCodigoSku() + "' ya está en uso");
                        return redirigirFormulario(esNuevo, producto.getIdProducto());
                    }
                }

                // Validar stock
                if (variante.getStock() == null || variante.getStock() < 0) {
                    redirectAttributes.addFlashAttribute("error",
                            "La variante #" + (i+1) + " debe tener un stock válido (mínimo 0)");
                    return redirigirFormulario(esNuevo, producto.getIdProducto());
                }
            }

            // ========================================
            // VALIDACIÓN 4: Validar precio de oferta (si existe)
            // ========================================
            if (producto.getPrecioOferta() != null &&
                    producto.getPrecioOferta().compareTo(producto.getPrecioBase()) >= 0) {
                redirectAttributes.addFlashAttribute("error",
                        "El precio de oferta debe ser menor al precio base");
                return redirigirFormulario(esNuevo, producto.getIdProducto());
            }

            // ========================================
            // VALIDACIÓN 5: Imágenes (al menos 1)
            // Solo para productos nuevos o si se eliminan todas las existentes
            // ========================================
            boolean tieneImagenesNuevas = nuevasImagenes != null && nuevasImagenes.length > 0 &&
                    Arrays.stream(nuevasImagenes).anyMatch(img -> !img.isEmpty());

            if (!esNuevo) {
                // Al editar: verificar que no se quede sin imágenes
                List<ImagenProducto> imagenesActuales = imagenService.obtenerPorProducto(producto.getIdProducto());
                int cantidadAEliminar = 0;

                if (imagenesAEliminar != null && !imagenesAEliminar.isEmpty()) {
                    cantidadAEliminar = imagenesAEliminar.split(",").length;
                }

                int imagenesRestantes = imagenesActuales.size() - cantidadAEliminar;

                if (imagenesRestantes <= 0 && !tieneImagenesNuevas) {
                    redirectAttributes.addFlashAttribute("error",
                            "El producto debe tener al menos una imagen");
                    return redirigirFormulario(esNuevo, producto.getIdProducto());
                }
            } else {
                // Producto nuevo: debe tener al menos 1 imagen
                if (!tieneImagenesNuevas) {
                    redirectAttributes.addFlashAttribute("error",
                            "Debes agregar al menos una imagen al producto");
                    return redirigirFormulario(esNuevo, producto.getIdProducto());
                }
            }

            // ========================================
            // TODO VALIDADO ✅ - Proceder a guardar
            // ========================================

            log.info("Guardando producto: {} (Nuevo: {})", producto.getNombre(), esNuevo);

            // 1. Guardar producto base (sin variantes para evitar cascade)
            producto.setVariantes(null);
            Producto productoGuardado = productoService.guardar(producto);
            log.info("Producto guardado con ID: {}", productoGuardado.getIdProducto());

            // 2. Guardar variantes
            for (VarianteProducto variante : variantes) {
                variante.setProducto(productoGuardado);
                varianteService.guardar(variante);
            }
            log.info("Guardadas {} variantes", variantes.size());

            // 3. Eliminar imágenes marcadas
            if (imagenesAEliminar != null && !imagenesAEliminar.isEmpty()) {
                String[] idsAEliminar = imagenesAEliminar.split(",");
                int eliminadas = 0;

                for (String idStr : idsAEliminar) {
                    try {
                        Long idImagen = Long.parseLong(idStr.trim());
                        imagenService.eliminarImagen(idImagen);
                        eliminadas++;
                    } catch (NumberFormatException e) {
                        log.warn("ID de imagen inválido para eliminar: {}", idStr);
                    } catch (Exception e) {
                        log.error("Error al eliminar imagen {}: {}", idStr, e.getMessage());
                    }
                }
                log.info("Eliminadas {} imágenes", eliminadas);
            }

            // 4. Guardar nuevas imágenes
            if (tieneImagenesNuevas) {
                int imagenesGuardadas = guardarImagenes(productoGuardado, nuevasImagenes);
                log.info("Guardadas {} imágenes nuevas", imagenesGuardadas);
            }

            // ========================================
            // ÉXITO - Redirigir con mensaje
            // ========================================
            String mensaje = esNuevo
                    ? "Producto '" + productoGuardado.getNombre() + "' creado exitosamente"
                    : "Producto '" + productoGuardado.getNombre() + "' actualizado exitosamente";

            redirectAttributes.addFlashAttribute("mensaje", mensaje);
            return "redirect:/admin/productos";

        } catch (IllegalArgumentException e) {
            // Errores de validación
            log.error("Error de validación: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return redirigirFormulario(
                    producto.getIdProducto() == null,
                    producto.getIdProducto()
            );

        } catch (Exception e) {
            // Errores inesperados
            log.error("Error inesperado al guardar producto: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error inesperado. Por favor, inténtalo de nuevo.");
            return redirigirFormulario(
                    producto.getIdProducto() == null,
                    producto.getIdProducto()
            );
        }
    }
    private String redirigirFormulario(boolean esNuevo, Long idProducto) {
        if (esNuevo) {
            return "redirect:/admin/productos/nuevo";
        } else {
            return "redirect:/admin/productos/editar/" + idProducto;
        }
    }

    @PostMapping("/productos/eliminar/{id}")
    public String eliminarProducto(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            // ========================================
            // PASO 1: Verificar que el producto existe
            // ========================================
            Producto producto = productoService.obtenerPorId(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

            log.info("Iniciando eliminación del producto ID: {} - {}", id, producto.getNombre());

            // ========================================
            // PASO 2: Eliminar IMÁGENES primero
            // ========================================
            // ¿Por qué primero? Porque además de borrar registros de la BD,
            // también eliminamos los archivos físicos del servidor

            List<ImagenProducto> imagenes = imagenService.obtenerPorProducto(id);
            log.info("Eliminando {} imágenes del producto", imagenes.size());

            imagenService.eliminarPorProductoId(id);

            // ========================================
            // PASO 3: Eliminar VARIANTES después
            // ========================================
            // Las variantes solo tienen registros en BD, no archivos físicos

            List<VarianteProducto> variantes = varianteService.obtenerPorProducto(id);
            log.info("Eliminando {} variantes del producto", variantes.size());

            varianteService.eliminarPorProductoId(id);

            // ========================================
            // PASO 4: Ahora SÍ eliminar el PRODUCTO
            // ========================================
            // Al no tener dependencias, se elimina sin problemas

            productoService.eliminar(id);
            log.info("Producto eliminado exitosamente");

            // ========================================
            // PASO 5: Mensaje de éxito
            // ========================================
            redirectAttributes.addFlashAttribute("mensaje",
                    "Producto '" + producto.getNombre() + "' eliminado exitosamente");

        } catch (RuntimeException e) {
            // Error controlado (ej: producto no encontrado)
            log.error("Error al eliminar producto: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "No se pudo eliminar el producto: " + e.getMessage());

        } catch (Exception e) {
            // Error inesperado (ej: problema de BD, archivos, etc)
            log.error("Error inesperado al eliminar producto ID {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Ocurrió un error inesperado al eliminar el producto");
        }

        return "redirect:/admin/productos";
    }
    private int guardarImagenes(Producto producto, MultipartFile[] imagenes) throws IOException {
        Path uploadPath = Paths.get(UPLOAD_DIR);

        // Crear directorio si no existe
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
            log.info("Directorio de uploads creado: {}", uploadPath);
        }

        // Obtener el último orden para continuar la secuencia
        int orden = imagenService.obtenerUltimoOrden(producto.getIdProducto()) + 1;
        int imagenesGuardadas = 0;

        for (MultipartFile imagen : imagenes) {
            // Saltar archivos vacíos
            if (imagen.isEmpty()) {
                continue;
            }

            // ========================================
            // VALIDACIÓN: Tipo de archivo
            // ========================================
            String contentType = imagen.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                log.warn("Archivo rechazado (no es imagen): {}", imagen.getOriginalFilename());
                throw new IllegalArgumentException(
                        "El archivo '" + imagen.getOriginalFilename() + "' no es una imagen válida"
                );
            }

            // ========================================
            // VALIDACIÓN: Tamaño de archivo (5MB máximo)
            // ========================================
            long maxSize = 5 * 1024 * 1024; // 5MB en bytes
            if (imagen.getSize() > maxSize) {
                log.warn("Archivo rechazado (tamaño excedido): {} - {} bytes",
                        imagen.getOriginalFilename(), imagen.getSize());
                throw new IllegalArgumentException(
                        "La imagen '" + imagen.getOriginalFilename() +
                                "' excede el tamaño máximo de 5MB"
                );
            }

            // ========================================
            // GUARDAR: Archivo físico
            // ========================================
            String nombreOriginal = imagen.getOriginalFilename();
            String extension = "";

            if (nombreOriginal != null && nombreOriginal.contains(".")) {
                extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
            }

            // Generar nombre único: UUID + extensión
            String nombreArchivo = UUID.randomUUID().toString() + extension;
            Path rutaArchivo = uploadPath.resolve(nombreArchivo);

            Files.copy(imagen.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            log.info("Archivo guardado: {}", nombreArchivo);

            // ========================================
            // GUARDAR: Registro en base de datos
            // ========================================
            ImagenProducto productoImagen = ImagenProducto.builder()
                    .producto(producto)
                    .urlImagen("/uploads/productos/" + nombreArchivo)
                    .orden(orden++)
                    .esPrincipal(orden == 1) // La primera imagen es principal
                    .build();

            imagenService.guardar(productoImagen);
            imagenesGuardadas++;
        }

        return imagenesGuardadas;
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

        // Calcular pedidos pendientes
        Long pedidosPendientes = pedidoService.contarPorEstado(Pedido.EstadoPedido.PENDIENTE);

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
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

    @PostMapping("/pedidos/{id}/cambiar-estado")
    public String cambiarEstadoPedido(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            RedirectAttributes redirectAttributes) {

        try {
            Pedido.EstadoPedido estado = Pedido.EstadoPedido.valueOf(nuevoEstado);

            pedidoService.actualizarEstado(id, estado);

            log.info("Estado del pedido {} cambiado a {}", id, estado);

            redirectAttributes.addFlashAttribute("mensaje",
                    "Estado del pedido actualizado a " + estado);

        } catch (IllegalArgumentException e) {
            log.error("Estado inválido: {}", nuevoEstado);
            redirectAttributes.addFlashAttribute("error",
                    "Estado inválido");
        } catch (Exception e) {
            log.error("Error al cambiar estado del pedido {}: {}", id, e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error",
                    "Error al actualizar el estado del pedido");
        }

        return "redirect:/admin/pedidos/" + id;
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