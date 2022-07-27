package com.example.application

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.application.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private lateinit var appBD: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appBD = AppDatabase.getDatabase(this)

        binding.btnWriteData.setOnClickListener {
            writeData()
        }

        binding.btnReadData.setOnClickListener {
            readData()
        }

        binding.btnDeleteAll.setOnClickListener {
            GlobalScope.launch {
                appBD.studentDao().deleteAll()
            }
        }

        binding.btnUpdateData.setOnClickListener {
             updateData()
        }

    }

    private fun updateData() {

        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val rollNo = binding.etRollNo.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){

            GlobalScope.launch(Dispatchers.IO) {
                appBD.studentDao().update(firstName,lastName,rollNo.toInt())
            }
            ClearInputs()
            Toast.makeText(this@MainActivity,"Se Actualizo Correctamente el registro",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this@MainActivity,"Todos los campos son Requeridos!!",Toast.LENGTH_LONG).show()
        }

    }

    private fun writeData(){
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val rollNo = binding.etRollNo.text.toString()

        if (firstName.isNotEmpty() && lastName.isNotEmpty() && rollNo.isNotEmpty()){
            val student = Student(
                null,firstName,lastName,rollNo.toInt()
            )
            GlobalScope.launch(Dispatchers.IO) {
                appBD.studentDao().insert(student)
            }
            ClearInputs()
            Toast.makeText(this@MainActivity,"Se Inserto Correctamente el registro",Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(this@MainActivity,"Todos los campos son Requeridos!!",Toast.LENGTH_LONG).show()
        }

    }

    private  suspend fun displayData(student: Student){

        withContext(Dispatchers.Main){
            binding.tvFirstName.text = student.firstName
            binding.tvLastName.text = student.lastName
            binding.tvRollNo.text = student.rollNo.toString()
        }
    }

    private fun readData(){
    val rollNo = binding.etRollNoRead.text.toString()

        if (rollNo.isNotEmpty()){
            lateinit var student: Student

            GlobalScope.launch {
               student = appBD.studentDao().findByRoll(rollNo.toInt())
                displayData(student)
            }
        }

    }


    private fun ClearInputs(){
        binding.etFirstName.text.clear()
        binding.etLastName.text.clear()
        binding.etRollNo.text.clear()

    }
}