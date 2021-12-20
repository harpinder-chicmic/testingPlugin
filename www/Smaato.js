var argscheck = require('cordova/argscheck'),
exec = require('cordova/exec');

var smaatoExport = {};

smaatoExport.setOptions =
	function(options, successCallback, failureCallback) {
	  if(typeof options === 'object' 
		  && typeof options.publisherId === 'string'
	      && options.publisherId.length > 0) {
		  cordova.exec(
			      successCallback,
			      failureCallback,
			      'Smaato',
			      'setOptions',
			      [options]
			  );
	  } else {
		  if(typeof failureCallback === 'function') {
			  failureCallback('options.publisherId should be specified.')
		  }
	  }
	};


/**
 * Creates a new AdMob banner view.
 *
 * @param {!Object} options The options used to create a banner.  They should
 *        be specified similar to the following.
 *
 *        {
 *          'publisherId': 'MY_PUBLISHER_ID',
 *          'adSize': AdMob.AD_SIZE.AD_SIZE_CONSTANT,
 *          'positionAtTop': false
 *        }
 *
 *        publisherId is the publisher ID from your AdMob site, adSize
 *        is one of the AdSize constants, and positionAtTop is a boolean to
 *        determine whether to create the banner above or below the app content.
 *        A publisher ID and AdSize are required.  The default for postionAtTop
 *        is false, meaning the banner would be shown below the app content.
 * @param {function()} successCallback The function to call if the banner was
 *         created successfully.
 * @param {function()} failureCallback The function to call if create banner
 *         was unsuccessful.
 */

 smaatoExport.initSmaato =
 function(options, successCallback, failureCallback) {
   console.log('initSmaato called 1');
   if(typeof options === 'undefined' || options == null) options = {};
   cordova.exec(
       successCallback,
       failureCallback,
       'Smaato',
       'initSmaato',
       [ options ]
   );
   console.log('initSmaato called 2');
};

smaatoExport.showBannerAd =
function(options, successCallback, failureCallback) {
  console.log('showBannerAd called 1');
  if(typeof options === 'undefined' || options == null) options = {};
  cordova.exec(
      successCallback,
      failureCallback,
      'Smaato',
      'showBannerAd',
      [ options ]
  );
  console.log('showBannerAd called 2');
};



smaatoExport.showInterstitialAd =
function(options, successCallback, failureCallback) {
  console.log('showInterstitialAd called 1');
  if(typeof options === 'undefined' || options == null) options = {};
  cordova.exec(
      successCallback,
      failureCallback,
      'Smaato',
      'showInterstitialAd',
      [ options ]
  );
  console.log('showInterstitialAd called 2');
};

smaatoExport.loadRewardedAd =
function(options, successCallback, failureCallback) {
  console.log('loadRewardedAd called 1');
  if(typeof options === 'undefined' || options == null) options = {};
  cordova.exec(
      successCallback,
      failureCallback,
      'Smaato',
      'loadRewardedAd',
      [ options ]
  );
  console.log('loadRewardedAd called 2');
};
smaatoExport.showRewardedAd =
function(options, successCallback, failureCallback) {
  console.log('showRewardedAd called 1');
  if(typeof options === 'undefined' || options == null) options = {};
  cordova.exec(
      successCallback,
      failureCallback,
      'Smaato',
      'showRewardedAd',
      [ options ]
  );
  console.log('showRewardedAd called 2');
};

smaatoExport.closeBannerAd = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, "Smaato", "closeBannerAd", []);
},

smaatoExport.closeRewardedAd = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, "Smaato", "closeRewardedAd", []);
},

smaatoExport.play  =  function (path, options, successCallback, errorCallback) {
  options = this.merge(this.DEFAULT_OPTIONS, options);
  exec(successCallback, errorCallback, "Smaato", "play", [path, options]);
},

smaatoExport.close = function (successCallback, errorCallback) {
  exec(successCallback, errorCallback, "Smaato", "close", []);
},

smaatoExport.DEFAULT_OPTIONS= {
  volume: 1.0,
  scalingMode: 1
},

smaatoExport.CALING_MODE= {
  SCALE_TO_FIT: 1,
  SCALE_TO_FIT_WITH_CROPPING: 2
},

smaatoExport.merge= function () {
  var obj = {};
  Array.prototype.slice.call(arguments).forEach(function(source) {
      for (var prop in source) {
          obj[prop] = source[prop];
      }
  });
  return obj;
}

module.exports = smaatoExport;