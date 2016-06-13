# cordova-plugin-panasonic-toughpad

Cordova android plugin for Panasonic ToughPad device, this version support for barcode reader only.

#### Prerequisite
1. Download & install Panasonic ToughPad SDK from official web site
2. Go to installation directory(for default is in C:\Program Files (x86)\Panasonic\Toughpad SDK\Library) and copy file Toughpad.jar to our source code directory in src/android/libs

#### Usage
Init API
```
PanasonicToughpad.initAPI('', function (res) {
    // success
}, function (error) {
    // error
});
```

Scan barcode
```
PanasonicToughpad.scanBarcode('', function (res) {
    // success
}, function (error) {
    // error
});
```