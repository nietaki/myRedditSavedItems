window.RedditCommunicator = function() {
    console.log("RedditCommunicator loaded");
    
    var transformData = function(outerData) {
        var kind = outerData.kind;
        var data = outerData.data;

        return {
            kind: kind,
            name: data.name,
            subreddit: data.subreddit,
            isLink: (kind === "t3") && data.is_self,
            isSelfPost: (kind === "t3") && !data.is_self,
            isComment: kind === "t1"
        };
    };

    var onSuccess = function(data, textStatus, request) {
        alert("success");
        console.log(data);
        smallData = $.map(data.data.children, transformData);
        console.log(smallData);
    };


    $(function() {
        console.log("sending ajax");
        var token = ($.cookie("reddit_access_token"));
        $.ajax({
            url: "https://oauth.reddit.com/user/nietaki/saved",
            success: onSuccess,
            headers: {"Authorization" : "bearer " + token}
        });
    });
    
    return {};
}();