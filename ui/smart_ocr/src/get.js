import axios from 'axios';

axios.defaults.baseURL = 'https://3.15.215.137:2020/'
//axios.defaults.baseURL = 'https://localhost:2020/'
export default {
  /**
   * Fetch the list of transcripts in the queue
   * @returns API response promise object
   */
  getTranscriptQueue() {
    return axios.get('/queue/fetch');
  },

  /**
   * Get a transcript from the queue
   * @param transcriptId identifier of the transcript
   * @returns API response promise object
   */
  getQueuedTranscriptResults(transcriptId){
    const queuedTranscriptCmd = {
      "id": transcriptId
    };
    return axios.post('/queue/fetch/one', JSON.stringify(queuedTranscriptCmd));
  },

  /**
   * Get a transcript image from the API
   * @param transcriptId identifier of the transcript image being requested
   * @returns API response promise object
   */
  getTranscriptImage(transcriptId){
    const transcriptImageCmd = {
      "id": transcriptId
    };
    return axios.post('/transcript/img/fetch/one', transcriptId);
  }
}
