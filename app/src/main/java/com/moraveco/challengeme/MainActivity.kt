package com.moraveco.challengeme

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAddAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.rentme.ui.profile.UpdatePasswordScreen
import com.google.android.gms.wallet.PaymentData
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.moraveco.challengeme.data.Post
import com.moraveco.challengeme.data.ProfileUser
import com.moraveco.challengeme.data.UpdateToken
import com.moraveco.challengeme.data.User
import com.moraveco.challengeme.data.toUser
import com.moraveco.challengeme.nav.Screens
import com.moraveco.challengeme.ui.add.AddPostScreen
import com.moraveco.challengeme.ui.contact.ContactScreen
import com.moraveco.challengeme.ui.home.HomeScreen
import com.moraveco.challengeme.ui.home.HomeViewModel
import com.moraveco.challengeme.ui.home.MainViewModel
import com.moraveco.challengeme.ui.login.LoginScreen
import com.moraveco.challengeme.ui.login.reset_password.ResetPasswordScreen
import com.moraveco.challengeme.ui.posts.PostScreen
import com.moraveco.challengeme.ui.posts.PostViewModel
import com.moraveco.challengeme.ui.profile.MenuScreen
import com.moraveco.challengeme.ui.profile.ProfileScreen
import com.moraveco.challengeme.ui.profile.UserProfileScreen
import com.moraveco.challengeme.ui.profile.donate.DonationScreen
import com.moraveco.challengeme.ui.profile.edit.EditProfileScreen
import com.moraveco.challengeme.ui.profile.edit.EditProfileViewModel
import com.moraveco.challengeme.ui.register.RegisterScreen
import com.moraveco.challengeme.ui.register.SecondRegisterScreen
import com.moraveco.challengeme.ui.requests.FriendViewModel
import com.moraveco.challengeme.ui.requests.RequestsScreen
import com.moraveco.challengeme.ui.scoreboard.ScoreboardScreen
import com.moraveco.challengeme.ui.scoreboard.ScoreboardViewModel
import com.moraveco.challengeme.ui.search.SearchScreen
import com.moraveco.challengeme.ui.theme.Background
import com.moraveco.challengeme.ui.theme.Bars
import com.moraveco.challengeme.ui.theme.ChallengeMeTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import kotlin.reflect.KClass

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<HomeViewModel>()
    private val mainViewModel by viewModels<MainViewModel>()
    private val postViewModel by viewModels<PostViewModel>()
    private val friendViewModel by viewModels<FriendViewModel>()
    private val scoreboardViewModel by viewModels<ScoreboardViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FirebaseApp.initializeApp(this)


        setContent {
            ChallengeMeTheme(darkTheme = true) {

                val navController = rememberNavController()
                val authState by viewModel.authState.collectAsStateWithLifecycle(initialValue = null)
                FirebaseMessaging.getInstance()
                    .token
                    .addOnSuccessListener { token ->
                        if (!authState?.uid.isNullOrEmpty()) {
                            mainViewModel.updateToken(UpdateToken(authState?.uid!!, token))

                        }
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();

                    }
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                var showBottomBar by remember { mutableStateOf(true) }

                navBackStackEntry?.destination?.let { currentDestination ->
                    showBottomBar = (
                            currentDestination.hasRoute(Screens.Home::class) ||
                                    currentDestination.hasRoute(Screens.Add::class) ||
                                    currentDestination.hasRoute(Screens.Scoreboard::class) ||
                                    currentDestination.hasRoute(Screens.Profile::class)
                            // currentDestination.hasRoute(Screens.EditProfile::class)

                            )
                }




                Scaffold(
                    bottomBar = {
                        if (showBottomBar) CustomBottomNavigationBar(navController)
                    },
                    containerColor = Background
                ) {
                    Navigate(navController, modifier = Modifier.padding(it))
                    HandleAuthState(authState?.uid, navController)
                }
            }

        }

    }

    @Composable
    fun Navigate(navHostController: NavHostController, modifier: Modifier = Modifier) {
        val auth by viewModel.authState.collectAsStateWithLifecycle(User.empty())
        val userId = auth.uid
        val name = auth.name
        val email = auth.email

        NavHost(
            navController = navHostController,
            startDestination = Screens.Home,
            modifier = modifier
        ) {

            composable<Screens.Register> {
                RegisterScreen(navController = navHostController)
            }

            composable<Screens.SecondRegister> {
                val args = it.toRoute<Screens.SecondRegister>()
                SecondRegisterScreen(navHostController, args.email, args.password)
            }

            composable<Screens.Login> {
                LoginScreen(navController = navHostController)
            }

            composable<Screens.ForgotPassword> {
                ResetPasswordScreen(navHostController)
            }

            composable<Screens.Home> {


                HomeScreen(
                    name = name,
                    navController = navHostController,
                    myUid = userId
                )
            }

            composable<Screens.Add> {
                LaunchedEffect(userId) {
                    postViewModel.getPostsById(userId)
                }

                val posts by postViewModel.profilePosts.collectAsState()
                val post = posts.find { LocalDate.parse(it.time) == LocalDate.now() }

                AddPostScreen(navHostController, userId, post)
            }

            composable<Screens.Scoreboard> {
                LaunchedEffect(Unit) {
                    scoreboardViewModel.getToday()
                    scoreboardViewModel.getGlobal()
                    scoreboardViewModel.getFriends(userId)
                }

                val today by scoreboardViewModel.today.collectAsState()
                val global by scoreboardViewModel.global.collectAsState()
                val friends by scoreboardViewModel.friends.collectAsState()

                ScoreboardScreen(today, global, friends)
            }

            composable<Screens.Profile> {
                LaunchedEffect(userId) {
                    mainViewModel.deleteUserState()
                    mainViewModel.getUserById(userId)
                    postViewModel.getPostsById(userId)
                }

                val user by mainViewModel.user.collectAsStateWithLifecycle()
                val posts by postViewModel.profilePosts.collectAsStateWithLifecycle()

                ProfileScreen(user, posts, navHostController)
            }

            composable<Screens.Post> {
                val args = it.toRoute<Screens.Post>()
                PostScreen(email, args.postId, userId, navHostController)
            }

            composable<Screens.Search> {
                LaunchedEffect(Unit) {
                    mainViewModel.fetchAllUsersData()
                }

                val users by mainViewModel.users.collectAsStateWithLifecycle()
                SearchScreen(navHostController, users)
            }

            composable<Screens.UserProfile> {
                val args = it.toRoute<Screens.UserProfile>()

                LaunchedEffect(args.userId) {
                    mainViewModel.deleteUserState()
                    mainViewModel.getUserById(args.userId)
                    postViewModel.getPostsById(args.userId)
                    friendViewModel.getFriends(userId)
                }

                val user by mainViewModel.user.collectAsState()
                val posts by postViewModel.profilePosts.collectAsState()
                val friend = friendViewModel.getMyFriendRequest(userId, args.userId)

                UserProfileScreen(
                    name = name,
                    user = user,
                    posts = posts,
                    myUid = userId,
                    friend = friend,
                    navController = navHostController,
                    acceptRequest = friendViewModel::acceptRequest,
                    followUser = friendViewModel::addFriend,
                    deleteFriend = friendViewModel::deleteFriend,
                    blockUser = mainViewModel::blockUser
                )
            }

            composable<Screens.Request> {
                LaunchedEffect(userId) {
                    friendViewModel.getFriends(userId)
                }

                val friends by friendViewModel.friends.collectAsState()
                RequestsScreen(
                    name,
                    friends,
                    navHostController,
                    friendViewModel::acceptRequest,
                    friendViewModel::deleteFriend
                )
            }

            composable<Screens.Menu> {
                LaunchedEffect(userId) {
                    mainViewModel.getUserById(userId)
                }

                val user by mainViewModel.user.collectAsState()
                MenuScreen(navHostController, user.toUser(), viewModel::deleteUser)
            }

            composable<Screens.EditProfile> {
                LaunchedEffect(userId) {
                    mainViewModel.getUserById(userId)
                }

                val user by mainViewModel.user.collectAsState()
                EditProfileScreen(user.toUser(), navHostController, logout = viewModel::deleteUser)
            }

            composable<Screens.EditPassword> {
                UpdatePasswordScreen(navHostController, userId)
            }

            composable<Screens.Donate> {
                DonationScreen({navHostController.popBackStack()})
            }

            composable<Screens.Contact> {
                ContactScreen({navHostController.popBackStack()}, email = email)
            }
        }
    }
}


