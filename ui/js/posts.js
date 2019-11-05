/**
 * Post selected file to server
 * @param file file to upload
 * @param onComplete handle server response to image
 */
function sendFilePost(file, onComplete){
    let sendFileCmd = {
        file : file
    };
    sendRequest(apiNodeMap.postFile, sendFileCmd, onComplete);
}