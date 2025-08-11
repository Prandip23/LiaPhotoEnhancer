Lia — AI Image Enhancer (Ready-to-build package)

This package is prepared for high-end Android devices and contains the full app source code.
IMPORTANT: The actual high-quality TFLite model is NOT included due to size and licensing.
Place your model at: app/src/main/assets/model.tflite
or configure GitHub Actions to download it using a Hugging Face token (HF_TOKEN).

Languages :
// Kotlin : Utilized Kotlin as the primary programming language for developing the LiaPhotoEnhancer Android application. Leveraged Kotlin's conciseness, null safety features, and coroutines for building responsive and robust UI components with Jetpack Compose, and for implementing application logic including image processing interactions and file management.
MainActivity.kt, is written entirely in Kotlin.

// Groovy (for Gradle scripts) : Managed project build configurations and dependencies using Groovy for Gradle build scripts. Responsible for setting up library integrations, build types, product flavors (if any), and signing configurations.
build.gradle files (both at the project level and app level) are written in Groovy
