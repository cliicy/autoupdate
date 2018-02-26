
var rss_href = "";
function D2DFeedsControl(feedUrl, options) {
  // node elements.
  this.nodes = {};
  this.collapseElements = [];
  
  // the feeds.
  this.feedUrl = feedUrl;
  this.result = "";
  
  this.parseOptions_(options);
  
  if (this.ti_container == null || this.desc_container==null || this.whole_div == null || this.title_imageContainer == null) return;
  this.setup_();
}


/*
 * Default display time in milliseconds for each entry.
 * @type Number
 */
D2DFeedsControl.DEFAULT_DISPLAY_TIME = 5000;
/*
 * Default fadeout transition time in milliseconds for each entry.
 * @type Number
 */
D2DFeedsControl.DEFAULT_FADEIN_TIME = 1000;
/*
 * Default time between transition steps in milliseconds
 * @type Number
 */
D2DFeedsControl.DEFAULT_TRANSISTION_STEP = 40;

D2DFeedsControl.DEFAULT_NUM_RESULTS = 10;
/**
 * Setup default option map and apply overrides from constructor.
 * @param {Object} options Options map.
 * @private
 */
D2DFeedsControl.prototype.parseOptions_ = function(options) {
  // Default Options
  this.options = {
	title_container:'title_container',
	desc_container:'desc_container',
	title_imageContainer:'titleImage_container',
	whole_container:'whole_container',
	title_imageHtml:'<img class="news_image" src="images/feed-icon-28x28.png" />',
	numResults : D2DFeedsControl.DEFAULT_NUM_RESULTS,
    linkTarget : google.feeds.LINK_TARGET_BLANK,
    displayTime : D2DFeedsControl.DEFAULT_DISPLAY_TIME,
    transitionTime : D2DFeedsControl.DEFAULT_TRANSISTION_TIME,
    transitionStep : D2DFeedsControl.DEFAULT_TRANSISTION_STEP,
    fadeInTime: D2DFeedsControl.DEFAULT_FADEIN_TIME,
    pauseOnHover : true
  };

  if (options) {
    for (var o in this.options) {
      if (typeof options[o] != 'undefined') {
        this.options[o] = options[o];
      }
    }
  }
  

  // Override strange/bad options
  this.options.displayTime = Math.max(200, this.options.displayTime);
  this.options.fadeInTime = Math.max(0, this.options.fadeInTime);

  this.fadeInTime =  this.options.fadeInTime ;
  this.fadeInTimer = null;

  
    
  if (typeof this.options.title_container == "string") {
	  this.ti_container = document.getElementById(this.options.title_container);
  }
  if (typeof this.options.desc_container == "string") {
	  this.desc_container = document.getElementById(this.options.desc_container);
  }
  if(typeof this.options.whole_container == "string"){
	  this.whole_div =  document.getElementById(this.options.whole_container);
  }
  if(typeof this.options.title_imageContainer == "string"){
	  this.title_imageContainer =  document.getElementById(this.options.title_imageContainer);
  }
  if(typeof this.options.title_imageHtml == "string"){
	  this.title_imageHtml =  this.options.title_imageHtml;
  }
};

/**
 * Basic setup.
 * @private
 */
D2DFeedsControl.prototype.setup_ = function() {


  // The feedControl instance for generating entry HTML.
  this.feedControl = new google.feeds.FeedControl();
  this.feedControl.setLinkTarget(this.options.linkTarget);

  var feed = new google.feeds.Feed(this.feedUrl);
  feed.setResultFormat(google.feeds.Feed.JSON_FORMAT);
  feed.setNumEntries(10);
  feed.load(this.bind_(this.feedLoaded_));
  
};

/**
 * Helper method to bind this instance correctly.
 * @param {Object} method function/method to bind.
 * @return {Function}
 * @private
 */
D2DFeedsControl.prototype.bind_ = function(method) {
  var self = this;
  var opt_args = [].slice.call(arguments, 1);
  return function() {
    var args = opt_args.concat([].slice.call(arguments));
    return method.apply(self, args);
  }
};

/**
 * Callback associated with the AJAX Feed api after load.
 * @param {Object} result Loaded result.
 * @private
 */
D2DFeedsControl.prototype.feedLoaded_ = function( result) {
  if (result.error) {
     this.ti_container.innerHTML = '';
	 this.desc_container.innerHTML = '';
	 this.title_imageContainer.innerHTML = '';
	 this.whole_div.style.borderWidth = '0px';
     return;
  }

  this.result=result;
  this.displayResult_();

};



/**
 * Setup to display the Result
 * @private
 */
D2DFeedsControl.prototype.displayResult_ = function() {
  var result = this.result;

  this.entries = result.feed.entries;
  if(this.entries.length >0)
	  this.displayEntries_();
}


/**
 * Begin to display the entries.
 * @private
 */
D2DFeedsControl.prototype.displayEntries_ = function() {
  this.entryIndex = 0;
  this.displayCurrentEntry_();
  this.setDisplayTimer_();
  
}

/**
 * Display next entry.
 * @private
 */
D2DFeedsControl.prototype.displayNextEntry_ = function() {

  if (++this.entryIndex >= this.entries.length) {
	this.setup_();
    
    this.entryIndex = 0;
  }

  this.displayCurrentEntry_();
  this.setDisplayTimer_();
}

/**
 * Display current entry.
 * @private
 */
