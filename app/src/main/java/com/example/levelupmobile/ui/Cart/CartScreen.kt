package cl.duoc.levelupmobile.ui.cart

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupmobile.data.local.entities.CartItem
import cl.duoc.levelupmobile.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: CartViewModel = viewModel()
) {
    // 1. Recolectamos los estados del ViewModel (Backend AWS)
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotal by viewModel.subtotal.collectAsState()
    val discount by viewModel.discount.collectAsState()
    val total by viewModel.total.collectAsState()

    // ELIMINADO: val currentUser... (Ya no lo necesitamos aquí)

    var showCheckoutDialog by remember { mutableStateOf(false) }

    // DIÁLOGO DE COMPRA EXITOSA
    if (showCheckoutDialog) {
        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            icon = {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreen) // Usé NeonGreen que tienes en tu tema
            },
            title = { Text("¡Compra exitosa!") },
            text = {
                Column {
                    Text("Tu pedido ha sido procesado en el servidor.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total pagado: $${total.toString().reversed().chunked(3).joinToString(".").reversed()} CLP")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearCart() // Borra en AWS
                        showCheckoutDialog = false
                        onNavigateToHome()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Text("VOLVER AL INICIO")
                }
            },
            containerColor = DarkGray
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Carrito de Compras")
                        Text(
                            "${cartItems.size} productos",
                            style = MaterialTheme.typography.bodySmall,
                            color = LightGray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGray,
                    titleContentColor = White,
                    navigationIconContentColor = ElectricBlue
                )
            )
        },
        containerColor = Black
    ) { paddingValues ->
        if (cartItems.isEmpty()) {
            // VISTA VACÍA
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = LightGray,
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Tu carrito está vacío",
                        style = MaterialTheme.typography.titleLarge,
                        color = LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "¡Los datos vienen desde AWS!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = LightGray.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onNavigateToHome,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("EXPLORAR PRODUCTOS")
                    }
                }
            }
        } else {
            // LISTA DE PRODUCTOS
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Lista scrolleable
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cartItems, key = { it.id }) { item ->
                        CartItemCard(
                            cartItem = item,
                            onQuantityChange = { newQuantity ->
                                // Esto ahora llama al ViewModel que llama a AWS
                                // (Nota: Tu backend actual solo tiene "agregar" o "borrar",
                                // para simplificar, si cambian cantidad aquí podrías tener que ajustar lógica,
                                // pero por ahora el botón eliminar funciona perfecto).
                                if (newQuantity <= 0) viewModel.removeItem(item)
                            },
                            onRemove = {
                                viewModel.removeItem(item)
                            }
                        )
                    }
                }

                // TARJETA DE RESUMEN (TOTALES)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            "Resumen de compra",
                            style = MaterialTheme.typography.titleLarge,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Subtotal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Subtotal", color = LightGray)
                            Text(
                                "$${subtotal.toString().reversed().chunked(3).joinToString(".").reversed()} CLP",
                                color = White
                            )
                        }

                        // Discount (Solo se muestra si es > 0)
                        if (discount > 0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.LocalOffer, // Cambié a LocalOffer si Discount no existe en Material Icons default
                                        contentDescription = null,
                                        tint = NeonGreen,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Descuento Duoc", color = NeonGreen)
                                }
                                Text(
                                    "-$${discount.toString().reversed().chunked(3).joinToString(".").reversed()} CLP",
                                    color = NeonGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = LightGray.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // Total Final
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total",
                                style = MaterialTheme.typography.titleLarge,
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                "$${total.toString().reversed().chunked(3).joinToString(".").reversed()} CLP",
                                style = MaterialTheme.typography.headlineMedium,
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón Pagar
                        Button(
                            onClick = { showCheckoutDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = Black
                            )
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "PROCESAR COMPRA",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// TARJETA DE ITEM INDIVIDUAL
@Composable
fun CartItemCard(
    cartItem: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkGray),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    cartItem.productName,
                    style = MaterialTheme.typography.titleMedium,
                    color = White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "$${cartItem.productPrice.toString().reversed().chunked(3).joinToString(".").reversed()} CLP",
                    style = MaterialTheme.typography.bodyLarge,
                    color = ElectricBlue
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Controles de Cantidad
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Botón Borrar (Simplificado para AWS)
                IconButton(
                    onClick = onRemove,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = ErrorRed.copy(alpha = 0.2f),
                        contentColor = ErrorRed
                    )
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}