package jesko.labelprinter.app

import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged


// courtesy of https://github.com/probonopd/ptouch-770

class MainActivity : AppCompatActivity() {
    private lateinit var labelText:EditText
    private lateinit var printButton: Button
    private lateinit var imageView: ImageView
    private lateinit var typefaceSelector: Spinner
    private lateinit var horizontalPadding: SeekBar
    private lateinit var borderThickness: SeekBar
    private lateinit var textOutline: CheckBox
    private lateinit var textSizeInput: SeekBar
    private val pxPmm = 170/24
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        labelText = findViewById(R.id.editTextTextPersonName2)
        printButton = findViewById(R.id.button_printJob)
        imageView = findViewById(R.id.imageView)
        typefaceSelector = findViewById(R.id.spinner)
        borderThickness = findViewById(R.id.seekBarBorderThickness)
        horizontalPadding = findViewById(R.id.seekBarHorizontalPadding)
        textOutline = findViewById(R.id.checkBox2)
        textSizeInput = findViewById(R.id.seekBarTextSize)

        printButton.setOnClickListener {
            labelText.setText("lkajsdf")
        }

        labelText.doAfterTextChanged { updateImage() }

        textOutline.setOnClickListener(
            OnClickListener { updateImage() })

        horizontalPadding.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateImage()
            }
        })

        borderThickness.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateImage()
            }
        })

        textSizeInput.setOnSeekBarChangeListener(object: OnSeekBarChangeListener{
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                // TODO Auto-generated method stub
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateImage()
            }
        })

        ArrayAdapter.createFromResource(
            this,
            R.array.Typefaces,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            typefaceSelector.adapter = adapter
        }

        typefaceSelector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateImage()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
    }

    private fun updateImage(){
        val text = labelText.text.toString()
        imageView.setImageBitmap(textAsBitmap(text,Color.BLACK));
    }

    private fun textAsBitmap(text: String?, textColor: Int): Bitmap? {
        if (text == null) {
            return null
        }
        if (text.isEmpty()) {
            return null
        }

        val height = 170 //px
        val deadBorderY = 21 //px
        val textScale = textSizeInput.progress.toFloat()/10

        val rectThickness = borderThickness.progress.toFloat()
        val textSize = ((height-(deadBorderY*2)-rectThickness)*textScale)

        val paintText = Paint()
        paintText.textSize = textSize
        paintText.color = textColor
        paintText.textAlign = Paint.Align.LEFT
        paintText.isFilterBitmap = false
        paintText.isAntiAlias = false
        paintText.typeface = getTypefaceFromSpinner()
        if (textOutline.isChecked){
            paintText.style = Paint.Style.STROKE
            paintText.strokeWidth = 2F
        }

        val textOffsetX = getHorizontalPaddingPx()
        val width = paintText.measureText(text) + textOffsetX*2+rectThickness*2

        val image = Bitmap.createBitmap(width.toInt(), height, Bitmap.Config.ARGB_8888)
        image.eraseColor(Color.WHITE);
        val canvas = Canvas(image)

        if (rectThickness > 0) {
            val paintRect = Paint()
            paintRect.style = Paint.Style.STROKE
            paintRect.isFilterBitmap = false
            paintRect.isAntiAlias = false
            paintRect.color = textColor
            paintRect.strokeWidth = rectThickness

            canvas.drawRect(
                textOffsetX.toFloat() + rectThickness / 2,
                deadBorderY.toFloat() + rectThickness / 2,
                width - textOffsetX.toFloat() - rectThickness / 2,
                height - deadBorderY.toFloat() - rectThickness / 2, paintRect
            )
        }

        if (text != null) {
            var yPos = height/2+textSize/2-textSize/4
            canvas.drawText("$text ", textOffsetX.toFloat()+rectThickness, yPos.toFloat() , paintText)
        }

        return image
    }

    private fun getTypefaceFromSpinner(): Typeface? {
        return when (typefaceSelector.selectedItem) {
            "Default" -> Typeface.DEFAULT
            "Default Bold" -> Typeface.DEFAULT_BOLD
            "Monospace" -> Typeface.MONOSPACE
            "Serif" -> Typeface.SERIF
            "Sans Serif" -> Typeface.SANS_SERIF
            else -> {
                Typeface.DEFAULT
            }
        }
    }

    private fun getHorizontalPaddingPx(): Int {
        var textOffsetX = 2*pxPmm // default is 2mm
        val guiOffset = horizontalPadding.progress
        if(guiOffset != null && guiOffset > 0 ){
            textOffsetX = guiOffset *pxPmm
        }
        if (guiOffset != null && guiOffset > 30){
            textOffsetX = 30 *pxPmm
        }
        return textOffsetX
    }
}
