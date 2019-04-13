package ru.appkode.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import kotlinx.android.synthetic.main.activity_main.*
import ru.appkode.base.data.storage.DatabaseHelper
import ru.appkode.base.ui.books.BooksMainController
import ru.appkode.base.ui.core.core.util.obtainHorizontalTransaction

class MainActivity : AppCompatActivity() {

    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DatabaseHelper.createDatabase(applicationContext)
        router = Conductor.attachRouter(this, main_container, savedInstanceState)
        if (!router.hasRootController())
            router.setRoot(
                // Just math random. Nothing else.
                //if (Math.random() > 0.5f) DuckListController().obtainHorizontalTransaction()
                //else TaskListController().obtainHorizontalTransaction()
                //BookDetailsController.createController(136251).obtainVerticalTransaction()
                BooksMainController().obtainHorizontalTransaction()
                //BooksSearchController().obtainFadeTransaction()
            )
    }

    override fun onBackPressed() {
        if (!router.handleBack())
            super.onBackPressed()
    }
}
