import Vuex from "vuex";
import Vue from "vue";
import get from './get';

Vue.use(Vuex)

const Store = new Vuex.Store({
  state : {
    // WebSocket client object
    client : null,
    // Transcript map, key is document id, value is transcript data object
    transcripts: new Map()
  },
  actions : {
    handleUpdate(context, msg) {
      // Get the command payload from the message object
      const cmd = JSON.parse(msg.data);
      // Dispatch the action related to the command type with the transcript id
      context.commit(cmd.type, cmd.did);
    },
    handleError( { context } ) {
      // TODO: something here / DEBUG
      console.log("SOCKET EXCEPTION");
    },
    /**
     * Create a new WebSockets connection to the transcript queue socket, if necessary
     * @param context context object
     */
    createSocketConnection(context){
      /* Do we really need to create a new connection?
       * The answer is probably not as this action is dispatched every time
       * the user returns to the transcript queue screen
       */
      if(context.state.client != null){
        // Do nothing - there is already a connection, tho thanks for checking in
        return;
      }
      // Create new WebSocket client object
      const client = new WebSocket("ws://localhost:2021");
      // Set handlers for the WebSocket client
      client.onmessage = (msg) => context.dispatch('handleUpdate', msg);
      client.onerror = () => context.dispatch('handleError');
      // Commit mutation to store the client object that was just created
      context.commit('createConnection', client);
    },
    /**
     * Send a check out message to the transcript queue socket
     * @param context context object
     * @param transcriptId Transcript ID that was checked out
     */
    checkOut(context, transcriptId) {
      // Create the check out command JSON object to be sent to the queue socket
      const checkOutCmd = JSON.stringify({
        endpoint : "queue",
        // Command type is check out
        type : "checkOut",
        // Document ID is the transcript ID that was checked out
        did : transcriptId,
        uid : sessionStorage.getItem("uid")
      });
      // Send the check out command to the queue socket
      context.state.client.send(checkOutCmd);
    },
    /**
     * Send a check in message to the transcript queue socket
     * @param context context object
     * @param transcriptId transcript ID that was checked in
     */
    checkIn(context, transcriptId) {
      // Create the check in command JSON object to be send to the queue socket
      const checkInCmd = JSON.stringify({
        endpoint : "queue",
        // Command type is check in
        type : "checkIn",
        // Document ID is the transcript ID that was checked in
        did : transcriptId,
        uid : sessionStorage.getItem("uid")
      });
      // Send the check in command to the queue socket
      context.state.client.send(checkInCmd);
    },
    /**
     * Fetch the available transcripts in the queue from the API
     * @param context context object
     */
    fetchTranscripts(context) {
      get.getTranscriptQueue().then(transcripts => {
        context.commit('setTranscripts', transcripts.data.queue);
      });
    }
  },
  mutations : {
    /**
     * Create a new WebSockets connection when the application is opened
     * @param state store state object
     * @param client WebSocket client created in the handler action
     */
    createConnection(state, client) {
      state.client = client;
    },
    /**
     * Update the transcript map in application state
     * @param state state object
     * @param transcripts transcripts array from API
     */
    setTranscripts(state, transcripts) {
      // Create temp map
      const transcriptsMap = new Map();
      // Iterate through API response array to get transcripts
      for(let i = 0; i < transcripts.length; i++) {
        const transcript = transcripts[i];
        // Set a transcript to the temp map
        transcriptsMap.set(transcript.DOCUMENT_ID, transcript);
      }
      // Update the state object with the new transcripts
      state.transcripts = transcriptsMap;
    },
    /**
     * Handle another client telling us that a transcript has been checked out
     * @param state store state object
     * @param transcriptId Document ID of the transcript that has been checked out
     */
    checkOut(state, transcriptId){
      // Fetch the transcript that another client told us they checked out from the queue map
      const mutatedTranscript = state.transcripts.get(transcriptId);
      // Set the checked out flag to true ~ we just were told that it was checked out
      mutatedTranscript.CHECKED_OUT = true;
      // Perform mutation to the transcript queue map
      state.transcripts.set(transcriptId, mutatedTranscript);
    },
    /**
     * Handle another client telling us that a transcript has been checked in
     * @param state store state object
     * @param transcriptId Document ID of the transcript that has been checked in
     */
    checkIn(state, transcriptId){
      // Fetch the transcript that another client told us they checked in from the queue map
      const mutatedTranscript = state.transcripts.get(transcriptId);
      // Set the checked in flag to false ~ we just were told that it was checked in
      mutatedTranscript.CHECKED_OUT = false;
      // Perform mutation to the transcript queue map
      state.transcripts.set(transcriptId, mutatedTranscript);
    }
  }
});

export default Store;
