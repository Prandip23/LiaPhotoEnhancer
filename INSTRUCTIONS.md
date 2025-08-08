Lia — How to build & install (quick guide)

1) Download this ZIP and extract it locally.
2) (Optional) If you want GitHub Actions to download the TFLite model automatically, edit .github/workflows/build-debug.yml and set MODEL_URL, and add a GitHub secret HF_TOKEN with your Hugging Face token.
3) Add your TFLite model manually if you prefer: place the .tflite file at:
   app/src/main/assets/model.tflite
4) Build locally with Android Studio:
   - Open the project in Android Studio (Arctic Fox or newer recommended).
   - Let Gradle sync and install required SDKs.
   - Build > Build Bundle(s) / APK(s) > Build APK(s) to generate a signed debug APK (Android Studio will use a debug key automatically).
   - Or run: ./gradlew assembleDebug
5) Install the APK on your device via USB (adb install) or by transferring the APK and opening it on your phone.
6) To produce a Play-ready AAB, use the provided build-release workflow or build locally with Android Studio > Build > Generate Signed Bundle / APK (you'll need a keystore).
7) If you want me to integrate the exact Real-ESRGAN TFLite model and the precise pre/post-processing code, reply here and either:
   - provide a direct download link to the .tflite file (so I can include it in the package), or
   - add the model file into app/src/main/assets/ yourself and I'll give exact model wiring code.

If you'd like, I can now proceed to integrate the Real-ESRGAN model and provide the final build (I will need the actual model file or permission to download it).
