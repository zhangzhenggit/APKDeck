# APK Deck

APK Deck is an Android Studio and IntelliJ IDEA plugin for inspecting project
application modules on connected Android devices. It provides reinstall,
clear-data, uninstall, and system APK push operations from one native IDE dialog.

## Build

Set the Android Studio path in `local.properties`:

```properties
studioPath=D:/Android/Android Studio
```

Alternatively set `APK_DECK_STUDIO_PATH`, then run:

```powershell
.\gradlew.bat compileKotlin
```

Generate the installable plugin ZIP:

```powershell
.\gradlew.bat buildPlugin
```

Delivery builds must increment `pluginVersion` in `gradle.properties` and
`PLUGIN_VERSION` in `ApkDeckDialog.kt` first. ZIP output is written to
`build/distributions/`.

## Artwork attribution

Android robot artwork is based on work created and shared by Google and is used
under the Creative Commons Attribution 3.0 License.
