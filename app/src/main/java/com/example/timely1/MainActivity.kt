package com.example.timely1

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.timely1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var conf: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Привязка макета через ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Устанавливаем Toolbar как ActionBar
        setSupportActionBar(binding.actionBar.toolbar)

        // Настройка системных отступов для WindowInsets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Настройка контроллера навигации
        navController = findNavController(R.id.fragmentContainerView)
        conf = AppBarConfiguration(
            setOf(
                R.id.Today,
                R.id.This_week,
                R.id.This_mounts,
                R.id.All_entries,
                R.id.Earning,
                R.id.Setings,
                R.id.New_entries
            ), binding.drawer
        )

        // Подключение контроллера навигации к Toolbar и NavigationView
        setupActionBarWithNavController(navController, conf)

        // Обработка кликов на пункты меню
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawer.closeDrawer(GravityCompat.START) // Закрываем DrawerLayout

            // Опции навигации: очищаем текущий фрагмент из стека
            val navOptions = NavOptions.Builder()
                .setPopUpTo(menuItem.itemId, true)
                .build()

            when (menuItem.itemId) {
                R.id.Today -> navController.navigate(R.id.Today, null, navOptions)
                R.id.This_week -> navController.navigate(R.id.This_week, null, navOptions)
                R.id.This_mounts -> navController.navigate(R.id.This_mounts, null, navOptions)
                R.id.All_entries -> navController.navigate(R.id.All_entries, null, navOptions)
                R.id.Earning -> navController.navigate(R.id.Earning, null, navOptions)
                R.id.Setings -> navController.navigate(R.id.Setings, null, navOptions)
                R.id.New_entries -> navController.navigate(R.id.New_entries, null, navOptions)
            }
            true
        }

        // Обработка кнопки для перехода на New_entries
        binding.newEntriesBtn.setOnClickListener {
            // Закрываем DrawerLayout
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            }

            // Выполняем навигацию на New_entries
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.New_entries, true) // Опционально очищаем стек
                .build()
            navController.navigate(R.id.New_entries, null, navOptions)
        }
    }

    // Обработка кнопки "Назад" в Toolbar
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(conf) || super.onSupportNavigateUp()
    }
}
