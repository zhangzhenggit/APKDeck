# APK Deck Design

## Product position

APK Deck is a native Android Studio / IntelliJ IDEA utility for operating on the
application modules in the current project across connected Android devices.
It is an application operations surface, not a general-purpose package browser.

## Naming system

| Surface | Name |
|---|---|
| Product and plugin | APK Deck |
| Repository | `apk-deck` |
| Gradle root project | `APKDeck` |
| Plugin ID and group | `dev.apkdeck` |
| Kotlin package | `dev.apkdeck` |
| Entry action | `OpenApkDeckAction` |
| Main dialog | `ApkDeckDialog` |
| Table model | `AppInstallationTableModel` |
| Environment variable | `APK_DECK_STUDIO_PATH` |

The display name uses a space. Code identifiers use `ApkDeck`; technical runtime
names such as worker threads use `APKDeck`.

## Native UI principles

- Use `DialogWrapper`, `ComboBox`, `JBTable`, `JBScrollPane`, standard buttons,
  checkboxes, labels, and IDE-provided icons.
- Use `JBUI` spacing and `UIManager` colors so Light, Darcula, high contrast, and
  DPI scaling remain controlled by the host IDE.
- Installation states reuse native SVG and animated IDE assets where possible.
  Custom painting is limited to the system-app chip and circular push progress.
- Avoid fixed decorative backgrounds, gradients, shadows, and web-style cards.

## Main dialog structure

```text
Device  [connected device                         ] [refresh]    summary [reboot required]

Application                         Installation                         Actions
module name                         [state icon] state               reinstall clear uninstall push
package name                        active APK path

version                             live operation status                              Close
```

The table has three logical regions:

1. **Application** — module name and copyable package name.
2. **Installation** — centered icon + status as one group, with the active APK
   path on the second line.
3. **Actions** — reinstall, clear data, uninstall, and system APK push.

## Retained behavior

- Current device selector and explicit refresh.
- Stable project-module ordering.
- Installed count summary.
- Dynamic status text in the footer.
- Plugin version in the footer.
- Reboot-required indicator and reboot flow.
- Row-level operation spinners.
- Circular system APK push progress.
- Separate Push System APK dialog.
- Local APK package-name validation, including validation progress and errors.
- Device target path explanation.
- Remove `/data/app` overlay and clear app data options.
- All ADB work off the EDT and all UI updates on the EDT.

## Removed behavior

- Row selection checkboxes.
- Select All / Deselect All.
- Batch uninstall.

These controls added persistent visual weight for a destructive operation that is
safer and clearer at row level. No device/package synchronization checkbox is
introduced; selecting the device already defines the operation target.

## Installation states

| State | Glyph | Color |
|---|---|---|
| Installed user app | Filled Android head | Android green |
| Updated system app | Filled Android head | Android green |
| System app | Microchip | IDE blue |
| Not installed | Filled Android head | Disabled foreground |
| Querying | Native animated progress icon | IDE loading foreground |

The glyph and primary state label are centered together rather than centering the
glyph and text independently.
