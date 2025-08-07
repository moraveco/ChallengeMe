import android.content.Context
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable

@Composable
fun PermissionHandler(
    context: Context,
    permissions: Array<String>,
    onPermissionsGranted: () -> Unit
) {
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val allGranted = result.all { it.value }
        if (allGranted) {
            onPermissionsGranted()
        } else {
            Toast.makeText(context, "Please grant all permissions to continue", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(Unit) {
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }

        if (notGranted.isEmpty()) {
            onPermissionsGranted()
        } else {
            launcher.launch(notGranted.toTypedArray())
        }
    }
}

