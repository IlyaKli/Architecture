package com.yusmp.plan.presentation

import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.squareup.seismic.ShakeDetector
import com.yusmp.plan.R
import com.yusmp.plan.databinding.ActivityMainBinding
import com.yusmp.plan.presentation.auth.phone.PhoneAuthFragment
import com.yusmp.plan.presentation.common.extentions.observeFlow
import com.yusmp.plan.presentation.common.models.AppEvent
import com.yusmp.plan.presentation.common.utils.AppSnackBarUtils.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.mapNotNull

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding
    private val navController: NavController by lazy {
        val host = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        host.navController
    }

    private val bottomNaviVisibilityList = setOf(
        R.id.profile_tab_fragment,
        R.id.catalog_tab_fragment,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        observeViewModel()
    }

    private fun observeViewModel() {
        observeFlow(viewModel.appWideEvents.mapNotNull { it.firstOrNull() }) { event ->
            event.handleAppWideEvent()
            viewModel.removeAppWideEvent(event.id)
        }
        observeFlow(viewModel.uiEvents.mapNotNull { it.firstOrNull() }) { event ->
            event.handleEvent()
            viewModel.removeEvent(event.id)
        }
    }

    private fun AppEvent.handleAppWideEvent() {
        val message = when (this) {
            is AppEvent.ErrorMessage -> this.message
            is AppEvent.Unknown -> getString(R.string.general_error_text_unknown)
            is AppEvent.NoInternet -> getString(R.string.general_error_text_no_internet)
        }
        showSnackBar(viewGroup = binding.root, message = message)
    }

    private fun MainUiEvent.handleEvent() {
        when (this) {
            is MainUiEvent.SetHomeAsStartDestination -> {
                setHomeAsStartDestination()
            }

            MainUiEvent.SetAuthAsStartDestination -> {
                setAuthAsStartDestination()
            }

            MainUiEvent.ObserveDeviceShake -> {
                observeDeviceShake()
            }
        }
    }

    private fun setAuthAsStartDestination() {
        binding.bottomNavView.isVisible = false
        val graph = navController.navInflater.inflate(R.navigation.main_nav_graph).apply {
            setStartDestination(R.id.authorization_nav_graph)
        }
        navController.setGraph(graph, PhoneAuthFragment.createArgBundle(isFirstLaunch = true))
    }

    fun setHomeAsStartDestination() {
        binding.bottomNavView.isVisible = true
        val graph = navController.navInflater.inflate(R.navigation.main_nav_graph).apply {
            setStartDestination(R.id.profile_tab_fragment)
        }
        navController.setGraph(graph, null)
        setUpBottomNav()
    }

    private fun setUpBottomNav() {
        binding.bottomNavView.setupWithNavController(navController)
        observeFlow(navController.currentBackStackEntryFlow) { navBackStackEntry ->
            binding.bottomNavView.isVisible = navBackStackEntry.destination.id in bottomNaviVisibilityList
        }
    }

    private fun observeDeviceShake() {
        val sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        val shakeDetector = ShakeDetector { viewModel.launchChuckerActivity() }
        shakeDetector.start(sensorManager, SensorManager.SENSOR_DELAY_GAME)
    }
}