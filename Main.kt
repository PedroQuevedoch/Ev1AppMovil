import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

// Clases de datos
data class Producto(
    val id: Int,
    val nombre: String,
    val precio: Double,
    val stock: Int
)

// Clase sellada para estados
sealed class EstadoCompra {
    object Exitosa : EstadoCompra()
    class Error(val mensaje: String) : EstadoCompra()
}

// Clase principal de la tienda
class TiendaReposteria {
    private val productos = listOf(
        Producto(1, "Pastel Chocolate", 250.0, 10),
        Producto(2, "Galletas", 80.0, 20),
        Producto(3, "Pan Dulce", 35.0, 15),
        Producto(4, "Flan", 120.0, 8),
        Producto(5, "Café", 45.0, 30)
    )

    fun mostrarMenu() {
        println("\n===TIENDA DE REPOSTERÍA ===")
        println("1. Ver productos")
        println("2. Comprar")
        println("3. Salir")
        print("Seleccione: ")
    }

    fun mostrarProductos() {
        println("\n--- PRODUCTOS ---")
        productos.forEach { producto ->
            println("${producto.id}. ${producto.nombre} - $${producto.precio} (Stock: ${producto.stock})")
        }
    }

    suspend fun procesarCompra(idProducto: Int, cantidad: Int): EstadoCompra {
        try {
            println("Procesando compra...")
            delay(1500)

            val producto = productos.find { it.id == idProducto }

            return when {
                producto == null -> EstadoCompra.Error("Producto no existe")
                producto.stock < cantidad -> EstadoCompra.Error("Stock insuficiente")
                else -> {
                    val total = producto.precio * cantidad
                    println("Total: $${total}")
                    EstadoCompra.Exitosa
                }
            }
        } catch (e: Exception) {
            return EstadoCompra.Error("Error: ${e.message}")
        }
    }

    fun existeProducto(id: Int): Boolean = productos.any { it.id == id }
}

// Función principal
fun main() = runBlocking {
    val tienda = TiendaReposteria()
    var ejecutando = true

    println("¡Bienvenido a la Tienda de Repostería!")

    while (ejecutando) {
        tienda.mostrarMenu()

        when (readln()) {
            "1" -> tienda.mostrarProductos()
            "2" -> {
                tienda.mostrarProductos()

                print("ID del producto: ")
                val id = readln().toIntOrNull() ?: 0

                print("Cantidad: ")
                val cantidad = readln().toIntOrNull() ?: 0

                if (id == 0 || cantidad == 0) {
                    println("❌ Datos inválidos")
                    continue
                }

                if (!tienda.existeProducto(id)) {
                    println("❌ Producto no existe")
                    continue
                }

                when (val resultado = tienda.procesarCompra(id, cantidad)) {
                    is EstadoCompra.Exitosa -> println("✅ Compra exitosa!")
                    is EstadoCompra.Error -> println("❌ ${resultado.mensaje}")
                }
            }
            "3" -> {
                ejecutando = false
                println("👋 ¡Gracias por visitarnos!")
            }
            else -> println("❌ Opción inválida")
        }
    }
}