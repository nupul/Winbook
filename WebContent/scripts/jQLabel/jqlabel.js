/**
 * @name: jQLabel
 * @version:1.0.0
 * @Author: Hasin Hayder [hasin@leevio.com]
 * @date: 14th Feb, 2011
 *
 * Use jQLabel to create labels like Gmail on the fly.
 * jQLabel searches for the text inside an element, and by matching its pattern
 * in a given array. If found, it makes it look like a Gmail Label.
 *
 * usage: see demo.html
 * usage: $(".label").jQLabel({"labels":{"Overdue":"#C73B0B","Expiring Today":"#978E43"}});
 *
 * changelog
 * March 08, 2011: Removed the requirement of adding CSS exclusively and made it a builtin feature
 * March 03, 2011: Added support to convert spans with a particular class look like a label
 * $(".important").jQLabel({color:"#FEFEFE"});
 */
(function($){
    var opts;
    var styleadded = false;
    $.fn.jQLabel = function(options){
        var defaults = {
            pointer:false,
            backgroundColor:"#CE0000" /* this will only be used if labels are not supplied */
        }
        opts = $.extend(defaults, options);
        if(!styleadded){
            $("head").append("\n\
                    <style type='text/css'>\n\
                        .jq-base{\n\
                            border-radius:3px;\n\
                            -moz-border-radius:3px;\n\
                            -webkit-border-radius:3px;\n\
                            color:#FEFEFE;\n\
                            padding:2px 4px;\n\
                            font:10px verdana,arial,sans-serif;\n\
                            margin:1px 2px 1px 2px;\n\
                            white-space: nowrap;\n\
                            vertical-align:middle;\n\
                            cursor:default;\n\
                            position:relative;\n\
                            top:-2px\n\
                         }\n\
                    </style>\n\
                    ");
            styleadded=true;
        }
        var labels={}
        for(j in opts['labels']){
            labels[j.toLowerCase()]=opts['labels'][j];
        }
        if($.isEmptyObject(opts['labels'])){
            $(this).addClass("jq-base");
            $(this).css("backgroundColor",opts['backgroundColor']);
            if(opts['pointer']) $(this).css("cursor","pointer");
        }else{
            $(this).each(function(){
                var key = $(this).text().toLowerCase();
                if(labels[key]) {
                    $(this).addClass("jq-base");
                    $(this).css("backgroundColor",labels[key]);
                    if(opts['pointer']) $(this).css("cursor","pointer");
                }
            })
        }
    }

})(jQuery);