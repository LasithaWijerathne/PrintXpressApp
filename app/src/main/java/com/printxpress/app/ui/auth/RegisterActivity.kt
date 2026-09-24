package com.printxpress.app.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.printxpress.app.databinding.ActivityRegisterBinding
import com.printxpress.app.ui.main.MainActivity
import com.printxpress.app.util.Result

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }
        binding.textGoLogin.setOnClickListener { finish() }

        binding.buttonRegister.setOnClickListener {
            viewModel.register(
                name = binding.inputName.text.toString().trim(),
                email = binding.inputEmail.text.toString().trim(),
                phone = "", // reserved for future phone-auth support, see AuthRepository
                password = binding.inputPassword.text.toString(),
                confirmPassword = binding.inputConfirmPassword.text.toString()
            )
        }

        viewModel.registerResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> setLoading(true)
                is Result.Success -> {
                    setLoading(false)
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, MainActivity::class.java)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    finish()
                }
                is Result.Error -> {
                    setLoading(false)
                    binding.textError.text = result.message
                    binding.textError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.buttonRegister.isEnabled = !loading
        if (loading) binding.textError.visibility = View.GONE
    }
}
