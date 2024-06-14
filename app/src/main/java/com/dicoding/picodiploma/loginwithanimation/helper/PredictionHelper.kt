package com.dicoding.picodiploma.loginwithanimation.helper

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.R
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.tflite.client.TfLiteInitializationOptions
import com.google.android.gms.tflite.gpu.GpuDelegate
import com.google.android.gms.tflite.gpu.support.TfLiteGpu
import com.google.android.gms.tflite.java.TfLite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class PredictionHelper(
    private val modelName: String = "predict_realestate.tflite",
    val context: Context,
    private val onResult: (String) -> Unit,
    private val onError: (String) -> Unit,
    private val onInitialized: () -> Unit
) {

    private var interpreter: Interpreter? = null
    private var gpuDelegate: GpuDelegate? = null
    private var isGPUSupported: Boolean = false
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    init {
        initializeTfLite()
    }

    fun isInterpreterInitialized(): Boolean {
        return interpreter != null
    }

    fun initializeTfLite() {
        coroutineScope.launch {
            try {
                Log.d(TAG, "Checking GPU availability...")
                val task = TfLiteGpu.isGpuDelegateAvailable(context)
                Tasks.await(task)
                val gpuAvailable = task.result ?: false
                val optionsBuilder = TfLiteInitializationOptions.builder()
                if (gpuAvailable) {
                    Log.d(TAG, "GPU available, enabling GPU delegate support.")
                    optionsBuilder.setEnableGpuDelegateSupport(true)
                    isGPUSupported = true
                } else {
                    Log.d(TAG, "GPU not available.")
                }
                TfLite.initialize(context, optionsBuilder.build())
                Log.d(TAG, "TensorFlow Lite initialized.")
                loadLocalModel()
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(context.getString(R.string.tflite_is_not_initialized_yet))
                }
                Log.e(TAG, "Error during TensorFlow Lite initialization: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private suspend fun loadLocalModel() {
        try {
            Log.d(TAG, "Loading model from assets...")
            val buffer: ByteBuffer = withContext(Dispatchers.IO) {
                loadModelFile(context.assets, modelName)
            }
            Log.d(TAG, "Model loaded successfully.")
            initializeInterpreter(buffer)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            withContext(Dispatchers.Main) {
                onError(ioException.message.toString())
            }
            Log.e(TAG, "Error loading model: ${ioException.message}")
        }
    }

    private suspend fun initializeInterpreter(model: ByteBuffer) {
        try {
            Log.d(TAG, "Initializing interpreter...")
            val options = Interpreter.Options()
            if (isGPUSupported) {
                gpuDelegate = GpuDelegate()
                options.addDelegate(gpuDelegate)
                Log.d(TAG, "GPU delegate added.")
            }
            withContext(Dispatchers.IO) {
                interpreter = Interpreter(model, options)
            }
            if (interpreter != null) {
                Log.d(TAG, "Interpreter initialized successfully.")
                withContext(Dispatchers.Main) {
                    onInitialized() // Add this line
                }
            } else {
                Log.e(TAG, "Failed to initialize interpreter.")
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                onError(e.message.toString())
            }
            Log.e(TAG, "Error initializing interpreter: ${e.message}")
        }
    }

    fun predict(
        bathroom: String,
        bedroom: String,
        buildingArea: String,
        landArea: String,
        location: String
    ) {
        if (interpreter == null) {
            onError(context.getString(R.string.no_tflite_interpreter_loaded))
            Log.e(TAG, "Interpreter is null.")
            return
        }

        coroutineScope.launch {
            try {
                Log.d(TAG, "Starting prediction...")

                val locationNumeric = when (location) {
                    "Bandung" -> 0.0f
                    "Bekasi" -> 1.0f
                    "Bogor" -> 2.0f
                    "Depok" -> 3.0f
                    "JAKSEL" -> 4.0f
                    "Jakarta Barat" -> 5.0f
                    "Jakarta Pusat" -> 6.0f
                    "Jakarta Selatan" -> 7.0f
                    "Jakarta Timur" -> 8.0f
                    "Jakarta Utara" -> 9.0f
                    else -> 0.0f
                }

                val inputs = FloatArray(28)
                inputs[0] = bathroom.toFloat()
                inputs[1] = bedroom.toFloat()
                inputs[2] = buildingArea.toFloat()
                inputs[3] = landArea.toFloat()
                inputs[4] = locationNumeric
                for (i in 5 until 28) {
                    inputs[i] = 0.0f
                }

                val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 28), DataType.FLOAT32)
                inputFeature0.loadArray(inputs)

                val outputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
                interpreter?.run(inputFeature0.buffer, outputFeature0.buffer)

                val result = outputFeature0.floatArray[0].toString()
                Log.d(TAG, "Prediction result: $result")

                withContext(Dispatchers.Main) {
                    onResult(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onError(e.message.toString())
                }
                Log.e(TAG, "Error during prediction: ${e.message}")
            }
        }
    }

    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
        return assetManager.openFd(modelPath).use { fileDescriptor ->
            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
                val fileChannel = inputStream.channel
                val startOffset = fileDescriptor.startOffset
                val declaredLength = fileDescriptor.declaredLength
                fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            }
        }
    }

    fun close() {
        interpreter?.close()
        gpuDelegate?.close()
    }

    companion object {
        private const val TAG = "PredictionHelper"
    }
}
//class PredictionHelper(
//    private val modelName: String = "predict_realestate.tflite",
//    val context: Context,
//    private val onResult: (String) -> Unit,
//    private val onError: (String) -> Unit,
//) {
//
//    private var interpreter: InterpreterApi? = null
//    private var isGPUSupported: Boolean = false
//
//    init {
//        TfLiteGpu.isGpuDelegateAvailable(context).onSuccessTask { gpuAvailable ->
//            val optionsBuilder = TfLiteInitializationOptions.builder()
//            if (gpuAvailable) {
//                optionsBuilder.setEnableGpuDelegateSupport(true)
//                isGPUSupported = true
//            }
//            TfLite.initialize(context, optionsBuilder.build())
//        }.addOnSuccessListener {
//            loadLocalModel()
//        }.addOnFailureListener {
//            onError(context.getString(R.string.tflite_is_not_initialized_yet))
//        }
//    }
//
//    private fun loadLocalModel() {
//        try {
//            val buffer: ByteBuffer = loadModelFile(context.assets, modelName)
//            initializeInterpreter(buffer)
//        } catch (ioException: IOException) {
//            ioException.printStackTrace()
//        }
//    }
//
//    private fun initializeInterpreter(model: Any) {
//        interpreter?.close()
//        try {
//            val options = InterpreterApi.Options()
//                .setRuntime(InterpreterApi.Options.TfLiteRuntime.FROM_SYSTEM_ONLY)
//            if (isGPUSupported){
//                options.addDelegateFactory(GpuDelegateFactory())
//            }
//            if (model is ByteBuffer) {
//                interpreter = InterpreterApi.create(model, options)
//            }
//        } catch (e: Exception) {
//            onError(e.message.toString())
//            Log.e(TAG, e.message.toString())
//        }
//    }
//
//    fun predict(
//        bathroom: String,
//        bedroom: String,
//        buildingArea: String,
//        landArea: String,
//        location: String
//    ) {
//        if (interpreter == null) {
//            onError(context.getString(R.string.no_tflite_interpreter_loaded))
//            return
//        }
//
//        try {
//            // Convert inputs to float array
//            val inputs = floatArrayOf(
//                bathroom.toFloat(),
//                bedroom.toFloat(),
//                buildingArea.toFloat(),
//                landArea.toFloat(),
//                location.toFloat()
//            )
//
//            // Create input tensor
//            val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 5), DataType.FLOAT32)
//            inputFeature0.loadArray(inputs)
//
//            // Prepare output tensor
//            val outputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 1), DataType.FLOAT32)
//
//            // Run model inference
//            interpreter?.run(inputFeature0.buffer, outputFeature0.buffer)
//
//            // Get the result
//            val result = outputFeature0.floatArray[0].toString()
//
//            // Call onResult callback with prediction result
//            onResult(result)
//        } catch (e: Exception) {
//            onError(e.message.toString())
//            Log.e(TAG, e.message.toString())
//        }
//    }
//
//    private fun loadModelFile(assetManager: AssetManager, modelPath: String): MappedByteBuffer {
//        assetManager.openFd(modelPath).use { fileDescriptor ->
//            FileInputStream(fileDescriptor.fileDescriptor).use { inputStream ->
//                val fileChannel = inputStream.channel
//                val startOffset = fileDescriptor.startOffset
//                val declaredLength = fileDescriptor.declaredLength
//                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
//            }
//        }
//    }
//
////    fun close() {
////        interpreter?.close()
////    }
//
//    companion object {
//        private const val TAG = "PredictionHelper"
//    }
//
//
//}