@Composable
private fun HandleAuthState(authState: String?, navController: NavController) {
    LaunchedEffect(authState) {
        if (authState != null && authState == "-1") {
            navController.navigate(Screens.Login)
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    Row(modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)).background(
        Bars).padding(horizontal = 15.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("ChallengeMe+", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
        Row {
            IconButton(onClick = { navController.navigate(Screens.Request) }) {
                Icon(Icons.Default.PersonAddAlt, contentDescription = "Profile", tint = Color.White)
            }

            IconButton(onClick = { navController.navigate(Screens.Search)}) {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            }

        }

    }

}



@Composable
fun CustomBottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = Bars,
                shape = RoundedCornerShape(24.dp)
            ),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val navItems = listOf(
            BottomNavItem.Home,
            BottomNavItem.Add,
            BottomNavItem.Trending,
            BottomNavItem.Profile
        )

        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Zabrání vytváření více instancí stejné destinace
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Povolí více instancí stejné destinace
                        launchSingleTop = true
                        // Obnoví stav při návratu na destinaci
                        restoreState = true
                    }
                },
                icon = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = "",
                            tint = if (currentRoute?.hasRoute(item.route::class) == true) Color(0xFF5C84FF) else Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.Transparent,
                    unselectedIconColor = Color.Transparent,
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}


sealed class BottomNavItem(val route: Any, val icon: ImageVector) {
    object Home : BottomNavItem(Screens.Home, Icons.Filled.Home)
    object Add : BottomNavItem(Screens.Add, Icons.Filled.Add)
    object Trending : BottomNavItem(Screens.Scoreboard, Icons.Filled.TrendingUp)
    object Profile : BottomNavItem(Screens.Profile, Icons.Filled.Person)
}


