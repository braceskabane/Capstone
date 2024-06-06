package com.dicoding.picodiploma.loginwithanimation.data.response

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class Response(
	val backend: String? = null,
	val kerasVersion: String? = null,
	val className: String? = null,
	val config: Config? = null
) : Parcelable

@Parcelize
data class KernelInitializer(
//	val registeredName: Any? = null,
	val module: String? = null,
	val className: String? = null,
	val config: Config? = null
) : Parcelable

@Parcelize
data class Config(
	val name: String? = null,
	val layers: List<LayersItem?>? = null,
	val biasInitializer: BiasInitializer? = null,
//	val kernelRegularizer: Any? = null,
//	val kernelConstraint: Any? = null,
	val dtype: String? = null,
	val useBias: Boolean? = null,
	val units: Int? = null,
//	val biasConstraint: Any? = null,
	val trainable: Boolean? = null,
//	val activityRegularizer: Any? = null,
	val kernelInitializer: KernelInitializer? = null,
	val activation: String? = null,
//	val biasRegularizer: Any? = null,
//	val seed: Any? = null,
//	val noiseShape: Any? = null,
//	val rate: Any? = null,
	val ragged: Boolean? = null,
	val sparse: Boolean? = null,
//	val batchInputShape: List<Any?>? = null
) : Parcelable

//@Parcelize
//data class BuildConfig(
////	val inputShape: List<Any?>? = null
//) : Parcelable

@Parcelize
data class BiasInitializer(
//	val registeredName: Any? = null,
	val module: String? = null,
	val className: String? = null,
	val config: Config? = null
) : Parcelable

@Parcelize
data class LayersItem(
//	val registeredName: Any? = null,
	val module: String? = null,
//	val buildConfig: BuildConfig? = null,
	val className: String? = null,
	val config: Config? = null
) : Parcelable
