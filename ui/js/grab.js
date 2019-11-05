/**
 * Request a token from the API
 * @param onComplete validate token method
 */
function getApiToken(onComplete){
    sendNoParamRequest(apiNodeMap.getToken, onComplete);
}