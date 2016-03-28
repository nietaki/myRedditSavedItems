window.RedditCommunicator = function() {
    console.log("RedditCommunicator loaded");
    
    var dataCallback = function(data) {
        console.log("dataCallback called with");
        console.log(data);
    };
  
    var removePrefix = function(thingName) {
        var i = thingName.indexOf('_');
        return thingName.substr(i+1)
    };
    
    var getPermalinkFromDataObject = function(data) {
        if(data.permalink) {
            return "https://www.reddit.com" + data.permalink;
        }
        
        var linkIdWithoutPrefix = removePrefix(data.link_id);
        var nameWithoutPrefix = removePrefix(data.name);
        
        return "https://www.reddit.com/r/" + data.subreddit + "/comments/" + linkIdWithoutPrefix + "/no_slug/" + nameWithoutPrefix
    };
    
    var transformData = function(outerData) {
        var kind = outerData.kind;
        var data = outerData.data;
        
        var body = null;
        if(data.body) {
            body = data.body;
        } else {
            if(data.selftext) { // to cater for the falsy ""
                body = data.selftext;
            }
        }
        
        var usableThumbnail = null;
        if(data.thumbnail && (data.thumbnail.indexOf("http") == 0)) {
            usableThumbnail = data.thumbnail;
        }
        
        return {
            kind: kind,
            name: data.name,
            subreddit: data.subreddit,
            isLink: (kind === "t3") && !data.is_self,
            isSelfPost: (kind === "t3") && data.is_self,
            isComment: kind === "t1",
            title: data.title || data.link_title,
            permalink: getPermalinkFromDataObject(data),
            url: data.url || data.link_url,
            body: body,
            createdUtc: data.created_utc,
            thumbnail: usableThumbnail,
            nsfw: data.over_18
        };
    };

    var displayResults = function() {
        console.log(RedditCommunicator.savedItems);
        /*
        var $content = $("#contentOutput");
        $content.empty();
        $.each(RedditCommunicator.savedItems, function(idx, obj) {
            $content.append("" + idx +". <a href=\"" + obj.permalink + "\">" + obj.title + "</a><br />");
        });
        */
        
        dataCallback(RedditCommunicator.savedItems);
    } 
    
    var onSuccess = function(data, textStatus, request) {
        console.log("success");
        var after = data.data.after;
        console.log(data);
        smallData = $.map(data.data.children, transformData);
        $.merge(RedditCommunicator.savedItems, smallData)
        
        if(smallData.length == 100) {
            retrieveSavedItems(after)
        } else {
            console.log("got back too few items, displaying");
            displayResults();
        }
    };
    
    var onError = function(data, textStatus, request) {
        console.log(textStatus);
        console.log(data);
        console.log("got an error, display the results");
        displayResults();
    };
    
    
    var retrieveSavedItems = function(after) {
        console.log("sending ajax");
        var token = ($.cookie("reddit_access_token"));
        var url = "https://oauth.reddit.com/user/nietaki/saved?limit=100";
        
        if(after) {
            url = url + "&after=" + after;
        }
        
        $.ajax({
            url: url,
            headers: {"Authorization" : "bearer " + token},
            success: onSuccess,
            error: onError
        });
    };

    var retrieveSavedItemsWithCallback = function(callback) {
        if(callback) {
            dataCallback = callback;
        }
        
        RedditCommunicator.savedItems = [];
        retrieveSavedItems(null);
    };
    
    return {
        savedItems: [],
        retrieveSavedItemsWithCallback: retrieveSavedItemsWithCallback
    }
}();