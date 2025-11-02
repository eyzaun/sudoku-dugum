package com.extremesudoku.presentation.auth

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.extremesudoku.R
import com.extremesudoku.presentation.theme.AppDimensions
import com.extremesudoku.presentation.theme.LocalThemeColors
import com.extremesudoku.util.LocaleManager

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var isSignUp by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val themeColors = LocalThemeColors.current
    val context = LocalContext.current
    val activity = context as? Activity
    
    var currentLanguage by remember { 
        mutableStateOf(LocaleManager.getSavedLanguage(context))
    }
    var showLanguageDialog by remember { mutableStateOf(false) }
    
    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = { language ->
                currentLanguage = language
                LocaleManager.setLocale(context, language)
                showLanguageDialog = false
                activity?.recreate()
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) {
            onNavigateToHome()
            viewModel.onNavigationComplete()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(
                message = it,
                duration = SnackbarDuration.Short
            )
            viewModel.clearError()
        }
    }

    Scaffold(
        containerColor = themeColors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            themeColors.background,
                            themeColors.cardBackground
                        )
                    )
                )
        ) {
            // Sol üst - Dil değiştirme butonu
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
                    .zIndex(10f)
                    .clickable { showLanguageDialog = true }
                    .background(
                        color = themeColors.surface.copy(alpha = 0.9f),
                        shape = MaterialTheme.shapes.small
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = stringResource(R.string.language),
                    tint = themeColors.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = currentLanguage.code.uppercase(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = themeColors.text
                )
            }
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top decorative element
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(
                            color = themeColors.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(30.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GridOn,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = themeColors.primary
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.text
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = if (isSignUp) {
                        stringResource(id = R.string.auth_create_account_title)
                    } else {
                        stringResource(id = R.string.auth_welcome_back_title)
                    },
                    fontSize = 16.sp,
                    color = themeColors.textSecondary,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(40.dp))

                // AnimatedContent for form transition
                AnimatedContent(
                    targetState = isSignUp,
                    transitionSpec = {
                        slideInVertically(
                            initialOffsetY = { 500 },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        ) togetherWith slideOutVertically(
                            targetOffsetY = { -500 },
                            animationSpec = tween(400, easing = FastOutSlowInEasing)
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { _ ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Email Field
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            label = { Text(stringResource(id = R.string.email)) },
                            placeholder = { Text(stringResource(id = R.string.auth_email_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.isLoading,
                            isError = uiState.error != null && uiState.email.isNotBlank(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = themeColors.primary
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColors.primary,
                                unfocusedBorderColor = themeColors.primary.copy(alpha = 0.3f),
                                focusedLabelColor = themeColors.primary,
                                cursorColor = themeColors.primary
                            )
                        )

                        // Password Field
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            label = { Text(stringResource(id = R.string.password)) },
                            placeholder = { Text(stringResource(id = R.string.auth_password_placeholder)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !uiState.isLoading,
                            isError = uiState.error != null && uiState.password.isNotBlank(),
                            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = themeColors.primary
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { showPassword = !showPassword }) {
                                    Icon(
                                        imageVector = if (showPassword) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = themeColors.textSecondary
                                    )
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColors.primary,
                                unfocusedBorderColor = themeColors.primary.copy(alpha = 0.3f),
                                focusedLabelColor = themeColors.primary,
                                cursorColor = themeColors.primary
                            )
                        )

                        // Error Message
                        if (uiState.error != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = themeColors.wrongCell.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = themeColors.wrongCell,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = uiState.error ?: "",
                                    color = themeColors.wrongCell,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Main Action Button
                        Button(
                            onClick = {
                                if (isSignUp) {
                                    viewModel.onSignUpClicked()
                                } else {
                                    viewModel.onSignInClicked()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = !uiState.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeColors.primary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = if (isSignUp) {
                                        stringResource(id = R.string.auth_create_account_button)
                                    } else {
                                        stringResource(id = R.string.sign_in)
                                    },
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Guest Mode Button
                        OutlinedButton(
                            onClick = { viewModel.onContinueAsGuestClicked() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                            enabled = !uiState.isLoading,
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        themeColors.primary.copy(alpha = 0.5f),
                                        themeColors.primary.copy(alpha = 0.3f)
                                    )
                                )
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = themeColors.primary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(id = R.string.auth_play_as_guest),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Toggle Sign In / Sign Up
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isSignUp) {
                            stringResource(id = R.string.auth_already_have_account)
                        } else {
                            stringResource(id = R.string.auth_dont_have_account)
                        },
                        color = themeColors.textSecondary,
                        fontSize = 14.sp
                    )
                    TextButton(
                        onClick = { isSignUp = !isSignUp }
                    ) {
                        Text(
                            text = if (isSignUp) {
                                stringResource(id = R.string.sign_in)
                            } else {
                                stringResource(id = R.string.sign_up)
                            },
                            color = themeColors.primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: LocaleManager.Language,
    onLanguageSelected: (LocaleManager.Language) -> Unit,
    onDismiss: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.language_selection_title),
                color = themeColors.text
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
            ) {
                LocaleManager.Language.values().forEach { language ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentLanguage == language) 
                                themeColors.primary.copy(alpha = 0.1f) 
                            else 
                                themeColors.cardBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.spacingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = themeColors.text
                            )
                            
                            if (currentLanguage == language) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = themeColors.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = themeColors.primary
                )
            }
        },
        containerColor = themeColors.surface
    )
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: LocaleManager.Language,
    onLanguageSelected: (LocaleManager.Language) -> Unit,
    onDismiss: () -> Unit
) {
    val themeColors = LocalThemeColors.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.language_selection_title),
                color = themeColors.text
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppDimensions.spacingSmall)
            ) {
                LocaleManager.Language.values().forEach { language ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageSelected(language) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentLanguage == language) 
                                themeColors.primary.copy(alpha = 0.1f) 
                            else 
                                themeColors.cardBackground
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(AppDimensions.spacingMedium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = language.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                color = themeColors.text
                            )
                            
                            if (currentLanguage == language) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = themeColors.primary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(R.string.cancel),
                    color = themeColors.primary
                )
            }
        },
        containerColor = themeColors.surface
    )
}
