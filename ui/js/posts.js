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

function sendSubmissionPost(user, fields, file, onComplete){
    let sendSubmissionCmd = {
        user: user,
        fields: fields,
        file: file
    };
    sendRequest(apiNodeMap.postSubmission, sendSubmissionCmd, onComplete);
}