/**
 * Request a token from the API
 * @param onComplete validate token method
 */
function getApiToken(onComplete){
    sendNoParamRequest(apiNodeMap.getToken, onComplete);
}

function getSubmissions(onComplete){
    sendRequest(apiNodeMap.getSubmissions, {vibe: "check"},onComplete);
}