package com.printxpress.app.ui.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.printxpress.app.databinding.ActivityGuidelinesFaqBinding


class GuidelinesFaqActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityGuidelinesFaqBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonBack.setOnClickListener { finish() }

        val requirements = listOf(
            "Accepted formats: PDF, PNG, JPG",
            "Minimum resolution: 300 DPI",
            "Colour mode: CMYK preferred (RGB files are converted automatically)",
            "Include a 3mm bleed margin on all sides",
            "Maximum file size: 10MB",
        )
        requirements.forEach { text ->
            val row = TextView(this)
            row.text = "\u2022  $text"
            row.setTextColor(getColor(com.printxpress.app.R.color.text_secondary))
            row.textSize = 12.5f
            row.setPadding(0, 4, 0, 4)
            binding.containerRequirements.addView(row)
        }

        val faqs = listOf(
            FaqItem(
                "How long does printing take?",
                "Most orders are ready within 1\u20132 business days after confirmation. Large banner or bulk orders may take longer \u2014 the estimated date is shown on your order."
            ),
            FaqItem(
                "Can I cancel an order?",
                "Yes, any time before it enters the Printing status. Open the order from My Orders and tap Cancel Order."
            ),
            FaqItem(
                "What if my file has low resolution?",
                "We'll flag it during file checks and contact you before printing \u2014 low-resolution artwork can print blurry, especially on large formats like banners."
            ),
            FaqItem(
                "Do you deliver outside Colombo?",
                "Yes, island-wide delivery is available at checkout for an additional fee, or you can choose free pickup from our location."
            ),
        )
        binding.recyclerFaq.layoutManager = LinearLayoutManager(this)
        binding.recyclerFaq.adapter = FaqAdapter(faqs)

        binding.buttonContactSupport.setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@printxpress.lk"))
            intent.putExtra(Intent.EXTRA_SUBJECT, "PrintXpress support request")
            startActivity(Intent.createChooser(intent, "Contact Support"))
        }
    }
}
