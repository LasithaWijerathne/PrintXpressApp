# Firestore deserializes documents into these model classes by reflection,
# so their field names must survive minification/obfuscation.
-keep class com.printxpress.app.data.model.** { *; }
