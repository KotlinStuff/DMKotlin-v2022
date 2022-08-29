package es.javiercarrasco.mydialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.javiercarrasco.mydialogs.databinding.ActivityMainBinding
import es.javiercarrasco.mydialogs.databinding.DialogLayoutBinding
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            btnWithDialogFragment.setOnClickListener {
                val myDialogFragment = MyDialogFragment()
                myDialogFragment.show(supportFragmentManager, "teGusta")
            }

            btnAlertDialog.setOnClickListener {
                myAlertDialog(getString(R.string.txtAlertDialog))
            }

            btnAlertDialogList.setOnClickListener {
                myAlertDialogList()
            }

            val namesArray: Array<String> = resources.getStringArray(R.array.array_nombres)

            btnAlertDialogSinglePersistentList.setOnClickListener {
                myAlertDialogSinglePersistentList(namesArray)
            }

            btnAlertDialogMultiPersistentList.setOnClickListener {
                myAlertDialogMultiPersistentList(namesArray)
            }

            btnCustomAlertDialog.setOnClickListener {
                myCustomAlertDialog()
            }

            btnTimePicker.setOnClickListener {
                val cal = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, minute)

                    tvTimePicker.text = SimpleDateFormat("HH:mm").format(cal.time)
                    tvTimePicker.visibility = View.VISIBLE
                }

                // Al estar dentro del with(binding), se debe especificar
                // el contexto con this@MainActivity.
                TimePickerDialog(
                    this@MainActivity,
                    timeSetListener,
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE),
                    true
                ).show()
            }

            btnDatePicker.setOnClickListener {
                val cal = Calendar.getInstance()
                val dateSetListener = DatePickerDialog.OnDateSetListener { _, i, i2, i3 ->
                    cal.set(Calendar.YEAR, i)
                    // ENERO - 0, FEBRERO - 1, ..., DICIEMBRE - 11
                    cal.set(Calendar.MONTH, i2 + 1)
                    cal.set(Calendar.DAY_OF_MONTH, i3)

                    tvDatePicker.text = "${cal.get(Calendar.DAY_OF_MONTH)}" +
                            "/${cal.get(Calendar.MONTH)}" +
                            "/${cal.get(Calendar.YEAR)}"
                    tvDatePicker.visibility = View.VISIBLE
                }

                // Dentro de with(binding) se especifica el contexto con this@MainActivity.
                DatePickerDialog(
                    this@MainActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()

            }

            btnStartProgressBar.setOnClickListener {
                var progressBarStatus = 0
                var seccion = 0
                progressBar.progress = 0

                // Se inicia el Thread.
                // Se crea un hilo ficticio imitando una tarea.
                Thread(Runnable {
                    while (progressBarStatus < 100) {
                        // Se actualiza el estado del Progress Bar.
                        progressBarStatus = seccion
                        progressBar.progress = progressBarStatus

                        // Operación que se realizará.
                        try {
                            seccion += 10
                            Thread.sleep(1000)
                        } catch (e: InterruptedException) {
                            e.printStackTrace()
                        }
                    }

                    // Acciones que se realizarán al finalizar la tarea.
                    this@MainActivity.runOnUiThread {
                        Toast.makeText(this@MainActivity, "Tarea finalizada!!!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }).start()
            }
        }
    }

    private val actionButton = { dialog: DialogInterface, which: Int ->
        Toast.makeText(this, android.R.string.ok, Toast.LENGTH_SHORT).show()
        binding.root.setBackgroundColor(Color.GREEN)
    }

    private fun myAlertDialog(message: String) {
        // Se crea el AlertDialog.
        MaterialAlertDialogBuilder(this).apply {
            // Se asigna un título.
            setTitle(android.R.string.dialog_alert_title)
            // Se asgina el cuerpo del mensaje.
            setMessage(message)
            // Se define el comportamiento de los botones.
            setPositiveButton(
                android.R.string.ok,
                DialogInterface.OnClickListener(function = actionButton)
            )
            setNegativeButton("No") { _, _ ->
                Toast.makeText(context, "No", Toast.LENGTH_SHORT).show()
                binding.root.setBackgroundColor(Color.RED)
            }
            setNeutralButton(android.R.string.cancel) { _, _ ->
                Toast.makeText(context, android.R.string.cancel, Toast.LENGTH_SHORT).show()
                binding.root.setBackgroundColor(Color.WHITE)
            }
        }.show() // Se muestra el AlertDialog.
    }

    private fun myAlertDialogList() {
        val namesArray = resources.getStringArray(R.array.array_nombres)

        AlertDialog.Builder(this).apply {
            setTitle("My AlertDialog con lista")
            setItems(R.array.array_nombres) { _, which ->
                Toast.makeText(
                    context,
                    namesArray[which],
                    Toast.LENGTH_LONG
                ).show()
            }
        }.show()
    }

    private fun myAlertDialogSinglePersistentList(names: Array<String>) {
        AlertDialog.Builder(this).apply {
            var selectedPosition = -1
            setTitle("My AlertDialog con lista simple")

            setSingleChoiceItems(R.array.array_nombres, -1) { _, which ->
                selectedPosition = which
                Log.d("DEBUG", names[selectedPosition])
            }

            setPositiveButton(android.R.string.ok) { dialog, _ ->
//                val selectedPosition = (dialog as AlertDialog).listView.checkedItemPosition
                if (selectedPosition != -1) {
                    Toast.makeText(
                        context,
                        names[selectedPosition],
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            setNegativeButton(android.R.string.cancel) { _, _ ->
                Toast.makeText(
                    context,
                    android.R.string.cancel,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }.show()
    }

    private fun myAlertDialogMultiPersistentList(names: Array<String>) {
        AlertDialog.Builder(this).apply {
            val selectedItems = ArrayList<Int>()

            setTitle("My AlertDialog con lista multiple")

            setMultiChoiceItems(R.array.array_nombres, null) { _, which, isChecked ->
                if (isChecked) {
                    selectedItems.add(which)
                    Log.d("DEBUG", "Checked: " + names[which])
                } else if (selectedItems.contains(which)) {
                    selectedItems.remove(which)
                    Log.d("DEBUG", "UnChecked: " + names[which])
                }
            }

            setPositiveButton(android.R.string.ok) { _, _ ->
                var textToShow = "Checked: "
                if (selectedItems.size > 0) {
                    for (item in selectedItems) {
                        textToShow = textToShow + names[item] + " "
                    }
                } else textToShow = "No items checked!"

                Toast.makeText(context, textToShow, Toast.LENGTH_SHORT).show()
            }

            setNegativeButton(android.R.string.cancel) { _, _ ->
                Toast.makeText(context, android.R.string.cancel, Toast.LENGTH_SHORT).show()
            }
        }.show()
    }

    private fun myCustomAlertDialog() {
        AlertDialog.Builder(this).apply {
            // Se infla el layout personalizado del diálogo.
            val bindingCustom = DialogLayoutBinding.inflate(layoutInflater)

            setView(bindingCustom.root)

            setPositiveButton(android.R.string.ok) { _, _ ->
                Toast.makeText(
                    context,
                    "User: ${bindingCustom.etUsername.text}\n" +
                            "Pass: ${bindingCustom.etPassword.text}",
                    Toast.LENGTH_SHORT
                ).show()
            }

            setNegativeButton(android.R.string.cancel) { dialog, _ ->
                Toast.makeText(
                    context,
                    android.R.string.cancel,
                    Toast.LENGTH_SHORT
                ).show()
                dialog.dismiss()
            }
        }.show()
    }
}

class MyDialogFragment : DialogFragment() {
    // Se crea la estructura del diálogo.
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            AlertDialog.Builder(it).run {
                setMessage(R.string.my_first_dialog)
                setPositiveButton(android.R.string.ok) { dialog, which ->
                    // Acciones si se pulsa OK.
                    Log.d("DEBUG", "Acciones si acepta.")
                    Toast.makeText(
                        it,
                        "Acciones si acepta",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                setNeutralButton(android.R.string.cancel) { dialog, which ->
                    // Acciones si se pulsa CANCEL.
                    Log.d("DEBUG", "Acciones si cancela.")
                    Toast.makeText(
                        it,
                        "Acciones si cancela",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }.create()
        } ?: throw IllegalStateException("La Activity no puede ser nula")
    }
}