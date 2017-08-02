/*
read the following wiki before using rule file
https://github.com/alibaba/anyproxy/wiki/What-is-rule-file-and-how-to-write-one
*/
module.exports = {
	/*
	These functions will overwrite the default ones, write your own when necessary.
    Comments in Chinese are nothing but a translation of key points. Be relax if you dont understand.
    致中文用户：中文注释都是只摘要，必要时请参阅英文文档。欢迎提出修改建议。
	*/
    summary:function(){
        return "this is a blank rule for AnyProxy";
    },




    //=======================
    //when getting a request from user
    //收到用户请求之后
    //=======================

    //是否截获https请求
    //should intercept https request, or it will be forwarded to real server
    shouldInterceptHttpsReq :function(req){
		if(req.headers.host =="mp.weixin.qq.com:443"){
			return true;
		}else{
			return false;
		}
    },

    //是否在本地直接发送响应（不再向服务器发出请求）
	//whether to intercept this request by local logic 
	//if the return value is true, anyproxy will call dealLocalResponse to get response data and will not send request to remote server anymore
    //req is the user's request sent to the proxy server
	shouldUseLocalResponse : function(req,reqBody){
        return false;
	},

    //如果shouldUseLocalResponse返回true，会调用这个函数来获取本地响应内容
    //you may deal the response locally instead of sending it to server
    //this function be called when shouldUseLocalResponse returns true 
    //callback(statusCode,resHeader,responseData)
    //e.g. callback(200,{"content-type":"text/html"},"hello world")
	dealLocalResponse : function(req,reqBody,callback){
        callback(statusCode,resHeader,responseData)
	},



    //=======================
    //when ready to send a request to server
    //向服务端发出请求之前
    //=======================

    //替换向服务器发出的请求协议（http和https的替换）
    //replace the request protocol when sending to the real server
    //protocol : "http" or "https"
    replaceRequestProtocol:function(req,protocol){
    	var newProtocol = protocol;
    	return newProtocol;
    },

    //替换向服务器发出的请求参数（option)
    //option is the configuration of the http request sent to remote server. You may refers to http://nodejs.org/api/http.html#http_http_request_options_callback
    //you may return a customized option to replace the original one
    //you should not overwrite content-length header in options, since anyproxy will handle it for you
    replaceRequestOption : function(req,option){
        var newOption = option;
        return newOption;
    },

    //替换请求的body
    //replace the request body
    replaceRequestData: function(req,data){
        return data;
    },



    //=======================
    //when ready to send the response to user after receiving response from server
    //向用户返回服务端的响应之前
    //=======================

    //替换服务器响应的http状态码
    //replace the statusCode before it's sent to the user
    replaceResponseStatusCode: function(req,res,statusCode){
    	var newStatusCode = statusCode;
    	return newStatusCode;
    },

    //替换服务器响应的http头
    //replace the httpHeader before it's sent to the user
    //Here header == res.headers
    replaceResponseHeader: function(req,res,header){
    	var newHeader = header;
    	return newHeader;
    },

    //替换服务器响应的数据
    //replace the response from the server before it's sent to the user
    //you may return either a Buffer or a string
    //serverResData is a Buffer. for those non-unicode reponse , serverResData.toString() should not be your first choice.
    replaceServerResDataAsync: function(req,res,serverResData,callback){
		debugger;
		if(/mp\/getmasssendmsg/i.test(req.url)){//当链接地址为公众号历史消息页面时(第一种页面形式)
			console.log("---------1");
            if(serverResData.toString() !== ""){
                try {//防止报错退出程序
                    var reg = /msgList = (.*?);/;//定义历史消息正则匹配规则
                    var ret = reg.exec(serverResData.toString());//转换变量为string
                    HttpPost(ret[1],req.url,"/html");//这个函数是后文定义的，将匹配到的历史消息json发送到自己的服务器
                }catch(e){//如果上面的正则没有匹配到，那么这个页面内容可能是公众号历史消息页面向下翻动的第二页，因为历史消息第一页是html格式的，第二页就是json格式的。
                    console.log("---------2");
					 try {
                        var json = JSON.parse(serverResData.toString());
                        if (json.general_msg_list != []) {
                        HttpPost(json.general_msg_list,req.url,"/json");//这个函数和上面的一样是后文定义的，将第二页历史消息的json发送到自己的服务器
                        }
                     }catch(e){
                       console.log(e);//错误捕捉
                     }
                    callback(serverResData);//直接返回第二页json内容
                }
            }
        }else if(/mp\/profile_ext\?action=home/i.test(req.url)){//当链接地址为公众号历史消息页面时(第二种页面形式)
            console.log("---------3");
			try {
                var reg = /var msgList = \'(.*?)\';/;//定义历史消息正则匹配规则（和第一种页面形式的正则不同）
                var ret = reg.exec(serverResData.toString());//转换变量为string
                HttpPost(ret[1],req.url,"/html");//这个函数是后文定义的，将匹配到的历史消息json发送到自己的服务器
            }catch(e){
                callback(serverResData);
            }
        }else if(/mp\/profile_ext\?action=getmsg/i.test(req.url)){//第二种页面表现形式的向下翻页后的json
            console.log("---------4");
			try {
                var json = JSON.parse(serverResData.toString());
                if (json.general_msg_list != []) {
                    HttpPost(json.general_msg_list,req.url,"/json");//这个函数和上面的一样是后文定义的，将第二页历史消息的json发送到自己的服务器
                }
            }catch(e){
                console.log(e);
            }
            callback(serverResData);
        }
        callback(serverResData);
    },

    //Deprecated    
    // replaceServerResData: function(req,res,serverResData){
    //     return serverResData;
    // },

    //在请求返回给用户前的延迟时间
    //add a pause before sending response to user
    pauseBeforeSendingResponse : function(req,res){
    	var timeInMS = 1; //delay all requests for 1ms
    	return timeInMS; 
    }

};

function HttpPost(str,url,path) {//将json发送到服务器，str为json内容，url为历史消息页面地址，path是接收程序的路径和文件名
    var http = require('http');
    var data = {
        str: str,
        url: url
    };
    content = require('querystring').stringify(data);
    var options = {
        method: "POST",
        host: "localhost",//注意没有http://，这是服务器的域名。
        port: 1707,
        path: path,//接收程序的路径和文件名
        headers: {
            'Content-Type': 'application/json; charset=UTF-8',
            "Content-Length": content.length
        }
    };
    var req = http.request(options, function (res) {
        res.setEncoding('utf8');
        res.on('data', function (chunk) {
            console.log('BODY: ' + chunk);
        });
    });
    req.on('error', function (e) {
        console.log('problem with request: ' + e.message);
    });
    req.write(content);
    req.end();
}