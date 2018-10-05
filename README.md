# SWSendSMS plugin

It gives functionality to send sms to single/multiple users.



## Install


### or via GIT

```
cordova plugin add http://182.48.193.130/Plugins-collection/cordova-plugin-swsendsms.git
```

## Usage

### 1. For sending sms;


### SEND SMS:

```
$obj = {"sms_list":[{"number":"", "message":"Hello"}]};
cordova.exec(function(success) {console.log(success)},function(error) {console.log(error)},"swsmsplugin","sendSMS",[$obj]);
```

### Sample response:

```
{"status":"success"}
```

### SEND WhatsApp message

```
$obj = {"sms_list":[{"number":"", "message":"Hello"}, {"number":"", "message":"Hello"}]};
cordova.exec(function(success) {console.log(success)},function(error) {console.log(error)},"swwhatsappplugin","sendMessage",[$obj]);
```

