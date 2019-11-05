//API URL ADDRESS
const apiUrl = "https://localhost:2020";
//API ENDPOINT MAP
let apiNodeMap = {
    getToken : "/token/get"
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