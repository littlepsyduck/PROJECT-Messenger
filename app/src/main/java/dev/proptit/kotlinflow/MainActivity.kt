package dev.proptit.kotlinflow

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import dev.proptit.kotlinflow.screen.auth.LoginFragment
import dev.proptit.kotlinflow.screen.chat.ChatListFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Start with login fragment
        if (savedInstanceState == null) {
            showFragment(LoginFragment())
        }
    }

    fun showFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
        
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        
        transaction.commit()
    }

    fun showChatList() {
        showFragment(ChatListFragment(), false)
    }
}