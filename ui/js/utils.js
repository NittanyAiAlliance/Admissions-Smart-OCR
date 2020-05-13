//API URL ADDRESS
const apiUrl = "http://3.15.215.137:2020";
//const apiUrl = "http://localhost:2020";
//API ENDPOINT MAP
let apiNodeMap = {
    getToken : "/token/get",
    postFile : "/file/post",
    postSubmission: "/submission/post",
    getSubmissions: "/submission/get"
};
/**
 * Send an API request with no parameters
 * @param node API node to call on
 * @param onComplete function to call on completion of request
 */
function sendNoParamRequest(node, onComplete){
    //Assemble request url
    let reqUrl = apiUrl + node;
    //Perform request
    $.ajax({
        type : "POST",
        url : reqUrl,
        success : function(apiResponse){
            onComplete(JSON.parse(apiResponse));
        }
    });
}

/**
 * Send a request to the API which contains parameters
 * @param node API node to call on
 * @param req request parameters
 * @param onComplete function to call on completion of request
 */
function sendRequest(node, req, onComplete){
    //Add assigned client token to request
    req = addClientToken(req);
    //Perform request
    let reqUrl = apiUrl + node;
    $.ajax({
        type : "POST",
        url : reqUrl,
        data : JSON.stringify(req),
        success : function(apiReturn) {
            onComplete(JSON.parse(apiReturn));
        }
    });
}

/**
 * Add client token to API request
 * @param req request/cmd object
 * @returns req object but with a token
 */
function addClientToken(req){
    req.token = getToken();
    return req;
}

function setToken(token){
    sessionStorage.setItem("token", token);
}

function getToken(req){
    return "Q9O/dH4FGf5V+K5GxOzo1Cu3S6gKobbg9XMuElCv6u8=";
}