D2DFeedsControl.prototype.displayCurrentEntry_ = function() {
// first we hide the whole div
  this.whole_div.FadeState = -2;
  this.whole_div.style.opacity =  '0';
  this.whole_div.style.filter = 'alpha(opacity = 0))';
  this.whole_div.style.borderWidth = "1px";

  var currentDescpt = this.entries[this.entryIndex].contentSnippet;
  var currentTitle = this.entries[this.entryIndex].title;
//  currentDescpt = " backup settings let you configure backup properties for your full, incremental and resync jobs. Click Backup Now to submit jobs based on stored settings. View the video for more information. backup settings let you configure backup properties for your full, incremental and resync jobs. Click Backup Now to submit jobs based on stored settings. View the video for more information. backup settings let you configure backup properties for your full, incremental and resync jobs. Click Backup Now to submit jobs based on stored settings. View the video for more information.";
  var currentLink =  this.entries[this.entryIndex].link;
//  var link = this.createLink_(currentLink,
//		  currentTitle,
//          this.options.linkTarget);
  this.clearNode_(this.ti_container);
  rss_href = currentLink;
  //this.ti_container.appendChild(link);
  this.ti_container.innerHTML = currentTitle;
  this.desc_container.innerHTML = currentDescpt;
  this.title_imageContainer.innerHTML = this.title_imageHtml;
  if (this.options.pauseOnHover) {
	  this.ti_container.onmouseover = this.bind_(this.entryMouseOver_);
	  this.ti_container.onmouseout = this.bind_(this.entryMouseOut_);
	  this.desc_container.onmouseover = this.bind_(this.entryMouseOver_);
	  this.desc_container.onmouseout = this.bind_(this.entryMouseOut_);
	  }
  this.fade_in();
}

/**
 * 					1
 * transparcy -2 ---------> opacity 2
 * 				<----------
 * 					-1 
 * @private
 */
D2DFeedsControl.prototype.animateFade_ = function(lastTick) 
{  
  var curTick = new Date().getTime();
  var elapsedTicks = curTick - lastTick;
  
  var element = this.whole_div;
 
  if(element.FadeTimeLeft <= elapsedTicks)
  {
    element.style.opacity = element.FadeState == 1 ? '1' : '0';
    element.style.filter = 'alpha(opacity = ' 
        + (element.FadeState == 1 ? '100' : '0') + ')';
    element.FadeState = element.FadeState == 1 ? 2 : -2;
    return;
  }
 
  element.FadeTimeLeft -= elapsedTicks;
  var newOpVal = element.FadeTimeLeft/this.fadeInTime;
  if(element.FadeState == 1)
    newOpVal = 1 - newOpVal;

  element.style.opacity = newOpVal;
  element.style.filter = 'alpha(opacity = ' + (newOpVal*100) + ')';
  var cb = this.bind_(this.animateFade_, curTick);
  if (this.fadeInTimer) {
	    clearTimeout(this.fadeInTimer);
	    this.fadeInTimer = null;
	  }
  this.fadeInTimer = setTimeout(cb, 33);
}


D2DFeedsControl.prototype.fade_in = function()
{

  if(this.whole_div == null)
    return;
  this.whole_div.FadeState = 1;
  this.whole_div.FadeTimeLeft = this.fadeInTime;
  var cb = this.bind_(this.animateFade_, new Date().getTime());
  if (this.fadeInTimer) {
	    clearTimeout(this.fadeInTimer);
	    this.fadeInTimer = null;
  }
  this.fadeInTimer = setTimeout(cb, 33);
   
}

/**
 * Mouse over events for main entry.
 * @private
 */
D2DFeedsControl.prototype.entryMouseOver_ = function(e) {
  this.clearDisplayTimer_();
  if (this.transitionTimer) {
    this.clearTransitionTimer_();
    this.displayCurrentEntry_();
  }
}

/**
 * Mouse out events for main entry.
 * @private
 */
D2DFeedsControl.prototype.entryMouseOut_ = function(e) {
  this.setDisplayTimer_();
}


/**
 * Sets the display timer.
 * @private
 */
D2DFeedsControl.prototype.setDisplayTimer_ = function() {
  if (this.displayTimer) {
    this.clearDisplayTimer_();
  }
  var cb = this.bind_(this.setFadeOutTimer_);
  this.displayTimer = setTimeout(cb, this.options.displayTime);
};

/**
 * Class helper method for the time now in milliseconds
 * @private
 */
D2DFeedsControl.timeNow = function() {
  var d = new Date();
  return d.getTime();
};

/**
 * Transition animation for fadeout. Cleanup when finished.
 * @private
 */
D2DFeedsControl.prototype.fadeOutEntry_ = function() {
  // Finished.
  this.clearTransitionTimer_();
  this.displayNextEntry_();
};

/**
 * Sets the transition timer for fadeout.
 * @private
 */
D2DFeedsControl.prototype.setFadeOutTimer_ = function() {
  this.clearTransitionTimer_();
  this.lastTick = D2DFeedsControl.timeNow();
  var cb = this.bind_(this.fadeOutEntry_);
  this.transitionTimer = setInterval(cb, this.options.transitionStep);
};

/**
 * Clear the transition timer. Used to prevent leaks.
 * @private
 */
D2DFeedsControl.prototype.clearTransitionTimer_ = function() {
  if (this.transitionTimer) {
    clearInterval(this.transitionTimer);
    this.transitionTimer = null;
  }
};

/**
 * Clear the display timer.
 * @private
 */
D2DFeedsControl.prototype.clearDisplayTimer_ = function() {
  if (this.displayTimer) {
    clearTimeout(this.displayTimer);
    this.displayTimer = null;
  }
};



/**
 * Helper method to properly clear a node and its children.
 * @param {Object} node Node to clear.
 * @private
 */
D2DFeedsControl.prototype.clearNode_ = function(node) {
  if (node == null) return;
  var child;
  while ((child = node.firstChild)) {
    node.removeChild(child);
  }
};




