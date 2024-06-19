package com.dicoding.picodiploma.loginwithanimation.view.predict

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityPredictBinding
import java.text.DecimalFormat
import java.text.NumberFormat
import kotlin.math.pow

class PredictActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPredictBinding
    private var result: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPredictBinding.inflate(layoutInflater)
        setContentView(binding.root)

        result = intent.getStringExtra("PREDICTION_RESULT")
        result?.let {
            val formattedResult = formatUang(it.toDouble() * 10000)
            binding.tvResult.text = formattedResult
            binding.tvResult2.text = formattedResult
        }

        setupAction()
    }

    private fun setupAction() {
        binding.btnPredict.setOnClickListener {
            val hargaProperti = result?.toDoubleOrNull()?.times(10000) ?: 0.0
            val tenorKredit = binding.edKredit.text.toString().toIntOrNull() ?: 0
            val sukuBungaTahunan = binding.edSukubunga.text.toString().toDoubleOrNull() ?: 0.0
            val uangMuka = binding.edUangMuka.text.toString().toDoubleOrNull() ?: 0.0

            val cicilanPerBulan = hitungCicilanPerBulan(hargaProperti, tenorKredit, sukuBungaTahunan, uangMuka)
            binding.edCicilan.text = formatUang(cicilanPerBulan)

            binding.tableLayout.visibility = View.VISIBLE
        }
    }

    private fun hitungCicilanPerBulan(hargaProperti: Double, tenorKredit: Int, sukuBungaTahunan: Double, uangMuka: Double): Double {
        val jumlahPinjaman = hargaProperti - uangMuka
        val sukuBungaBulanan = sukuBungaTahunan / 12 / 100
        val jumlahCicilan = tenorKredit * 12

        return if (jumlahPinjaman > 0 && sukuBungaBulanan > 0 && jumlahCicilan > 0) {
            jumlahPinjaman * (sukuBungaBulanan * (1 + sukuBungaBulanan).pow(jumlahCicilan.toDouble())) /
                    ((1 + sukuBungaBulanan).pow(jumlahCicilan.toDouble()) - 1)
        } else {
            0.0
        }
    }

    private fun formatUang(amount: Double): String {
        val formatter: NumberFormat = DecimalFormat("#,###")
        return formatter.format(amount)
    }
}