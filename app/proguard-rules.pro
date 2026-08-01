# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class io.github.muntasimulhaque.ninetynine.data.** {
    *** Companion;
}
-keepclasseswithmembers class io.github.muntasimulhaque.ninetynine.data.** {
    kotlinx.serialization.KSerializer serializer(...);
}
