var getNativeData = function(type, successCallback, failureCallback, data) {
   if (data === undefined) {
       data = '';
   }

   cordova.exec(successCallback, failureCallback, "swsmsplugin", type, [data]);
};
