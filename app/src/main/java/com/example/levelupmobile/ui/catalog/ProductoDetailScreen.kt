package cl.duoc.levelupmobile.ui.catalog

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.levelupmobile.data.local.database.AppDatabase
import cl.duoc.levelupmobile.data.local.datastore.PreferencesManager
import cl.duoc.levelupmobile.data.local.entities.CartItem
import cl.duoc.levelupmobile.data.repository.CartRepository
import cl.duoc.levelupmobile.ui.theme.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    productCode: String,
    onNavigateBack: () -> Unit,
    onNavigateToCart: () -> Unit,
    viewModel: CatalogViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val database = AppDatabase.getDatabase(context)
    val cartRepository = CartRepository(database.cartDao())
    val preferencesManager = PreferencesManager(context)

    var product by remember { mutableStateOf<cl.duoc.levelupmobile.data.local.entities.Product?>(null) }
    var quantity by remember { mutableStateOf(1) }
    var showSnackbar by remember { mutableStateOf(false) }

    LaunchedEffect(productCode) {
        product = viewModel.getProductByCode(productCode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Producto") },
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
        containerColor = Black, snackbarHost = {
            if (showSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = onNavigateToCart) {
                            Text("VER CARRITO", color = ElectricBlue)
                        }
                    },
                    containerColor = DarkGray,
                    contentColor = White,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Producto agregado al carrito")
                }
            }
        }
    ) { paddingValues ->
        product?.let { prod ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
// Product Header
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
// Category badge
                        Surface(
                            color = ElectricBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                prod.category,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                color = ElectricBlue,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            prod.name,
                            style = MaterialTheme.typography.headlineMedium,
                            color = White,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    if (index < prod.rating.toInt()) Icons.Default.Star else Icons.Default.StarOutline,
                                    contentDescription = null,
                                    tint = NeonGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "${prod.rating} (${prod.reviewCount} reseñas)",
                                color = LightGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Divider(color = LightGray.copy(alpha = 0.3f))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Price
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Precio",
                                    color = LightGray,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    "$${
                                        prod.price.toString().reversed().chunked(3)
                                            .joinToString(".").reversed()
                                    } CLP",
                                    style = MaterialTheme.typography.headlineLarge,
                                    color = ElectricBlue,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Stock indicator
                            Surface(
                                color = if (prod.stock > 0) SuccessGreen.copy(alpha = 0.2f) else ErrorRed.copy(
                                    alpha = 0.2f
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 12.dp,
                                        vertical = 8.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (prod.stock > 0) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                        contentDescription = null,
                                        tint = if (prod.stock > 0) SuccessGreen else ErrorRed,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        if (prod.stock > 0) "Stock: ${prod.stock}" else "Sin stock",
                                        color = if (prod.stock > 0) SuccessGreen else ErrorRed,
                                        style = MaterialTheme.typography.labelMedium
                                    )
                                }
                            }
                        }
                    }
                }

                // Description
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Description,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Descripción",
                                style = MaterialTheme.typography.titleMedium,
                                color = White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            prod.description,
                            color = LightGray,
                            style = MaterialTheme.typography.bodyMedium,
                            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quantity Selector
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = DarkGray),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Cantidad",
                            style = MaterialTheme.typography.titleMedium,
                            color = White
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(
                                onClick = { if (quantity > 1) quantity-- },
                                enabled = quantity > 1,
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = ElectricBlue.copy(alpha = 0.2f),
                                    contentColor = ElectricBlue
                                )
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Disminuir")
                            }

                            Text(
                                quantity.toString(),
                                style = MaterialTheme.typography.headlineSmall,
                                color = White,
                                fontWeight = FontWeight.Bold
                            )

                            IconButton(
                                onClick = { if (quantity < prod.stock) quantity++ },
                                enabled = quantity < prod.stock,
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = ElectricBlue.copy(alpha = 0.2f),
                                    contentColor = ElectricBlue
                                )
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Aumentar")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Add to Cart Button
                Button(
                    onClick = {
                        scope.launch {
                            val userId = preferencesManager.userIdFlow.firstOrNull()
                            userId?.let { id ->
                                val cartItem = CartItem(
                                    productCode = prod.code,
                                    productName = prod.name,
                                    productPrice = prod.price,
                                    quantity = quantity,
                                    userId = id
                                )
                                cartRepository.addToCart(cartItem)
                                showSnackbar = true
                                kotlinx.coroutines.delay(3000)
                                showSnackbar = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonGreen,
                        contentColor = Black
                    ),
                    enabled = prod.stock > 0
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (prod.stock > 0) "AGREGAR AL CARRITO" else "SIN STOCK",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = ElectricBlue)
        }
    }
}